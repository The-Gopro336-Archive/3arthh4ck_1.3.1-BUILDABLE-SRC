package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.modules.player.speedmine.mode.ESPMode;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.render.Interpolation;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

final class ListenerRender
extends ModuleListener<Speedmine, Render3DEvent> {
    public ListenerRender(Speedmine module) {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
        if (!PlayerUtil.isCreative(ListenerRender.mc.player) && ((Speedmine)this.module).esp.getValue() != ESPMode.None && ((Speedmine)this.module).bb != null) {
            GL11.glPushMatrix();
            GL11.glPushAttrib(1048575);
            float max = Math.min(((Speedmine)this.module).maxDamage, 1.0f);
            AxisAlignedBB bb = Interpolation.interpolateAxis(((Speedmine)this.module).bb);
            ((Speedmine)this.module).esp.getValue().drawEsp((Speedmine)this.module, bb, max);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }
}
