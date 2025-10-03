package me.m56738.smoothcoasters.implementation;

import me.m56738.smoothcoasters.network.EntityPropertiesPayload;
import me.m56738.smoothcoasters.network.EntityRotationPayload;
import me.m56738.smoothcoasters.network.MainThreadPayloadHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ImplV5 extends ImplV6 {
    @Override
    public byte getVersion() {
        return 5;
    }

    @Override
    public void register() {
        super.register();
        ClientPlayNetworking.registerReceiver(EntityRotationPayload.ID, new MainThreadPayloadHandler<>(this::handleEntityRotation));
        ClientPlayNetworking.registerReceiver(EntityPropertiesPayload.ID, new MainThreadPayloadHandler<>(this::handleEntityProperties));
    }

    @Override
    public void unregister() {
        super.unregister();
        ClientPlayNetworking.unregisterReceiver(EntityRotationPayload.ID.id());
        ClientPlayNetworking.unregisterReceiver(EntityPropertiesPayload.ID.id());
    }

    private void handleEntityRotation(EntityRotationPayload payload, ClientPlayNetworking.Context context) {
    }

    private void handleEntityProperties(EntityPropertiesPayload payload, ClientPlayNetworking.Context context) {
    }
}
