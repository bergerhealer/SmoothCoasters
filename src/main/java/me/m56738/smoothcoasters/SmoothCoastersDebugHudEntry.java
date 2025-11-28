package me.m56738.smoothcoasters;

import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

public class SmoothCoastersDebugHudEntry implements DebugScreenEntry {
    @Override
    public void display(DebugScreenDisplayer lines, @Nullable Level world, @Nullable LevelChunk clientChunk, @Nullable LevelChunk chunk) {
        SmoothCoasters sc = SmoothCoasters.getInstance();
        byte network = sc.getNetworkVersion();
        if (network == 0) {
            lines.addLine("SmoothCoasters " + sc.getVersion() + ": Server not supported");
        } else {
            lines.addLine("SmoothCoasters " + sc.getVersion() + ": V" + network);
        }
    }

    @Override
    public boolean isAllowed(boolean reducedDebugInfo) {
        return true;
    }
}
