package me.earth.earthhack.impl.gui.visibility;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.SettingContainer;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.gui.visibility.PageBuilder;

public class NumberPageBuilder
extends PageBuilder<Integer> {
    public NumberPageBuilder(SettingContainer container, String name, int pages) {
        super(container, new NumberSetting<Integer>(name, 1, 1, pages));
    }

    public NumberPageBuilder addPage(int page, Setting<?> from, Setting<?> to) {
        return (NumberPageBuilder)super.addPage((T v) -> v == page, from, to);
    }

    public NumberPageBuilder addPage(int page, Setting<?> ... settings) {
        return (NumberPageBuilder)super.addPage((T v) -> v == page, settings);
    }

    public static NumberPageBuilder autoPage(SettingContainer container, String name, int settingsPerPage, Iterable<? extends Setting<?>> settings) {
        if (settingsPerPage <= 0) {
            throw new IllegalArgumentException("SettingsPerPage needs to be an integer bigger than 0!");
        }
        HashMap<Integer, Setting[]> pages = new HashMap<Integer, Setting[]>();
        int i = 0;
        int page = 1;
        Setting[] current = new Setting[settingsPerPage];
        Iterator<Setting<?>> iterator = settings.iterator();
        while (iterator.hasNext()) {
            Setting<?> setting;
            current[i] = setting = iterator.next();
            if (++i != settingsPerPage) continue;
            pages.put(page++, current);
            current = new Setting[settingsPerPage];
            i = 0;
        }
        if (current[0] != null) {
            pages.put(page, current);
        }
        NumberPageBuilder pageBuilder = new NumberPageBuilder(container, name, pages.size());
        for (Map.Entry entry : pages.entrySet()) {
            pageBuilder.addPage((int)((Integer)entry.getKey()), (Setting[])entry.getValue());
        }
        return pageBuilder;
    }
}
