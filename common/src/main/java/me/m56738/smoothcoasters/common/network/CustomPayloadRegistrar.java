package me.m56738.smoothcoasters.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.Nullable;

public interface CustomPayloadRegistrar {
    <T extends CustomPacketPayload> void register(CustomPacketPayload.Type<T> type, StreamCodec<? super FriendlyByteBuf, T> codec, @Nullable PacketFlow flow, CustomPayloadHandler<T> handler);
}
