package me.earth.earthhack.impl.modules.render.sounds;

import java.awt.Color;
import java.util.Map;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.render.sounds.Sounds;
import me.earth.earthhack.impl.modules.render.sounds.util.CustomSound;
import me.earth.earthhack.impl.modules.render.sounds.util.SoundPosition;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;

final class ListenerRender
extends ModuleListener<Sounds, Render3DEvent> {
    public ListenerRender(Sounds module) {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
        if (!((Sounds)this.module).render.getValue().booleanValue()) {
            return;
        }
        for (Map.Entry<SoundPosition, Long> e : ((Sounds)this.module).sounds.entrySet()) {
            SoundPosition p = e.getKey();
            int color = ((Sounds)this.module).color.getRGB();
            if (p instanceof CustomSound) {
                color = Color.HSBtoRGB(Managers.COLOR.getHueByPosition(p.getY()), 1.0f, 1.0f);
            }
            if (((Sounds)this.module).fade.getValue().booleanValue()) {
                int alpha = color >>> 24;
                double t = System.currentTimeMillis() - e.getValue();
                double factor = 1.0 - t / (double)((Sounds)this.module).remove.getValue().intValue();
                if (factor <= 0.0) continue;
                alpha = MathUtil.clamp((int)((double)alpha * factor), 0, 255) << 24;
                color = color & 0xFFFFFF | alpha;
            }
            double x = p.getX() - Interpolation.getRenderPosX();
            double y = p.getY() - Interpolation.getRenderPosY();
            double z = p.getZ() - Interpolation.getRenderPosZ();
            String c = "\u00a7z" + String.format("%08X", color);
            RenderUtil.drawNametag(c + p.getName(), x, y, z, ((Sounds)this.module).scale.getValue().floatValue(), -1, ((Sounds)this.module).rect.getValue());
        }
    }
}
