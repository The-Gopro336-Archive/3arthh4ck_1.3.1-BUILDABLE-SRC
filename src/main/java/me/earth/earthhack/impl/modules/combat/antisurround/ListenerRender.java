package me.earth.earthhack.impl.modules.combat.antisurround;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.antisurround.AntiSurround;
import me.earth.earthhack.impl.util.render.Interpolation;
import net.minecraft.util.math.BlockPos;

final class ListenerRender
extends ModuleListener<AntiSurround, Render3DEvent> {
    public ListenerRender(AntiSurround module) {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
        BlockPos pos;
        if (((AntiSurround)this.module).active.get() && ((AntiSurround)this.module).drawEsp.getValue().booleanValue() && (pos = ((AntiSurround)this.module).pos) != null) {
            ((AntiSurround)this.module).esp.render(Interpolation.interpolatePos(pos, 1.0f));
        }
    }
}
