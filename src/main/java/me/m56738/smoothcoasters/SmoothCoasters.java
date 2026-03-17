package me.m56738.smoothcoasters;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import me.m56738.smoothcoasters.implementation.Implementation;
import me.m56738.smoothcoasters.mixin.ClientConnectionAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class SmoothCoasters implements ClientModInitializer {
    private static final Logger LOG = LogManager.getLogger("SmoothCoasters");
    private static final Identifier HANDSHAKE = new Identifier("smoothcoasters", "hs");
    private static SmoothCoasters instance;

    private final Map<Identifier, Implementation.PacketHandler> packetHandlers = new HashMap<>();
    private Implementation currentImplementation;
    private String version;
    private KeyBinding toggleBinding;
    private boolean pipelineInstalled;

    public static SmoothCoasters getInstance() {
        return instance;
    }

    public String getVersion() {
        return version;
    }

    public byte getNetworkVersion() {
        return currentImplementation != null ? currentImplementation.getVersion() : 0;
    }

    @Override
    public void onInitializeClient() {
        instance = this;
        version = FabricLoader.getInstance().getModContainer("smoothcoasters")
                .orElseThrow(NoSuchElementException::new).getMetadata().getVersion().getFriendlyString();

        LOG.info("SmoothCoasters {} loaded", version);

        toggleBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.smoothcoasters.toggle.camera",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F9,
                "category.smoothcoasters"
        ));

        ClientPlayNetworking.registerGlobalReceiver(HANDSHAKE, (c, h, b, s) -> {});
        for (String ch : new String[]{"rot", "bulk", "erot", "eprop", "rmode", "limit"}) {
            ClientPlayNetworking.registerGlobalReceiver(new Identifier("smoothcoasters", ch), (c, h, b, s) -> {});
        }

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.getNetworkHandler() != null && !pipelineInstalled) {
                installPipelineHandler(client);
            } else if (client.getNetworkHandler() == null) {
                pipelineInstalled = false;
            }

            while (toggleBinding.wasPressed()) {
                boolean enabled = !getRotationToggle();
                setRotationToggle(enabled);
                if (enabled) {
                    client.inGameHud.getChatHud().addMessage(new TranslatableText("smoothcoasters.camera.enabled"));
                } else {
                    client.inGameHud.getChatHud().addMessage(new TranslatableText("smoothcoasters.camera.disabled"));
                }
            }
        });
    }

    private void installPipelineHandler(MinecraftClient client) {
        try {
            Channel channel = ((ClientConnectionAccessor) client.getNetworkHandler().getConnection()).getChannel();
            if (channel != null && channel.pipeline().get("smoothcoasters") == null) {
                channel.pipeline().addBefore("packet_handler", "smoothcoasters", new PacketInterceptor());
            }
            pipelineInstalled = true;
        } catch (Exception e) {
            LOG.warn("Failed to install pipeline handler", e);
            pipelineInstalled = true;
        }
    }

    public boolean handlePacket(CustomPayloadS2CPacket packet) {
        Identifier channel = packet.getChannel();
        PacketByteBuf data = packet.getData();

        if (channel.equals(HANDSHAKE)) {
            byte[] versions = data.readByteArray();
            MinecraftClient.getInstance().execute(() -> performHandshake(versions));
            return true;
        }

        Implementation.PacketHandler handler = packetHandlers.get(channel);
        if (handler != null) {
            PacketByteBuf copy = new PacketByteBuf(data.copy());
            MinecraftClient.getInstance().execute(() -> {
                try {
                    handler.handle(copy);
                } finally {
                    copy.release();
                }
            });
            return true;
        }

        return false;
    }

    private void setCurrentImplementation(Implementation implementation) {
        packetHandlers.clear();
        currentImplementation = implementation;

        if (currentImplementation != null) {
            PacketByteBuf response = new PacketByteBuf(Unpooled.buffer());
            response.writeByte(currentImplementation.getVersion());
            response.writeString(version);

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.getNetworkHandler() != null) {
                client.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(HANDSHAKE, response));
            }

            currentImplementation.register(packetHandlers);
        }
    }

    private Implementation findImplementation(byte[] offeredVersions) {
        for (Implementation implementation : Implementation.IMPLEMENTATIONS) {
            byte version = implementation.getVersion();
            for (byte offeredVersion : offeredVersions) {
                if (offeredVersion == version) {
                    return implementation;
                }
            }
        }
        return null;
    }

    private void performHandshake(byte[] offeredVersions) {
        Implementation impl = findImplementation(offeredVersions);
        if (impl != null) {
            LOG.info("Negotiated protocol V{}", impl.getVersion());
        } else {
            LOG.warn("No compatible protocol version found");
        }
        setCurrentImplementation(impl);
    }

    public void onDisconnected() {
        packetHandlers.clear();
        currentImplementation = null;
    }

    public void reset() {
        setRotation(Quaternion.IDENTITY, 0);
        setRotationLimit(-180f, 180f, -90f, 90f);
    }

    public void setRotation(Quaternion rotation, int ticks) {
        ((Rotatable) MinecraftClient.getInstance().gameRenderer).scSetRotation(rotation, ticks);
    }

    public void setEntityRotation(int entityId, Quaternion rotation, int ticks) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world != null) {
            Entity entity = world.getEntityById(entityId);
            if (entity != null) {
                ((Rotatable) entity).scSetRotation(rotation, ticks);
            }
        }
    }

    public void setEntityTicks(int entityId, int ticks) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world != null) {
            Entity entity = world.getEntityById(entityId);
            if (entity != null) {
                ((Animatable) entity).scSetTicks(ticks);
            }
        }
    }

    public void setRotationMode(RotationMode mode) {
        ((GameRendererMixinInterface) MinecraftClient.getInstance().gameRenderer).scSetRotationMode(mode);
    }

    public void setRotationLimit(float minYaw, float maxYaw, float minPitch, float maxPitch) {
        ((GameRendererMixinInterface) MinecraftClient.getInstance().gameRenderer).scSetRotationLimit(
                minYaw, maxYaw, minPitch, maxPitch);
    }

    public boolean getRotationToggle() {
        return ((GameRendererMixinInterface) MinecraftClient.getInstance().gameRenderer).scGetRotationToggle();
    }

    public void setRotationToggle(boolean enabled) {
        ((GameRendererMixinInterface) MinecraftClient.getInstance().gameRenderer).scSetRotationToggle(enabled);
    }
}
