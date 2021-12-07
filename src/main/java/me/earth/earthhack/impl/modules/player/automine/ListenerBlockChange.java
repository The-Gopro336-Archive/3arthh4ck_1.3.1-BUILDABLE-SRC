/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.server.SPacketBlockChange
 */
package me.earth.earthhack.impl.modules.player.automine;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.automine.AutoMine;
import net.minecraft.network.play.server.SPacketBlockChange;

final class ListenerBlockChange
extends ModuleListener<AutoMine, PacketEvent.Receive<SPacketBlockChange>> {
    public ListenerBlockChange(AutoMine module) {
        super(module, PacketEvent.Receive.class, SPacketBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketBlockChange> event) {
        SPacketBlockChange packet = (SPacketBlockChange)event.getPacket();
        mc.addScheduledTask(() -> {
            if (((AutoMine)this.module).constellation != null && ((AutoMine)this.module).constellation.isAffected(packet.getBlockPosition(), packet.getBlockState())) {
                ((AutoMine)this.module).constellation = null;
            }
        });
    }
}

