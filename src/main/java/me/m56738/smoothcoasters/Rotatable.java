package me.m56738.smoothcoasters;

import net.minecraft.util.math.Quaternion;

public interface Rotatable {
    void scSetRotation(Quaternion rotation, int ticks);
}
