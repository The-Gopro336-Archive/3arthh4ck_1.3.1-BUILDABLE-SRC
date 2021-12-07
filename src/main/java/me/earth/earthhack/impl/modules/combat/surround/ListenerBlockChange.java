package me.earth.earthhack.impl.modules.combat.surround;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.surround.ListenerMotion;
import me.earth.earthhack.impl.modules.combat.surround.Surround;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockChange;

final class ListenerBlockChange
extends ModuleListener<Surround, PacketEvent.Receive<SPacketBlockChange>> {
    public ListenerBlockChange(Surround module) {
        super(module, PacketEvent.Receive.class, SPacketBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketBlockChange> event) {
        SPacketBlockChange packet = (SPacketBlockChange)event.getPacket();
        event.addPostEvent(() -> {
            if (((Surround)this.module).targets.contains(packet.getBlockPosition())) {
                if (packet.getBlockState().getBlock() == Blocks.AIR) {
                    ((Surround)this.module).confirmed.remove(packet.getBlockPosition());
                    if (((Surround)this.module).shouldInstant(false)) {
                        ListenerMotion.start((Surround)this.module);
                    }
                } else if (!packet.getBlockState().func_185904_a().isReplaceable()) {
                    ((Surround)this.module).confirmed.add(packet.getBlockPosition());
                }
            }
        });
    }
}
