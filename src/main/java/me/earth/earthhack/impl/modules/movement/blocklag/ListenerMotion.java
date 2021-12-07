package me.earth.earthhack.impl.modules.movement.blocklag;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.movement.blocklag.BlockLag;
import me.earth.earthhack.impl.modules.player.freecam.Freecam;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.SpecialBlocks;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

final class ListenerMotion
extends ModuleListener<BlockLag, MotionUpdateEvent> {
    public ListenerMotion(BlockLag module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        int slot;
        BlockPos upUp;
        IBlockState upState;
        BlockPos pos;
        BlockPos currentPos;
        if (event.getStage() == Stage.PRE && ((BlockLag)this.module).isInsideBlock() && ((BlockLag)this.module).bypass.getValue().booleanValue()) {
            event.setY(event.getY() - ((BlockLag)this.module).bypassOffset.getValue());
            event.setOnGround(false);
        }
        if (!((BlockLag)this.module).timer.passed(((BlockLag)this.module).delay.getValue().intValue()) || !((BlockLag)this.module).stage.getValue().shouldBlockLag(event.getStage())) {
            return;
        }
        if (((BlockLag)this.module).wait.getValue().booleanValue() && !(currentPos = ((BlockLag)this.module).getPlayerPos()).equals(((BlockLag)this.module).startPos)) {
            ((BlockLag)this.module).disable();
            return;
        }
        if (((BlockLag)this.module).isInsideBlock()) {
            return;
        }
        EntityPlayer rEntity = ListenerMotion.mc.player;
        if (BlockLag.FREECAM.isEnabled()) {
            if (!((BlockLag)this.module).freecam.getValue().booleanValue()) {
                ((BlockLag)this.module).disable();
                return;
            }
            rEntity = ((Freecam)BlockLag.FREECAM.get()).getPlayer();
            if (rEntity == null) {
                rEntity = ListenerMotion.mc.player;
            }
        }
        if (!ListenerMotion.mc.world.getBlockState(pos = PositionUtil.getPosition(rEntity)).func_185904_a().isReplaceable()) {
            if (!((BlockLag)this.module).wait.getValue().booleanValue()) {
                ((BlockLag)this.module).disable();
            }
            return;
        }
        BlockPos posHead = PositionUtil.getPosition(rEntity).up().up();
        if (!ListenerMotion.mc.world.getBlockState(posHead).func_185904_a().isReplaceable() && ((BlockLag)this.module).wait.getValue().booleanValue()) {
            return;
        }
        CPacketUseEntity attacking = null;
        boolean crystals = false;
        float currentDmg = Float.MAX_VALUE;
        for (Entity entity : ListenerMotion.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
            if (entity == null || rEntity.equals(entity) || ListenerMotion.mc.player.equals(entity) || EntityUtil.isDead(entity) || !entity.preventEntitySpawning) continue;
            if (entity instanceof EntityEnderCrystal && ((BlockLag)this.module).attack.getValue().booleanValue() && Managers.SWITCH.getLastSwitch() >= (long)((BlockLag)this.module).cooldown.getValue().intValue()) {
                float damage = DamageUtil.calculate(entity, (EntityLivingBase)ListenerMotion.mc.player);
                if (damage < currentDmg) {
                    currentDmg = damage;
                    if (((BlockLag)this.module).pop.getValue().shouldPop(damage, ((BlockLag)this.module).popTime.getValue())) {
                        attacking = new CPacketUseEntity(entity);
                        continue;
                    }
                }
                crystals = true;
                continue;
            }
            if (!((BlockLag)this.module).wait.getValue().booleanValue()) {
                ((BlockLag)this.module).disable();
            }
            return;
        }
        int weaknessSlot = -1;
        if (crystals) {
            if (attacking == null) {
                if (!((BlockLag)this.module).wait.getValue().booleanValue()) {
                    ((BlockLag)this.module).disable();
                }
                return;
            }
            if (!(DamageUtil.canBreakWeakness(true) || ((BlockLag)this.module).antiWeakness.getValue().booleanValue() && ((BlockLag)this.module).cooldown.getValue() == 0 && (weaknessSlot = DamageUtil.findAntiWeakness()) != -1)) {
                if (!((BlockLag)this.module).wait.getValue().booleanValue()) {
                    ((BlockLag)this.module).disable();
                }
                return;
            }
        }
        if (!((BlockLag)this.module).allowUp.getValue().booleanValue() && (upState = ListenerMotion.mc.world.getBlockState(upUp = pos.up(2))).func_185904_a().blocksMovement()) {
            if (!((BlockLag)this.module).wait.getValue().booleanValue()) {
                ((BlockLag)this.module).disable();
            }
            return;
        }
        int n = ((BlockLag)this.module).anvil.getValue() != false ? InventoryUtil.findHotbarBlock(Blocks.ANVIL, new Block[0]) : (((BlockLag)this.module).beacon.getValue() != false ? InventoryUtil.findHotbarBlock(Blocks.BEACON, new Block[0]) : (slot = ((BlockLag)this.module).echest.getValue() != false || ListenerMotion.mc.world.getBlockState(pos.down()).getBlock() == Blocks.ENDER_CHEST ? InventoryUtil.findHotbarBlock(Blocks.ENDER_CHEST, Blocks.OBSIDIAN) : InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN, Blocks.ENDER_CHEST)));
        if (slot == -1) {
            ModuleUtil.disableRed((Module)this.module, "No Block found!");
            return;
        }
        EnumFacing f = BlockUtil.getFacing(pos);
        if (f == null) {
            if (!((BlockLag)this.module).wait.getValue().booleanValue()) {
                ((BlockLag)this.module).disable();
            }
            return;
        }
        double y = ((BlockLag)this.module).applyScale(((BlockLag)this.module).getY(rEntity, ((BlockLag)this.module).offsetMode.getValue()));
        if (Double.isNaN(y)) {
            return;
        }
        BlockPos on = pos.offset(f);
        float[] r = RotationUtil.getRotations(on, f.getOpposite(), rEntity);
        RayTraceResult result = RayTraceUtil.getRayTraceResultWithEntity(r[0], r[1], rEntity);
        float[] vec = RayTraceUtil.hitVecToPlaceVec(on, result.hitVec);
        boolean sneaking = !SpecialBlocks.shouldSneak(on, true);
        EntityPlayer finalREntity = rEntity;
        int finalWeaknessSlot = weaknessSlot;
        CPacketUseEntity finalAttacking = attacking;
        if (((BlockLag)this.module).singlePlayerCheck(pos)) {
            if (!((BlockLag)this.module).wait.getValue().booleanValue() || ((BlockLag)this.module).placeDisable.getValue().booleanValue()) {
                ((BlockLag)this.module).disable();
            }
            return;
        }
        Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> {
            int lastSlot = ListenerMotion.mc.player.inventory.currentItem;
            if (((BlockLag)this.module).attackBefore.getValue().booleanValue() && finalAttacking != null) {
                ((BlockLag)this.module).attack(finalAttacking, finalWeaknessSlot);
            }
            if (((BlockLag)this.module).conflict.getValue().booleanValue() || ((BlockLag)this.module).rotate.getValue().booleanValue()) {
                if (((BlockLag)this.module).rotate.getValue().booleanValue()) {
                    if (finalREntity.getPositionVector().equals(Managers.POSITION.getVec())) {
                        PacketUtil.doRotation(r[0], r[1], true);
                    } else {
                        PacketUtil.doPosRot(finalREntity.posX, finalREntity.posY, finalREntity.posZ, r[0], r[1], true);
                    }
                } else {
                    PacketUtil.doPosition(finalREntity.posX, finalREntity.posY, finalREntity.posZ, true);
                }
            }
            PacketUtil.doY(finalREntity, finalREntity.posY + 0.42, ((BlockLag)this.module).onGround.getValue());
            PacketUtil.doY(finalREntity, finalREntity.posY + 0.75, ((BlockLag)this.module).onGround.getValue());
            PacketUtil.doY(finalREntity, finalREntity.posY + 1.01, ((BlockLag)this.module).onGround.getValue());
            PacketUtil.doY(finalREntity, finalREntity.posY + 1.16, ((BlockLag)this.module).onGround.getValue());
            if (((BlockLag)this.module).highBlock.getValue().booleanValue()) {
                // empty if block
            }
            if (!((BlockLag)this.module).attackBefore.getValue().booleanValue() && finalAttacking != null) {
                ((BlockLag)this.module).attack(finalAttacking, finalWeaknessSlot);
            }
            InventoryUtil.switchTo(slot);
            if (!sneaking) {
                ListenerMotion.mc.player.connection.sendPacket(new CPacketEntityAction(ListenerMotion.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            }
            PacketUtil.place(on, f.getOpposite(), slot, vec[0], vec[1], vec[2]);
            if (((BlockLag)this.module).highBlock.getValue().booleanValue()) {
                PacketUtil.doY(finalREntity, finalREntity.posY + 1.67, ((BlockLag)this.module).onGround.getValue());
                PacketUtil.doY(finalREntity, finalREntity.posY + 2.01, ((BlockLag)this.module).onGround.getValue());
                PacketUtil.doY(finalREntity, finalREntity.posY + 2.42, ((BlockLag)this.module).onGround.getValue());
                BlockPos highPos = pos.up();
                EnumFacing face = EnumFacing.DOWN;
                PacketUtil.place(highPos.offset(face), face.getOpposite(), slot, vec[0], vec[1], vec[2]);
            }
            PacketUtil.swing(slot);
            InventoryUtil.switchTo(lastSlot);
        });
        if (!sneaking) {
            ListenerMotion.mc.player.connection.sendPacket(new CPacketEntityAction(ListenerMotion.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }
        PacketUtil.doY(rEntity, y, false);
        ((BlockLag)this.module).timer.reset();
        if (!((BlockLag)this.module).wait.getValue().booleanValue() || ((BlockLag)this.module).placeDisable.getValue().booleanValue()) {
            ((BlockLag)this.module).disable();
        }
    }
}
