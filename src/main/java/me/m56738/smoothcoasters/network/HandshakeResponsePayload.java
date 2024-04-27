package me.m56738.smoothcoasters.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record HandshakeResponsePayload(byte protocolVersion, String modVersion) implements CustomPayload {
    public static final Id<HandshakeResponsePayload> ID = new Id<>(new Identifier("smoothcoasters", "hs"));
    public static final PacketCodec<PacketByteBuf, HandshakeResponsePayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BYTE, HandshakeResponsePayload::protocolVersion,
            PacketCodecs.STRING, HandshakeResponsePayload::modVersion,
            HandshakeResponsePayload::new);

    @Override
    public Id<HandshakeResponsePayload> getId() {
        return ID;
    }
}
