package me.m56738.smoothcoasters;

import net.minecraft.util.math.Quaternion;

public interface Rotatable {
    Quaternion scCalculate(float tickDelta);

    void scSetRotation(Quaternion rotation, int ticks);
}
