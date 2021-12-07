/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockAir
 *  net.minecraft.block.BlockObsidian
 *  net.minecraft.entity.Entity
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketEntityAction
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 */
package me.earth.earthhack.impl.modules.combat.bowkill;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.bowkill.BowKiller;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

final class ListenerMotion
extends ModuleListener<BowKiller, MotionUpdateEvent> {
    public ListenerMotion(BowKiller module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        ((BowKiller)this.module).entityDataArrayList.removeIf(e -> e.getTime() + 60000L < System.currentTimeMillis());
        if (!ListenerMotion.mc.player.collidedVertically) {
            return;
        }
        if (event.getStage() == Stage.PRE) {
            int newSlot;
            ((BowKiller)this.module).blockUnder = this.isBlockUnder();
            if (((BowKiller)this.module).rotate.getValue().booleanValue() && ListenerMotion.mc.player.getActiveItemStack().getItem() == Items.BOW && ListenerMotion.mc.gameSettings.keyBindUseItem.isKeyDown() && ((BowKiller)this.module).blockUnder) {
                float[] rotations;
                ((BowKiller)this.module).target = ((BowKiller)this.module).findTarget();
                if (((BowKiller)this.module).target != null && (rotations = ((BowKiller)this.module).rotationSmoother.getRotations((Entity)RotationUtil.getRotationPlayer(), ((BowKiller)this.module).target, ((BowKiller)this.module).height.getValue(), ((BowKiller)this.module).soft.getValue().floatValue())) != null) {
                    if (((BowKiller)this.module).silent.getValue().booleanValue()) {
                        event.setYaw(rotations[0]);
                        event.setPitch(rotations[1]);
                    } else {
                        ListenerMotion.mc.player.rotationYaw = rotations[0];
                        ListenerMotion.mc.player.rotationPitch = rotations[1];
                    }
                }
            }
            if (ListenerMotion.mc.player.getActiveItemStack().getItem() == Items.BOW && ListenerMotion.mc.player.isHandActive() && !((BowKiller)this.module).blockUnder && (newSlot = this.findBlockInHotbar()) != -1) {
                int oldSlot = ListenerMotion.mc.player.inventory.currentItem;
                ListenerMotion.mc.player.inventory.currentItem = newSlot;
                this.placeBlock(PositionUtil.getPosition((Entity)RotationUtil.getRotationPlayer()).down(1), event);
                ListenerMotion.mc.player.inventory.currentItem = oldSlot;
            }
        } else if (ListenerMotion.mc.player.getActiveItemStack().getItem() != Items.BOW) {
            ((BowKiller)this.module).cancelling = false;
            ((BowKiller)this.module).packetsSent = 0;
        } else if (ListenerMotion.mc.player.getActiveItemStack().getItem() == Items.BOW && ListenerMotion.mc.player.isHandActive() && ((BowKiller)this.module).cancelling && ((BowKiller)this.module).blockUnder) {
            ++((BowKiller)this.module).packetsSent;
            if (((BowKiller)this.module).packetsSent > ((BowKiller)this.module).runs.getValue() * 2 && !((BowKiller)this.module).always.getValue().booleanValue() && ((BowKiller)this.module).needsMessage) {
                ModuleUtil.sendMessage((Module)this.module, "\u00a7aCharged!");
            }
        }
    }

    private int findBlockInHotbar() {
        for (int i = 0; i < 9; ++i) {
            Block block;
            ItemStack stack = ListenerMotion.mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock) || !((block = ((ItemBlock)stack.getItem()).getBlock()) instanceof BlockObsidian)) continue;
            return i;
        }
        return -1;
    }

    private boolean canBeClicked(BlockPos pos) {
        return ListenerMotion.mc.world.getBlockState(pos).getBlock().canCollideCheck(ListenerMotion.mc.world.getBlockState(pos), false);
    }

    private void placeBlock(BlockPos pos, MotionUpdateEvent event) {
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbor = pos.offset(side);
            EnumFacing side2 = side.getOpposite();
            if (!this.canBeClicked(neighbor)) continue;
            Vec3d hitVec = new Vec3d((Vec3i)neighbor).add(new Vec3d(0.5, 0.5, 0.5)).add(new Vec3d(side2.getDirectionVec()).scale(0.5));
            float[] rotations = RotationUtil.getRotations(hitVec);
            event.setYaw(rotations[0]);
            event.setPitch(rotations[1]);
            ListenerMotion.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)ListenerMotion.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            ListenerMotion.mc.playerController.processRightClickBlock(ListenerMotion.mc.player, ListenerMotion.mc.world, neighbor, side2, hitVec, EnumHand.MAIN_HAND);
            ListenerMotion.mc.player.swingArm(EnumHand.MAIN_HAND);
            ListenerMotion.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)ListenerMotion.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            return;
        }
    }

    private boolean isBlockUnder() {
        return !(ListenerMotion.mc.world.getBlockState(PositionUtil.getPosition((Entity)RotationUtil.getRotationPlayer()).down(1)).getBlock() instanceof BlockAir);
    }
}

