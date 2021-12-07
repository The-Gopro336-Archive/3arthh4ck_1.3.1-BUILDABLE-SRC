package org.spongepowered.asm.mixin.gen;

import java.util.Set;
import org.spongepowered.asm.mixin.gen.AccessorGenerator;
import org.spongepowered.asm.mixin.gen.AccessorGeneratorFieldGetter;
import org.spongepowered.asm.mixin.gen.AccessorInfo;

final class AccessorInfo$AccessorType$1
extends AccessorInfo.AccessorType {
    AccessorInfo$AccessorType$1(Set expectedPrefixes) {
    }

    @Override
    AccessorGenerator getGenerator(AccessorInfo info) {
        return new AccessorGeneratorFieldGetter(info);
    }
}
