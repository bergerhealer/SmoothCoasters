package me.m56738.smoothcoasters;

import me.m56738.smoothcoasters.implementation.Implementation;
import me.m56738.smoothcoasters.network.EntityPropertiesPayload;
import me.m56738.smoothcoasters.network.EntityRotationPayload;
import me.m56738.smoothcoasters.network.HandshakePayload;
import me.m56738.smoothcoasters.network.HandshakeResponsePayload;
import me.m56738.smoothcoasters.network.RotationLimitPayload;
import me.m56738.smoothcoasters.network.RotationPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.C2SPlayChannelEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.debug.DebugHudEntries;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.lwjgl.glfw.GLFW;

import java.util.NoSuchElementException;

public class SmoothCoasters implements ClientModInitializer {
    private static final Identifier HANDSHAKE = Identifier.of("smoothcoasters", "hs");
    private static final KeyBinding.Category CAMERA = KeyBinding.Category.create(Identifier.of("smoothcoasters", "camera"));
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
                CAMERA
        ));

        PayloadTypeRegistry.playS2C().register(HandshakePayload.ID, HandshakePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(RotationPayload.ID, RotationPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(EntityRotationPayload.ID, EntityRotationPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(EntityPropertiesPayload.ID, EntityPropertiesPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(RotationLimitPayload.ID, RotationLimitPayload.CODEC);

        PayloadTypeRegistry.playC2S().register(HandshakeResponsePayload.ID, HandshakeResponsePayload.CODEC);

        ClientPlayNetworking.registerGlobalReceiver(HandshakePayload.ID, this::handleHandshake);

        C2SPlayChannelEvents.UNREGISTER.register((handler, sender, server, channels) -> {
            if (channels.contains(HANDSHAKE)) {
                reset();
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

        DebugHudEntries.register(Identifier.of("smoothcoasters", "version"), new SmoothCoastersDebugHudEntry());
    }

    private void handleHandshake(HandshakePayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> performHandshake(payload.versions()));
    }

    private void setCurrentImplementation(Implementation implementation) {
        if (currentImplementation != null) {
            currentImplementation.unregister();
        }

        currentImplementation = implementation;

        if (currentImplementation != null) {
            ClientPlayNetworking.send(new HandshakeResponsePayload(currentImplementation.getVersion(), version));
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

    private GameRendererMixinInterface getGameRenderer() {
        return (GameRendererMixinInterface) MinecraftClient.getInstance().gameRenderer;
    }

    public void setRotation(Quaternionfc rotation, int ticks) {
        getGameRenderer().smoothcoasters$setRotation(rotation, ticks);
    }

    public void setRotationLimit(float minYaw, float maxYaw, float minPitch, float maxPitch) {
        getGameRenderer().smoothcoasters$setRotationLimit(
                minYaw, maxYaw, minPitch, maxPitch);
    }

    public boolean getRotationToggle() {
        return getGameRenderer().smoothcoasters$getRotationToggle();
    }

    public void setRotationToggle(boolean enabled) {
        getGameRenderer().smoothcoasters$setRotationToggle(enabled);
    }
}
