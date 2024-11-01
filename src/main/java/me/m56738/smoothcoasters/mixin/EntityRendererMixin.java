package me.m56738.smoothcoasters.mixin;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    @SuppressWarnings("CancellableInjectionUsage")
    @Inject(method = "getBoundingBox", at = @At("RETURN"), cancellable = true)
    public void expandBoundingBox(Entity entity, CallbackInfoReturnable<Box> cir) {
    }
}
