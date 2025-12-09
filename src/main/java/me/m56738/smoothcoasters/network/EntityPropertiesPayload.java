package me.m56738.smoothcoasters.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record EntityPropertiesPayload(int entity, byte ticks) implements CustomPacketPayload {
    public static final Type<EntityPropertiesPayload> ID = new Type<>(Identifier.fromNamespaceAndPath("smoothcoasters", "eprop"));
    public static final StreamCodec<FriendlyByteBuf, EntityPropertiesPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, EntityPropertiesPayload::entity,
            ByteBufCodecs.BYTE, EntityPropertiesPayload::ticks,
            EntityPropertiesPayload::new);

    @Override
    public Type<EntityPropertiesPayload> type() {
        return ID;
    }
}
