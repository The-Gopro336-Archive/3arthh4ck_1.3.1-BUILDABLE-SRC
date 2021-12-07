package me.earth.earthhack.impl.managers.minecraft.combat;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;

class SetDeadManager$3
extends EventListener<UpdateEvent> {
    SetDeadManager$3(Class target) {
        super(target);
    }

    @Override
    public void invoke(UpdateEvent event) {
        SetDeadManager.this.updateKilled();
    }
}
