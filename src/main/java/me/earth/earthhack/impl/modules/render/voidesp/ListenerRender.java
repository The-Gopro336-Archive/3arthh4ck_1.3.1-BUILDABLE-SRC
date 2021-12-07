package me.earth.earthhack.impl.modules.render.voidesp;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.voidesp.VoidESP;
import net.minecraft.world.DimensionType;

final class ListenerRender
extends ModuleListener<VoidESP, Render3DEvent> {
    public ListenerRender(VoidESP module) {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
        if (ListenerRender.mc.player.dimension == DimensionType.THE_END.getId() || ListenerRender.mc.player.posY > (double)((VoidESP)this.module).maxY.getValue().intValue()) {
            return;
        }
        ((VoidESP)this.module).updateHoles();
        int holes = ((VoidESP)this.module).holes.getValue();
        for (int i = 0; i < holes && i < ((VoidESP)this.module).voidHoles.size(); ++i) {
            ((VoidESP)this.module).renderPos(((VoidESP)this.module).voidHoles.get(i));
        }
    }
}
