package me.m56738.smoothcoasters.mixin;

import me.m56738.smoothcoasters.GameRendererMixinInterface;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
@Environment(EnvType.CLIENT)
public class EntityMixin {
    @Inject(method = "setYRot", at = @At("RETURN"))
    private void setYRot(CallbackInfo info) {
        ((GameRendererMixinInterface) Minecraft.getInstance().gameRenderer)
                .smoothcoasters$updateRotation((Entity) (Object) this);
    }

    @Inject(method = "setXRot", at = @At("RETURN"))
    private void setXRot(CallbackInfo info) {
        ((GameRendererMixinInterface) Minecraft.getInstance().gameRenderer)
                .smoothcoasters$updateRotation((Entity) (Object) this);
    }

    @Inject(method = "turn", at = @At("HEAD"))
    private void turnHead(double cursorDeltaX, double cursorDeltaY, CallbackInfo info) {
        ((GameRendererMixinInterface) Minecraft.getInstance().gameRenderer)
                .smoothcoasters$loadLocalRotation((Entity) (Object) this);
    }

    @Inject(method = "turn", at = @At("RETURN"))
    private void turnTail(double cursorDeltaX, double cursorDeltaY, CallbackInfo info) {
        ((GameRendererMixinInterface) Minecraft.getInstance().gameRenderer)
                .smoothcoasters$applyLocalRotation((Entity) (Object) this);
    }
}
