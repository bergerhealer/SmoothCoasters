package me.m56738.smoothcoasters.implementation;

import me.m56738.smoothcoasters.SmoothCoasters;
import me.m56738.smoothcoasters.network.EntityPropertiesPayload;
import me.m56738.smoothcoasters.network.EntityRotationPayload;
import me.m56738.smoothcoasters.network.MainThreadPayloadHandler;
import me.m56738.smoothcoasters.network.RotationLimitPayload;
import me.m56738.smoothcoasters.network.RotationPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.joml.Quaternionf;

public class ImplV5 implements Implementation {
    @Override
    public byte getVersion() {
        return 5;
    }

    @Override
    public void register() {
        ClientPlayNetworking.registerReceiver(RotationPayload.ID, new MainThreadPayloadHandler<>(this::handleRotation));
        ClientPlayNetworking.registerReceiver(EntityRotationPayload.ID, new MainThreadPayloadHandler<>(this::handleEntityRotation));
        ClientPlayNetworking.registerReceiver(EntityPropertiesPayload.ID, new MainThreadPayloadHandler<>(this::handleEntityProperties));
        ClientPlayNetworking.registerReceiver(RotationLimitPayload.ID, new MainThreadPayloadHandler<>(this::handleRotationLimit));
    }

    @Override
    public void unregister() {
        ClientPlayNetworking.unregisterReceiver(RotationPayload.ID.id());
        ClientPlayNetworking.unregisterReceiver(EntityRotationPayload.ID.id());
        ClientPlayNetworking.unregisterReceiver(EntityPropertiesPayload.ID.id());
        ClientPlayNetworking.unregisterReceiver(RotationLimitPayload.ID.id());
    }

    private void handleRotation(RotationPayload payload, ClientPlayNetworking.Context context) {
        SmoothCoasters.getInstance().setRotation(payload.rotation().conjugate(new Quaternionf()), payload.ticks());
    }

    private void handleEntityRotation(EntityRotationPayload payload, ClientPlayNetworking.Context context) {
        SmoothCoasters.getInstance().setEntityRotation(payload.entity(), payload.rotation(), payload.ticks());
    }

    private void handleEntityProperties(EntityPropertiesPayload payload, ClientPlayNetworking.Context context) {
        if (payload.ticks() != 0) {
            SmoothCoasters.getInstance().setEntityTicks(payload.entity(), payload.ticks());
        }
    }

    private void handleRotationLimit(RotationLimitPayload payload, ClientPlayNetworking.Context context) {
        SmoothCoasters.getInstance().setRotationLimit(payload.minYaw(), payload.maxYaw(), payload.minPitch(), payload.maxPitch());
    }
}
