/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.server.SPacketTimeUpdate
 */
package me.earth.earthhack.impl.managers.minecraft;

import java.util.ArrayDeque;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.play.server.SPacketTimeUpdate;

public class TPSManager
extends SubscriberImpl {
    private final ArrayDeque<Float> queue = new ArrayDeque(20);
    private long time;
    private float tps;

    public TPSManager() {
        this.listeners.add(new EventListener<PacketEvent.Receive<SPacketTimeUpdate>>(PacketEvent.Receive.class, SPacketTimeUpdate.class){

            @Override
            public void invoke(PacketEvent.Receive<SPacketTimeUpdate> event) {
                if (TPSManager.this.time != 0L) {
                    if (TPSManager.this.queue.size() > 20) {
                        TPSManager.this.queue.poll();
                    }
                    TPSManager.this.queue.add(Float.valueOf(20.0f * (1000.0f / (float)(System.currentTimeMillis() - TPSManager.this.time))));
                    float factor = 0.0f;
                    for (Float qTime : TPSManager.this.queue) {
                        factor += Math.max(0.0f, Math.min(20.0f, qTime.floatValue()));
                    }
                    TPSManager.this.tps = (factor /= (float)TPSManager.this.queue.size());
                }
                TPSManager.this.time = System.currentTimeMillis();
            }
        });
    }

    public float getTps() {
        return this.tps;
    }

    public float getFactor() {
        return this.tps / 20.0f;
    }
}

