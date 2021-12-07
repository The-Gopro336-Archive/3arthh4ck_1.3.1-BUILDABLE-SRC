package me.earth.earthhack.impl.modules.player.automine.util;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.player.automine.util.IAutomine;
import me.earth.earthhack.impl.modules.player.automine.util.IConstellation;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.mine.MineUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.states.BlockStateHelper;
import me.earth.earthhack.impl.util.minecraft.blocks.states.IBlockStateHelper;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;

public class BigConstellation
implements IConstellation,
Globals {
    private final IBlockStateHelper helper;
    private final IAutomine automine;
    private final BlockPos[] positions;
    private final IBlockState[] states;
    private final EntityPlayer target;
    private int blockStateChanges;
    private boolean valid;

    public BigConstellation(IAutomine automine, BlockPos[] positions, IBlockState[] states, EntityPlayer target) {
        this.automine = automine;
        this.positions = positions;
        this.states = states;
        this.target = target;
        this.valid = true;
        this.helper = new BlockStateHelper();
        this.helper.addBlockState(positions[0], Blocks.OBSIDIAN.getDefaultState());
        for (int i = 1; i < positions.length; ++i) {
            this.helper.addAir(positions[i]);
        }
    }

    @Override
    public void update(IAutomine automine) {
        int i;
        this.valid = false;
        BlockPos attackPos = null;
        for (i = 0; i < this.states.length; ++i) {
            this.states[i] = BigConstellation.mc.world.getBlockState(this.positions[i]);
            if (i == 0 && (this.states[0].getBlock() == Blocks.OBSIDIAN || this.states[0].getBlock() == Blocks.BEDROCK || this.states[0].func_185904_a().isReplaceable())) {
                if (!this.positions[0].equals(automine.getCurrent())) continue;
                automine.setCurrent(null);
                continue;
            }
            if (i != 0 && this.states[i].getBlock() == Blocks.OBSIDIAN && !automine.shouldMineObby()) {
                return;
            }
            if (this.states[i].getBlock() != Blocks.AIR) {
                if (!MineUtil.canBreak(this.states[i], this.positions[i])) {
                    return;
                }
                attackPos = this.positions[i];
                this.valid = true;
                continue;
            }
            if (!this.positions[i].equals(automine.getCurrent())) continue;
            automine.setCurrent(null);
        }
        if (!this.valid) {
            return;
        }
        for (i = 1; i < this.positions.length; ++i) {
            for (Entity entity : BigConstellation.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(this.positions[i]))) {
                if (entity == null || entity instanceof EntityItem || EntityUtil.isDead(entity)) continue;
                this.valid = false;
                return;
            }
            if (automine.getNewVEntities()) break;
        }
        BlockPos pos = this.positions[0];
        if (this.states[0].getBlock() != Blocks.OBSIDIAN && this.states[0].getBlock() != Blocks.BEDROCK) {
            for (Entity entity : BigConstellation.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
                if (entity == null || EntityUtil.isDead(entity) || !entity.preventEntitySpawning || entity instanceof EntityItem) continue;
                this.valid = false;
                return;
            }
        }
        if (RotationUtil.getRotationPlayer().getDistanceSq((float)pos.getX() + 0.5f, pos.getY() + 1, (float)pos.getZ() + 0.5f) >= automine.getBreakTrace() && !RayTraceUtil.canBeSeen(new Vec3d((float)pos.getX() + 0.5f, pos.getY() + 1, (float)pos.getZ() + 0.5f), (Entity)RotationUtil.getRotationPlayer())) {
            this.valid = false;
            return;
        }
        float self = DamageUtil.calculate((float)pos.getX() + 0.5f, pos.getY() + 1, (float)pos.getZ() + 0.5f, RotationUtil.getRotationPlayer().getEntityBoundingBox(), RotationUtil.getRotationPlayer(), this.helper, true);
        if (!automine.isSuicide() && self > automine.getMaxSelfDmg()) {
            this.valid = false;
            return;
        }
        if (this.target == null) {
            for (EntityPlayer player : BigConstellation.mc.world.playerEntities) {
                float d;
                if (player == null || EntityUtil.isDead(player) || Managers.FRIENDS.contains(player) || player.getDistanceSq(pos) > 144.0 || !((d = DamageUtil.calculate((float)pos.getX() + 0.5f, pos.getY() + 1, (float)pos.getZ() + 0.5f, player.getEntityBoundingBox(), player, this.helper, true)) >= automine.getMinDmg())) continue;
                if (automine.getCurrent() == null) {
                    automine.attackPos(attackPos);
                }
                return;
            }
        } else if (!EntityUtil.isDead(this.target) && DamageUtil.calculate((float)pos.getX() + 0.5f, pos.getY() + 1, (float)pos.getZ() + 0.5f, this.target.getEntityBoundingBox(), this.target, this.helper, true) >= automine.getMinDmg()) {
            if (automine.getCurrent() == null) {
                automine.attackPos(attackPos);
            }
            return;
        }
        this.valid = false;
    }

    @Override
    public boolean isAffected(BlockPos pos, IBlockState state) {
        for (BlockPos position : this.positions) {
            if (!position.equals(pos)) continue;
            if (position.equals(this.automine.getCurrent())) {
                this.automine.setCurrent(null);
            }
            ++this.blockStateChanges;
            return false;
        }
        return false;
    }

    @Override
    public boolean isValid(IBlockAccess world, boolean checkPlayerState) {
        return (double)this.blockStateChanges < (double)this.positions.length * 2.25 && this.valid;
    }

    @Override
    public boolean cantBeImproved() {
        return !this.automine.canBigCalcsBeImproved();
    }
}
