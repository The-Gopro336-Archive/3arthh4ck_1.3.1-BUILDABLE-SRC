/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$MutableBlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 */
package me.earth.earthhack.impl.modules.player.automine;

import java.util.List;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.player.automine.util.BigConstellation;
import me.earth.earthhack.impl.modules.player.automine.util.IAutomine;
import me.earth.earthhack.impl.util.math.BBUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.mine.MineUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.states.BlockStateHelper;
import me.earth.earthhack.impl.util.thread.SafeRunnable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class AutoMineCalc
implements SafeRunnable,
Globals {
    private final IAutomine automine;
    private final List<EntityPlayer> players;
    private final List<Entity> entities;
    private final EntityPlayer target;
    private final float minDamage;
    private final float maxSelf;
    private final double range;
    private final boolean obby;
    private final boolean newVer;
    private final boolean newVEntities;
    private final boolean mineObby;
    private final double breakTrace;
    private final boolean suicide;
    private int mX;
    private int mY;
    private int mZ;

    public AutoMineCalc(IAutomine automine, List<EntityPlayer> players, List<Entity> entities, EntityPlayer target, float minDamage, float maxSelf, double range, boolean obby, boolean newVer, boolean newVEntities, boolean mineObby, double breakTrace, boolean suicide) {
        this.automine = automine;
        this.players = players;
        this.entities = entities;
        this.target = target;
        this.minDamage = minDamage;
        this.maxSelf = maxSelf;
        this.range = range;
        this.obby = obby;
        this.newVer = newVer;
        this.newVEntities = newVEntities;
        this.mineObby = mineObby;
        this.breakTrace = breakTrace;
        this.suicide = suicide;
    }

    @Override
    public void runSafely() throws Throwable {
        BlockPos middle = PositionUtil.getPosition();
        this.mX = middle.getX();
        this.mY = middle.getY();
        this.mZ = middle.getZ();
        BlockPos.MutableBlockPos mPos = new BlockPos.MutableBlockPos();
        int intRange = (int)this.range;
        double rSquare = this.range * this.range;
        double bSquare = this.breakTrace * this.breakTrace;
        float maxDamage = Float.MIN_VALUE;
        BlockStateHelper helper = new BlockStateHelper();
        BigConstellation constellation = null;
        int x = this.mX - intRange;
        while ((double)x <= (double)this.mX + this.range) {
            int z = this.mZ - intRange;
            while ((double)z <= (double)this.mZ + this.range) {
                int y = this.mY - intRange;
                while ((double)y < (double)this.mY + this.range) {
                    block21: {
                        IBlockState upUpState;
                        IBlockState upState;
                        boolean isObbyState;
                        IBlockState state;
                        block22: {
                            if (this.dsq(x, y, z) > rSquare || this.dsq((float)x + 0.5f, y + 1, (float)z + 0.5f) >= bSquare && !RayTraceUtil.canBeSeen(new Vec3d((double)((float)x + 0.5f), (double)y + 2.7, (double)((float)z + 0.5f)), (Entity)RotationUtil.getRotationPlayer())) break block21;
                            mPos.setPos(x, y, z);
                            state = AutoMineCalc.mc.world.getBlockState((BlockPos)mPos);
                            boolean bl = isObbyState = state.getBlock() == Blocks.OBSIDIAN || state.getBlock() == Blocks.BEDROCK;
                            if (!this.obby && !isObbyState || !isObbyState && !state.getMaterial().isReplaceable() && !MineUtil.canBreak(state, (BlockPos)mPos)) break block21;
                            mPos.setY(y + 1);
                            upState = AutoMineCalc.mc.world.getBlockState((BlockPos)mPos);
                            if (upState.getBlock() != Blocks.AIR && !MineUtil.canBreak(upState, (BlockPos)mPos) || upState.getBlock() == Blocks.OBSIDIAN && !this.mineObby || upState.getBlock() != Blocks.AIR && this.dsq(x, y + 1, z) > rSquare) break block21;
                            upUpState = null;
                            if (this.newVer) break block22;
                            mPos.setY(y + 2);
                            upUpState = AutoMineCalc.mc.world.getBlockState((BlockPos)mPos);
                            if (upUpState.getBlock() != Blocks.AIR && !MineUtil.canBreak(upUpState, (BlockPos)mPos) || upUpState.getBlock() == Blocks.OBSIDIAN && !this.mineObby || upUpState.getBlock() != Blocks.AIR && this.dsq(x, y + 2, z) > rSquare) break block21;
                        }
                        boolean bad = false;
                        for (Entity entity : this.entities) {
                            if (entity.preventEntitySpawning && !isObbyState) {
                                mPos.setY(y);
                                if (BBUtil.intersects(entity.getEntityBoundingBox(), (Vec3i)mPos)) {
                                    bad = true;
                                    break;
                                }
                            }
                            mPos.setY(y + 1);
                            if (BBUtil.intersects(entity.getEntityBoundingBox(), (Vec3i)mPos)) {
                                bad = true;
                                break;
                            }
                            if (this.newVEntities) continue;
                            mPos.setY(y + 2);
                            if (!BBUtil.intersects(entity.getEntityBoundingBox(), (Vec3i)mPos)) continue;
                            bad = true;
                            break;
                        }
                        if (!bad) {
                            helper.clearAllStates();
                            mPos.setY(y);
                            helper.addBlockState((BlockPos)mPos, Blocks.OBSIDIAN.getDefaultState());
                            BlockPos up = new BlockPos(x, y + 1, z);
                            helper.addAir(up);
                            BlockPos upUp = null;
                            if (!this.newVer) {
                                upUp = up.up();
                                helper.addAir(upUp);
                            }
                            float self = DamageUtil.calculate((float)x + 0.5f, y + 1, (float)z + 0.5f, RotationUtil.getRotationPlayer().getEntityBoundingBox(), (EntityLivingBase)RotationUtil.getRotationPlayer(), helper, true);
                            if (this.suicide || !(self > this.maxSelf)) {
                                float damage = Float.MIN_VALUE;
                                if (this.target == null) {
                                    for (EntityPlayer player : this.players) {
                                        float f;
                                        float d;
                                        if (player.getDistanceSq((double)x, (double)y, (double)z) > 144.0 || !((d = DamageUtil.calculate((float)x + 0.5f, y + 1, (float)z + 0.5f, player.getEntityBoundingBox(), (EntityLivingBase)player, helper, true)) > damage)) continue;
                                        damage = d;
                                        if (!(f > this.minDamage)) continue;
                                        break;
                                    }
                                } else {
                                    damage = DamageUtil.calculate((float)x + 0.5f, y + 1, (float)z + 0.5f, this.target.getEntityBoundingBox(), (EntityLivingBase)this.target, helper, true);
                                }
                                if (!(damage < this.minDamage || damage < maxDamage || damage < self)) {
                                    BlockPos[] positions = new BlockPos[this.newVer ? 2 : 3];
                                    positions[0] = mPos.toImmutable();
                                    positions[1] = up;
                                    if (!this.newVer) {
                                        positions[2] = upUp;
                                    }
                                    IBlockState[] states = new IBlockState[this.newVer ? 2 : 3];
                                    states[0] = state;
                                    states[1] = upState;
                                    if (!this.newVer) {
                                        states[2] = upUpState;
                                    }
                                    maxDamage = damage;
                                    constellation = new BigConstellation(this.automine, positions, states, this.target);
                                }
                            }
                        }
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }
        if (constellation != null) {
            BigConstellation finalConstellation = constellation;
            mc.addScheduledTask(() -> {
                this.automine.setFuture(null);
                this.automine.offer(finalConstellation);
            });
        } else {
            mc.addScheduledTask(() -> this.automine.setFuture(null));
        }
    }

    private double dsq(double x, double y, double z) {
        return ((double)this.mX - x) * ((double)this.mX - x) + ((double)this.mZ - z) * ((double)this.mZ - z) + ((double)this.mY - y) * ((double)this.mY - y);
    }
}

