/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.util.math.path;

import java.util.ArrayList;
import java.util.List;
import me.earth.earthhack.impl.util.math.path.BlockingEntity;
import me.earth.earthhack.impl.util.math.path.Pathable;
import me.earth.earthhack.impl.util.math.raytrace.Ray;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public class BasePath
implements Pathable {
    private final List<BlockingEntity> blocking = new ArrayList<BlockingEntity>();
    private final int maxLength;
    private final BlockPos pos;
    private final Entity from;
    private boolean valid;
    private Ray[] path;

    public BasePath(Entity from, BlockPos pos, int maxLength) {
        this.from = from;
        this.pos = pos;
        this.maxLength = maxLength;
    }

    @Override
    public BlockPos getPos() {
        return this.pos;
    }

    @Override
    public Entity getFrom() {
        return this.from;
    }

    @Override
    public Ray[] getPath() {
        return this.path;
    }

    @Override
    public void setPath(Ray ... path) {
        this.path = path;
    }

    @Override
    public int getMaxLength() {
        return this.maxLength;
    }

    @Override
    public boolean isValid() {
        return this.valid;
    }

    @Override
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public List<BlockingEntity> getBlockingEntities() {
        return this.blocking;
    }
}

