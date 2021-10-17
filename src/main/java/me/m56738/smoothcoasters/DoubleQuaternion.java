package me.m56738.smoothcoasters;

import net.minecraft.client.util.math.Vector3d;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public final class DoubleQuaternion {
    private static final DoubleQuaternion tempA = new DoubleQuaternion();
    private static final DoubleQuaternion tempB = new DoubleQuaternion();

    private double x;
    private double y;
    private double z;
    private double w;

    public DoubleQuaternion(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public DoubleQuaternion() {
        this(0, 0, 0, 1);
    }

    public DoubleQuaternion(Quaternion quaternion) {
        this(quaternion.getX(), quaternion.getY(), quaternion.getZ(), quaternion.getW());
    }

    private static void lerp(DoubleQuaternion result, DoubleQuaternion from, DoubleQuaternion to, double t) {
        lerp(result, from, to, 1 - t, t);
    }

    private static void lerp(DoubleQuaternion result, DoubleQuaternion from, DoubleQuaternion to, double t0, double t1) {
        result.set(
                t0 * from.x + t1 * to.x, t0 * from.y + t1 * to.y,
                t0 * from.z + t1 * to.z, t0 * from.w + t1 * to.w
        );
        result.normalize();
    }

    public static void slerp(DoubleQuaternion result, DoubleQuaternion from, DoubleQuaternion to, double t) {
        double dot = from.dot(to);

        if (dot >= 0) {
            tempA.set(
                    to.x, to.y,
                    to.z, to.w
            );
        } else {
            dot = -dot;
            tempA.set(
                    -to.x, -to.y,
                    -to.z, -to.w
            );
        }

        if (dot >= 0.95) {
            lerp(result, from, tempA, t);
            return;
        }

        double a = Math.acos(dot);
        double d = 1 / Math.sin(a);
        double t0 = d * Math.sin(a * (1 - t));
        double t1 = d * Math.sin(a * t);
        lerp(result, from, tempA, (float) t0, (float) t1);
    }

    public static EulerAngle slerpToEuler(DoubleQuaternion from, DoubleQuaternion to, float t) {
        slerp(tempB, from, to, t);
        return tempB.toEuler();
    }

    public void toQuaternion(Quaternion result) {
        result.set((float) x, (float) y, (float) z, (float) w);
    }

    public void conjugate() {
        x = -x;
        y = -y;
        z = -z;
    }

    public void rotateX(double angle) {
        double r = Math.toRadians(angle) / 2;
        double c = Math.cos(r);
        double s = Math.sin(r);
        set(
                x * c + w * s,
                y * c + z * s,
                z * c - y * s,
                w * c - x * s
        );
        normalize();
    }

    public void rotateY(double angle) {
        double r = Math.toRadians(angle) / 2;
        double c = Math.cos(r);
        double s = Math.sin(r);
        set(
                x * c - z * s,
                y * c + w * s,
                z * c + x * s,
                w * c - y * s
        );
        normalize();
    }

    public void rotateZ(double angle) {
        double r = Math.toRadians(angle) / 2;
        double c = Math.cos(r);
        double s = Math.sin(r);
        set(
                x * c + y * s,
                y * c - x * s,
                z * c + w * s,
                w * c - z * s
        );
        normalize();
    }

    public void normalize() {
        double f = 1 / Math.sqrt(dot(this));
        x *= f;
        y *= f;
        z *= f;
        w *= f;
    }

    public void set(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public void setIdentity() {
        set(0, 0, 0, 1);
    }

    public void set(DoubleQuaternion other) {
        set(other.x, other.y, other.z, other.w);
    }

    public void set(Quaternion other) {
        set(other.getX(), other.getY(), other.getZ(), other.getW());
    }

    public void set(EulerAngle angle) {
        setIdentity();
        rotateZ(-angle.getRoll());
        rotateY(-angle.getYaw());
        rotateX(angle.getPitch());
    }

    public double dot(DoubleQuaternion other) {
        return x * other.x + y * other.y + z * other.z + w * other.w;
    }

    public Vector3d getForwardVector() {
        return new Vector3d(
                2 * (x * z + y * w),
                2 * (y * z - x * w),
                1 + 2 * (-x * x - y * y)
        );
    }

    public Vector3d getUpVector() {
        return new Vector3d(
                2 * (x * y - z * w),
                1 + 2 * (-x * x - z * z),
                2 * (y * z + x * w)
        );
    }

    public EulerAngle toEuler() {
        double rx = 1 - 2 * (y * y + z * z);
        double ry = 2 * (x * y + z * w);
        double rz = 2 * (x * z - y * w);
        double uz = 2 * (y * z + x * w);
        double fz = 1 - 2 * (x * x + y * y);

        if (Math.abs(rz) < 1 - 1e-6) {
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
                    sign * -2 * (float) Math.toDegrees(Math.atan2(x, w))
            );
        }
    }

    public Vec3d transform(Vec3d point) {
        double px = point.x;
        double py = point.y;
        double pz = point.z;
        return new Vec3d(
                px + 2 * (px * (-y * y - z * z) + py * (x * y - z * w) + pz * (x * z + y * w)),
                py + 2 * (px * (x * y + z * w) + py * (-x * x - z * z) + pz * (y * z - x * w)),
                pz + 2 * (px * (x * z - y * w) + py * (y * z + x * w) + pz * (-x * x - y * y))
        );
    }

    // Accessors

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getW() {
        return w;
    }

    public void setW(double w) {
        this.w = w;
    }

    // Utilities

    public static float getYaw(Vector3d forward, Vector3d up) {
        float yaw;
        if (forward.y < -0.995) {
            yaw = (float) Math.toDegrees(Math.atan2(-up.x, up.z));
        } else if (forward.y > 0.995) {
            yaw = (float) Math.toDegrees(Math.atan2(up.x, -up.z));
        } else {
            yaw = (float) Math.toDegrees(Math.atan2(-forward.x, forward.z));
        }
        return yaw;
    }

    public static float getPitch(Vector3d forward, Vector3d up) {
        double forwardXZ = Math.sqrt(forward.x * forward.x + forward.z * forward.z);
        return (float) Math.toDegrees(Math.atan2(-forward.y, forwardXZ));
    }
}
