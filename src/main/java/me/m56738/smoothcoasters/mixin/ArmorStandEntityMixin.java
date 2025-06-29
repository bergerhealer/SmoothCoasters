package me.m56738.smoothcoasters.mixin;

import me.m56738.smoothcoasters.AnimatedPose;
import me.m56738.smoothcoasters.ArmorStandMixinInterface;
import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.EulerAngle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorStandEntity.class)
public abstract class ArmorStandEntityMixin extends LivingEntityMixin implements ArmorStandMixinInterface {
    @Unique
    private final AnimatedPose scHead = new AnimatedPose(ArmorStandEntity.DEFAULT_HEAD_ROTATION);
    @Unique
    private final AnimatedPose scBody = new AnimatedPose(ArmorStandEntity.DEFAULT_BODY_ROTATION);
    @Unique
    private final AnimatedPose scLeftArm = new AnimatedPose(ArmorStandEntity.DEFAULT_LEFT_ARM_ROTATION);
    @Unique
    private final AnimatedPose scRightArm = new AnimatedPose(ArmorStandEntity.DEFAULT_RIGHT_ARM_ROTATION);
    @Unique
    private final AnimatedPose scLeftLeg = new AnimatedPose(ArmorStandEntity.DEFAULT_LEFT_LEG_ROTATION);
    @Unique
    private final AnimatedPose scRightLeg = new AnimatedPose(ArmorStandEntity.DEFAULT_RIGHT_LEG_ROTATION);
    @Unique
    private int scLerpTicks = 3;

    @Shadow
    public abstract EulerAngle getHeadRotation();

    @Shadow
    public abstract EulerAngle getBodyRotation();

    @Shadow
    public abstract EulerAngle getLeftArmRotation();

    @Shadow
    public abstract EulerAngle getRightArmRotation();

    @Shadow
    public abstract EulerAngle getLeftLegRotation();

    @Shadow
    public abstract EulerAngle getRightLegRotation();

    @Override
    public void smoothcoasters$setTicks(int ticks) {
        scLerpTicks = ticks;
    }

    @Override
    public void smoothcoasters$animate(ArmorStandEntityRenderState renderState, float delta) {
        renderState.headRotation = scHead.calculateEuler(delta);
        renderState.bodyRotation = scBody.calculateEuler(delta);
        renderState.leftArmRotation = scLeftArm.calculateEuler(delta);
        renderState.rightArmRotation = scRightArm.calculateEuler(delta);
        renderState.leftLegRotation = scLeftLeg.calculateEuler(delta);
        renderState.rightLegRotation = scRightLeg.calculateEuler(delta);
    }

    @Inject(method = "onTrackedDataSet", at = @At("HEAD"))
    private void onTrackedDataSet(TrackedData<?> data, CallbackInfo ci) {
        if (data.equals(ArmorStandEntity.TRACKER_HEAD_ROTATION)) {
            scHead.set(getHeadRotation(), scLerpTicks);
        }
        if (data.equals(ArmorStandEntity.TRACKER_BODY_ROTATION)) {
            scBody.set(getBodyRotation(), scLerpTicks);
        }
        if (data.equals(ArmorStandEntity.TRACKER_LEFT_ARM_ROTATION)) {
            scLeftArm.set(getLeftArmRotation(), scLerpTicks);
        }
        if (data.equals(ArmorStandEntity.TRACKER_RIGHT_ARM_ROTATION)) {
            scRightArm.set(getRightArmRotation(), scLerpTicks);
        }
        if (data.equals(ArmorStandEntity.TRACKER_LEFT_LEG_ROTATION)) {
            scLeftLeg.set(getLeftLegRotation(), scLerpTicks);
        }
        if (data.equals(ArmorStandEntity.TRACKER_RIGHT_LEG_ROTATION)) {
            scRightLeg.set(getRightLegRotation(), scLerpTicks);
        }
    }

    @Override
    public void baseTickEnd(CallbackInfo info) {
        scHead.tick();
        scBody.tick();
        scLeftArm.tick();
        scRightArm.tick();
        scLeftLeg.tick();
        scRightLeg.tick();
    }
}
