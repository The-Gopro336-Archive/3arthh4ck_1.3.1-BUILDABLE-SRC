/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$MutableBlockPos
 *  net.minecraft.util.math.Vec3i
 */
package me.earth.earthhack.impl.managers.thread.holes;

import java.util.ArrayList;
import java.util.List;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.thread.holes.HoleObserver;
import me.earth.earthhack.impl.managers.thread.holes.IHoleManager;
import me.earth.earthhack.impl.util.math.geocache.Sphere;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.HoleUtil;
import me.earth.earthhack.impl.util.thread.SafeRunnable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class HoleRunnable
implements SafeRunnable,
Globals {
    private final IHoleManager manager;
    private final List<BlockPos> safe;
    private final List<BlockPos> unsafe;
    private final List<BlockPos> longOnes;
    private final List<BlockPos> bigHoles;
    private final double holeRange;
    private final int longs;
    private final int big;
    private final int safes;
    private final int unsafes;

    public HoleRunnable(IHoleManager manager, HoleObserver observer) {
        this(manager, observer.getRange(), observer.getSafeHoles(), observer.getUnsafeHoles(), observer.get2x1Holes(), observer.get2x2Holes());
    }

    public HoleRunnable(IHoleManager manager, double holeRange, int safe, int unsafe, int longs, int big) {
        this.manager = manager;
        this.holeRange = holeRange;
        this.safes = safe;
        this.big = big;
        this.unsafes = unsafe;
        this.longs = longs;
        this.safe = new ArrayList<BlockPos>(safe);
        this.unsafe = new ArrayList<BlockPos>(unsafe);
        this.longOnes = new ArrayList<BlockPos>(longs);
        this.bigHoles = new ArrayList<BlockPos>(big);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void runSafely() {
        try {
            BlockPos middle = PositionUtil.getPosition();
            int mX = middle.getX();
            int mY = middle.getY();
            int mZ = middle.getZ();
            int maxRadius = Sphere.getRadius(this.holeRange);
            BlockPos.MutableBlockPos mPos = new BlockPos.MutableBlockPos();
            for (int i = 0; i < maxRadius; ++i) {
                Vec3i vec3i = Sphere.get(i);
                mPos.setPos(mX + vec3i.getX(), mY + vec3i.getY(), mZ + vec3i.getZ());
                boolean done = true;
                if (this.safe.size() < this.safes || this.unsafe.size() < this.unsafes) {
                    done = false;
                    boolean[] isHole = HoleUtil.isHole((BlockPos)mPos, true);
                    if (isHole[0]) {
                        if (isHole[1]) {
                            this.safe.add(mPos.toImmutable());
                            continue;
                        }
                        this.unsafe.add(mPos.toImmutable());
                        continue;
                    }
                }
                if (this.longOnes.size() < this.longs) {
                    done = false;
                    if (HoleUtil.is2x1(mPos.toImmutable())) {
                        this.longOnes.add(mPos.toImmutable());
                        continue;
                    }
                }
                if (this.bigHoles.size() < this.big) {
                    done = false;
                    if (HoleUtil.is2x2Partial(mPos.toImmutable())) {
                        this.bigHoles.add(mPos.toImmutable());
                        continue;
                    }
                }
                if (done) break;
            }
            this.manager.setSafe(this.safe);
            this.manager.setUnsafe(this.unsafe);
            this.manager.setLongHoles(this.longOnes);
            this.manager.setBigHoles(this.bigHoles);
        }
        finally {
            this.manager.setFinished();
        }
    }
}

