package me.m56738.smoothcoasters.mixin;

import me.m56738.smoothcoasters.Rotatable;
import me.m56738.smoothcoasters.Util;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin implements Rotatable {
    private final Quaternion scLast = new Quaternion(0, 0, 0, 1);
    private final Quaternion scCurrent = new Quaternion(0, 0, 0, 1);
    private final Quaternion scTarget = new Quaternion(0, 0, 0, 1);
    private final Quaternion scFrame = new Quaternion(0, 0, 0, 1);
    private int scTicks;

    @Override
    public void scSetRotation(Quaternion rotation, int ticks) {
        if (ticks == 0) {
            Util.copy(scLast, rotation);
            Util.copy(scCurrent, rotation);
        }
        Util.copy(scTarget, rotation);
        scTicks = ticks;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo info) {
        Util.copy(scLast, scCurrent);
        if (scTicks > 0) {
            Util.slerp(scCurrent, scCurrent, scTarget, 1f / scTicks);
            scTicks--;
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void render(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo info) {
        if (!camera.isThirdPerson()) {
            Util.slerp(scFrame, scLast, scCurrent, tickDelta);
            matrices.multiply(scFrame);
        }
    }
}
