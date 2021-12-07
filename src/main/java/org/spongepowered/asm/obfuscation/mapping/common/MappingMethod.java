package org.spongepowered.asm.obfuscation.mapping.common;

import com.google.common.base.Objects;
import org.spongepowered.asm.obfuscation.mapping.IMapping;

public class MappingMethod
implements IMapping<MappingMethod> {
    private final String owner;
    private final String name;
    private final String desc;

    public MappingMethod(String fullyQualifiedName, String desc) {
        this(MappingMethod.getOwnerFromName(fullyQualifiedName), MappingMethod.getBaseName(fullyQualifiedName), desc);
    }

    public MappingMethod(String owner, String simpleName, String desc) {
        this.owner = owner;
        this.name = simpleName;
        this.desc = desc;
    }

    @Override
    public IMapping.Type getType() {
        return IMapping.Type.METHOD;
    }

    @Override
    public String getName() {
        if (this.name == null) {
            return null;
        }
        return (this.owner != null ? this.owner + "/" : "") + this.name;
    }

    @Override
    public String getSimpleName() {
        return this.name;
    }

    @Override
    public String getOwner() {
        return this.owner;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }

    @Override
    public MappingMethod getSuper() {
        return null;
    }

    public boolean isConstructor() {
        return "<init>".equals(this.name);
    }

    @Override
    public MappingMethod move(String newOwner) {
        return new MappingMethod(newOwner, this.getSimpleName(), this.getDesc());
    }

    @Override
    public MappingMethod remap(String newName) {
        return new MappingMethod(this.getOwner(), newName, this.getDesc());
    }

    @Override
    public MappingMethod transform(String newDesc) {
        return new MappingMethod(this.getOwner(), this.getSimpleName(), newDesc);
    }

    @Override
    public MappingMethod copy() {
        return new MappingMethod(this.getOwner(), this.getSimpleName(), this.getDesc());
    }

    public MappingMethod addPrefix(String prefix) {
        String simpleName = this.getSimpleName();
        if (simpleName == null || simpleName.startsWith(prefix)) {
            return this;
        }
        return new MappingMethod(this.getOwner(), prefix + simpleName, this.getDesc());
    }

    public int hashCode() {
        return Objects.hashCode(this.getName(), this.getDesc());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof MappingMethod) {
            return Objects.equal(this.name, ((MappingMethod)obj).name) && Objects.equal(this.desc, ((MappingMethod)obj).desc);
        }
        return false;
    }

    @Override
    public String serialise() {
        return this.toString();
    }

    public String toString() {
        String desc = this.getDesc();
        return String.format("%s%s%s", this.getName(), desc != null ? " " : "", desc != null ? desc : "");
    }

    private static String getBaseName(String name) {
        if (name == null) {
            return null;
        }
        int pos = name.lastIndexOf(47);
        return pos > -1 ? name.substring(pos + 1) : name;
    }

    private static String getOwnerFromName(String name) {
        if (name == null) {
            return null;
        }
        int pos = name.lastIndexOf(47);
        return pos > -1 ? name.substring(0, pos) : null;
    }
}
