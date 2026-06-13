package me.m56738.smoothcoasters;

import com.mojang.blaze3d.platform.InputConstants;
import me.m56738.smoothcoasters.implementation.Implementation;
import me.m56738.smoothcoasters.network.EntityPropertiesPayload;
import me.m56738.smoothcoasters.network.EntityRotationPayload;
import me.m56738.smoothcoasters.network.HandshakePayload;
import me.m56738.smoothcoasters.network.HandshakeResponsePayload;
import me.m56738.smoothcoasters.network.RotationLimitPayload;
import me.m56738.smoothcoasters.network.RotationPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ClientboundPlayChannelEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.NoSuchElementException;
import java.util.Objects;

public class SmoothCoasters implements ClientModInitializer {
    private static final Identifier HANDSHAKE = Identifier.fromNamespaceAndPath("smoothcoasters", "hs");
    private static final KeyMapping.Category CAMERA = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("smoothcoasters", "camera"));
    private static final Quaternionf IDENTITY = new Quaternionf();
    private static @Nullable SmoothCoasters instance;
    private @Nullable Implementation currentImplementation;
    private @Nullable String version;
    private @Nullable KeyMapping toggleBinding;

    public static SmoothCoasters getInstance() {
        return Objects.requireNonNull(instance);
    }

    public @Nullable String getVersion() {
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

        toggleBinding = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.smoothcoasters.toggle.camera",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F9,
                CAMERA
        ));

        PayloadTypeRegistry.clientboundPlay().register(HandshakePayload.ID, HandshakePayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(RotationPayload.ID, RotationPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(EntityRotationPayload.ID, EntityRotationPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(EntityPropertiesPayload.ID, EntityPropertiesPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(RotationLimitPayload.ID, RotationLimitPayload.CODEC);

        PayloadTypeRegistry.serverboundPlay().register(HandshakeResponsePayload.ID, HandshakeResponsePayload.CODEC);

        ClientPlayNetworking.registerGlobalReceiver(HandshakePayload.ID, this::handleHandshake);

        ClientboundPlayChannelEvents.UNREGISTER.register((_, _, _, channels) -> {
            if (channels.contains(HANDSHAKE)) {
                reset();
                setCurrentImplementation(null);
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleBinding.consumeClick()) {
                boolean enabled = !getRotationToggle();
                setRotationToggle(enabled);
                if (enabled) {
                    client.gui.hud.getChat().addClientSystemMessage(Component.translatable("smoothcoasters.camera.enabled"));
                } else {
                    client.gui.hud.getChat().addClientSystemMessage(Component.translatable("smoothcoasters.camera.disabled"));
                }
            }
        });

        DebugScreenEntries.register(Identifier.fromNamespaceAndPath("smoothcoasters", "smoothcoasters_version"), new SmoothCoastersDebugHudEntry());
    }

    private void handleHandshake(HandshakePayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> performHandshake(payload.versions()));
    }

    private void setCurrentImplementation(@Nullable Implementation implementation) {
        if (currentImplementation != null) {
            currentImplementation.unregister();
        }

        currentImplementation = implementation;

        if (currentImplementation != null && version != null) {
            ClientPlayNetworking.send(new HandshakeResponsePayload(currentImplementation.getVersion(), version));
            currentImplementation.register();
        }
    }

    private @Nullable Implementation findImplementation(byte[] offeredVersions) {
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
        return (GameRendererMixinInterface) Minecraft.getInstance().gameRenderer;
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
