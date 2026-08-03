package me.m56738.smoothcoasters.common;

import com.mojang.blaze3d.platform.InputConstants;
import io.netty.buffer.Unpooled;
import me.m56738.smoothcoasters.common.network.*;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.Identifier;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

public class SmoothCoasters {
    public static final String MOD_ID = "smoothcoasters";
    public static final byte NETWORK_VERSION = 6;

    private static final KeyMapping.Category CAMERA = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("smoothcoasters", "camera"));
    private static final Quaternionf IDENTITY = new Quaternionf();
    private static @Nullable SmoothCoasters instance;
    private @Nullable KeyMapping toggleBinding;

    private final String version;
    private boolean enabled;

    public SmoothCoasters(String version) {
        this.version = version;
    }

    public static SmoothCoasters getInstance() {
        return Objects.requireNonNull(instance);
    }

    public static void setInstance(SmoothCoasters instance) {
        SmoothCoasters.instance = instance;
    }

    public void disable() {
        enabled = false;
        reset();
    }

    public void reset() {
        setRotation(IDENTITY, 0);
        setRotationLimit(-180f, 180f, -90f, 90f);
    }

    public void tick() {
        while (toggleBinding != null && toggleBinding.consumeClick()) {
            boolean enabled = !getRotationToggle();
            setRotationToggle(enabled);
            if (enabled) {
                Minecraft.getInstance().gui.hud.getChat().addClientSystemMessage(Component.translatable("smoothcoasters.camera.enabled"));
            } else {
                Minecraft.getInstance().gui.hud.getChat().addClientSystemMessage(Component.translatable("smoothcoasters.camera.disabled"));
            }
        }
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

    public void registerCustomPayloadTypes(CustomPayloadRegistrar registrar) {
        registrar.register(HandshakePayload.ID, HandshakePayload.CODEC, null, this::handleHandshake);
        registrar.register(RotationPayload.ID, RotationPayload.CODEC, PacketFlow.CLIENTBOUND, this::handleRotation);
        registrar.register(RotationLimitPayload.ID, RotationLimitPayload.CODEC, PacketFlow.CLIENTBOUND, this::handleRotationLimit);
    }

    public void registerKeyMappings(KeyMappingRegistrar registrar) {
        toggleBinding = new KeyMapping(
                "key.smoothcoasters.toggle.camera",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F9,
                CAMERA
        );
        registrar.register(toggleBinding);
    }

    private void handleHandshake(HandshakePayload payload, CustomPayloadHandler.Context context) {
        reset();
        FriendlyByteBuf input = new FriendlyByteBuf(Unpooled.wrappedBuffer(payload.data()));
        for (byte version : input.readByteArray()) {
            if (version == NETWORK_VERSION) {
                enabled = true;
                FriendlyByteBuf output = new FriendlyByteBuf(Unpooled.buffer());
                output.writeByte(NETWORK_VERSION);
                output.writeUtf(this.version);
                byte[] data = new byte[output.readableBytes()];
                output.readBytes(data);
                context.reply(new HandshakePayload(data));
                return;
            }
        }
        enabled = false;
    }

    private void handleRotation(RotationPayload payload, CustomPayloadHandler.Context context) {
        SmoothCoasters.getInstance().setRotation(payload.rotation(), payload.ticks());
    }

    private void handleRotationLimit(RotationLimitPayload payload, CustomPayloadHandler.Context context) {
        SmoothCoasters.getInstance().setRotationLimit(payload.minYaw(), payload.maxYaw(), payload.minPitch(), payload.maxPitch());
    }

    public String getVersion() {
        return version;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
