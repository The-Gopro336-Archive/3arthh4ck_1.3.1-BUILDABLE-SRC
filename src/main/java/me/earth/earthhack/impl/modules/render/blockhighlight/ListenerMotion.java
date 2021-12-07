/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.util.math.Vec3d
 */
package me.earth.earthhack.impl.modules.render.blockhighlight;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.minecraft.movement.PositionManager;
import me.earth.earthhack.impl.modules.render.blockhighlight.BlockHighlight;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

final class ListenerMotion
extends ModuleListener<BlockHighlight, MotionUpdateEvent> {
    public ListenerMotion(BlockHighlight module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (event.getStage() == Stage.POST && ((BlockHighlight)this.module).distance.getValue().booleanValue() && ((BlockHighlight)this.module).current != null && ListenerMotion.mc.objectMouseOver != null && ListenerMotion.mc.objectMouseOver.hitVec != null) {
            boolean see;
            double d;
            RayTraceResult r = ListenerMotion.mc.objectMouseOver;
            if (r.typeOfHit == RayTraceResult.Type.BLOCK && r.getBlockPos() != null) {
                BlockPos p = r.getBlockPos();
                d = ((BlockHighlight)this.module).hitVec.getValue() != false ? Managers.POSITION.getVec().distanceTo(r.hitVec) : Managers.POSITION.getVec().distanceTo(new Vec3d((double)p.getX() + 0.5, (double)p.getY() + 0.5, (double)p.getZ() + 0.5));
                see = this.canSee(r.hitVec, Managers.POSITION);
            } else if (r.typeOfHit == RayTraceResult.Type.ENTITY && r.entityHit != null) {
                Entity e = r.entityHit;
                d = r.entityHit.getDistance(Managers.POSITION.getX(), Managers.POSITION.getY(), Managers.POSITION.getZ());
                see = this.canSee(new Vec3d(e.posX, e.posY + (double)e.getEyeHeight(), e.posZ), Managers.POSITION);
            } else {
                d = Managers.POSITION.getVec().distanceTo(r.hitVec);
                see = this.canSee(r.hitVec, Managers.POSITION);
            }
            StringBuilder builder = new StringBuilder(((BlockHighlight)this.module).current);
            builder.append(", ");
            if (d >= 6.0) {
                builder.append("\u00a7c");
            } else if (d >= 3.0 && !see) {
                builder.append("\u00a76");
            } else {
                builder.append("\u00a7a");
            }
            builder.append(MathUtil.round(d, 2));
            ((BlockHighlight)this.module).current = builder.toString();
        }
    }

    private boolean canSee(Vec3d toSee, PositionManager m) {
        return RayTraceUtil.canBeSeen(toSee, m.getX(), m.getY(), m.getZ(), ListenerMotion.mc.player.getEyeHeight());
    }
}

