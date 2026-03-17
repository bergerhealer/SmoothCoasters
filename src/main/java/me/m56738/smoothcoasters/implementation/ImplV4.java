package me.m56738.smoothcoasters.implementation;

import me.m56738.smoothcoasters.RotationMode;
import me.m56738.smoothcoasters.SmoothCoasters;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.Map;

public class ImplV4 extends ImplV3 {
    private static final Identifier ROTATION_LIMIT = new Identifier("smoothcoasters", "limit");

    public ImplV4() {
        hasBulk = false;
        hasRotationMode = false;
    }

    @Override
    public byte getVersion() {
        return 4;
    }

    @Override
    public void register(Map<Identifier, PacketHandler> handlers) {
        super.register(handlers);
        handlers.put(ROTATION_LIMIT, this::handleRotationLimit);
        SmoothCoasters.getInstance().setRotationMode(RotationMode.PLAYER);
    }

    private void handleRotationLimit(PacketByteBuf buf) {
        final float minYaw = buf.readFloat();
        final float maxYaw = buf.readFloat();
        final float minPitch = buf.readFloat();
        final float maxPitch = buf.readFloat();
        SmoothCoasters.getInstance().setRotationLimit(minYaw, maxYaw, minPitch, maxPitch);
    }
}
