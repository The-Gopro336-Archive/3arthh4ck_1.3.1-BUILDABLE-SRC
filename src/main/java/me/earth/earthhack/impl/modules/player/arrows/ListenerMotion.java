/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemSpectralArrow
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.PotionType
 *  net.minecraft.potion.PotionUtils
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 */
package me.earth.earthhack.impl.modules.player.arrows;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.player.arrows.Arrows;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSpectralArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

final class ListenerMotion
extends ModuleListener<Arrows, MotionUpdateEvent> {
    private PotionType lastType;
    private long lastDown;

    public ListenerMotion(Arrows module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        ItemStack arrow;
        EnumHand hand = InventoryUtil.getHand((Item)Items.BOW);
        if (!((Arrows)this.module).shoot.getValue().booleanValue() || ListenerMotion.mc.player.isCreative() || ListenerMotion.mc.currentScreen != null || hand == null || (arrow = ((Arrows)this.module).findArrow()).isEmpty() || this.blocked()) {
            return;
        }
        boolean cycle = ((Arrows)this.module).cycle.getValue();
        if (((Arrows)this.module).badStack(arrow) || ((Arrows)this.module).fast && cycle) {
            if (!cycle) {
                return;
            }
            ((Arrows)this.module).cycle(false, true);
            ((Arrows)this.module).fast = false;
            arrow = ((Arrows)this.module).findArrow();
            if (((Arrows)this.module).badStack(arrow)) {
                return;
            }
        }
        if (event.getStage() == Stage.PRE) {
            if (ListenerMotion.mc.gameSettings.keyBindUseItem.isKeyDown()) {
                this.lastDown = System.currentTimeMillis();
            } else if (System.currentTimeMillis() - this.lastDown > 100L) {
                return;
            }
            EntityPlayer player = RotationUtil.getRotationPlayer();
            if (player.motionX != 0.0 || player.motionZ != 0.0) {
                Vec3d vec3d = player.getPositionVector().add(player.motionX, player.motionY + (double)player.getEyeHeight(), player.motionZ);
                float[] rotations = RotationUtil.getRotations(vec3d);
                event.setYaw(rotations[0]);
                event.setPitch(rotations[1]);
            } else {
                event.setPitch(-90.0f);
            }
        } else if (((Arrows)this.module).autoRelease.getValue().booleanValue() && !ListenerMotion.mc.player.getActiveItemStack().isEmpty()) {
            PotionType type = PotionUtils.getPotionFromItem((ItemStack)arrow);
            if (arrow.getItem() instanceof ItemSpectralArrow) {
                type = Arrows.SPECTRAL;
            }
            if (this.lastType == type && !((Arrows)this.module).timer.passed(((Arrows)this.module).shootDelay.getValue().intValue())) {
                return;
            }
            this.lastType = type;
            float ticks = (float)(ListenerMotion.mc.player.getHeldItem(hand).getMaxItemUseDuration() - ListenerMotion.mc.player.getItemInUseCount()) - (((Arrows)this.module).tpsSync.getValue() != false ? 20.0f - Managers.TPS.getTps() : 0.0f);
            if (ticks >= (float)((Arrows)this.module).releaseTicks.getValue().intValue() && ticks <= (float)((Arrows)this.module).maxTicks.getValue().intValue()) {
                Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> ListenerMotion.mc.playerController.onStoppedUsingItem((EntityPlayer)ListenerMotion.mc.player));
                ((Arrows)this.module).fast = ((Arrows)this.module).preCycle.getValue() != false && cycle;
                ((Arrows)this.module).timer.reset();
            }
        }
    }

    private boolean blocked() {
        BlockPos pos = PositionUtil.getPosition();
        return ListenerMotion.mc.world.getBlockState(pos.up()).getMaterial().blocksMovement() || ListenerMotion.mc.world.getBlockState(pos.up(2)).getMaterial().blocksMovement();
    }
}

