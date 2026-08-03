package me.m56738.smoothcoasters.common;

import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

public class SmoothCoastersDebugHudEntry implements DebugScreenEntry {
    @Override
    public void display(DebugScreenDisplayer lines, @Nullable Level world, @Nullable LevelChunk clientChunk, @Nullable LevelChunk chunk) {
        SmoothCoasters sc = SmoothCoasters.getInstance();
        if (sc.isEnabled()) {
            lines.addLine("SmoothCoasters " + sc.getVersion() + ": Enabled");
        } else {
            lines.addLine("SmoothCoasters " + sc.getVersion() + ": Server not supported");
        }
    }

    @Override
    public boolean isAllowed(boolean reducedDebugInfo) {
        return true;
    }
}
