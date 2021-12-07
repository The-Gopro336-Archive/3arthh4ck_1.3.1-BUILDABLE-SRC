package me.earth.earthhack.impl.util.math;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MathUtil {
    public static int clamp(int num, int min, int max) {
        return num < min ? min : Math.min(num, max);
    }

    public static float clamp(float num, float min, float max) {
        return num < min ? min : Math.min(num, max);
    }

    public static double clamp(double num, double min, double max) {
        return num < min ? min : Math.min(num, max);
    }

    public static long clamp(long num, long min, long max) {
        return num < min ? min : Math.min(num, max);
    }

    public static BigDecimal clamp(BigDecimal num, BigDecimal min, BigDecimal max) {
        return MathUtil.smallerThan(num, min) ? min : (MathUtil.biggerThan(num, max) ? max : num);
    }

    public static boolean biggerThan(BigDecimal bigger, BigDecimal than) {
        return bigger.compareTo(than) > 0;
    }

    public static boolean equal(BigDecimal bd1, BigDecimal bd2) {
        return bd1.compareTo(bd2) == 0;
    }

    public static boolean smallerThan(BigDecimal smaller, BigDecimal than) {
        return smaller.compareTo(than) < 0;
    }

    public static long squareToLong(int i) {
        return (long)i * (long)i;
    }

    public static int square(int i) {
        return i * i;
    }

    public static float square(float i) {
        return i * i;
    }

    public static double square(double i) {
        return i * i;
    }

    public static double simplePow(double number, int power) {
        if (power == 0) {
            return 1.0;
        }
        if (power < 0) {
            return 1.0 / MathUtil.simplePow(number, power * -1);
        }
        double result = number;
        for (int i = 1; i < power; ++i) {
            result *= number;
        }
        return result;
    }

    public static double round(double value, int places) {
        return places < 0 ? value : new BigDecimal(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
    }

    public static float round(float value, int places) {
        return places < 0 ? value : new BigDecimal(value).setScale(places, RoundingMode.HALF_UP).floatValue();
    }

    public static float round(float value, int places, float min, float max) {
        return MathHelper.clamp((float)(places < 0 ? value : new BigDecimal(value).setScale(places, RoundingMode.HALF_UP).floatValue()), (float)min, (float)max);
    }

    public static float rad(float angle) {
        return (float)((double)angle * Math.PI / 180.0);
    }

    public static double degree(double angle) {
        return angle / Math.PI * 180.0;
    }

    public static double angle(Vec3d vec3d, Vec3d other) {
        double lengthSq = vec3d.lengthVector() * other.lengthVector();
        if (lengthSq < 1.0E-4) {
            return 0.0;
        }
        double dot = vec3d.dotProduct(other);
        double arg = dot / lengthSq;
        if (arg > 1.0) {
            return 0.0;
        }
        if (arg < -1.0) {
            return 180.0;
        }
        return Math.acos(arg) * 180.0 / Math.PI;
    }

    public static Vec3d fromTo(Vec3d from, Vec3d to) {
        return MathUtil.fromTo(from.x, from.y, from.z, to);
    }

    public static Vec3d fromTo(Vec3d from, double x, double y, double z) {
        return MathUtil.fromTo(from.x, from.y, from.z, x, y, z);
    }

    public static Vec3d fromTo(double x, double y, double z, Vec3d to) {
        return MathUtil.fromTo(x, y, z, to.x, to.y, to.z);
    }

    public static Vec3d fromTo(double x, double y, double z, double x2, double y2, double z2) {
        return new Vec3d(x2 - x, y2 - y, z2 - z);
    }

    public static double distance2D(Vec3d from, Vec3d to) {
        double x = to.x - from.x;
        double z = to.z - from.z;
        return Math.sqrt(x * x + z * z);
    }

    public static EnumFacing[] getRotated(EnumFacing facing) {
        switch (facing) {
            case DOWN: 
            case UP: {
                break;
            }
            case NORTH: 
            case SOUTH: {
                return new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST};
            }
            case WEST: 
            case EAST: {
                return new EnumFacing[]{EnumFacing.NORTH, EnumFacing.SOUTH};
            }
        }
        return new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN};
    }

    public static int getRandomInRange(int min, int max) {
        return min + new Random().nextInt(max - min);
    }

    public static double getRandomInRange(double min, double max) {
        double shifted;
        Random random = new Random();
        double range = max - min;
        double scaled = random.nextDouble() * range;
        if (scaled > max) {
            scaled = max;
        }
        if ((shifted = scaled + min) > max) {
            shifted = max;
        }
        return shifted;
    }

    public static float getRandomInRange(float min, float max) {
        float shifted;
        Random random = new Random();
        float range = max - min;
        float scaled = random.nextFloat() * range;
        if (scaled > max) {
            scaled = max;
        }
        if ((shifted = scaled + min) > max) {
            shifted = max;
        }
        return shifted;
    }
}
