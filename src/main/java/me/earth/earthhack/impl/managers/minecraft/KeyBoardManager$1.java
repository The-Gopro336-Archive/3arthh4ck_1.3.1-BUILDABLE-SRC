package me.earth.earthhack.impl.managers.minecraft;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.managers.Managers;

class KeyBoardManager$1
extends EventListener<KeyboardEvent> {
    KeyBoardManager$1(Class target) {
        super(target);
    }

    @Override
    public void invoke(KeyboardEvent event) {
        if (event.getEventState()) {
            for (Module module : Managers.MODULES.getRegistered()) {
                if (module.getBind().getKey() != event.getKey()) continue;
                module.toggle();
            }
        } else {
            KeyBoardManager.this.onRelease(event.getKey());
        }
    }
}
