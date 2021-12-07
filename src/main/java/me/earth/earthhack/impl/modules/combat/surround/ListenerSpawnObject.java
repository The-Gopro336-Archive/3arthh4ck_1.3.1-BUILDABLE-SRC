/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer$Rotation
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 *  net.minecraft.network.play.server.SPacketSpawnObject
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.modules.combat.surround;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.surround.ListenerMotion;
import me.earth.earthhack.impl.modules.combat.surround.Surround;
import me.earth.earthhack.impl.util.helpers.blocks.modes.PlaceSwing;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Pop;
import me.earth.earthhack.impl.util.helpers.blocks.modes.RayTraceMode;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

final class ListenerSpawnObject
extends ModuleListener<Surround, PacketEvent.Receive<SPacketSpawnObject>> {
    public ListenerSpawnObject(Surround module) {
        super(module, PacketEvent.Receive.class, SPacketSpawnObject.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSpawnObject> event) {
        BlockPos pos;
        if (!((Surround)this.module).predict.getValue().booleanValue() || ((Surround)this.module).rotate.getValue() == Rotate.Normal || Managers.SWITCH.getLastSwitch() < (long)((Integer)((Surround)this.module).cooldown.getValue()).intValue()) {
            return;
        }
        SPacketSpawnObject packet = (SPacketSpawnObject)event.getPacket();
        if (packet.getType() != 51) {
            return;
        }
        EntityPlayer player = ((Surround)this.module).getPlayer();
        if (player.getDistanceSq(pos = new BlockPos(packet.getX(), packet.getY(), packet.getZ())) < 9.0) {
            if (!((Surround)this.module).async.getValue().booleanValue() || DamageUtil.isWeaknessed() || ((Surround)this.module).smartRay.getValue() != RayTraceMode.Fast || !((Surround)this.module).timer.passed(((Integer)((Surround)this.module).delay.getValue()).intValue()) || !((Pop)((Surround)this.module).pop.getValue()).shouldPop(DamageUtil.calculate(pos.down(), (EntityLivingBase)player), (Integer)((Surround)this.module).popTime.getValue())) {
                event.addPostEvent(() -> ListenerMotion.start((Surround)this.module));
                return;
            }
            try {
                this.placeAsync(packet, player);
            }
            catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    private void placeAsync(SPacketSpawnObject packet, EntityPlayer player) {
        int slot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN, Blocks.ENDER_CHEST);
        if (slot == -1) {
            return;
        }
        AxisAlignedBB bb = new EntityEnderCrystal((World)ListenerSpawnObject.mc.world, packet.getX(), packet.getY(), packet.getZ()).getEntityBoundingBox();
        Set<BlockPos> surrounding = ((Surround)this.module).createSurrounding(((Surround)this.module).createBlocked(), Managers.ENTITIES.getPlayers());
        ConcurrentHashMap<BlockPos, EnumFacing> toPlace = new ConcurrentHashMap<BlockPos, EnumFacing>();
        for (BlockPos pos : surrounding) {
            EnumFacing facing;
            if (!bb.intersects(new AxisAlignedBB(pos)) || !ListenerSpawnObject.mc.world.getBlockState(pos).getMaterial().isReplaceable() || (facing = BlockUtil.getFacing(pos, (IBlockAccess)ListenerSpawnObject.mc.world)) == null) continue;
            toPlace.put(pos.offset(facing), facing.getOpposite());
        }
        if (toPlace.isEmpty()) {
            return;
        }
        ArrayList placed = new ArrayList(Math.min((Integer)((Surround)this.module).blocks.getValue(), toPlace.size()));
        Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> {
            int lastSlot = ListenerSpawnObject.mc.player.inventory.currentItem;
            PacketUtil.attack(packet.getEntityID());
            InventoryUtil.switchTo(slot);
            boolean sneaking = ListenerSpawnObject.mc.player.isSneaking();
            if (!sneaking) {
                PacketUtil.sneak(true);
            }
            int blocks = 0;
            for (Map.Entry entry : toPlace.entrySet()) {
                float[] helpingRotations = RotationUtil.getRotations((BlockPos)entry.getKey(), (EnumFacing)entry.getValue(), (Entity)player);
                RayTraceResult result = RayTraceUtil.getRayTraceResultWithEntity(helpingRotations[0], helpingRotations[1], (Entity)player);
                if (((Surround)this.module).rotate.getValue() == Rotate.Packet) {
                    ListenerSpawnObject.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(helpingRotations[0], helpingRotations[1], ListenerSpawnObject.mc.player.onGround));
                }
                float[] f = RayTraceUtil.hitVecToPlaceVec((BlockPos)entry.getKey(), result.hitVec);
                ListenerSpawnObject.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock((BlockPos)entry.getKey(), (EnumFacing)entry.getValue(), InventoryUtil.getHand(slot), f[0], f[1], f[2]));
                if (((Surround)this.module).placeSwing.getValue() == PlaceSwing.Always) {
                    Swing.Packet.swing(InventoryUtil.getHand(slot));
                }
                placed.add(((BlockPos)entry.getKey()).offset((EnumFacing)entry.getValue()));
                if (++blocks < (Integer)((Surround)this.module).blocks.getValue()) continue;
                break;
            }
            if (((Surround)this.module).placeSwing.getValue() == PlaceSwing.Once) {
                Swing.Packet.swing(InventoryUtil.getHand(slot));
            }
            if (!sneaking) {
                PacketUtil.sneak(false);
            }
            InventoryUtil.switchTo(lastSlot);
        });
        ((Surround)this.module).timer.reset(((Integer)((Surround)this.module).delay.getValue()).intValue());
        if (((Surround)this.module).resync.getValue().booleanValue()) {
            mc.addScheduledTask(() -> ((Surround)this.module).placed.addAll(placed));
        }
    }
}

