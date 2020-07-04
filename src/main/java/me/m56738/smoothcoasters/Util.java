package me.m56738.smoothcoasters;

import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

@SuppressWarnings("WeakerAccess")
public class Util {
    private static final Quaternion temp = new Quaternion(0, 0, 0, 1);
    private static final Quaternion tempA = new Quaternion(0, 0, 0, 1);
    private static final Quaternion tempB = new Quaternion(0, 0, 0, 1);
    private static final Quaternion tempC = new Quaternion(0, 0, 0, 1);

    private Util() {
    }

    public static EulerAngle lerp(EulerAngle a, EulerAngle b, float t) {
        if (a == null || a.equals(b)) {
            return b;
        }

        toQuaternion(tempA, a);
        toQuaternion(tempB, b);
        slerp(tempC, t, tempA, tempB);

        return toEuler(tempC);
    }

    public static void rotateX(Quaternion quaternion, float angle) {
        float r = (float) Math.toRadians(angle) / 2;
        float c = MathHelper.cos(r);
        float s = MathHelper.sin(r);
        quaternion.set(
                quaternion.getX() * c + quaternion.getW() * s,
                quaternion.getY() * c + quaternion.getZ() * s,
                quaternion.getZ() * c - quaternion.getY() * s,
                quaternion.getW() * c - quaternion.getX() * s
        );
    }

    public static void rotateY(Quaternion quaternion, float angle) {
        float r = (float) Math.toRadians(angle) / 2;
        float c = MathHelper.cos(r);
        float s = MathHelper.sin(r);
        quaternion.set(
                quaternion.getX() * c - quaternion.getZ() * s,
                quaternion.getY() * c + quaternion.getW() * s,
                quaternion.getZ() * c + quaternion.getX() * s,
                quaternion.getW() * c - quaternion.getY() * s
        );
    }

    public static void rotateZ(Quaternion quaternion, float angle) {
        float r = (float) Math.toRadians(angle) / 2;
        float c = MathHelper.cos(r);
        float s = MathHelper.sin(r);
        quaternion.set(
                quaternion.getX() * c + quaternion.getY() * s,
                quaternion.getY() * c - quaternion.getX() * s,
                quaternion.getZ() * c + quaternion.getW() * s,
                quaternion.getW() * c - quaternion.getZ() * s
        );
    }

    public static void toQuaternion(Quaternion result, EulerAngle euler) {
        result.set(0, 0, 0, 1);
        rotateZ(result, -euler.getRoll());
        rotateY(result, -euler.getYaw());
        rotateX(result, euler.getPitch());
    }

    public static EulerAngle toEuler(Quaternion quaternion) {
        float qx = quaternion.getX();
        float qy = quaternion.getY();
        float qz = quaternion.getZ();
        float qw = quaternion.getW();

        float rx = 1 - 2 * (qy * qy + qz * qz);
        float ry = 2 * (qx * qy + qz * qw);
        float rz = 2 * (qx * qz - qy * qw);
        float uz = 2 * (qy * qz + qx * qw);
        float fz = 1 - 2 * (qx * qx + qy * qy);

        if (Math.abs(rz) < 1 - 1e-6f) {
            return new EulerAngle(
                    (float) Math.toDegrees(Math.atan2(uz, fz)),
                    (float) Math.toDegrees(Math.asin(rz)),
                    (float) Math.toDegrees(Math.atan2(-ry, rx))
            );
        } else {
            float sign = rz < 0 ? -1 : 1;
            return new EulerAngle(
                    0,
                    sign * 90,
                    sign * -2 * (float) Math.toDegrees(Math.atan2(qx, qw))
            );
        }
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

    public static void lerp(Quaternion result, float t, Quaternion from, Quaternion to) {
        lerp(result, 1 - t, t, from, to);
    }

    public static void lerp(Quaternion result, float t0, float t1, Quaternion from, Quaternion to) {
        result.set(
                t0 * from.getX() + t1 * to.getX(), t0 * from.getY() + t1 * to.getY(),
                t0 * from.getZ() + t1 * to.getZ(), t0 * from.getW() + t1 * to.getW()
        );
    }

    public static void slerp(Quaternion result, float t, Quaternion from, Quaternion to) {
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
            lerp(result, t, from, temp);
            return;
        }

        double a = Math.acos(dot);
        double d = 1 / Math.sin(a);
        double t0 = d * Math.sin(a * (1 - t));
        double t1 = d * Math.sin(a * t);
        lerp(result, (float) t0, (float) t1, from, temp);
    }
}
