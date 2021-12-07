/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Blocks
 *  net.minecraft.network.play.server.SPacketMultiBlockChange
 *  net.minecraft.network.play.server.SPacketMultiBlockChange$BlockUpdateData
 */
package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.modules.player.speedmine.mode.MineMode;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketMultiBlockChange;

final class ListenerMultiBlockChange
extends ModuleListener<Speedmine, PacketEvent.Receive<SPacketMultiBlockChange>> {
    public ListenerMultiBlockChange(Speedmine module) {
        super(module, PacketEvent.Receive.class, SPacketMultiBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketMultiBlockChange> event) {
        SPacketMultiBlockChange packet = (SPacketMultiBlockChange)event.getPacket();
        if ((((Speedmine)this.module).mode.getValue() != MineMode.Smart || ((Speedmine)this.module).sentPacket) && ((Speedmine)this.module).mode.getValue() != MineMode.Instant && ((Speedmine)this.module).mode.getValue() != MineMode.Civ) {
            for (SPacketMultiBlockChange.BlockUpdateData data : packet.getChangedBlocks()) {
                if (!data.getPos().equals((Object)((Speedmine)this.module).pos) || data.getBlockState().getBlock() != Blocks.AIR) continue;
                mc.addScheduledTask(((Speedmine)this.module)::reset);
            }
        }
    }
}

