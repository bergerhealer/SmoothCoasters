package me.m56738.smoothcoasters.mixin;

import me.m56738.smoothcoasters.Animatable;
import me.m56738.smoothcoasters.AnimatedPose;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorStandEntity.class)
public abstract class ArmorStandEntityMixin extends LivingEntity implements Animatable {
    private final AnimatedPose scHead = new AnimatedPose();
    private final AnimatedPose scBody = new AnimatedPose();
    private final AnimatedPose scLeftArm = new AnimatedPose();
    private final AnimatedPose scRightArm = new AnimatedPose();
    private final AnimatedPose scLeftLeg = new AnimatedPose();
    private final AnimatedPose scRightLeg = new AnimatedPose();

    @Shadow
    private EulerAngle headRotation;
    @Shadow
    private EulerAngle bodyRotation;
    @Shadow
    private EulerAngle leftArmRotation;
    @Shadow
    private EulerAngle rightArmRotation;
    @Shadow
    private EulerAngle leftLegRotation;
    @Shadow
    private EulerAngle rightLegRotation;

    protected ArmorStandEntityMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void scAnimate(float delta) {
        headRotation = scHead.lerp(delta);
        bodyRotation = scBody.lerp(delta);
        leftArmRotation = scLeftArm.lerp(delta);
        rightArmRotation = scRightArm.lerp(delta);
        leftLegRotation = scLeftLeg.lerp(delta);
        rightLegRotation = scRightLeg.lerp(delta);
    }

    @Inject(method = "setHeadRotation", at = @At("HEAD"))
    private void markHeadRotationChanged(EulerAngle angle, CallbackInfo info) {
        scHead.set(angle);
    }

    @Inject(method = "setBodyRotation", at = @At("HEAD"))
    private void markBodyRotationChanged(EulerAngle angle, CallbackInfo info) {
        scBody.set(angle);
    }

    @Inject(method = "setLeftArmRotation", at = @At("HEAD"))
    private void markLeftArmRotationChanged(EulerAngle angle, CallbackInfo info) {
        scLeftArm.set(angle);
    }

    @Inject(method = "setRightArmRotation", at = @At("HEAD"))
    private void markRightArmRotationChanged(EulerAngle angle, CallbackInfo info) {
        scRightArm.set(angle);
    }

    @Inject(method = "setLeftLegRotation", at = @At("HEAD"))
    private void markLeftLegRotationChanged(EulerAngle angle, CallbackInfo info) {
        scLeftLeg.set(angle);
    }

    @Inject(method = "setRightLegRotation", at = @At("HEAD"))
    private void markRightLegRotationChanged(EulerAngle angle, CallbackInfo info) {
        scRightLeg.set(angle);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickHead(CallbackInfo info) {
        scHead.tick();
        scBody.tick();
        scLeftArm.tick();
        scRightArm.tick();
        scLeftLeg.tick();
        scRightLeg.tick();

        headRotation = scHead.targetEuler;
        bodyRotation = scBody.targetEuler;
        leftArmRotation = scLeftArm.targetEuler;
        rightArmRotation = scRightArm.targetEuler;
        leftLegRotation = scLeftLeg.targetEuler;
        rightLegRotation = scRightLeg.targetEuler;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Box getVisibilityBoundingBox() {
        return super.getVisibilityBoundingBox().expand(3);
    }
}
