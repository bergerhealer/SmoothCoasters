package me.m56738.smoothcoasters.common.mixin;

import me.m56738.smoothcoasters.common.GameRendererMixinInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "setYRot", at = @At("RETURN"))
    private void setYRot(CallbackInfo info) {
        ((GameRendererMixinInterface) Minecraft.getInstance().gameRenderer)
                .smoothcoasters$updateRotation((Entity) (Object) this);
    }

    @Inject(method = "setXRot", at = @At("RETURN"))
    private void setXRot(CallbackInfo info) {
        ((GameRendererMixinInterface) Minecraft.getInstance().gameRenderer)
                .smoothcoasters$updateRotation((Entity) (Object) this);
    }

    @Inject(method = "turn", at = @At("HEAD"))
    private void turnHead(double cursorDeltaX, double cursorDeltaY, CallbackInfo info) {
        ((GameRendererMixinInterface) Minecraft.getInstance().gameRenderer)
                .smoothcoasters$loadLocalRotation((Entity) (Object) this);
    }

    @Inject(method = "turn", at = @At("RETURN"))
    private void turnTail(double cursorDeltaX, double cursorDeltaY, CallbackInfo info) {
        ((GameRendererMixinInterface) Minecraft.getInstance().gameRenderer)
                .smoothcoasters$applyLocalRotation((Entity) (Object) this);
    }

    @Inject(method = "getVehicleAttachmentPoint", at = @At("HEAD"))
    private void getVehicleAttachmentPointHead(Entity vehicle, CallbackInfoReturnable<Vec3> info) {
        // Attachment points must use the local look direction. The composed
        // coaster yaw can otherwise offset the player during vehicle movement.
        ((GameRendererMixinInterface) Minecraft.getInstance().gameRenderer)
                .smoothcoasters$loadLocalRotation((Entity) (Object) this);
    }

    @Inject(method = "getVehicleAttachmentPoint", at = @At("RETURN"))
    private void getVehicleAttachmentPointTail(Entity vehicle, CallbackInfoReturnable<Vec3> info) {
        ((GameRendererMixinInterface) Minecraft.getInstance().gameRenderer)
                .smoothcoasters$applyLocalRotation((Entity) (Object) this);
    }
}
