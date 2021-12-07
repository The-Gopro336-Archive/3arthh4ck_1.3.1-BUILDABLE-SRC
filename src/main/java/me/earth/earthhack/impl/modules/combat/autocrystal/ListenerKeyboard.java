package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.AutoSwitch;
import me.earth.earthhack.impl.modules.combat.offhand.Offhand;
import me.earth.earthhack.impl.modules.combat.offhand.modes.OffhandMode;

final class ListenerKeyboard
extends ModuleListener<AutoCrystal, KeyboardEvent> {
    private static final ModuleCache<Offhand> OFFHAND = Caches.getModule(Offhand.class);

    public ListenerKeyboard(AutoCrystal module) {
        super(module, KeyboardEvent.class);
    }

    @Override
    public void invoke(KeyboardEvent event) {
        if (event.getEventState() && event.getKey() == ((AutoCrystal)this.module).switchBind.getValue().getKey()) {
            if (((AutoCrystal)this.module).useAsOffhand.getValue().booleanValue() || ((AutoCrystal)this.module).isPingBypass()) {
                OffhandMode m = OFFHAND.returnIfPresent(Offhand::getMode, null);
                if (m != null) {
                    if (m.equals(OffhandMode.CRYSTAL)) {
                        OFFHAND.computeIfPresent(o -> o.setMode(OffhandMode.TOTEM));
                    } else {
                        OFFHAND.computeIfPresent(o -> o.setMode(OffhandMode.CRYSTAL));
                    }
                }
                ((AutoCrystal)this.module).switching = false;
            } else if (((AutoCrystal)this.module).autoSwitch.getValue() == AutoSwitch.Bind) {
                ((AutoCrystal)this.module).switching = !((AutoCrystal)this.module).switching;
            }
        }
    }
}
