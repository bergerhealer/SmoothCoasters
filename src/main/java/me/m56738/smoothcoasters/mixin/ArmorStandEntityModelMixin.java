package me.m56738.smoothcoasters.mixin;

import me.m56738.smoothcoasters.Animatable;
import net.minecraft.client.render.entity.model.ArmorStandEntityModel;
import net.minecraft.entity.decoration.ArmorStandEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorStandEntityModel.class)
public class ArmorStandEntityModelMixin {
    @Inject(method = "animateModel", at = @At("HEAD"))
    private void animateModel(ArmorStandEntity entity, float f, float g, float delta, CallbackInfo info) {
        ((Animatable) entity).scAnimate(delta);
    }
}
