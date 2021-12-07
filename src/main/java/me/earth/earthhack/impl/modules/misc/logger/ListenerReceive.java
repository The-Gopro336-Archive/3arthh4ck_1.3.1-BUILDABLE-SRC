/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.Packet
 */
package me.earth.earthhack.impl.modules.misc.logger;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.logger.Logger;
import net.minecraft.network.Packet;

final class ListenerReceive
extends ModuleListener<Logger, PacketEvent.Receive<?>> {
    public ListenerReceive(Logger module) {
        super(module, PacketEvent.Receive.class, Integer.MIN_VALUE);
    }

    @Override
    public void invoke(PacketEvent.Receive<?> event) {
        if (((Logger)this.module).incoming.getValue().booleanValue()) {
            ((Logger)this.module).logPacket((Packet<?>)event.getPacket(), "Receiving ", event.isCancelled());
        }
    }
}

