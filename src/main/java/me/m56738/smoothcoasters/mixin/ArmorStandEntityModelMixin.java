package me.m56738.smoothcoasters.mixin;

import me.m56738.smoothcoasters.ArmorStandMixinInterface;
import net.minecraft.client.render.entity.model.ArmorStandEntityModel;
import net.minecraft.entity.decoration.ArmorStandEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorStandEntityModel.class)
public class ArmorStandEntityModelMixin {
    @Inject(method = "animateModel(Lnet/minecraft/entity/decoration/ArmorStandEntity;FFF)V", at = @At("HEAD"))
    private void animateModel(ArmorStandEntity entity, float f, float g, float delta, CallbackInfo info) {
        ((ArmorStandMixinInterface) entity).smoothcoasters$animate(delta);
    }
}
