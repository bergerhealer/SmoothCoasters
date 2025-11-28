package me.m56738.smoothcoasters.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class MainThreadPayloadHandler<T extends CustomPacketPayload> implements ClientPlayNetworking.PlayPayloadHandler<T> {
    private final ClientPlayNetworking.PlayPayloadHandler<T> handler;

    public MainThreadPayloadHandler(ClientPlayNetworking.PlayPayloadHandler<T> handler) {
        this.handler = handler;
    }

    @Override
    public void receive(T payload, ClientPlayNetworking.Context context) {
        Minecraft client = context.client();
        if (client.isSameThread()) {
            handler.receive(payload, context);
        } else {
            client.execute(() -> handler.receive(payload, context));
        }
    }
}
