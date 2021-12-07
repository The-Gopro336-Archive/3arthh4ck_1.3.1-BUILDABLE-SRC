package me.earth.earthhack.impl.gui.visibility;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.SettingContainer;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.gui.visibility.VisibilityManager;
import me.earth.earthhack.impl.gui.visibility.VisibilitySupplier;

public class PageBuilder<T> {
    private final List<Map.Entry<List<Setting<?>>, VisibilitySupplier>> suppliers = new LinkedList();
    private final SettingContainer container;
    private final Setting<T> pageSetting;
    private Function<VisibilitySupplier, VisibilitySupplier> conversion;
    private Setting<?> position;
    private boolean injectBefore;

    public PageBuilder(SettingContainer container, Setting<T> setting) {
        this.pageSetting = Visibilities.requireNonNull(setting);
        this.conversion = v -> v;
        this.container = Objects.requireNonNull(container);
    }

    public PageBuilder<T> withConversion(Function<VisibilitySupplier, VisibilitySupplier> conversion) {
        this.conversion = conversion;
        return this;
    }

    public PageBuilder<T> reapplyConversion() {
        for (Map.Entry<List<Setting<?>>, VisibilitySupplier> entry : this.suppliers) {
            entry.setValue(this.conversion.apply(entry.getValue()));
        }
        return this;
    }

    public PageBuilder<T> setPagePositionBefore(String before) {
        return this.setPagePositionBefore(Visibilities.requireNonNull(this.container.getSetting(before)));
    }

    public PageBuilder<T> setPagePositionBefore(String before, Class<?> clazz) {
        return this.setPagePositionBefore((Setting<?>)Visibilities.requireNonNull(this.container.getSetting(before, clazz)));
    }

    public PageBuilder<T> setPagePositionAfter(String after, Class<?> clazz) {
        return this.setPagePositionAfter((Setting<?>)Visibilities.requireNonNull(this.container.getSetting(after, clazz)));
    }

    public PageBuilder<T> setPagePositionAfter(String after) {
        this.position = Visibilities.requireNonNull(this.container.getSetting(after));
        this.injectBefore = false;
        return this;
    }

    public PageBuilder<T> setPagePositionBefore(Setting<?> before) {
        this.position = Visibilities.requireNonNull(before);
        this.injectBefore = true;
        return this;
    }

    public PageBuilder<T> setPagePositionAfter(Setting<?> after) {
        this.position = Visibilities.requireNonNull(after);
        this.injectBefore = false;
        return this;
    }

    public PageBuilder<T> addVisibility(Predicate<T> visibility, Setting<?> setting) {
        ArrayList list = new ArrayList(1);
        list.add(setting);
        this.suppliers.add(new AbstractMap.SimpleEntry(list, this.toVis(visibility)));
        return this;
    }

    public PageBuilder<T> addPage(Predicate<T> visibility, Setting<?> start, Setting<?> end) {
        ArrayList settings = new ArrayList();
        boolean started = false;
        for (Setting<?> setting : this.container.getSettings()) {
            if (setting.equals(start)) {
                started = true;
                settings.add(setting);
            }
            if (started) {
                settings.add(setting);
            }
            if (!setting.equals(end)) continue;
            if (!started) {
                Earthhack.getLogger().warn("PageBuilder: found end: " + end.getName() + " but not start!");
                return this;
            }
            started = false;
            settings.add(setting);
        }
        if (started) {
            Earthhack.getLogger().warn("PageBuilder: found start: " + start.getName() + " but not end!");
            return this;
        }
        this.suppliers.add(new AbstractMap.SimpleEntry(settings, this.toVis(visibility)));
        return this;
    }

    public PageBuilder<T> addPage(Predicate<T> visibility, Setting<?> ... settings) {
        ArrayList toArrayList = new ArrayList(settings.length);
        for (Setting<?> setting : settings) {
            if (setting == null) continue;
            toArrayList.add(setting);
        }
        this.suppliers.add(new AbstractMap.SimpleEntry(toArrayList, this.toVis(visibility)));
        return this;
    }

    public PageBuilder<T> register(VisibilityManager manager) {
        for (Map.Entry<List<Setting<?>>, VisibilitySupplier> entry : this.suppliers) {
            Visibilities.register(manager, entry.getValue(), (Iterable)entry.getKey());
        }
        return this;
    }

    public PageBuilder<T> registerPageSetting() {
        if (this.position == null) {
            this.container.register(this.pageSetting);
            return this;
        }
        if (this.injectBefore) {
            this.container.registerBefore(this.pageSetting, this.position);
        } else {
            this.container.registerAfter(this.pageSetting, this.position);
        }
        return this;
    }

    public Setting<T> getPageSetting() {
        return this.pageSetting;
    }

    private VisibilitySupplier toVis(Predicate<T> predicate) {
        return this.conversion.apply(() -> predicate.test(this.pageSetting.getValue()));
    }
}
