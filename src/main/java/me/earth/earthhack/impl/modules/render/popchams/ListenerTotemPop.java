package me.earth.earthhack.impl.modules.render.popchams;

import me.earth.earthhack.impl.event.events.misc.TotemPopEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.popchams.PopChams;

final class ListenerTotemPop
extends ModuleListener<PopChams, TotemPopEvent> {
    public ListenerTotemPop(PopChams module) {
        super(module, TotemPopEvent.class);
    }

    @Override
    public void invoke(TotemPopEvent event) {
        if (!((PopChams)this.module).isValidEntity(event.getEntity())) {
            return;
        }
        ((PopChams)this.module).getPopDataHashMap().put(event.getEntity().getName(), new PopChams.PopData(event.getEntity(), System.currentTimeMillis(), event.getEntity().rotationYaw, event.getEntity().rotationPitch, event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ));
    }
}
