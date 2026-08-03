package me.m56738.smoothcoasters.neoforge.network;

import me.m56738.smoothcoasters.common.network.CustomPayloadHandler;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public class NeoForgeCustomPayloadHandlerWrapper<T extends CustomPacketPayload> implements IPayloadHandler<T> {
    private final CustomPayloadHandler<T> handler;

    public NeoForgeCustomPayloadHandlerWrapper(CustomPayloadHandler<T> handler) {
        this.handler = handler;
    }

    @Override
    public void handle(T payload, IPayloadContext context) {
        handler.handle(payload, new NeoForgeCustomPayloadHandlerContext(context));
    }

    public static class NeoForgeCustomPayloadHandlerContext implements CustomPayloadHandler.Context {
        private final IPayloadContext context;

        public NeoForgeCustomPayloadHandlerContext(IPayloadContext context) {
            this.context = context;
        }

        @Override
        public void reply(CustomPacketPayload payload) {
            context.reply(payload);
        }
    }
}
