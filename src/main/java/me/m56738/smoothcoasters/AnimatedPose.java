package me.m56738.smoothcoasters;

import net.minecraft.core.Rotations;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public class AnimatedPose {
    private final Quaternionf previous = new Quaternionf();
    private final Quaternionf target = new Quaternionf();
    private final Quaternionf lerp = new Quaternionf();
    private final Quaternionf current = new Quaternionf();
    public Rotations targetEuler = new Rotations(0, 0, 0);
    private int lerpTicks;
    private boolean first = true;

    public AnimatedPose() {
    }

    public AnimatedPose(Rotations eulerAngle) {
        set(eulerAngle, 0);
    }

    private static boolean isNotIdentity(Quaternionf q) {
        return !q.equals(0, 0, 0, 1);
    }

    public boolean isActive() {
        return isNotIdentity(previous) || isNotIdentity(lerp) || isNotIdentity(target);
    }

    public void set(Rotations angle, int ticks) {
        targetEuler = angle;
        target.rotationZYX(
                (float) Math.toRadians(-angle.z()),
                (float) Math.toRadians(-angle.y()),
                (float) Math.toRadians(angle.x())
        );
        lerp(ticks);
    }

    public void set(Quaternionfc rotation, int ticks) {
        targetEuler = null;
        target.set(rotation);
        lerp(ticks);
    }

    private void lerp(int ticks) {
        if (ticks < 0) {
            ticks = 1;
        }

        if (ticks == 1) {
            lerp.set(target);
        } else if (ticks == 0) {
            lerp.set(target);
            previous.set(target);
        }

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
            lerp.slerp(target, 1f / lerpTicks);
            lerpTicks--;
        } else {
            if (lerpTicks == 0) {
                previous.set(target);
            }
            lerp.set(target);
            lerpTicks = 0;
        }
    }

    public void calculate(Quaternionf result, float t) {
        previous.slerp(lerp, t, result);
    }

    public Rotations calculateEuler(float t) {
        previous.slerp(lerp, t, current);
        return MathUtil.getEuler(current);
    }
}
