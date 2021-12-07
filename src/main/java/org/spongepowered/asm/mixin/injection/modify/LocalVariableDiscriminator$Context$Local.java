package org.spongepowered.asm.mixin.injection.modify;

import org.spongepowered.asm.lib.Type;

public class LocalVariableDiscriminator$Context$Local {
    int ord = 0;
    String name;
    Type type;

    public LocalVariableDiscriminator$Context$Local(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String toString() {
        return String.format("Local[ordinal=%d, name=%s, type=%s]", this.ord, this.name, this.type);
    }
}
