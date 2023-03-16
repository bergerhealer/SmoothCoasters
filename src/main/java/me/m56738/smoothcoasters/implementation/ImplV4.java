package me.m56738.smoothcoasters.implementation;

import me.m56738.smoothcoasters.SmoothCoasters;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;

public class ImplV4 implements Implementation {
    private static final Identifier ROTATION = new Identifier("smoothcoasters", "rot");
    private static final Identifier ENTITY_ROTATION = new Identifier("smoothcoasters", "erot");
    private static final Identifier ENTITY_PROPERTIES = new Identifier("smoothcoasters", "eprop");
    private static final Identifier ROTATION_LIMIT = new Identifier("smoothcoasters", "limit");

    @Override
    public byte getVersion() {
        return 4;
    }

    @Override
    public void register() {
        ClientPlayNetworking.registerReceiver(ROTATION, this::handleRotation);
        ClientPlayNetworking.registerReceiver(ENTITY_ROTATION, this::handleEntityRotation);
        ClientPlayNetworking.registerReceiver(ENTITY_PROPERTIES, this::handleEntityProperties);
        ClientPlayNetworking.registerReceiver(ROTATION_LIMIT, this::handleRotationLimit);
    }

    @Override
    public void unregister() {
        ClientPlayNetworking.unregisterReceiver(ROTATION);
        ClientPlayNetworking.unregisterReceiver(ENTITY_ROTATION);
        ClientPlayNetworking.unregisterReceiver(ENTITY_PROPERTIES);
        ClientPlayNetworking.unregisterReceiver(ROTATION_LIMIT);
    }

    private void handleRotation(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        final Quaternionf rotation = new Quaternionf(
                -buf.readFloat(), -buf.readFloat(),
                -buf.readFloat(), buf.readFloat()
        );
        final byte ticks = buf.readByte();
        client.execute(() -> SmoothCoasters.getInstance().setRotation(rotation, ticks));
    }

    private void handleEntityRotation(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        final int entity = buf.readInt();
        final Quaternionf rotation = new Quaternionf(
                buf.readFloat(), buf.readFloat(),
                buf.readFloat(), buf.readFloat()
        );
        final byte ticks = buf.readByte();
        client.execute(() -> SmoothCoasters.getInstance().setEntityRotation(entity, rotation, ticks));
    }

    private void handleEntityProperties(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        final int entity = buf.readInt();
        final byte ticks = buf.readByte();
        client.execute(() -> {
            if (ticks != 0) {
                SmoothCoasters.getInstance().setEntityTicks(entity, ticks);
            }
        });
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
