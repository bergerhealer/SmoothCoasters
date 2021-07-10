package me.m56738.smoothcoasters.mixin;

import me.m56738.smoothcoasters.AnimatedPose;
import me.m56738.smoothcoasters.DoubleQuaternion;
import me.m56738.smoothcoasters.GameRendererMixinInterface;
import me.m56738.smoothcoasters.SmoothCoasters;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3d;
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
    private final DoubleQuaternion scDoubleQuaternion = new DoubleQuaternion();
    private final Quaternion scQuaternion = new Quaternion(0, 0, 0, 1);
    private float scLastYaw;
    private float scYaw;
    private float scPitch;

    @Shadow
    @Final
    private MinecraftClient client;

    @Override
    public void scSetRotation(Quaternion rotation, int ticks) {
        scPose.set(rotation, ticks);
    }

    private void scUpdateMouse(boolean updatePlayer, float tickDelta) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }

        scDoubleQuaternion.set(scPoseDoubleQuaternion);
        scDoubleQuaternion.rotateY(-scYaw);
        scDoubleQuaternion.rotateX(scPitch);
        scDoubleQuaternion.toQuaternion(scQuaternion);
        scQuaternion.conjugate();

        if (!updatePlayer) {
            return;
        }

        double x = scDoubleQuaternion.getX();
        double y = scDoubleQuaternion.getY();
        double z = scDoubleQuaternion.getZ();
        double w = scDoubleQuaternion.getW();

        Vector3d forward = new Vector3d(
                2 * (x * z + y * w),
                2 * (y * z - x * w),
                1 + 2 * (-x * x - y * y)
        );

        Vector3d up = new Vector3d(
                2 * (x * y - z * w),
                1 + 2 * (-x * x - z * z),
                2 * (y * z + x * w)
        );

        double forwardXZ = Math.sqrt(forward.x * forward.x + forward.z * forward.z);
        float yaw;
        float pitch;
        if (forward.y < -0.995) {
            yaw = (float) Math.toDegrees(Math.atan2(-up.x, up.z));
        } else if (forward.y > 0.995) {
            yaw = (float) Math.toDegrees(Math.atan2(up.x, -up.z));
        } else {
            yaw = (float) Math.toDegrees(Math.atan2(-forward.x, forward.z));
        }
        pitch = (float) Math.toDegrees(Math.atan2(-forward.y, forwardXZ));

        while (Math.abs(yaw - scLastYaw) >= 270) {
            if (yaw < scLastYaw) {
                yaw += 360;
            } else {
                yaw -= 360;
            }
        }
        scLastYaw = yaw;

        player.prevHeadYaw = yaw;
        player.prevYaw = yaw;
        player.prevPitch = pitch;
        player.setHeadYaw(yaw);
        player.setYaw(yaw);
        player.setPitch(pitch);
    }

    @Override
    public void scApplyLookDirection(float localYaw, float localPitch) {
        scYaw = localYaw;
        scPitch = localPitch;
        scPose.calculate(scPoseDoubleQuaternion, MinecraftClient.getInstance().getTickDelta());
        scPoseDoubleQuaternion.conjugate();
        scUpdateMouse(true, MinecraftClient.getInstance().getTickDelta());
    }

    @Inject(method = "reset", at = @At("HEAD"))
    private void reset(CallbackInfo info) {
        SmoothCoasters.getInstance().resetRotation();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo info) {
        scPose.tick();
    }

    @Inject(method = "render", at = @At(value = "HEAD"))
    private void render(float tickDelta, long startTime, boolean tick, CallbackInfo info) {
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;setupFrustum(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Matrix4f;)V"))
    private void renderWorld(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo info) {
        Perspective perspective = client.options.getPerspective();
        matrix.loadIdentity();
        if (perspective.isFirstPerson() || !perspective.isFrontView()) {
            matrix.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
        }
        matrix.multiply(scQuaternion);
    }
}
