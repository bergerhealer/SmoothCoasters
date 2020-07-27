package me.m56738.smoothcoasters;

import net.minecraft.util.math.EulerAngle;

public class AnimatedPose {
    private final DoubleQuaternion previous = new DoubleQuaternion();
    private final DoubleQuaternion target = new DoubleQuaternion();
    private final DoubleQuaternion lerp = new DoubleQuaternion();
    public EulerAngle targetEuler = new EulerAngle(0, 0, 0);
    private int lerpTicks;
    private boolean first = true;

    public void set(EulerAngle angle) {
        if (first) {
            targetEuler = angle;
            target.set(angle);
            previous.set(target);
            lerp.set(target);
            first = false;
        } else {
            lerpTicks = 3;
            targetEuler = angle;
            target.set(angle);
        }
    }

    public void tick() {
        previous.set(lerp);
        if (lerpTicks > 1) {
            DoubleQuaternion.slerp(lerp, lerp, target, 1f / lerpTicks);
            lerpTicks--;
        } else {
            lerp.set(target);
            lerpTicks = 0;
        }
    }

    public EulerAngle lerp(float t) {
        return DoubleQuaternion.slerpToEuler(previous, lerp, t);
    }
}
