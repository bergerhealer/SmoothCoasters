package me.m56738.smoothcoasters.implementation;

import me.m56738.smoothcoasters.RotationMode;
import me.m56738.smoothcoasters.SmoothCoasters;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.Map;

public class ImplV3 extends ImplV2 {
    private static final Identifier ENTITY_PROPERTIES = new Identifier("smoothcoasters", "eprop");
    private static final Identifier ROTATION_MODE = new Identifier("smoothcoasters", "rmode");

    protected boolean hasRotationMode = true;

    @Override
    public byte getVersion() {
        return 3;
    }

    @Override
    public void register(Map<Identifier, PacketHandler> handlers) {
        super.register(handlers);
        handlers.put(ENTITY_PROPERTIES, this::handleEntityProperties);
        if (hasRotationMode) handlers.put(ROTATION_MODE, this::handleRotationMode);
    }

    private void handleEntityProperties(PacketByteBuf buf) {
        final int entity = buf.readInt();
        final byte ticks = buf.readByte();
        if (ticks != 0) {
            SmoothCoasters.getInstance().setEntityTicks(entity, ticks);
        }
    }

    private void handleRotationMode(PacketByteBuf buf) {
        final RotationMode mode = RotationMode.values()[buf.readInt()];
        SmoothCoasters.getInstance().setRotationMode(mode);
    }
}
