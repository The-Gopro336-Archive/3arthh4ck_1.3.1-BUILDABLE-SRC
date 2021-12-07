/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockLiquid
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.modules.movement.jesus;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.jesus.Jesus;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.util.math.BlockPos;

final class ListenerMotion
extends ModuleListener<Jesus, MotionUpdateEvent> {
    public ListenerMotion(Jesus module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (ListenerMotion.mc.player.isDead || ListenerMotion.mc.player.isSneaking() || !((Jesus)this.module).timer.passed(800L)) {
            return;
        }
        switch (((Jesus)this.module).mode.getValue()) {
            case Dolphin: {
                if (PositionUtil.inLiquid() && ListenerMotion.mc.player.fallDistance < 3.0f && !ListenerMotion.mc.player.isSneaking()) {
                    ListenerMotion.mc.player.motionY = 0.1;
                }
                return;
            }
            case Trampoline: {
                if (event.getStage() != Stage.PRE) break;
                if (PositionUtil.inLiquid(false) && !ListenerMotion.mc.player.isSneaking()) {
                    ListenerMotion.mc.player.onGround = false;
                }
                Block block = ListenerMotion.mc.world.getBlockState(new BlockPos(ListenerMotion.mc.player.posX, ListenerMotion.mc.player.posY, ListenerMotion.mc.player.posZ)).getBlock();
                if (((Jesus)this.module).jumped && !ListenerMotion.mc.player.capabilities.isFlying && !ListenerMotion.mc.player.isInWater()) {
                    if (ListenerMotion.mc.player.motionY < -0.3 || ListenerMotion.mc.player.onGround || ListenerMotion.mc.player.isOnLadder()) {
                        ((Jesus)this.module).jumped = false;
                        return;
                    }
                    ListenerMotion.mc.player.motionY = ListenerMotion.mc.player.motionY / (double)0.98f + 0.08;
                    ListenerMotion.mc.player.motionY -= 0.03120000000005;
                }
                if (ListenerMotion.mc.player.isInWater() || ListenerMotion.mc.player.isInLava()) {
                    ListenerMotion.mc.player.motionY = 0.1;
                    break;
                }
                if (ListenerMotion.mc.player.isInLava() || !(block instanceof BlockLiquid) || !(ListenerMotion.mc.player.motionY < 0.2)) break;
                ListenerMotion.mc.player.motionY = 0.5;
                ((Jesus)this.module).jumped = true;
                break;
            }
        }
        if (event.getStage() == Stage.PRE && !PositionUtil.inLiquid() && PositionUtil.inLiquid(true) && !PositionUtil.isMovementBlocked() && ListenerMotion.mc.player.ticksExisted % 2 == 0) {
            event.setY(event.getY() + 0.02);
        }
    }
}

