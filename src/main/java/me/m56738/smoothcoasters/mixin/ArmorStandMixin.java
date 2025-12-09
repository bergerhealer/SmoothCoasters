package me.m56738.smoothcoasters.mixin;

import me.m56738.smoothcoasters.AnimatedPose;
import me.m56738.smoothcoasters.ArmorStandMixinInterface;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import net.minecraft.core.Rotations;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorStand.class)
public abstract class ArmorStandMixin extends LivingEntityMixin implements ArmorStandMixinInterface {
    @Unique
    private final AnimatedPose scHead = new AnimatedPose(ArmorStand.DEFAULT_HEAD_POSE);
    @Unique
    private final AnimatedPose scBody = new AnimatedPose(ArmorStand.DEFAULT_BODY_POSE);
    @Unique
    private final AnimatedPose scLeftArm = new AnimatedPose(ArmorStand.DEFAULT_LEFT_ARM_POSE);
    @Unique
    private final AnimatedPose scRightArm = new AnimatedPose(ArmorStand.DEFAULT_RIGHT_ARM_POSE);
    @Unique
    private final AnimatedPose scLeftLeg = new AnimatedPose(ArmorStand.DEFAULT_LEFT_LEG_POSE);
    @Unique
    private final AnimatedPose scRightLeg = new AnimatedPose(ArmorStand.DEFAULT_RIGHT_LEG_POSE);

    @Shadow
    public abstract Rotations getHeadPose();

    @Shadow
    public abstract Rotations getBodyPose();

    @Shadow
    public abstract Rotations getLeftArmPose();

    @Shadow
    public abstract Rotations getRightArmPose();

    @Shadow
    public abstract Rotations getLeftLegPose();

    @Shadow
    public abstract Rotations getRightLegPose();

    @Override
    public void smoothcoasters$animate(ArmorStandRenderState renderState, float delta) {
        renderState.headPose = scHead.calculateEuler(delta);
        renderState.bodyPose = scBody.calculateEuler(delta);
        renderState.leftArmPose = scLeftArm.calculateEuler(delta);
        renderState.rightArmPose = scRightArm.calculateEuler(delta);
        renderState.leftLegPose = scLeftLeg.calculateEuler(delta);
        renderState.rightLegPose = scRightLeg.calculateEuler(delta);
    }

    @Inject(method = "onSyncedDataUpdated", at = @At("HEAD"))
    private void onSyncedDataUpdated(EntityDataAccessor<?> data, CallbackInfo ci) {
        if (data.equals(ArmorStand.DATA_HEAD_POSE)) {
            scHead.set(getHeadPose());
        }
        if (data.equals(ArmorStand.DATA_BODY_POSE)) {
            scBody.set(getBodyPose());
        }
        if (data.equals(ArmorStand.DATA_LEFT_ARM_POSE)) {
            scLeftArm.set(getLeftArmPose());
        }
        if (data.equals(ArmorStand.DATA_RIGHT_ARM_POSE)) {
            scRightArm.set(getRightArmPose());
        }
        if (data.equals(ArmorStand.DATA_LEFT_LEG_POSE)) {
            scLeftLeg.set(getLeftLegPose());
        }
        if (data.equals(ArmorStand.DATA_RIGHT_LEG_POSE)) {
            scRightLeg.set(getRightLegPose());
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
