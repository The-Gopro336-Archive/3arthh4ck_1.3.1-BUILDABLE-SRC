package me.earth.earthhack.impl.util.math.path;

import java.util.Collection;
import java.util.TreeSet;
import me.earth.earthhack.impl.util.math.geocache.AbstractSphere;
import me.earth.earthhack.impl.util.math.path.PathFinder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class PathCache
extends AbstractSphere {
    public PathCache(int expectedSize, int indicesSize, double radius) {
        super(expectedSize, indicesSize, radius);
    }

    @Override
    protected Collection<BlockPos> sorter(BlockPos middle) {
        return new TreeSet<BlockPos>((o, p) -> {
            int zoDiff;
            int yoDiff;
            if (o.equals(p)) {
                return 0;
            }
            int xpDiff = middle.getX() - p.getX();
            int ypDiff = middle.getY() - p.getY();
            int zpDiff = middle.getZ() - p.getZ();
            int xoDiff = middle.getX() - o.getX();
            int compare = Integer.compare(PathFinder.produceOffsets(false, false, xoDiff, yoDiff = middle.getY() - o.getY(), zoDiff = middle.getZ() - o.getZ()).length, PathFinder.produceOffsets(false, false, xpDiff, ypDiff, zpDiff).length);
            if (compare != 0) {
                return compare;
            }
            compare = Double.compare(middle.distanceSq((Vec3i)o), middle.distanceSq((Vec3i)p));
            if (compare == 0) {
                compare = Integer.compare(Math.abs(o.getX()) + Math.abs(o.getY()) + Math.abs(o.getZ()), Math.abs(p.getX()) + Math.abs(p.getY()) + Math.abs(p.getZ()));
            }
            return compare == 0 ? 1 : compare;
        });
    }
}
