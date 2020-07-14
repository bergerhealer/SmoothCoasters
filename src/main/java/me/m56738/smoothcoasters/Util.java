package me.m56738.smoothcoasters;

import net.minecraft.util.math.Quaternion;

@SuppressWarnings("WeakerAccess")
public class Util {
    private static final Quaternion temp = new Quaternion(0, 0, 0, 1);

    private Util() {
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

    public static void lerp(Quaternion result, Quaternion from, Quaternion to, float t) {
        lerp(result, from, to, 1 - t, t);
    }

    public static void lerp(Quaternion result, Quaternion from, Quaternion to, float t0, float t1) {
        result.set(
                t0 * from.getX() + t1 * to.getX(), t0 * from.getY() + t1 * to.getY(),
                t0 * from.getZ() + t1 * to.getZ(), t0 * from.getW() + t1 * to.getW()
        );
        result.normalize();
    }

    public static void slerp(Quaternion result, Quaternion from, Quaternion to, float t) {
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
            lerp(result, from, temp, t);
            return;
        }

        double a = Math.acos(dot);
        double d = 1 / Math.sin(a);
        double t0 = d * Math.sin(a * (1 - t));
        double t1 = d * Math.sin(a * t);
        lerp(result, from, temp, (float) t0, (float) t1);
    }
}
