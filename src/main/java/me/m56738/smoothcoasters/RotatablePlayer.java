package me.m56738.smoothcoasters;

import net.minecraft.util.math.Quaternion;

public interface RotatablePlayer {
    void scSetPlayerRotation(Quaternion rotation, int ticks);
}
