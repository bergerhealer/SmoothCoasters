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
    private void markHeadRotationChanged(CallbackInfo info) {
        scHead.markChanged();
    }

    @Inject(method = "setBodyRotation", at = @At("HEAD"))
    private void markBodyRotationChanged(CallbackInfo info) {
        scBody.markChanged();
    }

    @Inject(method = "setLeftArmRotation", at = @At("HEAD"))
    private void markLeftArmRotationChanged(CallbackInfo info) {
        scLeftArm.markChanged();
    }

    @Inject(method = "setRightArmRotation", at = @At("HEAD"))
    private void markRightArmRotationChanged(CallbackInfo info) {
        scRightArm.markChanged();
    }

    @Inject(method = "setLeftLegRotation", at = @At("HEAD"))
    private void markLeftLegRotationChanged(CallbackInfo info) {
        scLeftLeg.markChanged();
    }

    @Inject(method = "setRightLegRotation", at = @At("HEAD"))
    private void markRightLegRotationChanged(CallbackInfo info) {
        scRightLeg.markChanged();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickHead(CallbackInfo info) {
        scHead.tick();
        scBody.tick();
        scLeftArm.tick();
        scRightArm.tick();
        scLeftLeg.tick();
        scRightLeg.tick();

        // Save as previous pose
        scHead.previous = headRotation;
        scBody.previous = bodyRotation;
        scLeftArm.previous = leftArmRotation;
        scRightArm.previous = rightArmRotation;
        scLeftLeg.previous = leftLegRotation;
        scRightLeg.previous = rightLegRotation;

        // Restore target
        headRotation = scHead.target;
        bodyRotation = scBody.target;
        leftArmRotation = scLeftArm.target;
        rightArmRotation = scRightArm.target;
        leftLegRotation = scLeftLeg.target;
        rightLegRotation = scRightLeg.target;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickTail(CallbackInfo info) {
        // Save target
        scHead.target = headRotation;
        scBody.target = bodyRotation;
        scLeftArm.target = leftArmRotation;
        scRightArm.target = rightArmRotation;
        scLeftLeg.target = leftLegRotation;
        scRightLeg.target = rightLegRotation;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Box getVisibilityBoundingBox() {
        return super.getVisibilityBoundingBox().expand(3);
    }
}
