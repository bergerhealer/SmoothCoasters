package me.m56738.smoothcoasters.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.CustomPayload;

public class MainThreadPayloadHandler<T extends CustomPayload> implements ClientPlayNetworking.PlayPayloadHandler<T> {
    private final ClientPlayNetworking.PlayPayloadHandler<T> handler;

    public MainThreadPayloadHandler(ClientPlayNetworking.PlayPayloadHandler<T> handler) {
        this.handler = handler;
    }

    @Override
    public void receive(T payload, ClientPlayNetworking.Context context) {
        MinecraftClient client = context.client();
        if (client.isOnThread()) {
            handler.receive(payload, context);
        } else {
            client.execute(() -> handler.receive(payload, context));
        }
    }
}
