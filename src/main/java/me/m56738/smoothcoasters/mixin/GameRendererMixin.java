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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements GameRendererMixinInterface {
    private final AnimatedPose scPose = new AnimatedPose();
    private final Quaternionf scPoseQuaternion = new Quaternionf();
    private final Quaternionf scCameraRotation = new Quaternionf();
    private final Quaternionf scDifference = new Quaternionf();
    private final Vector3d scForward = new Vector3d();
    private final Vector3d scUp = new Vector3d();
    @Shadow
    @Final
    MinecraftClient client;
    // Local angle
    private float scLastYaw;
    private float scYaw;
    private float scPitch;
    private boolean scHasLimit = false;
    private float scMinYaw = -180;
    private float scMaxYaw = 180;
    private float scMinPitch = -90;
    private float scMaxPitch = 90;
    private boolean scSuppressChanges;
    private boolean scActive;
    private boolean scToggle = true;
    @Shadow
    @Final
    private Camera camera;

    @Override
    public void scSetRotation(Quaternionf rotation, int ticks) {
        scPose.set(rotation, ticks);
        scPose.calculate(scPoseQuaternion, 0);
        scActive = scToggle && scPose.isActive();
        if (scActive && ticks == 0) {
            ClientPlayerEntity player = client.player;
            if (player != null) {
                // Update local yaw/pitch so the player still looks in the same direction
                scUpdateRotation(player);
            }
        }
    }

    @Override
    public void scSetRotationLimit(float minYaw, float maxYaw, float minPitch, float maxPitch) {
        scHasLimit = minYaw > -180f || maxYaw < 180f || minPitch > -90f || maxPitch < 90f;
        scMinYaw = minYaw;
        scMaxYaw = maxYaw;
        scMinPitch = minPitch;
        scMaxPitch = maxPitch;
        scEnforceRotationLimit();
    }

    private void scEnforceRotationLimit() {
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

    private void scApplyLocalRotation() {
        // Server-supplied rotation (excluding local player rotation)
        scPose.calculate(scPoseQuaternion, client.getTickDelta());

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
        player.prevHeadYaw = yaw;
        player.prevYaw = yaw;
        player.prevPitch = pitch;
        player.setHeadYaw(yaw);
        player.setYaw(yaw);
        player.setPitch(pitch);
        scSuppressChanges = false;
    }

    @Override
    public void scUpdateRotation(Entity entity) {
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
    public void scLoadLocalRotation(Entity entity) {
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
    public void scApplyLocalRotation(Entity entity) {
        if (!(entity instanceof ClientPlayerEntity) || !scActive) {
            return;
        }
        // Store new local rotation
        scYaw = entity.getYaw();
        scPitch = entity.getPitch();
        scEnforceRotationLimit();
        scApplyLocalRotation();
    }

    @Override
    public boolean scGetRotationToggle() {
        return scToggle;
    }

    @Override
    public void scSetRotationToggle(boolean enabled) {
        scPose.calculate(scPoseQuaternion, 0);
        scToggle = enabled;
        scActive = scToggle && scPose.isActive();
        if (scActive) {
            ClientPlayerEntity player = client.player;
            if (player != null) {
                scUpdateRotation(player);
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
    private void render(float tickDelta, long startTime, boolean tick, CallbackInfo info) {
        if (!scActive) {
            return;
        }
        scApplyLocalRotation();
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;setupFrustum(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Vec3d;Lorg/joml/Matrix4f;)V"))
    private void renderWorld(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo info) {
        if (camera.getFocusedEntity() != client.player || !scActive) {
            return;
        }

        Perspective perspective = client.options.getPerspective();
        matrix.loadIdentity(); // Don't use the player's yaw/pitch (the quaternion below already contains it)
        if (perspective.isFirstPerson() || !perspective.isFrontView()) {
            matrix.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        }
        matrix.multiply(scCameraRotation); // Apply the rotation (server-supplied + local)
    }
}
