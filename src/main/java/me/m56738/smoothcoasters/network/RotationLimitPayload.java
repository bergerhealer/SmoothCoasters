package me.m56738.smoothcoasters.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record RotationLimitPayload(
        float minYaw, float maxYaw, float minPitch, float maxPitch
) implements CustomPacketPayload {
    public static final Type<RotationLimitPayload> ID = new Type<>(Identifier.fromNamespaceAndPath("smoothcoasters", "limit"));
    public static final StreamCodec<FriendlyByteBuf, RotationLimitPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, RotationLimitPayload::minYaw,
            ByteBufCodecs.FLOAT, RotationLimitPayload::maxYaw,
            ByteBufCodecs.FLOAT, RotationLimitPayload::minPitch,
            ByteBufCodecs.FLOAT, RotationLimitPayload::maxPitch,
            RotationLimitPayload::new);

    @Override
    public Type<RotationLimitPayload> type() {
        return ID;
    }
}
