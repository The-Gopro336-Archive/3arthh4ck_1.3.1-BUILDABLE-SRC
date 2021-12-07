package me.earth.earthhack.impl.modules.client.pingbypass.submodules.sautocrystal;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.core.ducks.entity.IEntityPlayerSP;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypass;
import me.earth.earthhack.impl.modules.client.pingbypass.submodules.sautocrystal.ServerAutoCrystal;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.init.Items;

final class ListenerRotations
extends ModuleListener<ServerAutoCrystal, MotionUpdateEvent> {
    private static final ModuleCache<PingBypass> PINGBYPASS = Caches.getModule(PingBypass.class);
    private float offset = 4.0E-4f;

    public ListenerRotations(ServerAutoCrystal module) {
        super(module, MotionUpdateEvent.class, Integer.MIN_VALUE);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (event.getStage() == Stage.PRE && PINGBYPASS.isEnabled() && InventoryUtil.isHolding(Items.END_CRYSTAL)) {
            float yawDif = event.getYaw() - ((IEntityPlayerSP)((Object)ListenerRotations.mc.player)).getLastReportedYaw();
            float pitchDif = event.getPitch() - ((IEntityPlayerSP)((Object)ListenerRotations.mc.player)).getLastReportedPitch();
            if (yawDif == 0.0f && pitchDif == 0.0f) {
                this.offset = -this.offset;
                event.setYaw(event.getYaw() + this.offset);
                event.setPitch(event.getPitch() + this.offset);
            }
        }
    }
}
