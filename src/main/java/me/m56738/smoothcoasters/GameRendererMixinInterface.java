package me.m56738.smoothcoasters;

import net.minecraft.entity.Entity;

public interface GameRendererMixinInterface extends Rotatable {
    void scUpdateRotation(Entity entity);

    void scLoadLocalRotation(Entity entity);

    void scApplyLocalRotation(Entity entity);

    void scSetRotationMode(RotationMode mode);
}
