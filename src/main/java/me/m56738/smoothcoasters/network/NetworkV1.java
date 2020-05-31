package me.m56738.smoothcoasters.network;

import me.m56738.smoothcoasters.SmoothCoasters;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.Quaternion;

public class NetworkV1 implements NetworkImplementation {
    private static final Identifier ROTATION = new Identifier("smoothcoasters", "rot");

    @Override
    public byte getVersion() {
        return 1;
    }

    @Override
    public void register() {
        ClientSidePacketRegistry.INSTANCE.register(ROTATION, this::handleRotation);
    }

    @Override
    public void unregister() {
        ClientSidePacketRegistry.INSTANCE.unregister(ROTATION);
    }

    private void handleRotation(PacketContext context, PacketByteBuf buf) {
        final Quaternion rotation = new Quaternion(
                -buf.readFloat(), -buf.readFloat(),
                -buf.readFloat(), buf.readFloat()
        );
        final byte ticks = buf.readByte();
        context.getTaskQueue().execute(() -> SmoothCoasters.getInstance().setRotation(rotation, ticks));
    }
}
