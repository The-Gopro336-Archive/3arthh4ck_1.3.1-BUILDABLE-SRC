/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 *  org.apache.logging.log4j.Logger
 */
package me.earth.earthhack.impl.util.math.geocache;

import java.util.TreeSet;
import me.earth.earthhack.impl.util.math.MathUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.apache.logging.log4j.Logger;

public class Sphere {
    private static final Vec3i[] SPHERE = new Vec3i[4187707];
    private static final int[] INDICES = new int[101];

    private Sphere() {
        throw new AssertionError();
    }

    public static int getRadius(double radius) {
        return INDICES[MathUtil.clamp((int)Math.ceil(radius), 0, INDICES.length)];
    }

    public static Vec3i get(int index) {
        return SPHERE[index];
    }

    public static int getLength() {
        return SPHERE.length;
    }

    public static void cacheSphere(Logger logger) {
        logger.info("Caching Sphere...");
        long time = System.currentTimeMillis();
        BlockPos pos = BlockPos.ORIGIN;
        TreeSet<BlockPos> positions = new TreeSet<BlockPos>((o, p) -> {
            if (o.equals(p)) {
                return 0;
            }
            int compare = Double.compare(pos.distanceSq((Vec3i)o), pos.distanceSq((Vec3i)p));
            if (compare == 0) {
                compare = Integer.compare(Math.abs(o.getX()) + Math.abs(o.getY()) + Math.abs(o.getZ()), Math.abs(p.getX()) + Math.abs(p.getY()) + Math.abs(p.getZ()));
            }
            return compare == 0 ? 1 : compare;
        });
        double r = 100.0;
        double rSquare = r * r;
        int x = pos.getX() - (int)r;
        while ((double)x <= (double)pos.getX() + r) {
            int z = pos.getZ() - (int)r;
            while ((double)z <= (double)pos.getZ() + r) {
                int y = pos.getY() - (int)r;
                while ((double)y < (double)pos.getY() + r) {
                    double dist = (pos.getX() - x) * (pos.getX() - x) + (pos.getZ() - z) * (pos.getZ() - z) + (pos.getY() - y) * (pos.getY() - y);
                    if (dist < rSquare) {
                        positions.add(new BlockPos(x, y, z));
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }
        if (positions.size() != SPHERE.length) {
            throw new IllegalStateException("Unexpected Size for Sphere: " + positions.size() + ", expected " + SPHERE.length + "!");
        }
        int i = 0;
        int currentDistance = 0;
        for (BlockPos off : positions) {
            if (Math.sqrt(pos.distanceSq((Vec3i)off)) > (double)currentDistance) {
                Sphere.INDICES[currentDistance++] = i;
            }
            Sphere.SPHERE[i++] = off;
        }
        if (currentDistance != INDICES.length - 1) {
            throw new IllegalStateException("Sphere Indices not initialized!");
        }
        Sphere.INDICES[Sphere.INDICES.length - 1] = SPHERE.length;
        if (SPHERE[SPHERE.length - 1].getX() == Integer.MAX_VALUE) {
            throw new IllegalStateException("Sphere wasn't filled!");
        }
        time = System.currentTimeMillis() - time;
        logger.info("Cached sphere in " + time + "ms.");
    }

    static {
        Sphere.SPHERE[Sphere.SPHERE.length - 1] = new Vec3i(Integer.MAX_VALUE, 0, 0);
    }
}

