/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.client.CPacketHeldItemChange
 *  net.minecraft.network.play.server.SPacketHeldItemChange
 */
package me.earth.earthhack.impl.managers.minecraft.combat;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketHeldItemChange;

public class SwitchManager
extends SubscriberImpl {
    private final StopWatch timer = new StopWatch();
    private volatile int last_slot;

    public SwitchManager() {
        this.listeners.add(new EventListener<PacketEvent.Post<CPacketHeldItemChange>>(PacketEvent.Post.class, CPacketHeldItemChange.class){

            @Override
            public void invoke(PacketEvent.Post<CPacketHeldItemChange> event) {
                SwitchManager.this.timer.reset();
                SwitchManager.this.last_slot = ((CPacketHeldItemChange)event.getPacket()).getSlotId();
            }
        });
        this.listeners.add(new EventListener<PacketEvent.Receive<SPacketHeldItemChange>>(PacketEvent.Receive.class, SPacketHeldItemChange.class){

            @Override
            public void invoke(PacketEvent.Receive<SPacketHeldItemChange> event) {
                SwitchManager.this.last_slot = ((SPacketHeldItemChange)event.getPacket()).getHeldItemHotbarIndex();
            }
        });
    }

    public long getLastSwitch() {
        return this.timer.getTime();
    }

    public int getSlot() {
        return this.last_slot;
    }
}

