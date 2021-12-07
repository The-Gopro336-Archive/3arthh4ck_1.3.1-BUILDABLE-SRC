/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 */
package me.earth.earthhack.impl.modules.combat.selftrap;

import java.util.ArrayList;
import me.earth.earthhack.impl.modules.combat.selftrap.SelfTrap;
import me.earth.earthhack.impl.modules.combat.selftrap.SelfTrapMode;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListener;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyModule;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyUtil;
import me.earth.earthhack.impl.util.helpers.blocks.modes.RayTraceMode;
import me.earth.earthhack.impl.util.helpers.blocks.util.TargetResult;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.path.BasePath;
import me.earth.earthhack.impl.util.math.path.PathFinder;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

final class ListenerSelfTrap
extends ObbyListener<SelfTrap> {
    public ListenerSelfTrap(SelfTrap module) {
        super(module, -9);
    }

    @Override
    protected boolean updatePlaced() {
        if (!((SelfTrap)this.module).autoOff.getValue().booleanValue()) {
            return super.updatePlaced();
        }
        BlockPos p = PositionUtil.getPosition((Entity)RotationUtil.getRotationPlayer());
        if (!p.equals((Object)((SelfTrap)this.module).startPos)) {
            ((SelfTrap)this.module).disable();
            return true;
        }
        if (((SelfTrap)this.module).smartOff.getValue().booleanValue()) {
            for (Vec3i offset : ((SelfTrap)this.module).mode.getValue().getOffsets()) {
                if (ObbyModule.HELPER.getBlockState(p.add(offset)).getBlock() == ((SelfTrap)this.module).mode.getValue().getBlock()) continue;
                return super.updatePlaced();
            }
            ((SelfTrap)this.module).disable();
            return true;
        }
        return super.updatePlaced();
    }

    @Override
    protected TargetResult getTargets(TargetResult result) {
        Vec3i[] closest;
        if (((SelfTrap)this.module).smart.getValue().booleanValue() && ((closest = EntityUtil.getClosestEnemy()) == null || ListenerSelfTrap.mc.player.getDistanceSq((Entity)closest) > (double)MathUtil.square(((SelfTrap)this.module).range.getValue().floatValue()))) {
            return result.setValid(false);
        }
        if (((SelfTrap)this.module).mode.getValue() != SelfTrapMode.Obsidian) {
            for (Vec3i offset : ((SelfTrap)this.module).mode.getValue().getOffsets()) {
                result.getTargets().add(PositionUtil.getPosition((Entity)RotationUtil.getRotationPlayer()).add(offset));
            }
            return result;
        }
        BlockPos pos = PositionUtil.getPosition((Entity)RotationUtil.getRotationPlayer()).up(2);
        if (!ListenerSelfTrap.mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
            return result.setValid(false);
        }
        for (BlockPos alreadyPlaced : this.placed.keySet()) {
            ObbyModule.HELPER.addBlockState(alreadyPlaced, Blocks.OBSIDIAN.getDefaultState());
        }
        BasePath path = new BasePath((Entity)RotationUtil.getRotationPlayer(), pos, ((SelfTrap)this.module).maxHelping.getValue());
        if (((SelfTrap)this.module).prioBehind.getValue().booleanValue()) {
            ArrayList<BlockPos> checkFirst = new ArrayList<BlockPos>(13);
            EnumFacing look = ListenerSelfTrap.mc.player.getHorizontalFacing();
            BlockPos off = pos.offset(look.getOpposite());
            checkFirst.add(off);
            checkFirst.add(off.down());
            checkFirst.add(off.down(2));
            checkFirst.add(pos.up());
            for (EnumFacing facing : EnumFacing.values()) {
                if (facing == look) continue;
                checkFirst.add(off.offset(facing));
                checkFirst.add(off.down().offset(facing));
                checkFirst.add(off.down(2).offset(facing));
            }
            PathFinder.efficient(path, ((SelfTrap)this.module).placeRange.getValue(), ListenerSelfTrap.mc.world.loadedEntityList, (RayTraceMode)((Object)((SelfTrap)this.module).smartRay.getValue()), ObbyModule.HELPER, Blocks.OBSIDIAN.getDefaultState(), PathFinder.CHECK, checkFirst, pos.down(), pos.down(2));
        } else {
            PathFinder.findPath(path, ((SelfTrap)this.module).placeRange.getValue(), ListenerSelfTrap.mc.world.loadedEntityList, (RayTraceMode)((Object)((SelfTrap)this.module).smartRay.getValue()), ObbyModule.HELPER, Blocks.OBSIDIAN.getDefaultState(), PathFinder.CHECK, pos.down(), pos.down(2));
        }
        return result.setValid(ObbyUtil.place((ObbyModule)this.module, path));
    }

    @Override
    protected int getSlot() {
        switch (((SelfTrap)this.module).mode.getValue()) {
            case Obsidian: {
                return InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN, new Block[0]);
            }
            case Web: 
            case HighWeb: 
            case FullWeb: {
                return InventoryUtil.findHotbarBlock(Blocks.WEB, new Block[0]);
            }
        }
        return -1;
    }

    @Override
    protected String getDisableString() {
        switch (((SelfTrap)this.module).mode.getValue()) {
            case Obsidian: {
                return "Disabled, no Obsidian.";
            }
            case Web: 
            case HighWeb: 
            case FullWeb: {
                return "Disabled, no Webs.";
            }
        }
        return "Disabled, unknown Mode!";
    }
}

