package me.m56738.smoothcoasters;

import net.minecraft.util.math.EulerAngle;

public class AnimatedPose {
    private final DoubleQuaternion previous = new DoubleQuaternion();
    private final DoubleQuaternion target = new DoubleQuaternion();
    private final DoubleQuaternion lerp = new DoubleQuaternion();
    public EulerAngle targetEuler = new EulerAngle(0, 0, 0);
    private int lerpTicks;

    public void set(EulerAngle angle) {
        lerpTicks = 3;
        targetEuler = angle;
        target.set(angle);
    }

    public void tick() {
        previous.set(lerp);
        if (lerpTicks > 0) {
            DoubleQuaternion.slerp(lerp, lerp, target, 1f / lerpTicks);
            lerpTicks--;
        } else {
            lerp.set(target);
        }
    }

    public EulerAngle lerp(float t) {
        return DoubleQuaternion.slerpToEuler(previous, lerp, t);
    }
}
