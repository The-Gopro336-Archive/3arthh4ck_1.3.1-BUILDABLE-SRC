package me.earth.earthhack.impl.modules.player.spectate;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.spectate.Spectate;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.util.math.RayTraceResult;

final class ListenerMotion
extends ModuleListener<Spectate, MotionUpdateEvent> {
    public ListenerMotion(Spectate module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        RayTraceResult r;
        if (event.getStage() == Stage.PRE && ((Spectate)this.module).rotate.getValue().booleanValue() && (r = ListenerMotion.mc.objectMouseOver) != null && r.typeOfHit != RayTraceResult.Type.MISS && r.hitVec != null) {
            float[] rotations = RotationUtil.getRotations(r.hitVec.x, r.hitVec.y, r.hitVec.z, ListenerMotion.mc.player);
            event.setYaw(rotations[0]);
            event.setPitch(rotations[1]);
        }
    }
}
