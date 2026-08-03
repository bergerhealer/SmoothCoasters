package me.m56738.smoothcoasters.fabric.network;

import me.m56738.smoothcoasters.common.network.CustomPayloadHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class FabricCustomPayloadHandlerWrapper<T extends CustomPacketPayload> implements ClientPlayNetworking.PlayPayloadHandler<T> {
    private final CustomPayloadHandler<T> handler;

    public FabricCustomPayloadHandlerWrapper(CustomPayloadHandler<T> handler) {
        this.handler = handler;
    }

    @Override
    public void receive(T payload, ClientPlayNetworking.Context context) {
        if (context.client().isSameThread()) {
            handle(payload, context);
        } else {
            context.client().execute(() -> handle(payload, context));
        }
    }

    private void handle(T payload, ClientPlayNetworking.Context context) {
        handler.handle(payload, new FabricCustomPayloadHandlerWrapperContext(context));
    }

    public static class FabricCustomPayloadHandlerWrapperContext implements CustomPayloadHandler.Context {
        private final ClientPlayNetworking.Context context;

        public FabricCustomPayloadHandlerWrapperContext(ClientPlayNetworking.Context context) {
            this.context = context;
        }

        @Override
        public void reply(CustomPacketPayload payload) {
            context.responseSender().sendPacket(payload);
        }
    }
}
