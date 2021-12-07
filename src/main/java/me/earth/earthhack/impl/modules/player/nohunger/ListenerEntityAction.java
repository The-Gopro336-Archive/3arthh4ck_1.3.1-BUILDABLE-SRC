/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.client.CPacketEntityAction
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 */
package me.earth.earthhack.impl.modules.player.nohunger;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.nohunger.NoHunger;
import net.minecraft.network.play.client.CPacketEntityAction;

final class ListenerEntityAction
extends ModuleListener<NoHunger, PacketEvent.Send<CPacketEntityAction>> {
    public ListenerEntityAction(NoHunger module) {
        super(module, PacketEvent.Send.class, CPacketEntityAction.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketEntityAction> event) {
        CPacketEntityAction p;
        if (((NoHunger)this.module).sprint.getValue().booleanValue() && ((p = (CPacketEntityAction)event.getPacket()).getAction() == CPacketEntityAction.Action.START_SPRINTING || p.getAction() == CPacketEntityAction.Action.STOP_SPRINTING)) {
            event.setCancelled(true);
        }
    }
}

