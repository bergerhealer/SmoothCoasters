package me.m56738.smoothcoasters.network;

import me.m56738.smoothcoasters.SmoothCoasters;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class NetworkV1 implements NetworkImplementation {
    private static final Logger LOG = LogManager.getLogger();
    private static final Identifier ROTATION = new Identifier("smoothcoasters", "rot");
    private static final Identifier BULK = new Identifier("smoothcoasters", "bulk");

    @Override
    public byte getVersion() {
        return 1;
    }

    @Override
    public void register() {
        ClientSidePacketRegistry.INSTANCE.register(ROTATION, this::handleRotation);
        ClientSidePacketRegistry.INSTANCE.register(BULK, this::handleBulk);
    }

    @Override
    public void unregister() {
        ClientSidePacketRegistry.INSTANCE.unregister(ROTATION);
        ClientSidePacketRegistry.INSTANCE.unregister(BULK);
    }

    private void handleRotation(PacketContext context, PacketByteBuf buf) {
        final Quaternion rotation = new Quaternion(
                -buf.readFloat(), -buf.readFloat(),
                -buf.readFloat(), buf.readFloat()
        );
        final byte ticks = buf.readByte();
        context.getTaskQueue().execute(() -> SmoothCoasters.getInstance().setRotation(rotation, ticks));
    }

    @SuppressWarnings("unchecked")
    private void handleBulk(PacketContext context, PacketByteBuf buf) {
        int count = buf.readVarInt();
        final Packet<?>[] packets = new Packet[count];

        try {
            for (int i = 0; i < count; i++) {
                int length = buf.readVarInt();
                PacketByteBuf slice = new PacketByteBuf(buf.readSlice(length));

                int id = slice.readVarInt();
                Packet<?> packet = NetworkState.PLAY.getPacketHandler(NetworkSide.CLIENTBOUND, id);

                if (packet == null) {
                    throw new IOException("Unknown packet ID: " + id);
                }

                packet.read(slice);

                if (slice.isReadable()) {
                    throw new IOException("Packet not read completely: " + id + " (" + slice.readableBytes() + " extra bytes)");
                }

                packets[i] = packet;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final ClientPlayNetworkHandler handler = (ClientPlayNetworkHandler) context;

        context.getTaskQueue().execute(() -> {
            // Handle all packets in the same tick
            for (Packet<?> packet : packets) {
                try {
                    ((Packet<ClientPlayPacketListener>) packet).apply(handler);
                } catch (Throwable e) {
                    LOG.fatal("Handling bulk packet failed", e);
                }
            }
        });
    }
}
