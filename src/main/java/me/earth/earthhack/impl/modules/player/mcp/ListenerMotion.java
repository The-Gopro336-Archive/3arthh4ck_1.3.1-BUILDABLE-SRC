package me.earth.earthhack.impl.modules.player.mcp;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.player.mcp.MiddleClickPearl;

final class ListenerMotion
extends ModuleListener<MiddleClickPearl, MotionUpdateEvent> {
    public ListenerMotion(MiddleClickPearl module) {
        super(module, MotionUpdateEvent.class, Integer.MIN_VALUE);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (((MiddleClickPearl)this.module).runnable == null) {
            return;
        }
        if (event.getStage() == Stage.PRE) {
            event.setYaw(ListenerMotion.mc.player.rotationYaw);
            event.setPitch(ListenerMotion.mc.player.rotationPitch);
            Managers.ROTATION.setBlocking(true);
        } else {
            ((MiddleClickPearl)this.module).runnable.run();
            ((MiddleClickPearl)this.module).runnable = null;
            Managers.ROTATION.setBlocking(false);
        }
    }
}
