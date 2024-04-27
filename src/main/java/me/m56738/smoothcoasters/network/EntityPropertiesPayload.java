package me.m56738.smoothcoasters.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record EntityPropertiesPayload(int entity, byte ticks) implements CustomPayload {
    public static final Id<EntityPropertiesPayload> ID = new Id<>(new Identifier("smoothcoasters", "eprop"));
    public static final PacketCodec<PacketByteBuf, EntityPropertiesPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, EntityPropertiesPayload::entity,
            PacketCodecs.BYTE, EntityPropertiesPayload::ticks,
            EntityPropertiesPayload::new);

    @Override
    public Id<EntityPropertiesPayload> getId() {
        return ID;
    }
}
