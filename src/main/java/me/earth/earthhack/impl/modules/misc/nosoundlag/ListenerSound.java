package me.earth.earthhack.impl.modules.misc.nosoundlag;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.nosoundlag.NoSoundLag;
import net.minecraft.network.play.server.SPacketSoundEffect;

final class ListenerSound
extends ModuleListener<NoSoundLag, PacketEvent.Receive<SPacketSoundEffect>> {
    public ListenerSound(NoSoundLag module) {
        super(module, PacketEvent.Receive.class, SPacketSoundEffect.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSoundEffect> event) {
        if (((NoSoundLag)this.module).sounds.getValue().booleanValue() && NoSoundLag.SOUNDS.contains(((SPacketSoundEffect)event.getPacket()).getSound())) {
            event.setCancelled(true);
        }
    }
}
