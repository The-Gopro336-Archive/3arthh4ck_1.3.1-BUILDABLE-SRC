package me.earth.earthhack.impl.managers.minecraft;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.play.server.SPacketTimeUpdate;

class TPSManager$1
extends EventListener<PacketEvent.Receive<SPacketTimeUpdate>> {
    TPSManager$1(Class target, Class type) {
        super(target, type);
    }

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
}
