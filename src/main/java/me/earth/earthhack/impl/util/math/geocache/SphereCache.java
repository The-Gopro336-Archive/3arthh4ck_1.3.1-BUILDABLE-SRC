/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 */
package me.earth.earthhack.impl.util.math.geocache;

import java.util.Collection;
import java.util.TreeSet;
import me.earth.earthhack.impl.util.math.geocache.AbstractSphere;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class SphereCache
extends AbstractSphere {
    private static final SphereCache INSTANCE = new SphereCache();

    private SphereCache() {
        super(4187707, 101, 100.0);
    }

    public static SphereCache getInstance() {
        return INSTANCE;
    }

    @Override
    protected Collection<BlockPos> sorter(BlockPos middle) {
        return new TreeSet<BlockPos>((o, p) -> {
            if (o.equals(p)) {
                return 0;
            }
            int compare = Double.compare(middle.distanceSq((Vec3i)o), middle.distanceSq((Vec3i)p));
            if (compare == 0) {
                compare = Integer.compare(Math.abs(o.getX()) + Math.abs(o.getY()) + Math.abs(o.getZ()), Math.abs(p.getX()) + Math.abs(p.getY()) + Math.abs(p.getZ()));
            }
            return compare == 0 ? 1 : compare;
        });
    }

    static {
        INSTANCE.cache();
    }
}

