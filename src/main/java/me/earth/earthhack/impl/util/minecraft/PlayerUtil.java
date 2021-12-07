package me.earth.earthhack.impl.util.minecraft;

import com.mojang.authlib.GameProfile;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PlayerUtil
implements Globals {
    private static final Map<Integer, EntityOtherPlayerMP> FAKE_PLAYERS = new HashMap<Integer, EntityOtherPlayerMP>();

    public static EntityOtherPlayerMP createFakePlayerAndAddToWorld(GameProfile profile) {
        return PlayerUtil.createFakePlayerAndAddToWorld(profile, EntityOtherPlayerMP::new);
    }

    public static EntityOtherPlayerMP createFakePlayerAndAddToWorld(GameProfile profile, BiFunction<World, GameProfile, EntityOtherPlayerMP> create) {
        EntityOtherPlayerMP fakePlayer = PlayerUtil.createFakePlayer(profile, create);
        int randomID = -1000;
        while (FAKE_PLAYERS.containsKey(randomID) || PlayerUtil.mc.world.getEntityByID(randomID) != null) {
            randomID = ThreadLocalRandom.current().nextInt(-100000, -100);
        }
        FAKE_PLAYERS.put(randomID, fakePlayer);
        PlayerUtil.mc.world.addEntityToWorld(randomID, fakePlayer);
        return fakePlayer;
    }

    public static EntityOtherPlayerMP createFakePlayer(GameProfile profile, BiFunction<World, GameProfile, EntityOtherPlayerMP> create) {
        EntityOtherPlayerMP fakePlayer = create.apply(PlayerUtil.mc.world, profile);
        fakePlayer.inventory = PlayerUtil.mc.player.inventory;
        fakePlayer.inventoryContainer = PlayerUtil.mc.player.inventoryContainer;
        fakePlayer.setPositionAndRotation(PlayerUtil.mc.player.posX, PlayerUtil.mc.player.getEntityBoundingBox().minY, PlayerUtil.mc.player.posZ, PlayerUtil.mc.player.rotationYaw, PlayerUtil.mc.player.rotationPitch);
        fakePlayer.rotationYawHead = PlayerUtil.mc.player.rotationYawHead;
        fakePlayer.onGround = PlayerUtil.mc.player.onGround;
        fakePlayer.setSneaking(PlayerUtil.mc.player.isSneaking());
        fakePlayer.setHealth(PlayerUtil.mc.player.getHealth());
        fakePlayer.setAbsorptionAmount(PlayerUtil.mc.player.getAbsorptionAmount());
        for (PotionEffect effect : PlayerUtil.mc.player.getActivePotionEffects()) {
            fakePlayer.addPotionEffect(effect);
        }
        return fakePlayer;
    }

    public static void removeFakePlayer(EntityOtherPlayerMP fakePlayer) {
        if (fakePlayer != null && FAKE_PLAYERS.containsKey(fakePlayer.getEntityId())) {
            FAKE_PLAYERS.remove(fakePlayer.getEntityId());
            mc.addScheduledTask(() -> {
                if (PlayerUtil.mc.world != null) {
                    PlayerUtil.mc.world.removeEntity(fakePlayer);
                }
            });
        }
    }

    public static boolean isFakePlayer(Entity entity) {
        return entity != null && FAKE_PLAYERS.containsKey(entity.getEntityId());
    }

    public static boolean isOtherFakePlayer(Entity entity) {
        return entity != null && entity.getEntityId() < 0;
    }

    public static boolean isCreative(EntityPlayer player) {
        return player != null && (player.isCreative() || player.capabilities.isCreativeMode);
    }

    public static BlockPos getBestPlace(BlockPos pos, EntityPlayer player) {
        EnumFacing facing = PlayerUtil.getSide(player, pos);
        if (facing == EnumFacing.UP) {
            Block block = PlayerUtil.mc.world.getBlockState(pos).getBlock();
            Block block2 = PlayerUtil.mc.world.getBlockState(pos.offset(EnumFacing.UP)).getBlock();
            if (block2 instanceof BlockAir && (block == Blocks.OBSIDIAN || block == Blocks.BEDROCK)) {
                return pos;
            }
        } else {
            BlockPos blockPos = pos.offset(facing);
            Block block = PlayerUtil.mc.world.getBlockState(blockPos).getBlock();
            BlockPos blockPos2 = blockPos.down();
            Block block2 = PlayerUtil.mc.world.getBlockState(blockPos2).getBlock();
            if (block instanceof BlockAir && (block2 == Blocks.OBSIDIAN || block2 == Blocks.BEDROCK)) {
                return blockPos2;
            }
        }
        return null;
    }

    public static EnumFacing getSide(EntityPlayer player, BlockPos blockPos) {
        BlockPos playerPos = PositionUtil.getPosition(player);
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            if (!playerPos.offset(facing).equals(blockPos)) continue;
            return facing;
        }
        if (playerPos.offset(EnumFacing.UP).offset(EnumFacing.UP).equals(blockPos)) {
            return EnumFacing.UP;
        }
        return EnumFacing.DOWN;
    }

    public static boolean isInHole(EntityPlayer player) {
        BlockPos position = PositionUtil.getPosition(player);
        int count = 0;
        for (EnumFacing face : EnumFacing.values()) {
            if (face == EnumFacing.UP || face == EnumFacing.DOWN || BlockUtil.isReplaceable(position.offset(face))) continue;
            ++count;
        }
        return count >= 3;
    }

    public static EnumFacing getOppositePlayerFaceBetter(EntityPlayer player, BlockPos pos) {
        for (EnumFacing face : EnumFacing.HORIZONTALS) {
            BlockPos off = pos.offset(face);
            BlockPos off1 = pos.offset(face).offset(face);
            BlockPos playerOff = PositionUtil.getPosition(player);
            if (!new Vec3d(off).equals(new Vec3d(playerOff)) && !new Vec3d(off1).equals(new Vec3d(off1))) continue;
            return face.getOpposite();
        }
        return null;
    }
}
