package me.m56738.smoothcoasters.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;

public record EntityRotationPayload(int entity, Quaternionf rotation, byte ticks) implements CustomPayload {
    public static final Id<EntityRotationPayload> ID = new Id<>(new Identifier("smoothcoasters", "erot"));
    public static final PacketCodec<PacketByteBuf, EntityRotationPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, EntityRotationPayload::entity,
            PacketCodecs.QUATERNIONF, EntityRotationPayload::rotation,
            PacketCodecs.BYTE, EntityRotationPayload::ticks,
            EntityRotationPayload::new);

    @Override
    public Id<EntityRotationPayload> getId() {
        return ID;
    }
}
