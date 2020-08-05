package me.m56738.smoothcoasters.implementation;

import me.m56738.smoothcoasters.SmoothCoasters;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;

public class ImplV2 extends ImplV1 {
    private static final Identifier ENTITY_ROTATION = new Identifier("smoothcoasters", "erot");

    @Override
    public byte getVersion() {
        return 2;
    }

    @Override
    public void register() {
        super.register();
        ClientSidePacketRegistry.INSTANCE.register(ENTITY_ROTATION, this::handleEntityRotation);
    }

    @Override
    public void unregister() {
        super.unregister();
        ClientSidePacketRegistry.INSTANCE.unregister(ENTITY_ROTATION);
    }

    protected void handleEntityRotation(PacketContext context, PacketByteBuf buf) {
        final int entity = buf.readInt();
        final Quaternion rotation = new Quaternion(
                buf.readFloat(), buf.readFloat(),
                buf.readFloat(), buf.readFloat()
        );
        final byte ticks = buf.readByte();
        context.getTaskQueue().execute(() -> SmoothCoasters.getInstance().setEntityRotation(entity, rotation, ticks));
    }
}
