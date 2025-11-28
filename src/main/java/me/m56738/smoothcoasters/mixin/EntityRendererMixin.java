package me.m56738.smoothcoasters.mixin;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    @SuppressWarnings("CancellableInjectionUsage")
    @Inject(method = "getBoundingBoxForCulling", at = @At("RETURN"), cancellable = true)
    public void expandBoundingBox(Entity entity, CallbackInfoReturnable<AABB> cir) {
    }
}
