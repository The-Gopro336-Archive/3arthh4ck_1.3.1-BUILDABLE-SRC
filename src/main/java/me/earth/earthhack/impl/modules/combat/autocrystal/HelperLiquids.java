/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 */
package me.earth.earthhack.impl.modules.combat.autocrystal;

import java.util.List;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.combat.autocrystal.HelperPlace;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.MineSlots;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.PlaceData;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.PositionData;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.mine.MineUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class HelperLiquids
implements Globals {
    public PlaceData calculate(HelperPlace placeHelper, PlaceData placeData, List<EntityPlayer> friends, List<EntityPlayer> players, float minDamage) {
        PlaceData newData = new PlaceData(minDamage);
        newData.setTarget(placeData.getTarget());
        for (PositionData data : placeData.getLiquid()) {
            if (placeHelper.validate(data, friends) == null) continue;
            placeHelper.calcPositionData(newData, data, players);
        }
        return newData;
    }

    public EnumFacing getAbsorbFacing(BlockPos pos, List<Entity> entities, IBlockAccess access, double placeRange) {
        for (EnumFacing facing : EnumFacing.VALUES) {
            BlockPos offset;
            if (facing == EnumFacing.DOWN || BlockUtil.getDistanceSq(offset = pos.offset(facing)) >= MathUtil.square(placeRange) || !access.getBlockState(offset).getMaterial().isReplaceable()) continue;
            boolean found = false;
            AxisAlignedBB bb = new AxisAlignedBB(offset);
            for (Entity entity : entities) {
                if (entity == null || EntityUtil.isDead(entity) || !entity.preventEntitySpawning || !entity.getEntityBoundingBox().intersects(bb)) continue;
                found = true;
                break;
            }
            if (found) continue;
            return facing;
        }
        return null;
    }

    public static MineSlots getSlots(boolean onGroundCheck) {
        int bestBlock = -1;
        int bestTool = -1;
        float maxSpeed = 0.0f;
        for (int i = 8; i > -1; --i) {
            ItemStack stack = HelperLiquids.mc.player.inventory.getStackInSlot(i);
            if (!(stack.getItem() instanceof ItemBlock)) continue;
            Block block = ((ItemBlock)stack.getItem()).getBlock();
            int tool = MineUtil.findBestTool(BlockPos.ORIGIN, block.getDefaultState());
            float damage = MineUtil.getDamage(block.getDefaultState(), HelperLiquids.mc.player.inventory.getStackInSlot(tool), BlockPos.ORIGIN, !onGroundCheck || RotationUtil.getRotationPlayer().onGround);
            if (!(damage > maxSpeed)) continue;
            bestBlock = i;
            bestTool = tool;
            maxSpeed = damage;
        }
        return new MineSlots(bestBlock, bestTool, maxSpeed);
    }
}

