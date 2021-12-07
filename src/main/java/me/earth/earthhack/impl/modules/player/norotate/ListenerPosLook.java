/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.network.play.server.SPacketPlayerPosLook
 */
package me.earth.earthhack.impl.modules.player.norotate;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.norotate.NoRotate;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

final class ListenerPosLook
extends ModuleListener<NoRotate, PacketEvent.Receive<SPacketPlayerPosLook>> {
    public ListenerPosLook(NoRotate module) {
        super(module, PacketEvent.Receive.class, -5, SPacketPlayerPosLook.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketPlayerPosLook> event) {
        if (((NoRotate)this.module).noForceLook.getValue().booleanValue() && !event.isCancelled()) {
            event.setCancelled(true);
            if (((NoRotate)this.module).async.getValue().booleanValue()) {
                PacketUtil.handlePosLook((SPacketPlayerPosLook)event.getPacket(), (Entity)ListenerPosLook.mc.player, true);
            } else {
                mc.addScheduledTask(() -> PacketUtil.handlePosLook((SPacketPlayerPosLook)event.getPacket(), (Entity)ListenerPosLook.mc.player, true));
            }
        }
    }
}

