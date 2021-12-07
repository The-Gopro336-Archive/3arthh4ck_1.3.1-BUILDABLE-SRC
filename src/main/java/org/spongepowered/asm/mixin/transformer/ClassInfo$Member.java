package org.spongepowered.asm.mixin.transformer;

import org.spongepowered.asm.mixin.transformer.ClassInfo;

abstract class ClassInfo$Member {
    private final Type type;
    private final String memberName;
    private final String memberDesc;
    private final boolean isInjected;
    private final int modifiers;
    private String currentName;
    private String currentDesc;
    private boolean decoratedFinal;
    private boolean decoratedMutable;
    private boolean unique;

    protected ClassInfo$Member(ClassInfo$Member member) {
        this(member.type, member.memberName, member.memberDesc, member.modifiers, member.isInjected);
        this.currentName = member.currentName;
        this.currentDesc = member.currentDesc;
        this.unique = member.unique;
    }

    protected ClassInfo$Member(Type type, String name, String desc, int access) {
        this(type, name, desc, access, false);
    }

    protected ClassInfo$Member(Type type, String name, String desc, int access, boolean injected) {
        this.type = type;
        this.memberName = name;
        this.memberDesc = desc;
        this.isInjected = injected;
        this.currentName = name;
        this.currentDesc = desc;
        this.modifiers = access;
    }

    public String getOriginalName() {
        return this.memberName;
    }

    public String getName() {
        return this.currentName;
    }

    public String getOriginalDesc() {
        return this.memberDesc;
    }

    public String getDesc() {
        return this.currentDesc;
    }

    public boolean isInjected() {
        return this.isInjected;
    }

    public boolean isRenamed() {
        return !this.currentName.equals(this.memberName);
    }

    public boolean isRemapped() {
        return !this.currentDesc.equals(this.memberDesc);
    }

    public boolean isPrivate() {
        return (this.modifiers & 2) != 0;
    }

    public boolean isStatic() {
        return (this.modifiers & 8) != 0;
    }

    public boolean isAbstract() {
        return (this.modifiers & 0x400) != 0;
    }

    public boolean isFinal() {
        return (this.modifiers & 0x10) != 0;
    }

    public boolean isSynthetic() {
        return (this.modifiers & 0x1000) != 0;
    }

    public boolean isUnique() {
        return this.unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public boolean isDecoratedFinal() {
        return this.decoratedFinal;
    }

    public boolean isDecoratedMutable() {
        return this.decoratedMutable;
    }

    public void setDecoratedFinal(boolean decoratedFinal, boolean decoratedMutable) {
        this.decoratedFinal = decoratedFinal;
        this.decoratedMutable = decoratedMutable;
    }

    public boolean matchesFlags(int flags) {
        return ((~this.modifiers | flags & 2) & 2) != 0 && ((~this.modifiers | flags & 8) & 8) != 0;
    }

    public abstract ClassInfo getOwner();

    public ClassInfo getImplementor() {
        return this.getOwner();
    }

    public int getAccess() {
        return this.modifiers;
    }

    public String renameTo(String name) {
        this.currentName = name;
        return name;
    }

    public String remapTo(String desc) {
        this.currentDesc = desc;
        return desc;
    }

    public boolean equals(String name, String desc) {
        return !(!this.memberName.equals(name) && !this.currentName.equals(name) || !this.memberDesc.equals(desc) && !this.currentDesc.equals(desc));
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ClassInfo$Member)) {
            return false;
        }
        ClassInfo$Member other = (ClassInfo$Member)obj;
        return !(!other.memberName.equals(this.memberName) && !other.currentName.equals(this.currentName) || !other.memberDesc.equals(this.memberDesc) && !other.currentDesc.equals(this.currentDesc));
    }

    public int hashCode() {
        return this.toString().hashCode();
    }

    public String toString() {
        return String.format(this.getDisplayFormat(), this.memberName, this.memberDesc);
    }

    protected String getDisplayFormat() {
        return "%s%s";
    }

    static enum Type {
        METHOD,
        FIELD;

    }
}
