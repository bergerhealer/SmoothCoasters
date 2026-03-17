package me.m56738.smoothcoasters.implementation;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.Map;

public interface Implementation {
    Implementation[] IMPLEMENTATIONS = new Implementation[]{
            new ImplV4(),
            new ImplV3(),
            new ImplV2(),
            new ImplV1(),
    };

    byte getVersion();

    void register(Map<Identifier, PacketHandler> handlers);

    @FunctionalInterface
    interface PacketHandler {
        void handle(PacketByteBuf buf);
    }
}
