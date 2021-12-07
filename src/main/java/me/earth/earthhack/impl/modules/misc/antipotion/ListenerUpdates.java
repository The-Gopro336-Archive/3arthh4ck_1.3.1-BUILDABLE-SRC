/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.misc.antipotion;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.antipotion.AntiPotion;

final class ListenerUpdates
extends ModuleListener<AntiPotion, UpdateEvent> {
    public ListenerUpdates(AntiPotion module) {
        super(module, UpdateEvent.class);
    }

    @Override
    public void invoke(UpdateEvent event) {
        ListenerUpdates.mc.player.getActivePotionEffects().removeIf(effect -> {
            Object setting = ((AntiPotion)this.module).getSetting(AntiPotion.getPotionString(effect.getPotion()), BooleanSetting.class);
            return setting != null && (Boolean)((Setting)setting).getValue() != false;
        });
    }
}

