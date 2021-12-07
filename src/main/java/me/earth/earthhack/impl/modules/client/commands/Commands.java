/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.client.commands;

import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.commands.CommandData;
import me.earth.earthhack.impl.modules.client.commands.KeyboardListener;

public class Commands
extends Module {
    private static final SettingCache<String, StringSetting, Commands> PREFIX = Caches.getSetting(Commands.class, StringSetting.class, "Prefix", "+");
    protected final Setting<Boolean> prefixBind = this.register(new BooleanSetting("PrefixBind", false));
    protected char prefixChar = (char)43;

    public Commands() {
        super("Commands", Category.Client);
        StringSetting prefix = this.register(new StringSetting("Prefix", "+"));
        this.register(new BooleanSetting("BackgroundGui", false));
        prefix.addObserver(s -> {
            if (!s.isCancelled()) {
                this.prefixChar = ((String)s.getValue()).length() == 1 ? ((String)s.getValue()).charAt(0) : (char)'\u0000';
            }
        });
        PREFIX.setContainer(this);
        PREFIX.set((String)((Object)prefix));
        Bus.EVENT_BUS.register(new KeyboardListener(this));
        this.setData(new CommandData(this));
    }

    public static void setPrefix(String prefix) {
        PREFIX.computeIfPresent(s -> s.setValue(prefix));
    }

    public static String getPrefix() {
        if (!PREFIX.isPresent()) {
            return "+";
        }
        return PREFIX.getValue();
    }
}

