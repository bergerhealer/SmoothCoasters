package me.m56738.smoothcoasters.fabric.network;

import me.m56738.smoothcoasters.common.network.CustomPayloadHandler;
import me.m56738.smoothcoasters.common.network.CustomPayloadRegistrar;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.Nullable;

public class FabricCustomPayloadRegistrar implements CustomPayloadRegistrar {
    @Override
    public <T extends CustomPacketPayload> void register(CustomPacketPayload.Type<T> type, StreamCodec<? super FriendlyByteBuf, T> codec, @Nullable PacketFlow flow, CustomPayloadHandler<T> handler) {
        if (flow == null || flow == PacketFlow.SERVERBOUND) {
            PayloadTypeRegistry.serverboundPlay().register(type, codec);
        }
        if (flow == null || flow == PacketFlow.CLIENTBOUND) {
            PayloadTypeRegistry.clientboundPlay().register(type, codec);
            ClientPlayNetworking.registerGlobalReceiver(type, new FabricCustomPayloadHandlerWrapper<>(handler));
        }
    }
}
