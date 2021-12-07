/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketAnimation
 *  net.minecraft.network.play.client.CPacketPlayer$Rotation
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 */
package me.earth.earthhack.impl.modules.combat.antitrap;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.antitrap.AntiTrap;
import me.earth.earthhack.impl.modules.combat.antitrap.util.AntiTrapMode;
import me.earth.earthhack.impl.modules.combat.offhand.Offhand;
import me.earth.earthhack.impl.modules.combat.offhand.modes.OffhandMode;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyModule;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

final class ListenerMotion
extends ModuleListener<AntiTrap, MotionUpdateEvent> {
    protected static final ModuleCache<Offhand> OFFHAND = Caches.getModule(Offhand.class);

    public ListenerMotion(AntiTrap module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (((AntiTrap)this.module).autoOff.getValue().booleanValue() && !PositionUtil.getPosition().equals((Object)((AntiTrap)this.module).startPos)) {
            ((AntiTrap)this.module).disable();
            return;
        }
        switch (((AntiTrap)this.module).mode.getValue()) {
            case Crystal: {
                this.doCrystal(event);
                break;
            }
            case FacePlace: 
            case Fill: {
                this.doObby(event, ((AntiTrap)this.module).mode.getValue().getOffsets());
                break;
            }
        }
    }

    private void doObby(MotionUpdateEvent event, Vec3i[] offsets) {
        if (event.getStage() == Stage.PRE) {
            ((AntiTrap)this.module).rotations = null;
            ((AntiTrap)this.module).blocksPlaced = 0;
            for (BlockPos confirmed : ((AntiTrap)this.module).confirmed) {
                ((AntiTrap)this.module).placed.remove((Object)confirmed);
            }
            ((AntiTrap)this.module).placed.entrySet().removeIf(entry -> System.currentTimeMillis() - (Long)entry.getValue() >= (long)((AntiTrap)this.module).confirm.getValue().intValue());
            BlockPos playerPos = PositionUtil.getPosition();
            BlockPos[] positions = new BlockPos[offsets.length];
            for (int i = 0; i < offsets.length; ++i) {
                Vec3i offset = offsets[i];
                if (((AntiTrap)this.module).mode.getValue() == AntiTrapMode.Fill && ListenerMotion.mc.world.getBlockState(playerPos.add(offset.getX() / 2, 0, offset.getZ() / 2)).getBlock() == Blocks.BEDROCK) continue;
                positions[i] = playerPos.add(offset);
            }
            if (((AntiTrap)this.module).offhand.getValue().booleanValue()) {
                if (!InventoryUtil.isHolding(Blocks.OBSIDIAN)) {
                    ((AntiTrap)this.module).previous = OFFHAND.returnIfPresent(Offhand::getMode, null);
                    OFFHAND.computeIfPresent(o -> o.setMode(OffhandMode.CRYSTAL));
                    return;
                }
            } else {
                ((AntiTrap)this.module).slot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN, new Block[0]);
                if (((AntiTrap)this.module).slot == -1) {
                    ModuleUtil.disable((Module)this.module, "\u00a7cNo Obsidian found.");
                    return;
                }
            }
            boolean done = true;
            LinkedList<BlockPos> toPlace = new LinkedList<BlockPos>();
            for (BlockPos pos2 : positions) {
                if (pos2 == null || ((AntiTrap)this.module).mode.getValue() == AntiTrapMode.Fill && !((AntiTrap)this.module).highFill.getValue().booleanValue() && pos2.getY() > playerPos.getY() || !ListenerMotion.mc.world.getBlockState(pos2).getMaterial().isReplaceable()) continue;
                toPlace.add(pos2);
                done = false;
            }
            if (done) {
                ((AntiTrap)this.module).disable();
                return;
            }
            boolean hasPlaced = false;
            Optional<BlockPos> crystalPos = toPlace.stream().filter(pos -> !ListenerMotion.mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(pos)).isEmpty() && ListenerMotion.mc.world.getBlockState(pos).getMaterial().isReplaceable()).findFirst();
            if (crystalPos.isPresent()) {
                BlockPos pos3 = crystalPos.get();
                hasPlaced = ((AntiTrap)this.module).placeBlock(pos3);
            }
            if (!hasPlaced) {
                for (BlockPos pos2 : toPlace) {
                    if (((AntiTrap)this.module).placed.containsKey((Object)pos2) || !ObbyModule.HELPER.getBlockState(pos2).getMaterial().isReplaceable()) continue;
                    ((AntiTrap)this.module).confirmed.remove((Object)pos2);
                    if (!((AntiTrap)this.module).placeBlock(pos2)) continue;
                    break;
                }
            }
            if (((AntiTrap)this.module).rotate.getValue() != Rotate.None) {
                if (((AntiTrap)this.module).rotations != null) {
                    event.setYaw(((AntiTrap)this.module).rotations[0]);
                    event.setPitch(((AntiTrap)this.module).rotations[1]);
                }
            } else {
                Locks.acquire(Locks.PLACE_SWITCH_LOCK, ((AntiTrap)this.module)::execute);
            }
        } else {
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, ((AntiTrap)this.module)::execute);
        }
    }

    private void doCrystal(MotionUpdateEvent event) {
        if (event.getStage() == Stage.PRE) {
            EntityPlayer closest;
            List<BlockPos> positions = ((AntiTrap)this.module).getCrystalPositions();
            if (positions.isEmpty() || !((AntiTrap)this.module).isEnabled()) {
                if (!((AntiTrap)this.module).empty.getValue().booleanValue()) {
                    ((AntiTrap)this.module).disable();
                }
                return;
            }
            if (((AntiTrap)this.module).offhand.getValue().booleanValue()) {
                if (!InventoryUtil.isHolding(Items.END_CRYSTAL)) {
                    ((AntiTrap)this.module).previous = OFFHAND.returnIfPresent(Offhand::getMode, null);
                    OFFHAND.computeIfPresent(o -> o.setMode(OffhandMode.CRYSTAL));
                    return;
                }
            } else {
                ((AntiTrap)this.module).slot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL, new Item[0]);
                if (((AntiTrap)this.module).slot == -1) {
                    ModuleUtil.disable((Module)this.module, "\u00a7cNo crystals found.");
                    return;
                }
            }
            if ((closest = EntityUtil.getClosestEnemy()) != null) {
                positions.sort(Comparator.comparingDouble(pos -> BlockUtil.getDistanceSq((Entity)closest, pos)));
            }
            ((AntiTrap)this.module).pos = positions.get(positions.size() - 1);
            ((AntiTrap)this.module).rotations = RotationUtil.getRotationsToTopMiddle(((AntiTrap)this.module).pos.up());
            ((AntiTrap)this.module).result = RayTraceUtil.getRayTraceResult(((AntiTrap)this.module).rotations[0], ((AntiTrap)this.module).rotations[1], 3.0f);
            if (((AntiTrap)this.module).rotate.getValue() == Rotate.Normal) {
                event.setYaw(((AntiTrap)this.module).rotations[0]);
                event.setPitch(((AntiTrap)this.module).rotations[1]);
            } else {
                this.executeCrystal();
            }
        } else {
            this.executeCrystal();
        }
    }

    private void executeCrystal() {
        if (((AntiTrap)this.module).pos != null && ((AntiTrap)this.module).result != null) {
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, this::executeLocked);
        }
    }

    private void executeLocked() {
        int lastSlot = ListenerMotion.mc.player.inventory.currentItem;
        if (!InventoryUtil.isHolding(Items.END_CRYSTAL)) {
            if (((AntiTrap)this.module).offhand.getValue().booleanValue() || ((AntiTrap)this.module).slot == -1) {
                return;
            }
            InventoryUtil.switchTo(((AntiTrap)this.module).slot);
        }
        EnumHand hand = ListenerMotion.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
        CPacketPlayerTryUseItemOnBlock place = new CPacketPlayerTryUseItemOnBlock(((AntiTrap)this.module).pos, ((AntiTrap)this.module).result.sideHit, hand, (float)((AntiTrap)this.module).result.hitVec.x, (float)((AntiTrap)this.module).result.hitVec.y, (float)((AntiTrap)this.module).result.hitVec.z);
        CPacketAnimation swing = new CPacketAnimation(hand);
        if (((AntiTrap)this.module).rotate.getValue() == Rotate.Packet && ((AntiTrap)this.module).rotations != null) {
            ListenerMotion.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(((AntiTrap)this.module).rotations[0], ((AntiTrap)this.module).rotations[1], ListenerMotion.mc.player.onGround));
        }
        ListenerMotion.mc.player.connection.sendPacket((Packet)place);
        ListenerMotion.mc.player.connection.sendPacket((Packet)swing);
        InventoryUtil.switchTo(lastSlot);
        if (((AntiTrap)this.module).swing.getValue().booleanValue()) {
            Swing.Client.swing(hand);
        }
        ((AntiTrap)this.module).disable();
    }
}

