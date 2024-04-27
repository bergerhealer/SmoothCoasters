package me.m56738.smoothcoasters.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record RotationLimitPayload(
        float minYaw, float maxYaw, float minPitch, float maxPitch
) implements CustomPayload {
    public static final Id<RotationLimitPayload> ID = new Id<>(new Identifier("smoothcoasters", "limit"));
    public static final PacketCodec<PacketByteBuf, RotationLimitPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.FLOAT, RotationLimitPayload::minYaw,
            PacketCodecs.FLOAT, RotationLimitPayload::maxYaw,
            PacketCodecs.FLOAT, RotationLimitPayload::minPitch,
            PacketCodecs.FLOAT, RotationLimitPayload::maxPitch,
            RotationLimitPayload::new);

    @Override
    public Id<RotationLimitPayload> getId() {
        return ID;
    }
}
