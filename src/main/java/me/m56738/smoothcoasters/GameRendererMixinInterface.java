package me.m56738.smoothcoasters;

import net.minecraft.entity.Entity;

public interface GameRendererMixinInterface extends Rotatable {
    void scUpdateRotation(Entity entity);

    /**
     * Set the entity to its local yaw/pitch.
     * Called before processing mouse movement.
     */
    void scLoadLocalRotation(Entity entity);

    /**
     * Apply the entities current yaw/pitch as the local yaw/pitch.
     * Set the entity back to the global yaw/pitch.
     * Called after processing mouse movement.
     */
    void scApplyLocalRotation(Entity entity);

    void scSetRotationMode(RotationMode mode);

    void scSetRotationLimit(float minYaw, float maxYaw, float minPitch, float maxPitch);
}
