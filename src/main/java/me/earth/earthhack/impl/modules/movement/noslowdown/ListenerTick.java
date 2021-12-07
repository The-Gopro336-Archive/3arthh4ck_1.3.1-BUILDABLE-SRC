/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBow
 *  net.minecraft.item.ItemFood
 *  net.minecraft.item.ItemPotion
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketEntityAction
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 *  net.minecraft.network.play.client.CPacketPlayerDigging
 *  net.minecraft.network.play.client.CPacketPlayerDigging$Action
 *  net.minecraft.util.EnumFacing
 */
package me.earth.earthhack.impl.modules.movement.noslowdown;

import me.earth.earthhack.impl.core.ducks.entity.IEntity;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.movement.noslowdown.NoSlowDown;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;

final class ListenerTick
extends ModuleListener<NoSlowDown, TickEvent> {
    public ListenerTick(NoSlowDown module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        Managers.NCP.setStrict(((NoSlowDown)this.module).guiMove.getValue() != false && ((NoSlowDown)this.module).legit.getValue() != false);
        if (event.isSafe() && ((NoSlowDown)this.module).legit.getValue().booleanValue() && ((NoSlowDown)this.module).items.getValue().booleanValue()) {
            Item item = ListenerTick.mc.player.getActiveItemStack().getItem();
            if (MovementUtil.isMoving() && item instanceof ItemFood || item instanceof ItemBow || item instanceof ItemPotion) {
                ListenerTick.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, ListenerTick.mc.player.getPosition(), EnumFacing.DOWN));
            }
            if (!ListenerTick.mc.player.isHandActive() && Managers.ACTION.isSprinting() && ((NoSlowDown)this.module).sneakPacket.getValue().booleanValue()) {
                ListenerTick.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)ListenerTick.mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
            }
            if (((IEntity)ListenerTick.mc.player).inWeb() && !ListenerTick.mc.player.onGround && ((NoSlowDown)this.module).useTimerWeb.getValue().booleanValue()) {
                Managers.TIMER.setTimer(((NoSlowDown)this.module).timerSpeed.getValue().floatValue());
            } else {
                Managers.TIMER.reset();
            }
        }
    }
}

