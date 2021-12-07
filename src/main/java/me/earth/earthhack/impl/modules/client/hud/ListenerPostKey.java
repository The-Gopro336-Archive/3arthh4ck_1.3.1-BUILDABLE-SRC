/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.ScaledResolution
 */
package me.earth.earthhack.impl.modules.client.hud;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.Map;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Hidden;
import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.hud.HUD;
import me.earth.earthhack.impl.modules.client.hud.modes.Modules;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import net.minecraft.client.gui.ScaledResolution;

final class ListenerPostKey
extends ModuleListener<HUD, KeyboardEvent.Post> {
    public ListenerPostKey(HUD module) {
        super(module, KeyboardEvent.Post.class);
    }

    @Override
    public void invoke(KeyboardEvent.Post event) {
        if (ListenerPostKey.mc.player == null || ListenerPostKey.mc.world == null) {
            return;
        }
        ((HUD)this.module).resolution = new ScaledResolution(mc);
        ((HUD)this.module).width = ((HUD)this.module).resolution.getScaledWidth();
        ((HUD)this.module).height = ((HUD)this.module).resolution.getScaledHeight();
        ((HUD)this.module).modules.clear();
        if (((HUD)this.module).renderModules.getValue() != Modules.None) {
            for (Module mod : Managers.MODULES.getRegistered()) {
                if (!mod.isEnabled() || mod.isHidden() == Hidden.Hidden) continue;
                AbstractMap.SimpleEntry<String, Module> entry2 = new AbstractMap.SimpleEntry<String, Module>(ModuleUtil.getHudName(mod), mod);
                ((HUD)this.module).modules.add(entry2);
            }
            if (((HUD)this.module).renderModules.getValue() == Modules.Length) {
                ((HUD)this.module).modules.sort(Comparator.comparing(entry -> Managers.TEXT.getStringWidth((String)entry.getKey()) * -1));
            } else {
                ((HUD)this.module).modules.sort(Map.Entry.comparingByKey());
            }
        }
    }
}

