package me.m56738.smoothcoasters.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record HandshakePayload(byte[] data) implements CustomPacketPayload {
    public static final Type<HandshakePayload> ID = new Type<>(Identifier.fromNamespaceAndPath("smoothcoasters", "hs"));
    public static final StreamCodec<FriendlyByteBuf, HandshakePayload> CODEC = new StreamCodec<>() {
        @Override
        public HandshakePayload decode(FriendlyByteBuf input) {
            byte[] data = new byte[input.readableBytes()];
            input.readBytes(data);
            return new HandshakePayload(data);
        }

        @Override
        public void encode(FriendlyByteBuf output, HandshakePayload payload) {
            output.writeBytes(payload.data);
        }
    };

    @Override
    public Type<HandshakePayload> type() {
        return ID;
    }
}
