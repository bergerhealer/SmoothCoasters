package me.m56738.smoothcoasters.mixin;

import me.m56738.smoothcoasters.AnimatedPose;
import me.m56738.smoothcoasters.DoubleQuaternion;
import me.m56738.smoothcoasters.EntityMixinInterface;
import me.m56738.smoothcoasters.GameRendererMixinInterface;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Quaternion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
@Environment(EnvType.CLIENT)
public class EntityMixin implements EntityMixinInterface {
    private final AnimatedPose scPose = new AnimatedPose();
    private final DoubleQuaternion scDoubleQuaternion = new DoubleQuaternion();
    private final Quaternion scQuaternion = new Quaternion(0, 0, 0, 1);

    @Override
    public Quaternion scGetQuaternion(float tickDelta) {
        scPose.calculate(scDoubleQuaternion, tickDelta);
        scDoubleQuaternion.toQuaternion(scQuaternion);
        return scQuaternion;
    }

    @Override
    public void scSetRotation(Quaternion rotation, int ticks) {
        scPose.set(rotation, ticks);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo info) {
        scPose.tick();
    }

    @Inject(method = "setYaw", at = @At("RETURN"))
    private void setYaw(CallbackInfo info) {
        ((GameRendererMixinInterface) MinecraftClient.getInstance().gameRenderer)
                .scUpdateRotation((Entity) (Object) this);
    }

    @Inject(method = "setPitch", at = @At("RETURN"))
    private void setPitch(CallbackInfo info) {
        ((GameRendererMixinInterface) MinecraftClient.getInstance().gameRenderer)
                .scUpdateRotation((Entity) (Object) this);
    }

    @Inject(method = "changeLookDirection", at = @At("HEAD"))
    private void changeLookDirectionHead(double cursorDeltaX, double cursorDeltaY, CallbackInfo info) {
        ((GameRendererMixinInterface) MinecraftClient.getInstance().gameRenderer)
                .scLoadLocalRotation((Entity) (Object) this);
    }

    @Inject(method = "changeLookDirection", at = @At("TAIL"))
    private void changeLookDirectionTail(double cursorDeltaX, double cursorDeltaY, CallbackInfo info) {
        ((GameRendererMixinInterface) MinecraftClient.getInstance().gameRenderer)
                .scApplyLocalRotation((Entity) (Object) this);
    }
}
