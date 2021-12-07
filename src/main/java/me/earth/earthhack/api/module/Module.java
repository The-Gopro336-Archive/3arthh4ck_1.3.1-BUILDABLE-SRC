/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.api.module;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import me.earth.earthhack.api.event.bus.api.Listener;
import me.earth.earthhack.api.event.bus.api.Subscriber;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.api.module.data.ModuleData;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.module.util.Hidden;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.SettingContainer;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.api.util.bind.Toggle;
import me.earth.earthhack.api.util.interfaces.Displayable;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.api.util.interfaces.Hideable;
import me.earth.earthhack.api.util.interfaces.Nameable;

public abstract class Module
extends SettingContainer
implements Globals,
Subscriber,
Hideable,
Displayable,
Nameable {
    protected final List<Listener<?>> listeners = new ArrayList();
    private final AtomicBoolean enableCheck = new AtomicBoolean();
    private final AtomicBoolean inOnEnable = new AtomicBoolean();
    private final Setting<String> name;
    private final Setting<Bind> bind = this.register(new BindSetting("Bind", Bind.none()));
    private final Setting<Hidden> hidden = this.register(new EnumSetting<Hidden>("Hidden", Hidden.Visible));
    private final Setting<Boolean> enabled = this.register(new BooleanSetting("Enabled", false));
    private final Setting<Toggle> bindMode = this.register(new EnumSetting<Toggle>("Toggle", Toggle.Normal));
    private final Category category;
    private ModuleData data;

    public Module(String name, Category category) {
        this.name = this.register(new StringSetting("Name", name));
        this.category = category;
        this.data = new DefaultData<Module>(this);
        this.enabled.addObserver(event -> {
            if (event.isCancelled()) {
                return;
            }
            this.enableCheck.set((Boolean)event.getValue());
            if (((Boolean)event.getValue()).booleanValue() && !Bus.EVENT_BUS.isSubscribed(this)) {
                this.inOnEnable.set(true);
                this.onEnable();
                this.inOnEnable.set(false);
                if (this.enableCheck.get()) {
                    Bus.EVENT_BUS.subscribe(this);
                }
            } else if (!((Boolean)event.getValue()).booleanValue() && (Bus.EVENT_BUS.isSubscribed(this) || this.inOnEnable.get())) {
                Bus.EVENT_BUS.unsubscribe(this);
                this.onDisable();
            }
        });
    }

    @Override
    public String getName() {
        return this.name.getInitial();
    }

    @Override
    public String getDisplayName() {
        return this.name.getValue();
    }

    @Override
    public void setDisplayName(String name) {
        this.name.setValue(name);
    }

    public final void toggle() {
        if (this.isEnabled()) {
            this.disable();
        } else {
            this.enable();
        }
    }

    public final void enable() {
        if (!this.isEnabled()) {
            this.enabled.setValue(true);
        }
    }

    public final void disable() {
        if (this.isEnabled()) {
            this.enabled.setValue(false);
        }
    }

    public final void load() {
        if (this.isEnabled() && !Bus.EVENT_BUS.isSubscribed(this)) {
            Bus.EVENT_BUS.subscribe(this);
        }
        this.onLoad();
    }

    public boolean isEnabled() {
        return this.enableCheck.get();
    }

    public String getDisplayInfo() {
        return null;
    }

    public Category getCategory() {
        return this.category;
    }

    public ModuleData getData() {
        return this.data;
    }

    public void setData(ModuleData data) {
        if (data != null) {
            this.data = data;
        }
    }

    public Bind getBind() {
        return this.bind.getValue();
    }

    public void setBind(Bind bind) {
        this.bind.setValue(bind);
    }

    public Toggle getBindMode() {
        return this.bindMode.getValue();
    }

    @Override
    public void setHidden(Hidden hidden) {
        this.hidden.setValue(hidden);
    }

    @Override
    public Hidden isHidden() {
        return this.hidden.getValue();
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    protected void onLoad() {
    }

    @Override
    public Collection<Listener<?>> getListeners() {
        return this.listeners;
    }

    public int hashCode() {
        return this.name.getInitial().hashCode();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Module) {
            String name = this.name.getInitial();
            return name != null && name.equals(((Module)o).name.getInitial());
        }
        return false;
    }
}

