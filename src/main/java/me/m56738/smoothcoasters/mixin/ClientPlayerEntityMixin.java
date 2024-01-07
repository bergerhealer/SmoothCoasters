package me.m56738.smoothcoasters.mixin;

import me.m56738.smoothcoasters.SmoothCoasters;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(method = "init", at = @At("HEAD"))
    private void init(CallbackInfo info) {
        SmoothCoasters.getInstance().reset();
    }
}
