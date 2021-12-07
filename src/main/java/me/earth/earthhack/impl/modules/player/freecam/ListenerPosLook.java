package me.earth.earthhack.impl.modules.player.freecam;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.freecam.Freecam;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

final class ListenerPosLook
extends ModuleListener<Freecam, PacketEvent.Receive<SPacketPlayerPosLook>> {
    public ListenerPosLook(Freecam module) {
        super(module, PacketEvent.Receive.class, SPacketPlayerPosLook.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketPlayerPosLook> event) {
        mc.addScheduledTask(() -> {
            if (ListenerPosLook.mc.player == null) {
                return;
            }
            PacketUtil.handlePosLook((SPacketPlayerPosLook)event.getPacket(), ((Freecam)this.module).getPlayer() == null ? ListenerPosLook.mc.player : ((Freecam)this.module).getPlayer(), false);
            ((Freecam)this.module).getPlayer().onGround = true;
        });
        event.setCancelled(true);
    }
}
