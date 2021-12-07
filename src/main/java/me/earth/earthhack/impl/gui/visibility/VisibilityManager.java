package me.earth.earthhack.impl.gui.visibility;

import java.util.HashMap;
import java.util.Map;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.gui.visibility.VisibilitySupplier;

public class VisibilityManager {
    private static final VisibilitySupplier ALWAYS = () -> true;
    private final Map<Setting<?>, VisibilitySupplier> visibilities = new HashMap();

    public VisibilitySupplier getVisibility(Setting<?> setting) {
        return this.visibilities.getOrDefault(setting, ALWAYS);
    }

    public void registerVisibility(Setting<?> setting, VisibilitySupplier visibility) {
        if (visibility == null) {
            this.visibilities.remove(setting);
            return;
        }
        this.visibilities.compute(setting, (k, v) -> {
            if (v == null) {
                return visibility;
            }
            return visibility.compose((VisibilitySupplier)v);
        });
    }

    public boolean isVisible(Setting<?> setting) {
        return this.getVisibility(setting).isVisible();
    }
}
