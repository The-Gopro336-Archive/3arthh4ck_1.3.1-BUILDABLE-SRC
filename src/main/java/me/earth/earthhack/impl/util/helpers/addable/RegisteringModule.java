package me.earth.earthhack.impl.util.helpers.addable;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import me.earth.earthhack.api.module.data.ModuleData;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.GeneratedSettings;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.util.helpers.addable.AddableModule;

public class RegisteringModule<I, E extends Setting<I>>
extends AddableModule {
    protected final Function<Setting<?>, String> settingDescription;
    protected final Set<E> added = new LinkedHashSet();
    protected final Function<String, E> create;

    public RegisteringModule(String name, Category category, String command, String descriptor, Function<String, E> create, Function<Setting<?>, String> settingDescription) {
        super(name, category, command, descriptor);
        this.create = create;
        this.settingDescription = settingDescription;
    }

    @Override
    public void add(String string) {
        this.addSetting(string);
    }

    @Override
    public void del(String string) {
        super.del(string);
        Setting<?> setting = this.getSetting(string);
        if (setting != null) {
            this.unregister(setting);
        }
    }

    @Override
    public Setting<?> getSettingConfig(String name) {
        Setting<?> setting = super.getSetting(name);
        if (setting == null) {
            E generated = this.addSetting(name);
            if (generated != null) {
                ModuleData data = this.getData();
                if (data != null) {
                    data.settingDescriptions().put((Setting<?>)generated, this.settingDescription.apply((Setting<?>)generated));
                }
                GeneratedSettings.add(this, generated);
            }
            return generated;
        }
        return setting;
    }

    @Override
    public Setting<?> unregister(Setting<?> setting) {
        this.added.remove(setting);
        this.strings.remove(this.formatString(setting.getName()));
        return super.unregister(setting);
    }

    protected E addSetting(String string) {
        Setting newSetting = (Setting)this.create.apply(string);
        if (this.added.add(newSetting)) {
            ModuleData data = this.getData();
            if (data != null) {
                data.settingDescriptions().put(newSetting, this.settingDescription.apply(newSetting));
            }
            this.register(newSetting);
            super.add(string);
            return (E)newSetting;
        }
        return null;
    }
}
