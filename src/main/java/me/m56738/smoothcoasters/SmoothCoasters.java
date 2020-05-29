package me.m56738.smoothcoasters;

import io.netty.buffer.Unpooled;
import me.m56738.smoothcoasters.network.NetworkImplementation;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.Quaternion;

public class SmoothCoasters implements ModInitializer {
    private static final Identifier HANDSHAKE = new Identifier("smoothcoasters", "handshake");
    private static SmoothCoasters instance;
    private NetworkImplementation currentImplementation;

    public static SmoothCoasters getInstance() {
        return instance;
    }

    @Override
    public void onInitialize() {
        instance = this;
        ClientSidePacketRegistry.INSTANCE.register(HANDSHAKE, this::handleHandshake);
    }

    private void handleHandshake(PacketContext context, PacketByteBuf buf) {
        byte[] versions = buf.readByteArray();
        context.getTaskQueue().execute(() -> {
            performHandshake(versions);
        });
    }

    private void setCurrentImplementation(NetworkImplementation implementation) {
        if (currentImplementation != null) {
            currentImplementation.unregister();
        }

        currentImplementation = implementation;

        if (currentImplementation != null) {
            PacketByteBuf response = new PacketByteBuf(Unpooled.buffer());
            response.writeByte(currentImplementation.getVersion());
            ClientSidePacketRegistry.INSTANCE.sendToServer(HANDSHAKE, response);
            currentImplementation.register();
        }
    }

    private NetworkImplementation findImplementation(byte[] offeredVersions) {
        for (NetworkImplementation implementation : NetworkImplementation.IMPLEMENTATIONS) {
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
        setCurrentImplementation(null);
    }

    public void resetRotation() {
        setRotation(Quaternion.IDENTITY, 0);
    }

    public void setRotation(Quaternion rotation, int ticks) {
        ((Rotatable) MinecraftClient.getInstance().worldRenderer).scSetRotation(rotation, ticks);
    }
}
