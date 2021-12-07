package me.earth.earthhack.impl.modules.combat.surround;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.surround.ListenerMotion;
import me.earth.earthhack.impl.modules.combat.surround.Surround;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.util.math.BlockPos;

final class ListenerExplosion
extends ModuleListener<Surround, PacketEvent.Receive<SPacketExplosion>> {
    public ListenerExplosion(Surround module) {
        super(module, PacketEvent.Receive.class, SPacketExplosion.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketExplosion> event) {
        SPacketExplosion packet = (SPacketExplosion)event.getPacket();
        event.addPostEvent(() -> {
            for (BlockPos pos : packet.getAffectedBlockPositions()) {
                ((Surround)this.module).confirmed.remove(pos);
                if (!((Surround)this.module).shouldInstant(false)) continue;
                ListenerMotion.start((Surround)this.module);
            }
        });
    }
}
