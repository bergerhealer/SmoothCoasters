package me.m56738.smoothcoasters.mixin;

import me.m56738.smoothcoasters.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements GameRendererMixinInterface {
    private final AnimatedPose scPose = new AnimatedPose();
    private final DoubleQuaternion scPoseDoubleQuaternion = new DoubleQuaternion();
    private final Quaternion scPoseQuaternion = new Quaternion(0, 0, 0, 1);
    private final DoubleQuaternion scDoubleQuaternion = new DoubleQuaternion();
    private final Quaternion scQuaternion = new Quaternion(0, 0, 0, 1);

    private float scLastYaw;
    private float scYaw;
    private float scPitch;
    private boolean scHasLimit;
    private float scMinYaw = -180;
    private float scMaxYaw = 180;
    private float scMinPitch = -90;
    private float scMaxPitch = 90;

    private boolean scSuppressChanges;
    private RotationMode scRotationMode = RotationMode.CAMERA;
    private boolean scActive;
    private boolean scToggle = true;

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    @Final
    private Camera camera;

    @Override
    public void scSetRotation(Quaternion rotation, int ticks) {
        scPose.set(rotation, ticks);
        scPose.calculate(scPoseDoubleQuaternion, 0);
        scPoseDoubleQuaternion.conjugate();
        scActive = scToggle && scPose.isActive();
        if (scActive && ticks == 0) {
            ClientPlayerEntity player = client.player;
            if (player != null) {
                scUpdateRotation(player);
            }
        }
    }

    @Override
    public void scSetRotationMode(RotationMode mode) {
        scRotationMode = mode;
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
        scYaw = MathHelper.clamp(scYaw, scMinYaw, scMaxYaw);
        scPitch = MathHelper.clamp(scPitch, scMinPitch, scMaxPitch);
    }

    private void scApplyLocalRotation() {
        scPose.calculate(scPoseDoubleQuaternion, client.getTickDelta());
        scPoseDoubleQuaternion.toQuaternion(scPoseQuaternion);
        scPoseDoubleQuaternion.conjugate();

        if (scRotationMode != RotationMode.PLAYER) {
            return;
        }

        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }

        scDoubleQuaternion.set(scPoseDoubleQuaternion);
        scDoubleQuaternion.rotateY(-scYaw);
        scDoubleQuaternion.rotateX(scPitch);
        scDoubleQuaternion.toQuaternion(scQuaternion);
        scQuaternion.conjugate();

        Vector3d forward = scDoubleQuaternion.getForwardVector();
        Vector3d up = scDoubleQuaternion.getUpVector();
        float yaw = DoubleQuaternion.getYaw(forward, up);
        float pitch = DoubleQuaternion.getPitch(forward, up);

        while (Math.abs(yaw - scLastYaw) >= 270) {
            if (yaw < scLastYaw) {
                yaw += 360;
            } else {
                yaw -= 360;
            }
        }
        scLastYaw = yaw;

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
        if (scRotationMode != RotationMode.PLAYER || scSuppressChanges || !(entity instanceof ClientPlayerEntity)) {
            return;
        }

        ClientPlayerEntity player = (ClientPlayerEntity) entity;

        DoubleQuaternion difference = new DoubleQuaternion();
        difference.set(scPoseDoubleQuaternion);
        difference.conjugate();
        difference.rotateY(-player.getYaw());
        difference.rotateX(player.getPitch());

        Vector3d forward = difference.getForwardVector();
        Vector3d up = difference.getUpVector();
        scYaw = DoubleQuaternion.getYaw(forward, up);
        scPitch = DoubleQuaternion.getPitch(forward, up);
    }

    @Override
    public void scLoadLocalRotation(Entity entity) {
        if (scRotationMode != RotationMode.PLAYER || !(entity instanceof ClientPlayerEntity) || !scActive) {
            return;
        }
        scSuppressChanges = true;
        entity.setYaw(scYaw);
        entity.setPitch(scPitch);
    }

    @Override
    public void scApplyLocalRotation(Entity entity) {
        if (scRotationMode != RotationMode.PLAYER || !(entity instanceof ClientPlayerEntity) || !scActive) {
            return;
        }
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
        scPose.calculate(scPoseDoubleQuaternion, 0);
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

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;setupFrustum(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Matrix4f;)V"))
    private void renderWorld(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo info) {
        if (!scActive || camera.getFocusedEntity() != client.player) {
            return;
        }

        if (scRotationMode == RotationMode.PLAYER) {
            Perspective perspective = client.options.getPerspective();
            matrix.loadIdentity();
            if (perspective.isFirstPerson() || !perspective.isFrontView()) {
                matrix.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
            }
            matrix.multiply(scQuaternion);
        } else if (scRotationMode == RotationMode.CAMERA) {
            Perspective perspective = client.options.getPerspective();
            if (perspective.isFirstPerson()) {
                matrix.multiply(scPoseQuaternion);
            }
        }
    }
}
