package me.earth.earthhack.impl.modules.movement.antimove;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.antimove.NoMove;
import me.earth.earthhack.impl.modules.movement.antimove.modes.StaticMode;
import me.earth.earthhack.impl.modules.movement.packetfly.PacketFly;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

final class ListenerUpdate
extends ModuleListener<NoMove, UpdateEvent> {
    private static final ModuleCache<PacketFly> PACKET_FLY = Caches.getModule(PacketFly.class);

    public ListenerUpdate(NoMove module) {
        super(module, UpdateEvent.class);
    }

    @Override
    public void invoke(UpdateEvent event) {
        RayTraceResult trace;
        if (!(((NoMove)this.module).mode.getValue() != StaticMode.NoVoid || ListenerUpdate.mc.player.noClip || !(ListenerUpdate.mc.player.posY <= (double)((NoMove)this.module).height.getValue().floatValue()) || PACKET_FLY.isEnabled() || (trace = ListenerUpdate.mc.world.rayTraceBlocks(ListenerUpdate.mc.player.getPositionVector(), new Vec3d(ListenerUpdate.mc.player.posX, 0.0, ListenerUpdate.mc.player.posZ), false, false, false)) != null && trace.typeOfHit == RayTraceResult.Type.BLOCK)) {
            if (((NoMove)this.module).timer.getValue().booleanValue()) {
                Managers.TIMER.setTimer(0.1f);
            } else {
                ListenerUpdate.mc.player.setVelocity(0.0, 0.0, 0.0);
                if (ListenerUpdate.mc.player.getRidingEntity() != null) {
                    ListenerUpdate.mc.player.getRidingEntity().setVelocity(0.0, 0.0, 0.0);
                }
            }
        }
    }
}
