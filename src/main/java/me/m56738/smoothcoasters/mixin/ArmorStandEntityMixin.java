package me.m56738.smoothcoasters.mixin;

import me.m56738.smoothcoasters.Animatable;
import me.m56738.smoothcoasters.Util;
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
    @Shadow
    private EulerAngle headRotation;
    private EulerAngle scAnimatePrevHeadRotation;
    private EulerAngle scAnimateRealHeadRotation = new EulerAngle(0, 0, 0);

    @Shadow
    private EulerAngle bodyRotation;
    private EulerAngle scAnimatePrevBodyRotation;
    private EulerAngle scAnimateRealBodyRotation = new EulerAngle(0, 0, 0);

    @Shadow
    private EulerAngle leftArmRotation;
    private EulerAngle scAnimatePrevLeftArmRotation;
    private EulerAngle scAnimateRealLeftArmRotation = new EulerAngle(0, 0, 0);

    @Shadow
    private EulerAngle rightArmRotation;
    private EulerAngle scAnimatePrevRightArmRotation;
    private EulerAngle scAnimateRealRightArmRotation = new EulerAngle(0, 0, 0);

    @Shadow
    private EulerAngle leftLegRotation;
    private EulerAngle scAnimatePrevLeftLegRotation;
    private EulerAngle scAnimateRealLeftLegRotation = new EulerAngle(0, 0, 0);

    @Shadow
    private EulerAngle rightLegRotation;
    private EulerAngle scAnimatePrevRightLegRotation;
    private EulerAngle scAnimateRealRightLegRotation = new EulerAngle(0, 0, 0);

    protected ArmorStandEntityMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void scAnimate(float delta) {
        headRotation = Util.lerp(scAnimatePrevHeadRotation, scAnimateRealHeadRotation, delta);
        bodyRotation = Util.lerp(scAnimatePrevBodyRotation, scAnimateRealBodyRotation, delta);
        leftArmRotation = Util.lerp(scAnimatePrevLeftArmRotation, scAnimateRealLeftArmRotation, delta);
        rightArmRotation = Util.lerp(scAnimatePrevRightArmRotation, scAnimateRealRightArmRotation, delta);
        leftLegRotation = Util.lerp(scAnimatePrevLeftLegRotation, scAnimateRealLeftLegRotation, delta);
        rightLegRotation = Util.lerp(scAnimatePrevRightLegRotation, scAnimateRealRightLegRotation, delta);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickHead(CallbackInfo info) {
        // Restore real pose
        headRotation = scAnimateRealHeadRotation;
        bodyRotation = scAnimateRealBodyRotation;
        leftArmRotation = scAnimateRealLeftArmRotation;
        rightArmRotation = scAnimateRealRightArmRotation;
        leftLegRotation = scAnimateRealLeftLegRotation;
        rightLegRotation = scAnimateRealRightLegRotation;

        // Save as previous pose
        scAnimatePrevHeadRotation = headRotation;
        scAnimatePrevBodyRotation = bodyRotation;
        scAnimatePrevLeftArmRotation = leftArmRotation;
        scAnimatePrevRightArmRotation = rightArmRotation;
        scAnimatePrevLeftLegRotation = leftLegRotation;
        scAnimatePrevRightLegRotation = rightLegRotation;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickTail(CallbackInfo info) {
        // Save real pose
        scAnimateRealHeadRotation = headRotation;
        scAnimateRealBodyRotation = bodyRotation;
        scAnimateRealLeftArmRotation = leftArmRotation;
        scAnimateRealRightArmRotation = rightArmRotation;
        scAnimateRealLeftLegRotation = leftLegRotation;
        scAnimateRealRightLegRotation = rightLegRotation;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Box getVisibilityBoundingBox() {
        return super.getVisibilityBoundingBox().expand(3);
    }
}
