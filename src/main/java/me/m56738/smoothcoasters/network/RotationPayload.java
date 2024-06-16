package me.m56738.smoothcoasters.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;

public record RotationPayload(Quaternionf rotation, byte ticks) implements CustomPayload {
    public static final Id<RotationPayload> ID = new Id<>(Identifier.of("smoothcoasters", "rot"));
    public static final PacketCodec<PacketByteBuf, RotationPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.QUATERNIONF, RotationPayload::rotation,
            PacketCodecs.BYTE, RotationPayload::ticks,
            RotationPayload::new);

    @Override
    public Id<RotationPayload> getId() {
        return ID;
    }
}
