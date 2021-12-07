package org.spongepowered.tools.obfuscation.mirror;

import org.spongepowered.asm.obfuscation.mapping.IMapping;
import org.spongepowered.tools.obfuscation.mirror.Visibility;

public abstract class MemberHandle<T extends IMapping<T>> {
    private final String owner;
    private final String name;
    private final String desc;

    protected MemberHandle(String owner, String name, String desc) {
        this.owner = owner;
        this.name = name;
        this.desc = desc;
    }

    public final String getOwner() {
        return this.owner;
    }

    public final String getName() {
        return this.name;
    }

    public final String getDesc() {
        return this.desc;
    }

    public abstract Visibility getVisibility();

    public abstract T asMapping(boolean var1);
}
