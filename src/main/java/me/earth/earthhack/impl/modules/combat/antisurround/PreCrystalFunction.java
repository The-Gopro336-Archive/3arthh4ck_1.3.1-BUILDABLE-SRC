/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.IBlockAccess
 */
package me.earth.earthhack.impl.modules.combat.antisurround;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.combat.antisurround.AntiSurround;
import me.earth.earthhack.impl.modules.combat.antisurround.util.AntiSurroundFunction;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.MineSlots;
import me.earth.earthhack.impl.modules.combat.legswitch.LegSwitch;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;

final class PreCrystalFunction
implements AntiSurroundFunction,
Globals {
    private final AntiSurround module;

    public PreCrystalFunction(AntiSurround module) {
        this.module = module;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void accept(BlockPos pos, BlockPos down, BlockPos on, EnumFacing onFacing, int obbySlot, MineSlots slots, int crystalSlot, Entity blocking, EntityPlayer found, boolean execute) {
        if (blocking != null) {
            return;
        }
        IBlockState state = PreCrystalFunction.mc.world.getBlockState(down);
        if (state.getBlock() != Blocks.OBSIDIAN && state.getBlock() != Blocks.BEDROCK) {
            return;
        }
        AntiSurround antiSurround = this.module;
        synchronized (antiSurround) {
            if (this.module.isActive() || AntiSurround.LEG_SWITCH.returnIfPresent(LegSwitch::isActive, false).booleanValue()) {
                return;
            }
            if (this.module.normal.getValue().booleanValue() || this.module.async.getValue().booleanValue()) {
                this.module.semiActiveTime = System.nanoTime();
                this.module.semiActive.set(true);
                this.module.semiPos = down;
            }
            RayTraceResult ray = null;
            if (this.module.rotations != null) {
                ray = RotationUtil.rayTraceWithYP(down, (IBlockAccess)PreCrystalFunction.mc.world, this.module.rotations[0], this.module.rotations[1], (b, p) -> p.equals((Object)down));
            }
            if (ray == null) {
                this.module.rotations = RotationUtil.getRotations((float)down.getX() + 0.5f, down.getY() + 1, (float)down.getZ() + 0.5f, RotationUtil.getRotationPlayer().posX, RotationUtil.getRotationPlayer().posY, RotationUtil.getRotationPlayer().posZ, PreCrystalFunction.mc.player.getEyeHeight());
                ray = RotationUtil.rayTraceWithYP(down, (IBlockAccess)PreCrystalFunction.mc.world, this.module.rotations[0], this.module.rotations[1], (b, p) -> p.equals((Object)down));
                if (ray == null) {
                    ray = new RayTraceResult(new Vec3d(0.5, 1.0, 0.5), EnumFacing.UP, down);
                }
            }
            RayTraceResult finalResult = ray;
            float[] f = RayTraceUtil.hitVecToPlaceVec(down, ray.hitVec);
            EnumHand h = InventoryUtil.getHand(crystalSlot);
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> {
                int lastSlot = PreCrystalFunction.mc.player.inventory.currentItem;
                InventoryUtil.switchTo(crystalSlot);
                PreCrystalFunction.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(down, finalResult.sideHit, h, f[0], f[1], f[2]));
                InventoryUtil.switchTo(lastSlot);
            });
        }
    }
}

