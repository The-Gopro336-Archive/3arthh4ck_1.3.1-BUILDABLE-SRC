package org.spongepowered.asm.mixin.transformer;

import com.google.common.base.Function;
import org.spongepowered.asm.lib.Type;

class MixinInfo$1
implements Function<Type, String> {
    MixinInfo$1() {
    }

    @Override
    public String apply(Type input) {
        return input.getClassName();
    }
}
