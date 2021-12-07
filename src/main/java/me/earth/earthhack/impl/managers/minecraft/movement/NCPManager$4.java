package me.earth.earthhack.impl.managers.minecraft.movement;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.play.client.CPacketClickWindow;

class NCPManager$4
extends EventListener<PacketEvent.Post<CPacketClickWindow>> {
    NCPManager$4(Class target, int priority, Class type) {
        super(target, priority, type);
    }

    @Override
    public void invoke(PacketEvent.Post<CPacketClickWindow> event) {
        NCPManager.this.clickTimer.reset();
        if (!NCPManager.this.windowClicks && NCPManager.this.isStrict()) {
            NCPManager.this.release();
        }
    }
}
