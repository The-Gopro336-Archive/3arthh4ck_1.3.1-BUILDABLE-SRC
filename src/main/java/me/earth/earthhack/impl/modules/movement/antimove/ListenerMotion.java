package me.earth.earthhack.impl.modules.movement.antimove;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.antimove.NoMove;
import me.earth.earthhack.impl.modules.movement.antimove.modes.StaticMode;
import net.minecraft.network.play.client.CPacketPlayer;

final class ListenerMotion
extends ModuleListener<NoMove, MotionUpdateEvent> {
    public ListenerMotion(NoMove module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (event.getStage() == Stage.PRE && ((NoMove)this.module).mode.getValue() == StaticMode.Roof) {
            ListenerMotion.mc.player.connection.sendPacket(new CPacketPlayer.Position(ListenerMotion.mc.player.posX, 10000.0, ListenerMotion.mc.player.posZ, ListenerMotion.mc.player.onGround));
            ((NoMove)this.module).disable();
        }
    }
}
