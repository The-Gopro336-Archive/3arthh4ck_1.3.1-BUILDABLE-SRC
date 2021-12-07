package org.spongepowered.asm.obfuscation.mapping;

public interface IMapping<TMapping> {
    public Type getType();

    public TMapping move(String var1);

    public TMapping remap(String var1);

    public TMapping transform(String var1);

    public TMapping copy();

    public String getName();

    public String getSimpleName();

    public String getOwner();

    public String getDesc();

    public TMapping getSuper();

    public String serialise();

    public static enum Type {
        FIELD,
        METHOD,
        CLASS,
        PACKAGE;

    }
}
