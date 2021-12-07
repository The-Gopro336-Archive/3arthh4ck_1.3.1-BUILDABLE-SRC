package org.spongepowered.tools.obfuscation.mirror;

import com.google.common.base.Strings;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import org.spongepowered.asm.obfuscation.mapping.common.MappingField;
import org.spongepowered.tools.obfuscation.mirror.MemberHandle;
import org.spongepowered.tools.obfuscation.mirror.TypeUtils;
import org.spongepowered.tools.obfuscation.mirror.Visibility;

public class FieldHandle
extends MemberHandle<MappingField> {
    private final VariableElement element;
    private final boolean rawType;

    public FieldHandle(TypeElement owner, VariableElement element) {
        this(TypeUtils.getInternalName(owner), element);
    }

    public FieldHandle(String owner, VariableElement element) {
        this(owner, element, false);
    }

    public FieldHandle(TypeElement owner, VariableElement element, boolean rawType) {
        this(TypeUtils.getInternalName(owner), element, rawType);
    }

    public FieldHandle(String owner, VariableElement element, boolean rawType) {
        this(owner, element, rawType, TypeUtils.getName(element), TypeUtils.getInternalName(element));
    }

    public FieldHandle(String owner, String name, String desc) {
        this(owner, null, false, name, desc);
    }

    private FieldHandle(String owner, VariableElement element, boolean rawType, String name, String desc) {
        super(owner, name, desc);
        this.element = element;
        this.rawType = rawType;
    }

    public boolean isImaginary() {
        return this.element == null;
    }

    public VariableElement getElement() {
        return this.element;
    }

    @Override
    public Visibility getVisibility() {
        return TypeUtils.getVisibility(this.element);
    }

    public boolean isRawType() {
        return this.rawType;
    }

    @Override
    public MappingField asMapping(boolean includeOwner) {
        return new MappingField(includeOwner ? this.getOwner() : null, this.getName(), this.getDesc());
    }

    public String toString() {
        String owner = this.getOwner() != null ? "L" + this.getOwner() + ";" : "";
        String name = Strings.nullToEmpty(this.getName());
        String desc = Strings.nullToEmpty(this.getDesc());
        return String.format("%s%s:%s", owner, name, desc);
    }
}
