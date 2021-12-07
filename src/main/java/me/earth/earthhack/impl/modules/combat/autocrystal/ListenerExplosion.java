package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import net.minecraft.network.play.server.SPacketExplosion;

final class ListenerExplosion
extends ModuleListener<AutoCrystal, PacketEvent.Receive<SPacketExplosion>> {
    public ListenerExplosion(AutoCrystal module) {
        super(module, PacketEvent.Receive.class, Integer.MIN_VALUE, SPacketExplosion.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketExplosion> event) {
        if (((AutoCrystal)this.module).explosionThread.getValue().booleanValue() && !((SPacketExplosion)event.getPacket()).getAffectedBlockPositions().isEmpty()) {
            ((AutoCrystal)this.module).threadHelper.schedulePacket(event);
        }
    }
}
