package me.earth.earthhack.impl.modules.misc.packets;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.packets.Packets;
import me.earth.earthhack.impl.modules.misc.packets.util.BookCrashMode;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

final class ListenerTick
extends ModuleListener<Packets, TickEvent> {
    public ListenerTick(Packets module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        if (!event.isSafe()) {
            ((Packets)this.module).stateMap.clear();
            return;
        }
        if (!((Packets)this.module).crashing.get() && ((Packets)this.module).bookCrash.getValue() != BookCrashMode.None) {
            ((Packets)this.module).startCrash();
        }
        for (int i = 0; i < ((Packets)this.module).offhandCrashes.getValue(); ++i) {
            ListenerTick.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.SWAP_HELD_ITEMS, BlockPos.ORIGIN, EnumFacing.UP));
        }
    }
}
