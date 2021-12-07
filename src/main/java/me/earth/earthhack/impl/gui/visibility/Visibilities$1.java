package me.earth.earthhack.impl.gui.visibility;

import java.util.function.Function;
import me.earth.earthhack.impl.gui.visibility.VisibilitySupplier;

final class Visibilities$1
implements VisibilitySupplier {
    final VisibilitySupplier val$supplier;
    final Function val$composer;

    Visibilities$1(VisibilitySupplier visibilitySupplier, Function function) {
        this.val$supplier = visibilitySupplier;
        this.val$composer = function;
    }

    @Override
    public boolean isVisible() {
        return this.val$supplier.isVisible();
    }

    @Override
    public VisibilitySupplier compose(VisibilitySupplier other) {
        return () -> (Boolean)this.val$composer.apply(other);
    }
}
