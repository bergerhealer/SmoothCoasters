package me.m56738.smoothcoasters.implementation;

import me.m56738.smoothcoasters.SmoothCoasters;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;

public class ImplV2 extends ImplV1 {
    private static final Identifier ENTITY_ROTATION = new Identifier("smoothcoasters", "erot");

    @Override
    public byte getVersion() {
        return 2;
    }

    @Override
    public void register() {
        super.register();
        ClientPlayNetworking.registerReceiver(ENTITY_ROTATION, this::handleEntityRotation);
    }

    @Override
    public void unregister() {
        super.unregister();
        ClientPlayNetworking.unregisterReceiver(ENTITY_ROTATION);
    }

    private void handleEntityRotation(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        final int entity = buf.readInt();
        final Quaternion rotation = new Quaternion(
                buf.readFloat(), buf.readFloat(),
                buf.readFloat(), buf.readFloat()
        );
        final byte ticks = buf.readByte();
        client.execute(() -> SmoothCoasters.getInstance().setEntityRotation(entity, rotation, ticks));
    }
}
