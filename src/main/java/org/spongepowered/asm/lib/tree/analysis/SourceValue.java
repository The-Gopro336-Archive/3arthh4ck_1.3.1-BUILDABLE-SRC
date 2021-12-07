package org.spongepowered.asm.lib.tree.analysis;

import java.util.Set;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.analysis.SmallSet;
import org.spongepowered.asm.lib.tree.analysis.Value;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SourceValue
implements Value {
    public final int size;
    public final Set<AbstractInsnNode> insns;

    public SourceValue(int size) {
        this(size, SmallSet.emptySet());
    }

    public SourceValue(int size, AbstractInsnNode insn) {
        this.size = size;
        this.insns = new SmallSet<Object>(insn, null);
    }

    public SourceValue(int size, Set<AbstractInsnNode> insns) {
        this.size = size;
        this.insns = insns;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    public boolean equals(Object value) {
        if (!(value instanceof SourceValue)) {
            return false;
        }
        SourceValue v = (SourceValue)value;
        return this.size == v.size && this.insns.equals(v.insns);
    }

    public int hashCode() {
        return this.insns.hashCode();
    }
}
