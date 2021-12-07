/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockContainer
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketAnimation
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 */
package me.earth.earthhack.impl.modules.player.scaffold;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.core.ducks.IMinecraft;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.player.freecam.Freecam;
import me.earth.earthhack.impl.modules.player.scaffold.Scaffold;
import me.earth.earthhack.impl.modules.player.spectate.Spectate;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.SpecialBlocks;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

final class ListenerMotion
extends ModuleListener<Scaffold, MotionUpdateEvent> {
    private static final ModuleCache<Freecam> FREECAM = Caches.getModule(Freecam.class);
    private static final ModuleCache<Spectate> SPECTATE = Caches.getModule(Spectate.class);

    public ListenerMotion(Scaffold module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (FREECAM.isEnabled() && !((Scaffold)this.module).freecam.getValue().booleanValue() || SPECTATE.isEnabled() && !((Scaffold)this.module).spectate.getValue().booleanValue()) {
            return;
        }
        if (((Scaffold)this.module).aac.getValue().booleanValue() && ((Scaffold)this.module).aacTimer.passed(((Scaffold)this.module).aacDelay.getValue().intValue()) && ListenerMotion.mc.player.onGround) {
            ListenerMotion.mc.player.motionX = 0.0;
            ListenerMotion.mc.player.motionZ = 0.0;
            ((Scaffold)this.module).aacTimer.reset();
        }
        if (event.getStage() == Stage.PRE) {
            ((Scaffold)this.module).facing = null;
            BlockPos prev = ((Scaffold)this.module).pos;
            ((Scaffold)this.module).pos = null;
            ((Scaffold)this.module).pos = ((Scaffold)this.module).findNextPos();
            if (((Scaffold)this.module).pos != null) {
                ((Scaffold)this.module).rot = ((Scaffold)this.module).pos;
                if (!((Scaffold)this.module).pos.equals((Object)prev)) {
                    ((Scaffold)this.module).rotationTimer.reset();
                }
                this.setRotations(((Scaffold)this.module).pos, event);
            } else if (((Scaffold)this.module).rot != null && ((Scaffold)this.module).rotate.getValue().booleanValue() && ((Scaffold)this.module).keepRotations.getValue() != 0 && !((Scaffold)this.module).rotationTimer.passed(((Scaffold)this.module).keepRotations.getValue().intValue())) {
                this.setRotations(((Scaffold)this.module).rot, event);
            } else {
                ((Scaffold)this.module).rot = null;
            }
        } else {
            if (((Scaffold)this.module).pos == null || ((Scaffold)this.module).facing == null || ((Scaffold)this.module).preRotate.getValue() != 0 && ((Scaffold)this.module).rotate.getValue().booleanValue() && !((Scaffold)this.module).rotationTimer.passed(((Scaffold)this.module).preRotate.getValue().intValue())) {
                return;
            }
            int slot = -1;
            int optional = -1;
            ItemStack offhand = ListenerMotion.mc.player.getHeldItemOffhand();
            if (((Scaffold)this.module).isStackValid(offhand)) {
                if (offhand.getItem() instanceof ItemBlock) {
                    Block block = ((ItemBlock)offhand.getItem()).getBlock();
                    if (!((Scaffold)this.module).checkState.getValue().booleanValue() || !block.getBlockState().getBaseState().getMaterial().isReplaceable()) {
                        if (block instanceof BlockContainer) {
                            optional = -2;
                        } else {
                            slot = -2;
                        }
                    }
                } else {
                    optional = -2;
                }
            }
            if (slot == -1) {
                for (int i = 0; i < 9; ++i) {
                    ItemStack stack = ListenerMotion.mc.player.inventory.getStackInSlot(i);
                    if (!((Scaffold)this.module).isStackValid(stack) || !(stack.getItem() instanceof ItemBlock)) continue;
                    Block block = ((ItemBlock)stack.getItem()).getBlock();
                    if (((Scaffold)this.module).checkState.getValue().booleanValue() && block.getBlockState().getBaseState().getMaterial().isReplaceable()) continue;
                    if (block instanceof BlockContainer) {
                        optional = i;
                        continue;
                    }
                    slot = i;
                    if (i == ListenerMotion.mc.player.inventory.currentItem) break;
                }
            }
            int n = slot = slot == -1 ? optional : slot;
            if (slot != -1) {
                boolean sneaking;
                boolean sneak;
                boolean jump = ListenerMotion.mc.player.movementInput.jump && ((Scaffold)this.module).tower.getValue() != false;
                boolean bl = sneak = ListenerMotion.mc.player.movementInput.sneak && ((Scaffold)this.module).down.getValue() != false;
                if (jump && !sneak && !MovementUtil.isMoving()) {
                    ((IMinecraft)mc).setRightClickDelay(3);
                    ListenerMotion.mc.player.jump();
                    if (((Scaffold)this.module).towerTimer.passed(1500L)) {
                        ListenerMotion.mc.player.motionY = -0.28;
                        ((Scaffold)this.module).towerTimer.reset();
                    }
                } else {
                    ((Scaffold)this.module).towerTimer.reset();
                }
                boolean bl2 = sneaking = ((Scaffold)this.module).smartSneak.getValue() != false && !SpecialBlocks.shouldSneak(((Scaffold)this.module).pos.offset(((Scaffold)this.module).facing), true);
                if (((Scaffold)this.module).attack.getValue().booleanValue() && Managers.SWITCH.getLastSwitch() > (long)((Scaffold)this.module).cooldown.getValue().intValue() && ((Scaffold)this.module).breakTimer.passed(((Scaffold)this.module).breakDelay.getValue().intValue())) {
                    EntityEnderCrystal entity = null;
                    float minDmg = Float.MAX_VALUE;
                    for (EntityEnderCrystal crystal : ListenerMotion.mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(((Scaffold)this.module).pos))) {
                        float damage;
                        if (crystal == null || crystal.isDead || !((damage = DamageUtil.calculate((Entity)crystal)) < minDmg) || !((Scaffold)this.module).pop.getValue().shouldPop(damage, ((Scaffold)this.module).popTime.getValue())) continue;
                        entity = crystal;
                        minDmg = damage;
                    }
                    if (entity != null) {
                        PacketUtil.attack(entity);
                        ((Scaffold)this.module).breakTimer.reset();
                    }
                }
                int finalSlot = slot;
                Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> {
                    int lastSlot = ListenerMotion.mc.player.inventory.currentItem;
                    boolean sprinting = ListenerMotion.mc.player.isSprinting() && ((Scaffold)this.module).stopSprint.getValue() != false;
                    InventoryUtil.switchTo(finalSlot);
                    if (sprinting) {
                        PacketUtil.sendAction(CPacketEntityAction.Action.STOP_SPRINTING);
                    }
                    if (!sneaking) {
                        PacketUtil.sendAction(CPacketEntityAction.Action.START_SNEAKING);
                    }
                    RayTraceResult result = RayTraceUtil.getRayTraceResult(((Scaffold)this.module).rotations[0], ((Scaffold)this.module).rotations[1]);
                    ListenerMotion.mc.playerController.processRightClickBlock(ListenerMotion.mc.player, ListenerMotion.mc.world, ((Scaffold)this.module).pos.offset(((Scaffold)this.module).facing), ((Scaffold)this.module).facing.getOpposite(), result.hitVec, InventoryUtil.getHand(finalSlot));
                    ListenerMotion.mc.player.connection.sendPacket((Packet)new CPacketAnimation(InventoryUtil.getHand(finalSlot)));
                    if (!sneaking) {
                        PacketUtil.sendAction(CPacketEntityAction.Action.STOP_SNEAKING);
                    }
                    if (sprinting) {
                        PacketUtil.sendAction(CPacketEntityAction.Action.START_SPRINTING);
                    }
                    InventoryUtil.switchTo(lastSlot);
                });
                if (((Scaffold)this.module).swing.getValue().booleanValue()) {
                    Swing.Client.swing(InventoryUtil.getHand(slot));
                }
            }
        }
    }

    private void setRotations(BlockPos pos, MotionUpdateEvent event) {
        ((Scaffold)this.module).facing = BlockUtil.getFacing(pos);
        if (((Scaffold)this.module).facing != null) {
            this.setRotations(pos, event, ((Scaffold)this.module).facing);
        } else if (((Scaffold)this.module).helping.getValue().booleanValue()) {
            for (EnumFacing facing : EnumFacing.VALUES) {
                BlockPos p = pos.offset(facing);
                EnumFacing f = BlockUtil.getFacing(p);
                if (f == null) continue;
                ((Scaffold)this.module).facing = f;
                ((Scaffold)this.module).pos = p;
                this.setRotations(p, event, f);
            }
        }
    }

    private void setRotations(BlockPos pos, MotionUpdateEvent event, EnumFacing facing) {
        ((Scaffold)this.module).rotations = RotationUtil.getRotations(pos.offset(facing), facing.getOpposite());
        if (((Scaffold)this.module).rotate.getValue().booleanValue() && ((Scaffold)this.module).rotations != null) {
            event.setYaw(((Scaffold)this.module).rotations[0]);
            event.setPitch(((Scaffold)this.module).rotations[1]);
        }
    }
}

