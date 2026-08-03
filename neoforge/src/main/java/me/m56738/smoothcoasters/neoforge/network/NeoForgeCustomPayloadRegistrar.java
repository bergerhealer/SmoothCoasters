package me.m56738.smoothcoasters.neoforge.network;

import me.m56738.smoothcoasters.common.network.CustomPayloadHandler;
import me.m56738.smoothcoasters.common.network.CustomPayloadRegistrar;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jspecify.annotations.Nullable;

public class NeoForgeCustomPayloadRegistrar implements CustomPayloadRegistrar {
    private final PayloadRegistrar registrar;

    public NeoForgeCustomPayloadRegistrar(PayloadRegistrar registrar) {
        this.registrar = registrar;
    }

    @Override
    public <T extends CustomPacketPayload> void register(CustomPacketPayload.Type<T> type, StreamCodec<? super FriendlyByteBuf, T> codec, @Nullable PacketFlow flow, CustomPayloadHandler<T> handler) {
        NeoForgeCustomPayloadHandlerWrapper<T> wrapper = new NeoForgeCustomPayloadHandlerWrapper<>(handler);
        if (flow == PacketFlow.SERVERBOUND) {
            registrar.playToServer(type, codec, wrapper);
        } else if (flow == PacketFlow.CLIENTBOUND) {
            registrar.playToClient(type, codec, wrapper);
        } else {
            registrar.playBidirectional(type, codec, wrapper, wrapper);
        }
    }
}
