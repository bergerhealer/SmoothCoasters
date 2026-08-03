package me.m56738.smoothcoasters.common.mixin;

import com.mojang.math.Axis;
import me.m56738.smoothcoasters.common.CameraMixinInterface;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public class CameraMixin implements CameraMixinInterface {
    @Unique
    private final Quaternionf scCameraRotation = new Quaternionf();
    @Unique
    private boolean scActive;

    @Shadow
    @Final
    private Quaternionf rotation;

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    @Final
    private static Vector3fc FORWARDS;

    @Shadow
    @Final
    private static Vector3fc UP;

    @Shadow
    @Final
    private static Vector3fc LEFT;

    @Shadow
    @Final
    private Vector3f forwards;

    @Shadow
    @Final
    private Vector3f up;

    @Shadow
    @Final
    private Vector3f left;

    @Inject(method = "alignWithEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V", shift = At.Shift.AFTER))
    private void alignWithEntity(float partialTicks, CallbackInfo ci) {
        if (!scActive) {
            return;
        }

        // Overwrite the rotation (server-supplied + local)
        rotation.set(scCameraRotation);

        if (minecraft.options.getCameraType().isMirrored()) {
            rotation.mul(Axis.YP.rotationDegrees(180));
        }

        FORWARDS.rotate(rotation, forwards);
        UP.rotate(rotation, up);
        LEFT.rotate(rotation, left);
    }

    @Override
    public void smoothcoasters$setCameraRotation(Quaternionfc rotation) {
        scCameraRotation.set(rotation);
        scActive = true;
    }

    @Override
    public void smoothcoasters$resetCameraRotation() {
        scActive = false;
    }
}
