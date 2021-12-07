package me.earth.earthhack.impl.modules.player.automine.util;

import java.util.concurrent.Future;
import me.earth.earthhack.impl.modules.player.automine.util.IConstellation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public interface IAutomine {
    public boolean isValid(IBlockState var1);

    public void offer(IConstellation var1);

    public void attackPos(BlockPos var1);

    public void setCurrent(BlockPos var1);

    public BlockPos getCurrent();

    public void setFuture(Future<?> var1);

    public float getMinDmg();

    public float getMaxSelfDmg();

    public double getBreakTrace();

    public boolean getNewVEntities();

    public boolean shouldMineObby();

    public boolean isSuicide();

    public boolean canBigCalcsBeImproved();
}
