package me.earth.earthhack.impl.modules.player.freecam;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.freecam.Freecam;
import me.earth.earthhack.impl.modules.player.freecam.mode.CamMode;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;

final class ListenerMotion
extends ModuleListener<Freecam, MotionUpdateEvent> {
    public ListenerMotion(Freecam module) {
        super(module, MotionUpdateEvent.class, 99999999);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (event.getStage() == Stage.PRE && ((Freecam)this.module).mode.getValue() == CamMode.Position) {
            RayTraceResult result = ListenerMotion.mc.objectMouseOver;
            if (result != null) {
                float[] rotations = RotationUtil.getRotations(result.hitVec.x, result.hitVec.y, result.hitVec.z, ((Freecam)this.module).getPlayer());
                ((Freecam)this.module).rotate(rotations[0], rotations[1]);
            }
            ((Freecam)this.module).getPlayer().setHeldItem(EnumHand.MAIN_HAND, ListenerMotion.mc.player.getHeldItemMainhand());
            ((Freecam)this.module).getPlayer().setHeldItem(EnumHand.OFF_HAND, ListenerMotion.mc.player.getHeldItemOffhand());
            event.setX(((Freecam)this.module).getPlayer().posX);
            event.setY(((Freecam)this.module).getPlayer().getEntityBoundingBox().minY);
            event.setZ(((Freecam)this.module).getPlayer().posZ);
            event.setYaw(((Freecam)this.module).getPlayer().rotationYaw);
            event.setPitch(((Freecam)this.module).getPlayer().rotationPitch);
            event.setOnGround(((Freecam)this.module).getPlayer().onGround);
        }
    }
}
