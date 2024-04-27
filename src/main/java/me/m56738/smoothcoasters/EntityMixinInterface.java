package me.m56738.smoothcoasters;

import org.joml.Quaternionf;

public interface EntityMixinInterface extends Rotatable {
    Quaternionf smoothcoasters$getQuaternion(float tickDelta);
}
