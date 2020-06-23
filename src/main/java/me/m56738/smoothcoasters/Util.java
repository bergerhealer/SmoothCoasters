package me.m56738.smoothcoasters;

import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

public class Util {
    private static final Quaternion temp = new Quaternion(0, 0, 0, 1);

    private Util() {
    }

    public static EulerAngle lerp(EulerAngle a, EulerAngle b, float t) {
        if (a == null) {
            return b;
        }

        return new EulerAngle(
                MathHelper.lerpAngleDegrees(t, a.getPitch(), b.getPitch()),
                MathHelper.lerpAngleDegrees(t, a.getYaw(), b.getYaw()),
                MathHelper.lerpAngleDegrees(t, a.getRoll(), b.getRoll())
        );
    }

    public static void copy(Quaternion to, Quaternion from) {
        to.set(
                from.getX(), from.getY(),
                from.getZ(), from.getW()
        );
    }

    public static double dot(Quaternion a, Quaternion b) {
        return a.getX() * b.getX() + a.getY() * b.getY() + a.getZ() * b.getZ() + a.getW() * b.getW();
    }

    public static void lerp(Quaternion target, float t, Quaternion from, Quaternion to) {
        lerp(target, 1 - t, t, from, to);
    }

    public static void lerp(Quaternion target, float t0, float t1, Quaternion from, Quaternion to) {
        target.set(
                t0 * from.getX() + t1 * to.getX(), t0 * from.getY() + t1 * to.getY(),
                t0 * from.getZ() + t1 * to.getZ(), t0 * from.getW() + t1 * to.getW()
        );
    }

    public static void slerp(Quaternion target, float t, Quaternion from, Quaternion to) {
        double dot = dot(from, to);

        if (dot >= 0) {
            temp.set(
                    to.getX(), to.getY(),
                    to.getZ(), to.getW()
            );
        } else {
            dot = -dot;
            temp.set(
                    -to.getX(), -to.getY(),
                    -to.getZ(), -to.getW()
            );
        }

        if (dot >= 0.95) {
            lerp(target, t, from, temp);
            return;
        }

        double a = Math.acos(dot);
        double d = 1 / Math.sin(a);
        double t0 = d * Math.sin(a * (1 - t));
        double t1 = d * Math.sin(a * t);
        lerp(target, (float) t0, (float) t1, from, temp);
    }
}
