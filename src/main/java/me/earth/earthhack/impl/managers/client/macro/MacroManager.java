package me.earth.earthhack.impl.managers.client.macro;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.api.Listener;
import me.earth.earthhack.api.event.bus.api.Subscriber;
import me.earth.earthhack.api.register.IterationRegister;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.client.macro.DelegateMacro;
import me.earth.earthhack.impl.managers.client.macro.Macro;
import me.earth.earthhack.impl.managers.client.macro.MacroType;
import me.earth.earthhack.impl.util.text.ChatUtil;

public class MacroManager
extends IterationRegister<Macro>
implements Subscriber,
Globals {
    private final List<Listener<?>> listeners = new ArrayList();
    private boolean safe;

    public MacroManager() {
        this.listeners.add(new EventListener<KeyboardEvent>(KeyboardEvent.class, 100){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void invoke(KeyboardEvent event) {
                for (Macro macro : MacroManager.this.getRegistered()) {
                    if (macro.getType() == MacroType.DELEGATE || macro.getBind().getKey() != event.getKey() || macro.isRelease() == event.getEventState()) continue;
                    try {
                        MacroManager.this.safe = true;
                        macro.execute(Managers.COMMANDS);
                    }
                    catch (Throwable t) {
                        ChatUtil.sendMessage("\u00a7cAn error occurred while executing macro \u00a7f" + macro.getName() + "\u00a7c" + ": " + (t.getMessage() == null ? t.getClass().getName() : t.getMessage()) + ". I strongly recommend deleting it for now and checking your logic!");
                        t.printStackTrace();
                    }
                    finally {
                        MacroManager.this.safe = false;
                    }
                }
            }
        });
    }

    public void validateAll() {
        this.getRegistered().removeIf(macro -> {
            if (macro instanceof DelegateMacro && !((DelegateMacro)macro).isReferenced(this)) {
                Earthhack.getLogger().info("Deleting DelegateMacro " + macro.getName() + " it's not being referenced anymore.");
                return true;
            }
            return false;
        });
    }

    @Override
    public Collection<Listener<?>> getListeners() {
        return this.listeners;
    }

    public boolean isSafe() {
        return mc.isCallingFromMinecraftThread() && this.safe;
    }
}
