package me.m56738.smoothcoasters.mixin;

import me.m56738.smoothcoasters.AnimatedPose;
import me.m56738.smoothcoasters.GameRendererMixinInterface;
import me.m56738.smoothcoasters.MathUtil;
import me.m56738.smoothcoasters.SmoothCoasters;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
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
    private MinecraftClient client;
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
    private Camera camera;

    @Override
    public void smoothcoasters$setRotation(Quaternionfc rotation, int ticks) {
        scPose.set(rotation, ticks);
        scPose.calculate(scPoseQuaternion, 0);
        scActive = scToggle && scPose.isActive();
        if (scActive && ticks == 0) {
            ClientPlayerEntity player = client.player;
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
        scYaw = MathHelper.wrapDegrees(scYaw);
        scPitch = MathHelper.wrapDegrees(scPitch);
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
        scPose.calculate(scPoseQuaternion, client.getRenderTickCounter().getTickProgress(true));

        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }

        // Add the local yaw/pitch
        scCameraRotation.set(scPoseQuaternion);
        scCameraRotation.conjugate();
        scCameraRotation.rotateY(Math.toRadians(-scYaw));
        scCameraRotation.rotateX(Math.toRadians(scPitch));
        scCameraRotation.transformUnitPositiveZ(scForward);
        scCameraRotation.transformUnitPositiveY(scUp);
        scCameraRotation.conjugate();

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
        player.lastHeadYaw = yaw;
        player.lastYaw = yaw;
        player.lastPitch = pitch;
        player.setHeadYaw(yaw);
        player.setYaw(yaw);
        player.setPitch(pitch);
        scSuppressChanges = false;
    }

    @Override
    public void smoothcoasters$updateRotation(Entity entity) {
        if (scSuppressChanges || !(entity instanceof ClientPlayerEntity player)) {
            return;
        }

        // Difference from pose to desired look direction
        scDifference.set(scPoseQuaternion);
        scDifference.rotateY(Math.toRadians(-player.getYaw()));
        scDifference.rotateX(Math.toRadians(player.getPitch()));
        scDifference.transformUnitPositiveZ(scForward);
        scDifference.transformUnitPositiveY(scUp);
        scYaw = MathUtil.getYaw(scForward, scUp);
        scPitch = MathUtil.getPitch(scForward);
    }

    @Override
    public void smoothcoasters$loadLocalRotation(Entity entity) {
        if (!(entity instanceof ClientPlayerEntity) || !scActive) {
            return;
        }
        // Set entity to local rotation
        // Suppress changes for mouse movement
        scSuppressChanges = true;
        entity.setYaw(scYaw);
        entity.setPitch(scPitch);
    }

    @Override
    public void smoothcoasters$applyLocalRotation(Entity entity) {
        if (!(entity instanceof ClientPlayerEntity) || !scActive) {
            return;
        }
        // Store new local rotation
        scYaw = entity.getYaw();
        scPitch = entity.getPitch();
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
            ClientPlayerEntity player = client.player;
            if (player != null) {
                smoothcoasters$updateRotation(player);
            }
        }
    }

    @Inject(method = "reset", at = @At("HEAD"))
    private void reset(CallbackInfo info) {
        SmoothCoasters.getInstance().reset();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo info) {
        scPose.tick();
        scActive = scToggle && scPose.isActive();
    }

    @Inject(method = "render", at = @At(value = "HEAD"))
    private void render(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        if (!scActive) {
            return;
        }
        applyLocalRotation();
    }

    @ModifyArg(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;setupFrustum(Lnet/minecraft/util/math/Vec3d;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V"), index = 1)
    private Matrix4f renderWorld(Matrix4f matrix) {
        if (camera.getFocusedEntity() != client.player || !scActive) {
            return matrix;
        }

        matrix.identity();
        Perspective perspective = client.options.getPerspective();
        if (perspective.isFirstPerson() || !perspective.isFrontView()) {
            matrix.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        }
        matrix.rotate(scCameraRotation); // Apply the rotation (server-supplied + local)
        return matrix;
    }
}
