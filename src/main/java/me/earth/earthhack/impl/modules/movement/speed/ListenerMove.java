package me.earth.earthhack.impl.modules.movement.speed;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.packetfly.PacketFly;
import me.earth.earthhack.impl.modules.movement.speed.Speed;
import me.earth.earthhack.impl.modules.player.freecam.Freecam;
import me.earth.earthhack.impl.modules.player.ncptweaks.NCPTweaks;
import me.earth.earthhack.impl.util.math.position.PositionUtil;

final class ListenerMove
extends ModuleListener<Speed, MoveEvent> {
    private static final ModuleCache<PacketFly> PACKET_FLY = Caches.getModule(PacketFly.class);
    private static final ModuleCache<Freecam> FREECAM = Caches.getModule(Freecam.class);
    private static final ModuleCache<NCPTweaks> NCP_TWEAKS = Caches.getModule(NCPTweaks.class);

    public ListenerMove(Speed module) {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event) {
        if (PACKET_FLY.isEnabled() || FREECAM.isEnabled() || NCP_TWEAKS.isEnabled() && ((NCPTweaks)NCP_TWEAKS.get()).isSpeedStopped()) {
            return;
        }
        if (!((Speed)this.module).inWater.getValue().booleanValue() && (PositionUtil.inLiquid() || PositionUtil.inLiquid(true)) || ListenerMove.mc.player.isOnLadder() || ListenerMove.mc.player.isEntityInsideOpaqueBlock()) {
            ((Speed)this.module).stop = true;
            return;
        }
        if (((Speed)this.module).stop) {
            ((Speed)this.module).stop = false;
            return;
        }
        ((Speed)this.module).mode.getValue().move(event, (Speed)this.module);
    }
}
