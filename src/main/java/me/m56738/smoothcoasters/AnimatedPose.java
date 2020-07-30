package me.m56738.smoothcoasters;

import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Quaternion;

public class AnimatedPose {
    private final DoubleQuaternion previous = new DoubleQuaternion();
    private final DoubleQuaternion target = new DoubleQuaternion();
    private final DoubleQuaternion lerp = new DoubleQuaternion();
    public EulerAngle targetEuler = new EulerAngle(0, 0, 0);
    private int lerpTicks;
    private boolean first = true;

    public void set(EulerAngle angle, int ticks) {
        targetEuler = angle;
        target.set(angle);
        lerp(ticks);
    }

    public void set(Quaternion rotation, int ticks) {
        targetEuler = null;
        target.set(rotation);
        lerp(ticks);
    }

    private void lerp(int ticks) {
        if (first) {
            previous.set(target);
            lerp.set(target);
            first = false;
        } else {
            lerpTicks = ticks;
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

    public void calculate(DoubleQuaternion result, float t) {
        DoubleQuaternion.slerp(result, previous, lerp, t);
    }

    public EulerAngle calculateEuler(float t) {
        return DoubleQuaternion.slerpToEuler(previous, lerp, t);
    }
}
