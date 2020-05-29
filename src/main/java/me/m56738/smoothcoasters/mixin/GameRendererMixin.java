package me.m56738.smoothcoasters.mixin;

import me.m56738.smoothcoasters.SmoothCoasters;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "reset", at = @At("HEAD"))
    private void reset(CallbackInfo info) {
        SmoothCoasters.getInstance().resetRotation();
    }
}
