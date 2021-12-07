package me.earth.earthhack.impl.managers.thread.holes;

import java.util.List;
import net.minecraft.util.math.BlockPos;

public interface IHoleManager {
    public void setSafe(List<BlockPos> var1);

    public void setUnsafe(List<BlockPos> var1);

    public void setLongHoles(List<BlockPos> var1);

    public void setBigHoles(List<BlockPos> var1);

    public void setFinished();
}
