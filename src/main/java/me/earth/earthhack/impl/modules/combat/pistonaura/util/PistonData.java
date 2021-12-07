/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.modules.combat.pistonaura.util;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.combat.pistonaura.util.PistonStage;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class PistonData
implements Globals {
    private final BlockPos crystalPos;
    private final EntityPlayer target;
    private final BlockPos startPos;
    private final EnumFacing facing;
    private PistonStage[] order;
    private BlockPos redstonePos;
    private BlockPos pistonPos;
    private boolean valid;
    private boolean multi;

    public PistonData(EntityPlayer target, BlockPos crystalPos, EnumFacing facing) {
        this.crystalPos = crystalPos;
        this.target = target;
        this.startPos = PositionUtil.getPosition((Entity)target);
        this.facing = facing;
    }

    public boolean isValid() {
        return this.valid && this.order != null && EntityUtil.isValid((Entity)this.target, 9.0) && this.startPos.equals((Object)PositionUtil.getPosition((Entity)this.target));
    }

    public BlockPos getStartPos() {
        return this.startPos;
    }

    public BlockPos getCrystalPos() {
        return this.crystalPos;
    }

    public EntityPlayer getTarget() {
        return this.target;
    }

    public EnumFacing getFacing() {
        return this.facing;
    }

    public BlockPos getRedstonePos() {
        return this.redstonePos;
    }

    public void setRedstonePos(BlockPos redstonePos) {
        this.redstonePos = redstonePos;
    }

    public BlockPos getPistonPos() {
        return this.pistonPos;
    }

    public void setPistonPos(BlockPos pistonPos) {
        this.pistonPos = pistonPos;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public PistonStage[] getOrder() {
        return this.order;
    }

    public void setOrder(PistonStage[] order) {
        this.order = order;
    }

    public boolean isMulti() {
        return this.multi;
    }

    public void setMulti(boolean multi) {
        this.multi = multi;
    }
}

