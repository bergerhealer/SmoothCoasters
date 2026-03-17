package me.m56738.smoothcoasters.implementation;

import me.m56738.smoothcoasters.SmoothCoasters;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;

import java.util.Map;

public class ImplV2 extends ImplV1 {
    private static final Identifier ENTITY_ROTATION = new Identifier("smoothcoasters", "erot");

    @Override
    public byte getVersion() {
        return 2;
    }

    @Override
    public void register(Map<Identifier, PacketHandler> handlers) {
        super.register(handlers);
        handlers.put(ENTITY_ROTATION, this::handleEntityRotation);
    }

    private void handleEntityRotation(PacketByteBuf buf) {
        final int entity = buf.readInt();
        final Quaternion rotation = new Quaternion(
                buf.readFloat(), buf.readFloat(),
                buf.readFloat(), buf.readFloat()
        );
        final byte ticks = buf.readByte();
        SmoothCoasters.getInstance().setEntityRotation(entity, rotation, ticks);
    }
}
