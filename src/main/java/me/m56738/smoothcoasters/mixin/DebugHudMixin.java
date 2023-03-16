package me.m56738.smoothcoasters.mixin;

import me.m56738.smoothcoasters.SmoothCoasters;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugHud.class)
public class DebugHudMixin {
    @Inject(method = "getLeftText", at = @At("TAIL"))
    private void append(CallbackInfoReturnable<List<String>> info) {
        List<String> list = info.getReturnValue();

        SmoothCoasters sc = SmoothCoasters.getInstance();
        byte network = sc.getNetworkVersion();
        if (network == 0) {
            list.add("SmoothCoasters " + sc.getVersion() + ": Server not supported");
        } else {
            list.add("SmoothCoasters " + sc.getVersion() + ": V" + network);
        }
    }
}
