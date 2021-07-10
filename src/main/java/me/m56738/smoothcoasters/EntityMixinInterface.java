package me.m56738.smoothcoasters;

import net.minecraft.util.math.Quaternion;

public interface EntityMixinInterface extends Rotatable {
    Quaternion scGetQuaternion(float tickDelta);
}
