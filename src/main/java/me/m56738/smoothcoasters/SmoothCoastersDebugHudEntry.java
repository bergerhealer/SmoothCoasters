package me.m56738.smoothcoasters;

import net.minecraft.client.gui.hud.debug.DebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudLines;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

public class SmoothCoastersDebugHudEntry implements DebugHudEntry {
    @Override
    public void render(DebugHudLines lines, @Nullable World world, @Nullable WorldChunk clientChunk, @Nullable WorldChunk chunk) {
        SmoothCoasters sc = SmoothCoasters.getInstance();
        byte network = sc.getNetworkVersion();
        if (network == 0) {
            lines.addLine("SmoothCoasters " + sc.getVersion() + ": Server not supported");
        } else {
            lines.addLine("SmoothCoasters " + sc.getVersion() + ": V" + network);
        }
    }

    @Override
    public boolean canShow(boolean reducedDebugInfo) {
        return true;
    }
}
