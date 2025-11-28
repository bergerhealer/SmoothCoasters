package me.m56738.smoothcoasters.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;

public record EntityRotationPayload(int entity, Quaternionf rotation, byte ticks) implements CustomPacketPayload {
    public static final Type<EntityRotationPayload> ID = new Type<>(ResourceLocation.fromNamespaceAndPath("smoothcoasters", "erot"));
    public static final StreamCodec<FriendlyByteBuf, EntityRotationPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, EntityRotationPayload::entity,
            ByteBufCodecs.QUATERNIONF, EntityRotationPayload::rotation,
            ByteBufCodecs.BYTE, EntityRotationPayload::ticks,
            EntityRotationPayload::new);

    @Override
    public Type<EntityRotationPayload> type() {
        return ID;
    }
}
