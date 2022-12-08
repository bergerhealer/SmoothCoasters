package me.m56738.smoothcoasters;

import org.joml.Quaternionf;

public interface Rotatable {
    void scSetRotation(Quaternionf rotation, int ticks);
}
