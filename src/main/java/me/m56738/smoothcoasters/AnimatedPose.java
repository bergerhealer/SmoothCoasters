package me.m56738.smoothcoasters;

import net.minecraft.util.math.EulerAngle;

public class AnimatedPose {
    public EulerAngle previous;
    public EulerAngle target = new EulerAngle(0, 0, 0);

    private EulerAngle lerp = new EulerAngle(0, 0, 0);
    private int lerpTicks;

    public void markChanged() {
        lerpTicks = 3;
    }

    public void tick() {
        if (lerpTicks > 0) {
            lerp = Util.lerp(lerp, target, 1f / lerpTicks);
            lerpTicks--;
        } else {
            lerp = target;
        }
    }

    public EulerAngle lerp(float t) {
        return Util.lerp(previous, lerp, t);
    }
}
