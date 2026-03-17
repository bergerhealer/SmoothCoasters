package me.m56738.smoothcoasters.implementation;

import me.m56738.smoothcoasters.SmoothCoasters;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;

import java.util.Map;

public class ImplV1 implements Implementation {
    private static final Identifier ROTATION = new Identifier("smoothcoasters", "rot");

    protected boolean hasBulk = true;

    @Override
    public byte getVersion() {
        return 1;
    }

    @Override
    public void register(Map<Identifier, PacketHandler> handlers) {
        handlers.put(ROTATION, this::handleRotation);
    }

    private void handleRotation(PacketByteBuf buf) {
        final Quaternion rotation = new Quaternion(
                -buf.readFloat(), -buf.readFloat(),
                -buf.readFloat(), buf.readFloat()
        );
        final byte ticks = buf.readByte();
        SmoothCoasters.getInstance().setRotation(rotation, ticks);
    }
}
