package me.earth.earthhack.impl.modules.combat.surround;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.surround.ListenerMotion;
import me.earth.earthhack.impl.modules.combat.surround.Surround;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketMultiBlockChange;

final class ListenerMultiBlockChange
extends ModuleListener<Surround, PacketEvent.Receive<SPacketMultiBlockChange>> {
    public ListenerMultiBlockChange(Surround module) {
        super(module, PacketEvent.Receive.class, SPacketMultiBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketMultiBlockChange> event) {
        SPacketMultiBlockChange packet = (SPacketMultiBlockChange)event.getPacket();
        event.addPostEvent(() -> {
            boolean instant = false;
            for (SPacketMultiBlockChange.BlockUpdateData data : packet.getChangedBlocks()) {
                if (!((Surround)this.module).targets.contains(data.getPos())) continue;
                if (data.getBlockState().getBlock() == Blocks.AIR) {
                    ((Surround)this.module).confirmed.remove(data.getPos());
                    if (!((Surround)this.module).shouldInstant(false) || instant) continue;
                    instant = true;
                    ListenerMotion.start((Surround)this.module);
                    continue;
                }
                if (data.getBlockState().func_185904_a().isReplaceable()) continue;
                ((Surround)this.module).confirmed.add(data.getPos());
            }
        });
    }
}
