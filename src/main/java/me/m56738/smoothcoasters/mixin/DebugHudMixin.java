package me.m56738.smoothcoasters.mixin;

import me.m56738.smoothcoasters.SmoothCoasters;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Locale;

@Mixin(DebugHud.class)
public class DebugHudMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "getLeftText", at = @At("TAIL"))
    private void append(CallbackInfoReturnable<List<String>> info) {
        List<String> list = info.getReturnValue();

        // Look for XYZ:
        int eyeIndex = 0;
        for (String s : list) {
            eyeIndex++;
            if (s.startsWith("XYZ:")) {
                break;
            }
        }

        Vec3d pos = client.gameRenderer.getCamera().getPos();
        list.add(eyeIndex, String.format(Locale.ROOT, "Eyes: %.3f / %.5f / %.3f", pos.getX(), pos.getY(), pos.getZ()));

        SmoothCoasters sc = SmoothCoasters.getInstance();
        byte network = sc.getNetworkVersion();
        if (network == 0) {
            list.add("SmoothCoasters " + sc.getVersion() + ": Server not supported");
        } else {
            list.add("SmoothCoasters " + sc.getVersion() + ": V" + network);
        }
    }
}
