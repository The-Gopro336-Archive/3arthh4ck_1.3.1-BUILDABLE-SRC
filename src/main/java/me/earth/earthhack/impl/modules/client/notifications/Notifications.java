/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 */
package me.earth.earthhack.impl.modules.client.notifications;

import java.util.HashMap;
import java.util.Map;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.observable.Observable;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.event.events.client.PostInitEvent;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.notifications.ListenerDeath;
import me.earth.earthhack.impl.modules.client.notifications.ListenerTotems;
import me.earth.earthhack.impl.modules.client.notifications.NotificationData;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.entity.Entity;

public class Notifications
extends Module {
    protected final Setting<Boolean> totems = this.register(new BooleanSetting("TotemPops", true));
    protected final Setting<TextColor> totemColor = this.register(new EnumSetting<TextColor>("Totem-Color", TextColor.None));
    protected final Setting<TextColor> totemAmountColor = this.register(new EnumSetting<TextColor>("Amount-Color", TextColor.None));
    protected final Setting<TextColor> totemPlayerColor = this.register(new EnumSetting<TextColor>("Player-Color", TextColor.None));
    protected final Setting<Boolean> modules = this.register(new BooleanSetting("Modules", true));
    protected final Setting<Boolean> configure = this.register(new BooleanSetting("Show-Modules", true));
    protected final Setting<Category> categories = this.register(new EnumSetting<Category>("Categories", Category.Combat));
    protected final Map<Module, Setting<Boolean>> announceMap = new HashMap<Module, Setting<Boolean>>();

    public Notifications() {
        super("Notifications", Category.Client);
        this.listeners.add(new ListenerTotems(this));
        this.listeners.add(new ListenerDeath(this));
        this.setData(new NotificationData(this));
        Bus.EVENT_BUS.register(new EventListener<PostInitEvent>(PostInitEvent.class){

            @Override
            public void invoke(PostInitEvent event) {
                Notifications.this.createSettings();
            }
        });
    }

    private void createSettings() {
        this.announceMap.clear();
        Visibilities.VISIBILITY_MANAGER.registerVisibility(this.categories, this.configure::getValue);
        for (Module module : Managers.MODULES.getRegistered()) {
            Object enabled = module.getSetting("Enabled", BooleanSetting.class);
            if (enabled == null) continue;
            ((Observable)enabled).addObserver(event -> {
                if (this.isEnabled() && !event.isCancelled() && this.modules.getValue().booleanValue() && this.announceMap.get(module).getValue().booleanValue()) {
                    this.onToggleModule((Module)event.getSetting().getContainer(), (Boolean)event.getValue());
                }
            });
            String name = module.getName();
            if (this.getSetting(name) != null) {
                name = "Show" + name;
            }
            BooleanSetting setting = this.register(new BooleanSetting(name, false));
            this.announceMap.put(module, setting);
            Visibilities.VISIBILITY_MANAGER.registerVisibility(setting, () -> this.configure.getValue() != false && this.categories.getValue() == module.getCategory());
            this.getData().settingDescriptions().put(setting, "Announce Toggling of " + name + "?");
        }
    }

    protected void onToggleModule(Module module, boolean enabled) {
        Setting<Boolean> setting = this.announceMap.get(module);
        if (setting != null && setting.getValue().booleanValue()) {
            String message = "\u00a7l" + module.getDisplayName() + (enabled ? "\u00a7a" : "\u00a7c") + (enabled ? " enabled." : " disabled.");
            mc.addScheduledTask(() -> Managers.CHAT.sendDeleteMessage(message, module.getName(), 2000));
        }
    }

    public void onPop(Entity player, int totemPops) {
        if (this.isEnabled() && this.totems.getValue().booleanValue()) {
            String message = this.totemPlayerColor.getValue().getColor() + player.getName() + this.totemColor.getValue().getColor() + " popped " + this.totemAmountColor.getValue().getColor() + totemPops + this.totemColor.getValue().getColor() + " totem" + (totemPops == 1 ? "." : "s.");
            Managers.CHAT.sendDeleteMessage(message, player.getName(), 1000);
        }
    }

    public void onDeath(Entity player, int totemPops) {
        if (this.isEnabled() && this.totems.getValue().booleanValue()) {
            String message = this.totemPlayerColor.getValue().getColor() + player.getName() + this.totemColor.getValue().getColor() + " died after popping " + this.totemAmountColor.getValue().getColor() + totemPops + this.totemColor.getValue().getColor() + " totem" + (totemPops == 1 ? "." : "s.");
            Managers.CHAT.sendDeleteMessage(message, player.getName(), 1000);
        }
    }
}

