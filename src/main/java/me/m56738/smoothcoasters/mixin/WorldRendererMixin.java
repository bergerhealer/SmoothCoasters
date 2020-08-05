package me.m56738.smoothcoasters.mixin;

import me.m56738.smoothcoasters.AnimatedPose;
import me.m56738.smoothcoasters.DoubleQuaternion;
import me.m56738.smoothcoasters.Rotatable;
import me.m56738.smoothcoasters.RotatablePlayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin implements Rotatable {
    private final AnimatedPose scPose = new AnimatedPose();
    private final DoubleQuaternion scDoubleQuaternion = new DoubleQuaternion();
    private final Quaternion scQuaternion = new Quaternion(0, 0, 0, 1);

    @Shadow
    @Final
    private MinecraftClient client;

    @Override
    public Quaternion scCalculate(float tickDelta) {
        scPose.calculate(scDoubleQuaternion, tickDelta);
        scDoubleQuaternion.toQuaternion(scQuaternion);
        return scQuaternion;
    }

    @Override
    public void scSetRotation(Quaternion rotation, int ticks) {
        RotatablePlayer entity = (RotatablePlayer) client.player;
        if (entity != null) {
            entity.scSetPlayerRotation(rotation, ticks);
        }
        scPose.set(rotation, ticks);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo info) {
        scPose.tick();
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void render(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo info) {
        if (!camera.isThirdPerson()) {
            scCalculate(tickDelta);

            float x = scQuaternion.getX();
            float y = scQuaternion.getY();
            float z = scQuaternion.getZ();
            float w = scQuaternion.getW();

            double forwardXZ = Math.cos(Math.toRadians(camera.getPitch()));
            Vector3f forward = new Vector3f(
                    (float) (-forwardXZ * Math.sin(Math.toRadians(camera.getYaw()))),
                    (float) (-Math.sin(Math.toRadians(camera.getPitch()))),
                    (float) (forwardXZ * Math.cos(Math.toRadians(camera.getYaw())))
            );
            forward.normalize();

            Vector3f up = new Vector3f(
                    2 * (x * y - z * w),
                    1 + 2 * (-x * x - z * z),
                    2 * (y * z + x * w)
            );
            up.normalize();

            Vector3f right = up.copy();
            right.cross(forward);
            right.normalize();

            up = forward.copy();
            up.cross(right);
            up.normalize();

            float m00 = right.getX();
            float m01 = right.getY();
            float m02 = right.getZ();
            float m10 = up.getX();
            float m11 = up.getY();
            float m12 = up.getZ();
            float m20 = forward.getX();
            float m21 = forward.getY();
            float m22 = forward.getZ();

            float tr = m00 + m11 + m22;
            Quaternion rot;
            if (tr > 0) {
                rot = new Quaternion(m21 - m12, m02 - m20, m10 - m01, 1 + tr);
            } else if ((m00 > m11) & (m00 > m22)) {
                rot = new Quaternion(1 + m00 - m11 - m22, m01 + m10, m02 + m20, m21 - m12);
            } else if (m11 > m22) {
                rot = new Quaternion(m01 + m10, 1 + m11 - m00 - m22, m12 + m21, m02 - m20);
            } else {
                rot = new Quaternion(m02 + m20, m12 + m21, 1 + m22 - m00 - m11, m10 - m01);
            }
            rot.normalize();

            matrices.peek().getModel().loadIdentity();
            matrices.peek().getNormal().loadIdentity();
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180));
            matrices.multiply(rot);
        }
    }
}
