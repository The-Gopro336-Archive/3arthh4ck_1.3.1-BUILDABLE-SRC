package org.spongepowered.asm.lib;

public final class Handle {
    final int tag;
    final String owner;
    final String name;
    final String desc;
    final boolean itf;

    @Deprecated
    public Handle(int tag, String owner, String name, String desc) {
        this(tag, owner, name, desc, tag == 9);
    }

    public Handle(int tag, String owner, String name, String desc, boolean itf) {
        this.tag = tag;
        this.owner = owner;
        this.name = name;
        this.desc = desc;
        this.itf = itf;
    }

    public int getTag() {
        return this.tag;
    }

    public String getOwner() {
        return this.owner;
    }

    public String getName() {
        return this.name;
    }

    public String getDesc() {
        return this.desc;
    }

    public boolean isInterface() {
        return this.itf;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Handle)) {
            return false;
        }
        Handle h = (Handle)obj;
        return this.tag == h.tag && this.itf == h.itf && this.owner.equals(h.owner) && this.name.equals(h.name) && this.desc.equals(h.desc);
    }

    public int hashCode() {
        return this.tag + (this.itf ? 64 : 0) + this.owner.hashCode() * this.name.hashCode() * this.desc.hashCode();
    }

    public String toString() {
        return this.owner + '.' + this.name + this.desc + " (" + this.tag + (this.itf ? " itf" : "") + ')';
    }
}
