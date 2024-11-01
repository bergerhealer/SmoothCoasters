package me.m56738.smoothcoasters.mixin;

import me.m56738.smoothcoasters.ArmorStandMixinInterface;
import net.minecraft.client.render.entity.ArmorStandEntityRenderer;
import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorStandEntityRenderer.class)
public abstract class ArmorStandEntityRendererMixin extends EntityRendererMixin {
    @Inject(method = "updateRenderState(Lnet/minecraft/entity/decoration/ArmorStandEntity;Lnet/minecraft/client/render/entity/state/ArmorStandEntityRenderState;F)V", at = @At("HEAD"))
    private void animateModel(ArmorStandEntity entity, ArmorStandEntityRenderState state, float delta, CallbackInfo ci) {
        ((ArmorStandMixinInterface) entity).smoothcoasters$animate(delta);
    }

    @Override
    public void expandBoundingBox(Entity entity, CallbackInfoReturnable<Box> cir) {
        cir.setReturnValue(cir.getReturnValue().expand(3));
    }
}
