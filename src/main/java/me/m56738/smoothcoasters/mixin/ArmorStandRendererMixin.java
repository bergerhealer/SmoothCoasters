package me.m56738.smoothcoasters.mixin;

import me.m56738.smoothcoasters.ArmorStandMixinInterface;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorStandRenderer.class)
public abstract class ArmorStandRendererMixin extends EntityRendererMixin {
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/decoration/ArmorStand;Lnet/minecraft/client/renderer/entity/state/ArmorStandRenderState;F)V", at = @At("RETURN"))
    private void extractRenderState(ArmorStand entity, ArmorStandRenderState state, float delta, CallbackInfo ci) {
        ((ArmorStandMixinInterface) entity).smoothcoasters$animate(state, delta);
    }

    @Override
    public void expandBoundingBox(Entity entity, CallbackInfoReturnable<AABB> cir) {
        cir.setReturnValue(cir.getReturnValue().inflate(3));
    }
}
