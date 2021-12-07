package org.spongepowered.asm.mixin.transformer;

import com.google.common.base.Function;

class MixinInfo$2
implements Function<String, String> {
    MixinInfo$2() {
    }

    @Override
    public String apply(String input) {
        return MixinInfo.this.getParent().remapClassName(MixinInfo.this.getClassRef(), input);
    }
}
