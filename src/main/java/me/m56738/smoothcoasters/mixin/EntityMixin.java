package me.m56738.smoothcoasters.mixin;

import me.m56738.smoothcoasters.GameRendererMixinInterface;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
@Environment(EnvType.CLIENT)
public class EntityMixin {
    @Inject(method = "setYaw", at = @At("RETURN"))
    private void setYaw(CallbackInfo info) {
        ((GameRendererMixinInterface) MinecraftClient.getInstance().gameRenderer)
                .smoothcoasters$updateRotation((Entity) (Object) this);
    }

    @Inject(method = "setPitch", at = @At("RETURN"))
    private void setPitch(CallbackInfo info) {
        ((GameRendererMixinInterface) MinecraftClient.getInstance().gameRenderer)
                .smoothcoasters$updateRotation((Entity) (Object) this);
    }

    @Inject(method = "changeLookDirection", at = @At("HEAD"))
    private void changeLookDirectionHead(double cursorDeltaX, double cursorDeltaY, CallbackInfo info) {
        ((GameRendererMixinInterface) MinecraftClient.getInstance().gameRenderer)
                .smoothcoasters$loadLocalRotation((Entity) (Object) this);
    }

    @Inject(method = "changeLookDirection", at = @At("RETURN"))
    private void changeLookDirectionTail(double cursorDeltaX, double cursorDeltaY, CallbackInfo info) {
        ((GameRendererMixinInterface) MinecraftClient.getInstance().gameRenderer)
                .smoothcoasters$applyLocalRotation((Entity) (Object) this);
    }
}
