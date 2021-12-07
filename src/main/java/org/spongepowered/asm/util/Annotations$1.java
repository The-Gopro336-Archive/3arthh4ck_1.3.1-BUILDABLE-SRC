package org.spongepowered.asm.util;

import com.google.common.base.Function;
import org.spongepowered.asm.lib.tree.AnnotationNode;

final class Annotations$1
implements Function<AnnotationNode, String> {
    Annotations$1() {
    }

    @Override
    public String apply(AnnotationNode input) {
        return input.desc;
    }
}
