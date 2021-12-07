package me.earth.earthhack.impl.gui.visibility;

import java.util.function.Function;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.gui.visibility.VisibilityManager;
import me.earth.earthhack.impl.gui.visibility.VisibilitySupplier;

public class Visibilities {
    public static final VisibilityManager VISIBILITY_MANAGER = new VisibilityManager();

    public static <T, S extends Setting<T>> S requireNonNull(S setting) {
        if (setting == null) {
            throw new NullPointerException();
        }
        return setting;
    }

    public static void register(VisibilityManager manager, VisibilitySupplier supplier, Iterable<? extends Setting<?>> settings) {
        for (Setting<?> setting : settings) {
            manager.registerVisibility(setting, supplier);
        }
    }

    public static VisibilitySupplier andComposer(VisibilitySupplier supplier) {
        return Visibilities.withComposer(supplier, v -> v.isVisible() && supplier.isVisible());
    }

    public static VisibilitySupplier withComposer(final VisibilitySupplier supplier, final Function<VisibilitySupplier, Boolean> composer) {
        return new VisibilitySupplier(){

            @Override
            public boolean isVisible() {
                return supplier.isVisible();
            }

            @Override
            public VisibilitySupplier compose(VisibilitySupplier other) {
                return () -> (Boolean)composer.apply(other);
            }
        };
    }
}
