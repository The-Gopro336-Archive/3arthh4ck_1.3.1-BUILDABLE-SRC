package me.earth.earthhack.impl.modules.movement.fastswim;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.fastswim.FastSwim;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

public class ListenerSetback
extends ModuleListener<FastSwim, PacketEvent.Receive<SPacketPlayerPosLook>> {
    public ListenerSetback(FastSwim module) {
        super(module, PacketEvent.Receive.class, SPacketPlayerPosLook.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketPlayerPosLook> event) {
        ((FastSwim)this.module).waterSpeed = ((FastSwim)this.module).hWater.getValue();
        ((FastSwim)this.module).lavaSpeed = ((FastSwim)this.module).hLava.getValue();
    }
}
