package me.m56738.smoothcoasters.mixin;

import me.m56738.smoothcoasters.SmoothCoasters;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onDisconnected", at = @At("HEAD"))
    private void onDisconnected(CallbackInfo info) {
        SmoothCoasters.getInstance().onDisconnected();
    }
}
