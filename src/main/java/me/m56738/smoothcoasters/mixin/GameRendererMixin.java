package me.m56738.smoothcoasters.mixin;

import com.mojang.math.Axis;
import me.m56738.smoothcoasters.AnimatedPose;
import me.m56738.smoothcoasters.GameRendererMixinInterface;
import me.m56738.smoothcoasters.MathUtil;
import me.m56738.smoothcoasters.SmoothCoasters;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements GameRendererMixinInterface {
    @Unique
    private final AnimatedPose scPose = new AnimatedPose();
    @Unique
    private final Quaternionf scPoseQuaternion = new Quaternionf();
    @Unique
    private final Quaternionf scCameraRotation = new Quaternionf();
    @Unique
    private final Quaternionf scDifference = new Quaternionf();
    @Unique
    private final Vector3d scForward = new Vector3d();
    @Unique
    private final Vector3d scUp = new Vector3d();
    @Shadow
    @Final
    private Minecraft minecraft;
    // Local angle
    @Unique
    private float scLastYaw;
    @Unique
    private float scYaw;
    @Unique
    private float scPitch;
    @Unique
    private boolean scHasLimit = false;
    @Unique
    private float scMinYaw = -180;
    @Unique
    private float scMaxYaw = 180;
    @Unique
    private float scMinPitch = -90;
    @Unique
    private float scMaxPitch = 90;
    @Unique
    private boolean scSuppressChanges;
    @Unique
    private boolean scActive;
    @Unique
    private boolean scToggle = true;
    @Shadow
    @Final
    private Camera mainCamera;

    @Override
    public void smoothcoasters$setRotation(Quaternionfc rotation, int ticks) {
        scPose.set(rotation, ticks);
        scPose.calculate(scPoseQuaternion, 0);
        scActive = scToggle && scPose.isActive();
        if (scActive && ticks == 0) {
            LocalPlayer player = minecraft.player;
            if (player != null) {
                // Update local yaw/pitch so the player still looks in the same direction
                smoothcoasters$updateRotation(player);
            }
        }
    }

    @Override
    public void smoothcoasters$setRotationLimit(float minYaw, float maxYaw, float minPitch, float maxPitch) {
        scHasLimit = minYaw > -180f || maxYaw < 180f || minPitch > -90f || maxPitch < 90f;
        scMinYaw = minYaw;
        scMaxYaw = maxYaw;
        scMinPitch = minPitch;
        scMaxPitch = maxPitch;
        enforceRotationLimit();
    }

    @Unique
    private void enforceRotationLimit() {
        if (!scHasLimit) return;
        scYaw = Mth.wrapDegrees(scYaw);
        scPitch = Mth.wrapDegrees(scPitch);
        if (scYaw < scMinYaw) {
            scYaw = scMinYaw;
        }
        if (scYaw > scMaxYaw) {
            scYaw = scMaxYaw;
        }
        if (scPitch < scMinPitch) {
            scPitch = scMinPitch;
        }
        if (scPitch > scMaxPitch) {
            scPitch = scMaxPitch;
        }
    }

    @Unique
    private void applyLocalRotation() {
        // Server-supplied rotation (excluding local player rotation)
        scPose.calculate(scPoseQuaternion, minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true));

        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }

        // Add the local yaw/pitch
        scCameraRotation.set(scPoseQuaternion);
        scCameraRotation.rotateY(Math.toRadians(-scYaw));
        scCameraRotation.rotateX(Math.toRadians(scPitch));
        scCameraRotation.transformUnitPositiveZ(scForward);
        scCameraRotation.transformUnitPositiveY(scUp);

        // Compute the result yaw/pitch
        float yaw = MathUtil.getYaw(scForward, scUp);
        float pitch = MathUtil.getPitch(scForward);

        while (Math.abs(yaw - scLastYaw) >= 270) {
            if (yaw < scLastYaw) {
                yaw += 360;
            } else {
                yaw -= 360;
            }
        }
        scLastYaw = yaw;

        // Apply the result to the player
        scSuppressChanges = true;
        player.yHeadRotO = yaw;
        player.yRotO = yaw;
        player.xRotO = pitch;
        player.setYHeadRot(yaw);
        player.setYRot(yaw);
        player.setXRot(pitch);
        scSuppressChanges = false;
    }

    @Override
    public void smoothcoasters$updateRotation(Entity entity) {
        if (scSuppressChanges || !(entity instanceof LocalPlayer player)) {
            return;
        }

        // Difference from pose to desired look direction
        scPoseQuaternion.conjugate(scDifference);
        scDifference.rotateY(Math.toRadians(-player.getYRot()));
        scDifference.rotateX(Math.toRadians(player.getXRot()));
        scDifference.transformUnitPositiveZ(scForward);
        scDifference.transformUnitPositiveY(scUp);
        scYaw = MathUtil.getYaw(scForward, scUp);
        scPitch = MathUtil.getPitch(scForward);
    }

    @Override
    public void smoothcoasters$loadLocalRotation(Entity entity) {
        if (!(entity instanceof LocalPlayer) || !scActive) {
            return;
        }
        // Set entity to local rotation
        // Suppress changes for mouse movement
        scSuppressChanges = true;
        entity.setYRot(scYaw);
        entity.setXRot(scPitch);
    }

    @Override
    public void smoothcoasters$applyLocalRotation(Entity entity) {
        if (!(entity instanceof LocalPlayer) || !scActive) {
            return;
        }
        // Store new local rotation
        scYaw = entity.getYRot();
        scPitch = entity.getXRot();
        enforceRotationLimit();
        applyLocalRotation();
    }

    @Override
    public boolean smoothcoasters$getRotationToggle() {
        return scToggle;
    }

    @Override
    public void smoothcoasters$setRotationToggle(boolean enabled) {
        scPose.calculate(scPoseQuaternion, 0);
        scToggle = enabled;
        scActive = scToggle && scPose.isActive();
        if (scActive) {
            LocalPlayer player = minecraft.player;
            if (player != null) {
                smoothcoasters$updateRotation(player);
            }
        }
    }

    @Inject(method = "resetData", at = @At("HEAD"))
    private void resetData(CallbackInfo info) {
        SmoothCoasters.getInstance().reset();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo info) {
        scPose.tick();
        scActive = scToggle && scPose.isActive();
    }

    @Inject(method = "render", at = @At(value = "HEAD"))
    private void render(DeltaTracker tickCounter, boolean tick, CallbackInfo ci) {
        if (!scActive) {
            return;
        }
        applyLocalRotation();
    }

    @Inject(method = "updateCamera", at = @At(value = "RETURN"))
    private void updateCamera(DeltaTracker deltaTracker, CallbackInfo ci) {
        if (mainCamera.entity() != minecraft.player || !scActive) {
            return;
        }
        Quaternionf rotation = mainCamera.rotation();

        // Apply the rotation (server-supplied + local)
        rotation.set(scCameraRotation);

        if (minecraft.options.getCameraType() != CameraType.THIRD_PERSON_FRONT) {
            rotation.mul(Axis.YP.rotationDegrees(180));
        }
    }
}
