/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Enchantments
 *  net.minecraft.init.MobEffects
 *  net.minecraft.item.ItemAxe
 *  net.minecraft.item.ItemPickaxe
 *  net.minecraft.item.ItemSpade
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.util.minecraft.blocks.mine;

import java.util.Set;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.block.IBlock;
import me.earth.earthhack.impl.core.mixins.item.IItemTool;
import me.earth.earthhack.impl.util.misc.ReflectionUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class MineUtil
implements Globals {
    public static boolean canHarvestBlock(BlockPos pos, ItemStack stack) {
        IBlockState state = MineUtil.mc.world.getBlockState(pos);
        state = state.getActualState((IBlockAccess)MineUtil.mc.world, pos);
        Block block = state.getBlock();
        if (state.getMaterial().isToolNotRequired()) {
            return true;
        }
        if (stack.isEmpty()) {
            return stack.canHarvestBlock(state);
        }
        String tool = ((IBlock)block).getHarvestToolNonForge(state);
        if (tool == null) {
            return stack.canHarvestBlock(state);
        }
        int toolLevel = -1;
        if (stack.getItem() instanceof IItemTool) {
            String toolClass = null;
            if (stack.getItem() instanceof ItemPickaxe) {
                toolClass = "pickaxe";
            } else if (stack.getItem() instanceof ItemAxe) {
                toolClass = "axe";
            } else if (stack.getItem() instanceof ItemSpade) {
                toolClass = "shovel";
            }
            if (tool.equals(toolClass)) {
                toolLevel = ((IItemTool)stack.getItem()).getToolMaterial().getHarvestLevel();
            }
        }
        if (toolLevel < 0) {
            return stack.canHarvestBlock(state);
        }
        return toolLevel >= ((IBlock)block).getHarvestLevelNonForge(state);
    }

    public static int findBestTool(BlockPos pos) {
        return MineUtil.findBestTool(pos, MineUtil.mc.world.getBlockState(pos));
    }

    public static int findBestTool(BlockPos pos, IBlockState state) {
        int result = MineUtil.mc.player.inventory.currentItem;
        if (state.getBlockHardness((World)MineUtil.mc.world, pos) > 0.0f) {
            double speed = MineUtil.getSpeed(state, MineUtil.mc.player.getHeldItemMainhand());
            for (int i = 0; i < 9; ++i) {
                ItemStack stack = MineUtil.mc.player.inventory.getStackInSlot(i);
                double stackSpeed = MineUtil.getSpeed(state, stack);
                if (!(stackSpeed > speed)) continue;
                speed = stackSpeed;
                result = i;
            }
        }
        return result;
    }

    public static double getSpeed(IBlockState state, ItemStack stack) {
        double str = stack.getDestroySpeed(state);
        int effect = EnchantmentHelper.getEnchantmentLevel((Enchantment)Enchantments.EFFICIENCY, (ItemStack)stack);
        return Math.max(str + (str > 1.0 ? (double)(effect * effect) + 1.0 : 0.0), 0.0);
    }

    public static float getDamage(ItemStack stack, BlockPos pos, boolean onGround) {
        IBlockState state = MineUtil.mc.world.getBlockState(pos);
        return MineUtil.getDamage(state, stack, pos, onGround);
    }

    public static float getDamage(IBlockState state, ItemStack stack, BlockPos pos, boolean onGround) {
        return MineUtil.getDigSpeed(stack, state, onGround) / (state.getBlockHardness((World)MineUtil.mc.world, pos) * (float)(MineUtil.canHarvestBlock(pos, stack) ? 30 : 100));
    }

    private static float getDigSpeed(ItemStack stack, IBlockState state, boolean onGround) {
        int i;
        float digSpeed = 1.0f;
        if (!stack.isEmpty()) {
            digSpeed *= stack.getDestroySpeed(state);
        }
        if (digSpeed > 1.0f && (i = EnchantmentHelper.getEnchantmentLevel((Enchantment)Enchantments.EFFICIENCY, (ItemStack)stack)) > 0 && !stack.isEmpty()) {
            digSpeed += (float)(i * i + 1);
        }
        if (MineUtil.mc.player.isPotionActive(MobEffects.HASTE)) {
            digSpeed *= 1.0f + (float)(MineUtil.mc.player.getActivePotionEffect(MobEffects.HASTE).getAmplifier() + 1) * 0.2f;
        }
        if (MineUtil.mc.player.isPotionActive(MobEffects.MINING_FATIGUE)) {
            float miningFatigue;
            switch (MineUtil.mc.player.getActivePotionEffect(MobEffects.MINING_FATIGUE).getAmplifier()) {
                case 0: {
                    miningFatigue = 0.3f;
                    break;
                }
                case 1: {
                    miningFatigue = 0.09f;
                    break;
                }
                case 2: {
                    miningFatigue = 0.0027f;
                    break;
                }
                default: {
                    miningFatigue = 8.1E-4f;
                }
            }
            digSpeed *= miningFatigue;
        }
        if (MineUtil.mc.player.isInsideOfMaterial(Material.WATER) && !EnchantmentHelper.getAquaAffinityModifier((EntityLivingBase)MineUtil.mc.player)) {
            digSpeed /= 5.0f;
        }
        if (onGround && !MineUtil.mc.player.onGround) {
            digSpeed /= 5.0f;
        }
        return digSpeed < 0.0f ? 0.0f : digSpeed;
    }

    public static boolean canBreak(BlockPos pos) {
        return MineUtil.canBreak(MineUtil.mc.world.getBlockState(pos), pos);
    }

    public static boolean canBreak(IBlockState state, BlockPos pos) {
        return state.getBlockHardness((World)MineUtil.mc.world, pos) != -1.0f || state.getMaterial().isLiquid();
    }

    private static void setupHarvestLevels() {
        Block[] oreBlocks;
        Set blocks = (Set)ReflectionUtil.getField(ItemPickaxe.class, null, 0);
        for (Block block : blocks) {
            ((IBlock)block).setHarvestLevelNonForge("pickaxe", 0);
        }
        blocks = (Set)ReflectionUtil.getField(ItemSpade.class, null, 0);
        for (Block block : blocks) {
            ((IBlock)block).setHarvestLevelNonForge("shovel", 0);
        }
        blocks = (Set)ReflectionUtil.getField(ItemAxe.class, null, 0);
        for (Block block : blocks) {
            ((IBlock)block).setHarvestLevelNonForge("axe", 0);
        }
        ((IBlock)Blocks.OBSIDIAN).setHarvestLevelNonForge("pickaxe", 3);
        ((IBlock)Blocks.ENCHANTING_TABLE).setHarvestLevelNonForge("pickaxe", 0);
        for (Block block : oreBlocks = new Block[]{Blocks.EMERALD_ORE, Blocks.EMERALD_BLOCK, Blocks.DIAMOND_ORE, Blocks.DIAMOND_BLOCK, Blocks.GOLD_ORE, Blocks.GOLD_BLOCK, Blocks.REDSTONE_ORE, Blocks.LIT_REDSTONE_ORE}) {
            ((IBlock)block).setHarvestLevelNonForge("pickaxe", 2);
        }
        ((IBlock)Blocks.IRON_ORE).setHarvestLevelNonForge("pickaxe", 1);
        ((IBlock)Blocks.IRON_BLOCK).setHarvestLevelNonForge("pickaxe", 1);
        ((IBlock)Blocks.LAPIS_ORE).setHarvestLevelNonForge("pickaxe", 1);
        ((IBlock)Blocks.LAPIS_BLOCK).setHarvestLevelNonForge("pickaxe", 1);
        ((IBlock)Blocks.QUARTZ_ORE).setHarvestLevelNonForge("pickaxe", 0);
    }

    static {
        MineUtil.setupHarvestLevels();
    }
}

