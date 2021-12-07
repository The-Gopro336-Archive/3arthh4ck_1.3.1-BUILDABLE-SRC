package me.earth.earthhack.impl.modules.player.timer;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.timer.Timer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

final class ListenerPosLook
extends ModuleListener<Timer, PacketEvent.Receive<SPacketPlayerPosLook>> {
    public ListenerPosLook(Timer module) {
        super(module, PacketEvent.Receive.class, SPacketPlayerPosLook.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketPlayerPosLook> event) {
        ((Timer)this.module).packets = 0;
        ((Timer)this.module).sent = 0;
        ((Timer)this.module).pSpeed = 1.0f;
    }
}
