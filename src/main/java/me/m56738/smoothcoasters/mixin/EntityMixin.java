package me.m56738.smoothcoasters.mixin;

import me.m56738.smoothcoasters.AnimatedPose;
import me.m56738.smoothcoasters.DoubleQuaternion;
import me.m56738.smoothcoasters.Rotatable;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Quaternion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin implements Rotatable {
    private final AnimatedPose scPose = new AnimatedPose();
    private final DoubleQuaternion scDoubleQuaternion = new DoubleQuaternion();
    private final Quaternion scQuaternion = new Quaternion(0, 0, 0, 1);

    @Override
    public Quaternion scCalculate(float tickDelta) {
        scPose.calculate(scDoubleQuaternion, tickDelta);
        scDoubleQuaternion.toQuaternion(scQuaternion);
        return scQuaternion;
    }

    @Override
    public void scSetRotation(Quaternion rotation, int ticks) {
        scPose.set(rotation, ticks);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo info) {
        scPose.tick();
    }
}
