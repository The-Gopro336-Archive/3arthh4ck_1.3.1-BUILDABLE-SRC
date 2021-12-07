package org.spongepowered.asm.mixin;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.spongepowered.asm.mixin.MixinEnvironment;

public final class MixinEnvironment$Phase {
    static final MixinEnvironment$Phase NOT_INITIALISED = new MixinEnvironment$Phase(-1, "NOT_INITIALISED");
    public static final MixinEnvironment$Phase PREINIT = new MixinEnvironment$Phase(0, "PREINIT");
    public static final MixinEnvironment$Phase INIT = new MixinEnvironment$Phase(1, "INIT");
    public static final MixinEnvironment$Phase DEFAULT = new MixinEnvironment$Phase(2, "DEFAULT");
    static final List<MixinEnvironment$Phase> phases = ImmutableList.of(PREINIT, INIT, DEFAULT);
    final int ordinal;
    final String name;
    private MixinEnvironment environment;

    private MixinEnvironment$Phase(int ordinal, String name) {
        this.ordinal = ordinal;
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public static MixinEnvironment$Phase forName(String name) {
        for (MixinEnvironment$Phase phase : phases) {
            if (!phase.name.equals(name)) continue;
            return phase;
        }
        return null;
    }

    MixinEnvironment getEnvironment() {
        if (this.ordinal < 0) {
            throw new IllegalArgumentException("Cannot access the NOT_INITIALISED environment");
        }
        if (this.environment == null) {
            this.environment = new MixinEnvironment(this);
        }
        return this.environment;
    }
}
