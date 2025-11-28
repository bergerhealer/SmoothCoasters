package me.m56738.smoothcoasters.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record HandshakeResponsePayload(byte protocolVersion, String modVersion) implements CustomPacketPayload {
    public static final Type<HandshakeResponsePayload> ID = new Type<>(ResourceLocation.fromNamespaceAndPath("smoothcoasters", "hs"));
    public static final StreamCodec<FriendlyByteBuf, HandshakeResponsePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE, HandshakeResponsePayload::protocolVersion,
            ByteBufCodecs.STRING_UTF8, HandshakeResponsePayload::modVersion,
            HandshakeResponsePayload::new);

    @Override
    public Type<HandshakeResponsePayload> type() {
        return ID;
    }
}
