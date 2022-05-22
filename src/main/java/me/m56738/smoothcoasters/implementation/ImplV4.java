package me.m56738.smoothcoasters.implementation;

import me.m56738.smoothcoasters.RotationMode;
import me.m56738.smoothcoasters.SmoothCoasters;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ImplV4 extends ImplV3 {
    private static final Identifier ROTATION_LIMIT = new Identifier("smoothcoasters", "limit");

    public ImplV4() {
        // Disable deprecated features
        // After a deprecation period, ImplV1-3 can be deleted and the remaining features can be moved into this class

        // Packets contained in bulk packets bypass packet listeners, which can break stuff
        // They are rarely used because they are hard to implement on the server side
        hasBulk = false;

        // Camera mode is fundamentally broken and will be removed soon
        // Player mode is the only remaining mode (and the default since V4)
        hasRotationMode = false;
    }

    @Override
    public byte getVersion() {
        return 4;
    }

    @Override
    public void register() {
        super.register();
        ClientPlayNetworking.registerReceiver(ROTATION_LIMIT, this::handleRotationLimit);

        // Camera mode is deprecated, default to player mode
        SmoothCoasters.getInstance().setRotationMode(RotationMode.PLAYER);
    }

    @Override
    public void unregister() {
        super.unregister();
        ClientPlayNetworking.unregisterReceiver(ROTATION_LIMIT);
    }

    private void handleRotationLimit(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        final float minYaw = buf.readFloat();
        final float maxYaw = buf.readFloat();
        final float minPitch = buf.readFloat();
        final float maxPitch = buf.readFloat();
        client.execute(() -> {
            SmoothCoasters.getInstance().setRotationLimit(minYaw, maxYaw, minPitch, maxPitch);
        });
    }
}
