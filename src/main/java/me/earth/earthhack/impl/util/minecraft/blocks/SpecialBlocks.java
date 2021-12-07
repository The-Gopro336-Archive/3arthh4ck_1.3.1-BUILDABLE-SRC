/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  net.minecraft.block.Block
 *  net.minecraft.init.Blocks
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 */
package me.earth.earthhack.impl.util.minecraft.blocks;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class SpecialBlocks
implements Globals {
    public static final Set<Block> BAD_BLOCKS = Sets.newHashSet((Object[])new Block[]{Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER, Blocks.TRAPDOOR, Blocks.ENCHANTING_TABLE});
    public static final Set<Block> SHULKERS = Sets.newHashSet((Object[])new Block[]{Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX});
    public static final Set<Block> PRESSURE_PLATES = Sets.newHashSet((Object[])new Block[]{Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Blocks.STONE_PRESSURE_PLATE, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Blocks.WOODEN_PRESSURE_PLATE});
    public static final Predicate<Packet<?>> PACKETCHECK = p -> p instanceof CPacketPlayerTryUseItemOnBlock && SpecialBlocks.shouldSneak(((CPacketPlayerTryUseItemOnBlock)p).getPos(), false);
    public static final BiPredicate<Packet<?>, IBlockAccess> ACCESS_CHECK = (p, b) -> p instanceof CPacketPlayerTryUseItemOnBlock && SpecialBlocks.shouldSneak(((CPacketPlayerTryUseItemOnBlock)p).getPos(), b, false);

    public static boolean shouldSneak(BlockPos pos, boolean manager) {
        return SpecialBlocks.shouldSneak(pos, (IBlockAccess)SpecialBlocks.mc.world, manager);
    }

    public static boolean shouldSneak(BlockPos pos, IBlockAccess provider, boolean manager) {
        return SpecialBlocks.shouldSneak(provider.getBlockState(pos).getBlock(), manager);
    }

    public static boolean shouldSneak(Block block, boolean manager) {
        if (manager && Managers.ACTION.isSneaking()) {
            return false;
        }
        return BAD_BLOCKS.contains((Object)block) || SHULKERS.contains((Object)block);
    }
}

