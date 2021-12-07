package org.spongepowered.asm.mixin;

public enum MixinEnvironment$Option {
    DEBUG_ALL("debug"),
    DEBUG_EXPORT(DEBUG_ALL, "export"),
    DEBUG_EXPORT_FILTER(DEBUG_EXPORT, "filter", false),
    DEBUG_EXPORT_DECOMPILE(DEBUG_EXPORT, Inherit.ALLOW_OVERRIDE, "decompile"),
    DEBUG_EXPORT_DECOMPILE_THREADED(DEBUG_EXPORT_DECOMPILE, Inherit.ALLOW_OVERRIDE, "async"),
    DEBUG_EXPORT_DECOMPILE_MERGESIGNATURES(DEBUG_EXPORT_DECOMPILE, Inherit.ALLOW_OVERRIDE, "mergeGenericSignatures"),
    DEBUG_VERIFY(DEBUG_ALL, "verify"),
    DEBUG_VERBOSE(DEBUG_ALL, "verbose"),
    DEBUG_INJECTORS(DEBUG_ALL, "countInjections"),
    DEBUG_STRICT(DEBUG_ALL, Inherit.INDEPENDENT, "strict"),
    DEBUG_UNIQUE(DEBUG_STRICT, "unique"),
    DEBUG_TARGETS(DEBUG_STRICT, "targets"),
    DEBUG_PROFILER(DEBUG_ALL, Inherit.ALLOW_OVERRIDE, "profiler"),
    DUMP_TARGET_ON_FAILURE("dumpTargetOnFailure"),
    CHECK_ALL("checks"),
    CHECK_IMPLEMENTS(CHECK_ALL, "interfaces"),
    CHECK_IMPLEMENTS_STRICT(CHECK_IMPLEMENTS, Inherit.ALLOW_OVERRIDE, "strict"),
    IGNORE_CONSTRAINTS("ignoreConstraints"),
    HOT_SWAP("hotSwap"),
    ENVIRONMENT(Inherit.ALWAYS_FALSE, "env"),
    OBFUSCATION_TYPE(ENVIRONMENT, Inherit.ALWAYS_FALSE, "obf"),
    DISABLE_REFMAP(ENVIRONMENT, Inherit.INDEPENDENT, "disableRefMap"),
    REFMAP_REMAP(ENVIRONMENT, Inherit.INDEPENDENT, "remapRefMap"),
    REFMAP_REMAP_RESOURCE(ENVIRONMENT, Inherit.INDEPENDENT, "refMapRemappingFile", ""),
    REFMAP_REMAP_SOURCE_ENV(ENVIRONMENT, Inherit.INDEPENDENT, "refMapRemappingEnv", "searge"),
    REFMAP_REMAP_ALLOW_PERMISSIVE(ENVIRONMENT, Inherit.INDEPENDENT, "allowPermissiveMatch", true, "true"),
    IGNORE_REQUIRED(ENVIRONMENT, Inherit.INDEPENDENT, "ignoreRequired"),
    DEFAULT_COMPATIBILITY_LEVEL(ENVIRONMENT, Inherit.INDEPENDENT, "compatLevel"),
    SHIFT_BY_VIOLATION_BEHAVIOUR(ENVIRONMENT, Inherit.INDEPENDENT, "shiftByViolation", "warn"),
    INITIALISER_INJECTION_MODE("initialiserInjectionMode", "default");

    private static final String PREFIX = "mixin";
    final MixinEnvironment$Option parent;
    final Inherit inheritance;
    final String property;
    final String defaultValue;
    final boolean isFlag;
    final int depth;

    private MixinEnvironment$Option(String property) {
        this(null, property, true);
    }

    private MixinEnvironment$Option(Inherit inheritance, String property) {
        this(null, inheritance, property, true);
    }

    private MixinEnvironment$Option(String property, boolean flag) {
        this(null, property, flag);
    }

    private MixinEnvironment$Option(String property, String defaultStringValue) {
        this(null, Inherit.INDEPENDENT, property, false, defaultStringValue);
    }

    private MixinEnvironment$Option(MixinEnvironment$Option parent, String property) {
        this(parent, Inherit.INHERIT, property, true);
    }

    private MixinEnvironment$Option(MixinEnvironment$Option parent, Inherit inheritance, String property) {
        this(parent, inheritance, property, true);
    }

    private MixinEnvironment$Option(MixinEnvironment$Option parent, String property, boolean isFlag) {
        this(parent, Inherit.INHERIT, property, isFlag, null);
    }

    private MixinEnvironment$Option(MixinEnvironment$Option parent, Inherit inheritance, String property, boolean isFlag) {
        this(parent, inheritance, property, isFlag, null);
    }

    private MixinEnvironment$Option(MixinEnvironment$Option parent, String property, String defaultStringValue) {
        this(parent, Inherit.INHERIT, property, false, defaultStringValue);
    }

    private MixinEnvironment$Option(MixinEnvironment$Option parent, Inherit inheritance, String property, String defaultStringValue) {
        this(parent, inheritance, property, false, defaultStringValue);
    }

    private MixinEnvironment$Option(MixinEnvironment$Option parent, Inherit inheritance, String property, boolean isFlag, String defaultStringValue) {
        this.parent = parent;
        this.inheritance = inheritance;
        this.property = (parent != null ? parent.property : PREFIX) + "." + property;
        this.defaultValue = defaultStringValue;
        this.isFlag = isFlag;
        int depth = 0;
        while (parent != null) {
            parent = parent.parent;
            ++depth;
        }
        this.depth = depth;
    }

    MixinEnvironment$Option getParent() {
        return this.parent;
    }

    String getProperty() {
        return this.property;
    }

    public String toString() {
        return this.isFlag ? String.valueOf(this.getBooleanValue()) : this.getStringValue();
    }

    private boolean getLocalBooleanValue(boolean defaultValue) {
        return Boolean.parseBoolean(System.getProperty(this.property, Boolean.toString(defaultValue)));
    }

    private boolean getInheritedBooleanValue() {
        return this.parent != null && this.parent.getBooleanValue();
    }

    final boolean getBooleanValue() {
        if (this.inheritance == Inherit.ALWAYS_FALSE) {
            return false;
        }
        boolean local = this.getLocalBooleanValue(false);
        if (this.inheritance == Inherit.INDEPENDENT) {
            return local;
        }
        boolean inherited = local || this.getInheritedBooleanValue();
        return this.inheritance == Inherit.INHERIT ? inherited : this.getLocalBooleanValue(inherited);
    }

    final String getStringValue() {
        return this.inheritance == Inherit.INDEPENDENT || this.parent == null || this.parent.getBooleanValue() ? System.getProperty(this.property, this.defaultValue) : this.defaultValue;
    }

    <E extends Enum<E>> E getEnumValue(E defaultValue) {
        String value = System.getProperty(this.property, defaultValue.name());
        try {
            return (E)Enum.valueOf(defaultValue.getClass(), value.toUpperCase());
        }
        catch (IllegalArgumentException ex) {
            return defaultValue;
        }
    }

    private static enum Inherit {
        INHERIT,
        ALLOW_OVERRIDE,
        INDEPENDENT,
        ALWAYS_FALSE;

    }
}
