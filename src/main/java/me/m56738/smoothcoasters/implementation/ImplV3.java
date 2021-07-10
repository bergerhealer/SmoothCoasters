package me.m56738.smoothcoasters.implementation;

import me.m56738.smoothcoasters.RotationMode;
import me.m56738.smoothcoasters.SmoothCoasters;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ImplV3 extends ImplV2 {
    private static final Identifier ENTITY_PROPERTIES = new Identifier("smoothcoasters", "eprop");
    private static final Identifier ROTATION_MODE = new Identifier("smoothcoasters", "rmode");

    @Override
    public byte getVersion() {
        return 3;
    }

    @Override
    public void register() {
        super.register();
        ClientPlayNetworking.registerReceiver(ENTITY_PROPERTIES, this::handleEntityProperties);
        ClientPlayNetworking.registerReceiver(ROTATION_MODE, this::handleRotationMode);
    }

    @Override
    public void unregister() {
        super.unregister();
        ClientPlayNetworking.unregisterReceiver(ENTITY_PROPERTIES);
        ClientPlayNetworking.unregisterReceiver(ROTATION_MODE);
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

    private void handleRotationMode(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        final RotationMode mode = RotationMode.values()[buf.readInt()];
        client.execute(() -> {
            SmoothCoasters.getInstance().setRotationMode(mode);
        });
    }
}
