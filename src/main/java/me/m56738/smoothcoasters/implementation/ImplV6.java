package me.m56738.smoothcoasters.implementation;

import me.m56738.smoothcoasters.SmoothCoasters;
import me.m56738.smoothcoasters.network.MainThreadPayloadHandler;
import me.m56738.smoothcoasters.network.RotationLimitPayload;
import me.m56738.smoothcoasters.network.RotationPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ImplV6 implements Implementation {
    @Override
    public byte getVersion() {
        return 6;
    }

    @Override
    public void register() {
        ClientPlayNetworking.registerReceiver(RotationPayload.ID, new MainThreadPayloadHandler<>(this::handleRotation));
        ClientPlayNetworking.registerReceiver(RotationLimitPayload.ID, new MainThreadPayloadHandler<>(this::handleRotationLimit));
    }

    @Override
    public void unregister() {
        ClientPlayNetworking.unregisterReceiver(RotationPayload.ID.id());
        ClientPlayNetworking.unregisterReceiver(RotationLimitPayload.ID.id());
    }

    private void handleRotation(RotationPayload payload, ClientPlayNetworking.Context context) {
        SmoothCoasters.getInstance().setRotation(payload.rotation(), payload.ticks());
    }

    private void handleRotationLimit(RotationLimitPayload payload, ClientPlayNetworking.Context context) {
        SmoothCoasters.getInstance().setRotationLimit(payload.minYaw(), payload.maxYaw(), payload.minPitch(), payload.maxPitch());
    }
}
