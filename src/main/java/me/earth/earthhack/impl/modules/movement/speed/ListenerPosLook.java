package me.earth.earthhack.impl.modules.movement.speed;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.movement.speed.Speed;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

final class ListenerPosLook
extends ModuleListener<Speed, PacketEvent.Receive<SPacketPlayerPosLook>> {
    public ListenerPosLook(Speed module) {
        super(module, PacketEvent.Receive.class, SPacketPlayerPosLook.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketPlayerPosLook> event) {
        if (ListenerPosLook.mc.player != null) {
            ((Speed)this.module).distance = 0.0;
            ((Speed)this.module).speed = 0.0;
        }
        ((Speed)this.module).speed = 0.0;
        ((Speed)this.module).vanillaStage = 0;
        ((Speed)this.module).onGroundStage = 2;
        ((Speed)this.module).ncpStage = 1;
        ((Speed)this.module).gayStage = 1;
        ((Speed)this.module).vStage = 1;
        ((Speed)this.module).bhopStage = 4;
        ((Speed)this.module).stage = 4;
        ((Speed)this.module).lowStage = 4;
        ((Speed)this.module).constStage = 0;
        Managers.TIMER.setTimer(1.0f);
    }
}
