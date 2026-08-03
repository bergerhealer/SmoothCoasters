package me.m56738.smoothcoasters.common;

import org.joml.Quaternionfc;

public interface CameraMixinInterface {
    void smoothcoasters$setCameraRotation(Quaternionfc rotation);

    void smoothcoasters$resetCameraRotation();
}
