/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.managers.minecraft;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.event.events.render.GuiScreenEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.KeyBoardUtil;

public class KeyBoardManager
extends SubscriberImpl {
    public KeyBoardManager() {
        this.listeners.add(new EventListener<KeyboardEvent>(KeyboardEvent.class){

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
        });
        this.listeners.add(new EventListener<GuiScreenEvent<?>>(GuiScreenEvent.class, -2147483638){

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
        });
    }

    private void onRelease(int keyCode) {
        for (Module module : Managers.MODULES.getRegistered()) {
            if (module.getBind().getKey() != keyCode) continue;
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

