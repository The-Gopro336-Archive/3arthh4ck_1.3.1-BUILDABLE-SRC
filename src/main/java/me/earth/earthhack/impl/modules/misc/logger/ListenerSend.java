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
import me.earth.earthhack.impl.modules.misc.logger.util.LoggerMode;
import net.minecraft.network.Packet;

final class ListenerSend
extends ModuleListener<Logger, PacketEvent.Send<?>> {
    public ListenerSend(Logger module) {
        super(module, PacketEvent.Send.class, Integer.MIN_VALUE);
    }

    @Override
    public void invoke(PacketEvent.Send<?> event) {
        if (((Logger)this.module).outgoing.getValue().booleanValue() && ((Logger)this.module).mode.getValue() == LoggerMode.Normal) {
            ((Logger)this.module).logPacket((Packet<?>)event.getPacket(), "Sending ", event.isCancelled());
        }
    }
}

