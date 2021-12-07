package me.earth.earthhack.impl.managers.minecraft;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.impl.event.events.render.GuiScreenEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.KeyBoardUtil;

class KeyBoardManager$2
extends EventListener<GuiScreenEvent<?>> {
    KeyBoardManager$2(Class target, int priority) {
        super(target, priority);
    }

    @Override
    public void invoke(GuiScreenEvent<?> event) {
        if (event.isCancelled() || event.getScreen() == null) {
            return;
        }
        for (Module module : Managers.MODULES.getRegistered()) {
            if (!KeyBoardUtil.isKeyDown(module.getBind())) continue;
            switch (module.getBindMode()) {
                case Hold: {
                    module.toggle();
                    break;
                }
                case Disable: {
                    module.disable();
                    break;
                }
            }
        }
    }
}
