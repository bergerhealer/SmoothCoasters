package me.m56738.smoothcoasters;

import net.minecraft.core.Rotations;
import org.joml.Math;
import org.joml.Quaternionfc;
import org.joml.Vector3dc;

public class MathUtil {
    private MathUtil() {
    }

    public static Rotations getEuler(Quaternionfc quaternion) {
        double x = quaternion.x();
        double y = quaternion.y();
        double z = quaternion.z();
        double w = quaternion.w();

        double rx = 1 - 2 * (y * y + z * z);
        double ry = 2 * (x * y + z * w);
        double rz = 2 * (x * z - y * w);
        double uz = 2 * (y * z + x * w);
        double fz = 1 - 2 * (x * x + y * y);

        if (Math.abs(rz) < 1 - 1e-6) {
            return new Rotations(
                    (float) Math.toDegrees(Math.atan2(uz, fz)),
                    (float) Math.toDegrees(Math.asin(rz)),
                    (float) Math.toDegrees(Math.atan2(-ry, rx))
            );
        } else {
            float sign = rz < 0 ? -1 : 1;
            return new Rotations(
                    0,
                    sign * 90,
                    sign * -2 * (float) Math.toDegrees(Math.atan2(x, w))
            );
        }
    }

    public static float getYaw(Vector3dc forward, Vector3dc up) {
        float yaw;
        if (forward.y() < -0.995) {
            yaw = (float) Math.toDegrees(Math.atan2(-up.x(), up.z()));
        } else if (forward.y() > 0.995) {
            yaw = (float) Math.toDegrees(Math.atan2(up.x(), -up.z()));
        } else {
            yaw = (float) Math.toDegrees(Math.atan2(-forward.x(), forward.z()));
        }
        return yaw;
    }

    public static float getPitch(Vector3dc forward) {
        double forwardXZ = Math.sqrt(forward.x() * forward.x() + forward.z() * forward.z());
        return (float) Math.toDegrees(Math.atan2(-forward.y(), forwardXZ));
    }
}
