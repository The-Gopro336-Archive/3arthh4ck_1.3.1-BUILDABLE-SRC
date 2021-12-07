package me.earth.earthhack.impl.util.math.path;

import java.util.List;
import me.earth.earthhack.impl.util.math.path.BlockingEntity;
import me.earth.earthhack.impl.util.math.raytrace.Ray;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public interface Pathable {
    public BlockPos getPos();

    public Entity getFrom();

    public Ray[] getPath();

    public void setPath(Ray ... var1);

    public int getMaxLength();

    public boolean isValid();

    public void setValid(boolean var1);

    public List<BlockingEntity> getBlockingEntities();
}
