package me.m56738.smoothcoasters.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record HandshakePayload(byte[] versions) implements CustomPayload {
    public static final Id<HandshakePayload> ID = new Id<>(new Identifier("smoothcoasters", "hs"));
    public static final PacketCodec<PacketByteBuf, HandshakePayload> CODEC = PacketCodecs.BYTE_ARRAY.xmap(HandshakePayload::new, HandshakePayload::versions).cast();

    @Override
    public Id<HandshakePayload> getId() {
        return ID;
    }
}
