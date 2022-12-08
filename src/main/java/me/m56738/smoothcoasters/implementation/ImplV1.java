package me.m56738.smoothcoasters.implementation;

import me.m56738.smoothcoasters.SmoothCoasters;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.OffThreadException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Quaternionf;

import java.io.IOException;

public class ImplV1 implements Implementation {
    private static final Logger LOG = LogManager.getLogger();
    private static final Identifier ROTATION = new Identifier("smoothcoasters", "rot");
    private static final Identifier BULK = new Identifier("smoothcoasters", "bulk");

    protected boolean hasBulk = true;

    @Override
    public byte getVersion() {
        return 1;
    }

    @Override
    public void register() {
        ClientPlayNetworking.registerReceiver(ROTATION, this::handleRotation);
        if (hasBulk) ClientPlayNetworking.registerReceiver(BULK, this::handleBulk);
    }

    @Override
    public void unregister() {
        ClientPlayNetworking.unregisterReceiver(ROTATION);
        if (hasBulk) ClientPlayNetworking.unregisterReceiver(BULK);
    }

    private void handleRotation(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        final Quaternionf rotation = new Quaternionf(
                -buf.readFloat(), -buf.readFloat(),
                -buf.readFloat(), buf.readFloat()
        );
        final byte ticks = buf.readByte();
        client.execute(() -> SmoothCoasters.getInstance().setRotation(rotation, ticks));
    }

    @SuppressWarnings("unchecked")
    private void handleBulk(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        int count = buf.readVarInt();
        final Packet<?>[] packets = new Packet[count];

        try {
            for (int i = 0; i < count; i++) {
                int length = buf.readVarInt();
                PacketByteBuf slice = new PacketByteBuf(buf.readSlice(length));

                int id = slice.readVarInt();
                Packet<?> packet = NetworkState.PLAY.getPacketHandler(NetworkSide.CLIENTBOUND, id, slice);

                if (slice.isReadable()) {
                    throw new IOException("Packet not read completely: " + id + " (" + slice.readableBytes() + " extra bytes)");
                }

                packets[i] = packet;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Can't handle all at once in the main thread - some only work async
        // As a result, packets might (rarely) be handled in different ticks
        for (Packet<?> packet : packets) {
            try {
                ((Packet<ClientPlayPacketListener>) packet).apply(handler);
            } catch (OffThreadException ignored) {
            } catch (Throwable e) {
                LOG.fatal("Handling bulk packet failed", e);
            }
        }
    }
}
