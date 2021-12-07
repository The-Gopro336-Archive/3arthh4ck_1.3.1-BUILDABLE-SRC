/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.client.CPacketEntityAction
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 */
package me.earth.earthhack.impl.modules.movement.phase;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.phase.Phase;
import net.minecraft.network.play.client.CPacketEntityAction;

public class ListenerSneak
extends ModuleListener<Phase, PacketEvent.Send<CPacketEntityAction>> {
    public ListenerSneak(Phase module) {
        super(module, PacketEvent.Send.class, CPacketEntityAction.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketEntityAction> event) {
        if (((CPacketEntityAction)event.getPacket()).getAction() == CPacketEntityAction.Action.START_SNEAKING && ((Phase)this.module).isPhasing() && ((Phase)this.module).cancelSneak.getValue().booleanValue() && ListenerSneak.mc.gameSettings.keyBindSneak.isKeyDown()) {
            event.setCancelled(true);
        }
    }
}

