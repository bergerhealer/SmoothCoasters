package me.m56738.smoothcoasters;

import io.netty.buffer.Unpooled;
import me.m56738.smoothcoasters.implementation.Implementation;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.C2SPlayChannelEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;
import org.lwjgl.glfw.GLFW;

import java.util.NoSuchElementException;

public class SmoothCoasters implements ClientModInitializer {
    private static final Identifier HANDSHAKE = new Identifier("smoothcoasters", "hs");
    private static final Quaternionf IDENTITY = new Quaternionf();
    private static SmoothCoasters instance;
    private Implementation currentImplementation;
    private String version;
    private KeyBinding toggleBinding;

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

        toggleBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.smoothcoasters.toggle.camera",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F9,
                "category.smoothcoasters"
        ));

        C2SPlayChannelEvents.REGISTER.register((handler, sender, server, channels) -> {
            if (channels.contains(HANDSHAKE)) {
                ClientPlayNetworking.registerReceiver(HANDSHAKE, this::handleHandshake);
            }
        });

        C2SPlayChannelEvents.UNREGISTER.register((handler, sender, server, channels) -> {
            if (channels.contains(HANDSHAKE)) {
                reset();
                ClientPlayNetworking.unregisterReceiver(HANDSHAKE);
                setCurrentImplementation(null);
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleBinding.wasPressed()) {
                boolean enabled = !getRotationToggle();
                setRotationToggle(enabled);
                if (enabled) {
                    client.inGameHud.getChatHud().addMessage(Text.translatable("smoothcoasters.camera.enabled"));
                } else {
                    client.inGameHud.getChatHud().addMessage(Text.translatable("smoothcoasters.camera.disabled"));
                }
            }
        });
    }

    private void handleHandshake(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        byte[] versions = buf.readByteArray();
        client.execute(() -> performHandshake(versions));
    }

    private void setCurrentImplementation(Implementation implementation) {
        if (currentImplementation != null) {
            currentImplementation.unregister();
        }

        currentImplementation = implementation;

        if (currentImplementation != null) {
            PacketByteBuf response = new PacketByteBuf(Unpooled.buffer());
            response.writeByte(currentImplementation.getVersion());
            response.writeString(version);
            ClientPlayNetworking.send(HANDSHAKE, response);
            currentImplementation.register();
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
        setCurrentImplementation(findImplementation(offeredVersions));
    }

    public void onDisconnected() {
        currentImplementation = null;
    }

    public void reset() {
        setRotation(IDENTITY, 0);
        setRotationLimit(-180f, 180f, -90f, 90f);
    }

    public void setRotation(Quaternionf rotation, int ticks) {
        ((Rotatable) MinecraftClient.getInstance().gameRenderer).scSetRotation(rotation, ticks);
    }

    public void setEntityRotation(int entityId, Quaternionf rotation, int ticks) {
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

    public RotationMode getRotationMode() {
        return ((GameRendererMixinInterface) MinecraftClient.getInstance().gameRenderer).scGetRotationMode();
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
