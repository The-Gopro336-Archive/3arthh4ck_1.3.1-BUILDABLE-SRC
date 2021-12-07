package org.spongepowered.asm.obfuscation.mapping.common;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import org.spongepowered.asm.obfuscation.mapping.IMapping;

public class MappingField
implements IMapping<MappingField> {
    private final String owner;
    private final String name;
    private final String desc;

    public MappingField(String owner, String name) {
        this(owner, name, null);
    }

    public MappingField(String owner, String name, String desc) {
        this.owner = owner;
        this.name = name;
        this.desc = desc;
    }

    @Override
    public IMapping.Type getType() {
        return IMapping.Type.FIELD;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public final String getSimpleName() {
        return this.name;
    }

    @Override
    public final String getOwner() {
        return this.owner;
    }

    @Override
    public final String getDesc() {
        return this.desc;
    }

    @Override
    public MappingField getSuper() {
        return null;
    }

    @Override
    public MappingField move(String newOwner) {
        return new MappingField(newOwner, this.getName(), this.getDesc());
    }

    @Override
    public MappingField remap(String newName) {
        return new MappingField(this.getOwner(), newName, this.getDesc());
    }

    @Override
    public MappingField transform(String newDesc) {
        return new MappingField(this.getOwner(), this.getName(), newDesc);
    }

    @Override
    public MappingField copy() {
        return new MappingField(this.getOwner(), this.getName(), this.getDesc());
    }

    public int hashCode() {
        return Objects.hashCode(this.toString());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof MappingField) {
            return Objects.equal(this.toString(), ((MappingField)obj).toString());
        }
        return false;
    }

    @Override
    public String serialise() {
        return this.toString();
    }

    public String toString() {
        return String.format("L%s;%s:%s", this.getOwner(), this.getName(), Strings.nullToEmpty(this.getDesc()));
    }
}
