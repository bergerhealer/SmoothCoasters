package me.m56738.smoothcoasters.mixin;

import me.m56738.smoothcoasters.RotatablePlayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends LivingEntity implements RotatablePlayer {
    @Shadow
    @Final
    protected MinecraftClient client;
    private float scCurrentYaw;
    private float scCurrentPitch;
    private float scLastYaw;
    private float scLastPitch;
    private float scDeltaYaw;
    private float scDeltaPitch;
    private float scCurrentDeltaYaw;
    private float scCurrentDeltaPitch;
    private int scLerpTicks;

    public ClientPlayerEntityMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void scSetPlayerRotation(Quaternion rotation, int ticks) {
        float x = rotation.getX();
        float y = rotation.getY();
        float z = rotation.getZ();
        float w = rotation.getW();

        Vector3f forward = new Vector3f(
                2 * (x * z + y * w),
                2 * (y * z - x * w),
                1 + 2 * (-x * x - y * y)
        );

        double forwardXZ = Math.sqrt(forward.getX() * forward.getX() + forward.getZ() * forward.getZ());
        scCurrentYaw = (float) Math.toDegrees(Math.atan2(-forward.getX(), forward.getZ()));
        if (forwardXZ == 0) {
            scCurrentPitch = forward.getY() > 0 ? -90 : 90;
        } else {
            scCurrentPitch = (float) Math.toDegrees(Math.atan(-forward.getY() / forwardXZ));
        }

        scDeltaYaw += MathHelper.subtractAngles(scLastYaw, scCurrentYaw);
        scDeltaPitch += scCurrentPitch - scLastPitch;

        if (Math.abs(scDeltaYaw) > 90) {
            yaw += scDeltaYaw;
            headYaw += scDeltaYaw;
            prevYaw += scDeltaYaw;
            prevHeadYaw += scDeltaYaw;
            scDeltaYaw = 0;
        }

        scLastYaw = scCurrentYaw;
        scLastPitch = scCurrentPitch;

        scLerpTicks = ticks;
    }

    @Inject(method = "getYaw", at = @At("RETURN"), cancellable = true)
    private void getYaw(float tickDelta, CallbackInfoReturnable<Float> info) {
        if (info.getReturnValueF() == this.yaw) {
            info.setReturnValue(info.getReturnValueF() + (tickDelta - 1) * scCurrentDeltaYaw);
        }
    }

    @Inject(method = "getPitch", at = @At("RETURN"), cancellable = true)
    private void getPitch(float tickDelta, CallbackInfoReturnable<Float> info) {
        if (info.getReturnValueF() == this.pitch) {
            info.setReturnValue(MathHelper.clamp(info.getReturnValueF() + (tickDelta - 1) * scCurrentDeltaPitch, -90, 90));
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void tick(CallbackInfo info) {
        if (scLerpTicks > 0) {
            float dy = scDeltaYaw / scLerpTicks;
            float dp = scDeltaPitch / scLerpTicks;
            scDeltaYaw -= dy;
            scDeltaPitch -= dp;

            yaw += dy;
            pitch = MathHelper.clamp(pitch + dp, -90, 90);

            scCurrentDeltaYaw = dy;
            scCurrentDeltaPitch = dp;

            scLerpTicks--;
        } else {
            scCurrentDeltaYaw = 0;
            scCurrentDeltaPitch = 0;
        }

        String msg = String.format("%7.2f %7.2f %d", yaw, pitch, scLerpTicks);
        client.inGameHud.addChatMessage(MessageType.GAME_INFO, new LiteralText(msg), Util.NIL_UUID);
    }
}
