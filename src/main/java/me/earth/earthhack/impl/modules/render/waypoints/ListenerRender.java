package me.earth.earthhack.impl.modules.render.waypoints;

import java.awt.Color;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.waypoints.WayPointSetting;
import me.earth.earthhack.impl.modules.render.waypoints.WayPoints;
import me.earth.earthhack.impl.modules.render.waypoints.mode.WayPointRender;
import me.earth.earthhack.impl.modules.render.waypoints.mode.WayPointType;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

final class ListenerRender
extends ModuleListener<WayPoints, Render3DEvent> {
    public ListenerRender(WayPoints module) {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
        WayPointRender render = ((WayPoints)this.module).render.getValue();
        if (render == WayPointRender.None) {
            return;
        }
        WayPointType type = WayPointType.fromDimension(ListenerRender.mc.world.provider.getDimensionType());
        Entity entity = RenderUtil.getEntity();
        for (WayPointSetting setting : ((WayPoints)this.module).getWayPoints()) {
            double z;
            double y;
            BlockPos pos;
            double x;
            double distanceSq;
            double multiplier;
            block10: {
                block11: {
                    multiplier = 1.0;
                    if (setting.getType() == type) break block10;
                    if (type != WayPointType.OVW || setting.getType() != WayPointType.Nether || !((WayPoints)this.module).ovwInNether.getValue().booleanValue()) break block11;
                    multiplier = 8.0;
                    if (this.assign(8.0)) break block10;
                }
                if (type != WayPointType.Nether || setting.getType() != WayPointType.OVW || !((WayPoints)this.module).netherOvw.getValue().booleanValue()) continue;
                multiplier = 0.125;
                if (!this.assign(0.125)) continue;
            }
            if ((distanceSq = entity.getDistanceSq(x = Math.floor((double)(pos = (BlockPos)setting.getValue()).getX() * multiplier), y = (double)pos.getY(), z = Math.floor((double)pos.getZ() * multiplier))) > (double)MathUtil.squareToLong(((WayPoints)this.module).range.getValue())) continue;
            AxisAlignedBB bb = Interpolation.interpolateAxis(new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0));
            Color c = ((WayPoints)this.module).getColor(setting.getType());
            RenderUtil.startRender();
            RenderUtil.drawOutline(bb, 1.5f, c);
            RenderUtil.endRender();
            StringBuilder builder = new StringBuilder();
            builder.append(setting.getName()).append(" ");
            switch (render) {
                case Distance: {
                    this.appendDistance(builder, Math.sqrt(distanceSq));
                    break;
                }
                case Coordinates: {
                    this.appendCoordinates(builder, pos);
                    break;
                }
                case Both: {
                    this.appendCoordinates(builder, pos);
                    builder.append(" ");
                    this.appendDistance(builder, Math.sqrt(distanceSq));
                    break;
                }
            }
            RenderUtil.drawNametag(builder.toString(), bb, ((WayPoints)this.module).scale.getValue().floatValue(), c.getRGB());
        }
    }

    private boolean assign(double assignment) {
        return true;
    }

    private void appendCoordinates(StringBuilder builder, BlockPos pos) {
        builder.append("XYZ: ").append(pos.getX()).append(", ").append(pos.getY()).append(", ").append(pos.getZ());
    }

    private void appendDistance(StringBuilder builder, double distance) {
        builder.append("(").append(MathUtil.round(distance, 1)).append(")");
    }
}
