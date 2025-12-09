package me.m56738.smoothcoasters.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.joml.Quaternionfc;

public record RotationPayload(Quaternionfc rotation, byte ticks) implements CustomPacketPayload {
    public static final Type<RotationPayload> ID = new Type<>(Identifier.fromNamespaceAndPath("smoothcoasters", "rot"));
    public static final StreamCodec<FriendlyByteBuf, RotationPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.QUATERNIONF, RotationPayload::rotation,
            ByteBufCodecs.BYTE, RotationPayload::ticks,
            RotationPayload::new);

    @Override
    public Type<RotationPayload> type() {
        return ID;
    }
}
