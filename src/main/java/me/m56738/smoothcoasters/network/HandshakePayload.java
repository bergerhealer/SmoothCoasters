package me.m56738.smoothcoasters.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record HandshakePayload(byte[] versions) implements CustomPacketPayload {
    public static final Type<HandshakePayload> ID = new Type<>(Identifier.fromNamespaceAndPath("smoothcoasters", "hs"));
    public static final StreamCodec<FriendlyByteBuf, HandshakePayload> CODEC = ByteBufCodecs.BYTE_ARRAY.map(HandshakePayload::new, HandshakePayload::versions).cast();

    @Override
    public Type<HandshakePayload> type() {
        return ID;
    }
}
