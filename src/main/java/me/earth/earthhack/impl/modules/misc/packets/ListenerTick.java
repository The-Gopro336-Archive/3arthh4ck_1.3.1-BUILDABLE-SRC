/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayerDigging
 *  net.minecraft.network.play.client.CPacketPlayerDigging$Action
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.modules.misc.packets;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.packets.Packets;
import me.earth.earthhack.impl.modules.misc.packets.util.BookCrashMode;
import net.minecraft.network.Packet;
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
            ListenerTick.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.SWAP_HELD_ITEMS, BlockPos.ORIGIN, EnumFacing.UP));
        }
    }
}

