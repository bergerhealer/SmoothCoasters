package me.m56738.smoothcoasters.common.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface CustomPayloadHandler<T extends CustomPacketPayload> {
    void handle(T payload, Context context);

    interface Context {
        void reply(CustomPacketPayload payload);
    }
}
