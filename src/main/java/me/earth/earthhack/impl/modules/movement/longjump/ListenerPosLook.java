package me.earth.earthhack.impl.modules.movement.longjump;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.longjump.LongJump;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

final class ListenerPosLook
extends ModuleListener<LongJump, PacketEvent.Receive<SPacketPlayerPosLook>> {
    public ListenerPosLook(LongJump module) {
        super(module, PacketEvent.Receive.class, SPacketPlayerPosLook.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketPlayerPosLook> event) {
        if (((LongJump)this.module).noKick.getValue().booleanValue()) {
            mc.addScheduledTask(((LongJump)this.module)::disable);
        }
        ((LongJump)this.module).speed = 0.0;
        ((LongJump)this.module).stage = 0;
        ((LongJump)this.module).airTicks = 0;
        ((LongJump)this.module).groundTicks = 0;
    }
}
