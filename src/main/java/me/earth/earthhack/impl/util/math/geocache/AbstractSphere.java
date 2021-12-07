package me.earth.earthhack.impl.util.math.geocache;

import java.util.Collection;
import me.earth.earthhack.impl.util.math.geocache.AbstractGeoCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public abstract class AbstractSphere
extends AbstractGeoCache {
    private final double r;

    public AbstractSphere(int expectedSize, int indicesSize, double radius) {
        super(expectedSize, indicesSize);
        this.r = radius;
    }

    protected abstract Collection<BlockPos> sorter(BlockPos var1);

    @Override
    protected void fill(Vec3i[] cache, int[] indices) {
        BlockPos pos = BlockPos.ORIGIN;
        Collection<BlockPos> positions = this.sorter(pos);
        double rSquare = this.r * this.r;
        int x = pos.getX() - (int)this.r;
        while ((double)x <= (double)pos.getX() + this.r) {
            int z = pos.getZ() - (int)this.r;
            while ((double)z <= (double)pos.getZ() + this.r) {
                int y = pos.getY() - (int)this.r;
                while ((double)y < (double)pos.getY() + this.r) {
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
        if (positions.size() != cache.length) {
            throw new IllegalStateException("Unexpected Size for Sphere: " + positions.size() + ", expected " + cache.length + "!");
        }
        int i = 0;
        int currentDistance = 0;
        for (BlockPos off : positions) {
            if (Math.sqrt(pos.distanceSq(off)) > (double)currentDistance) {
                indices[currentDistance++] = i;
            }
            cache[i++] = off;
        }
        if (currentDistance != indices.length - 1) {
            throw new IllegalStateException("Sphere Indices not initialized!");
        }
        indices[indices.length - 1] = cache.length;
    }
}
