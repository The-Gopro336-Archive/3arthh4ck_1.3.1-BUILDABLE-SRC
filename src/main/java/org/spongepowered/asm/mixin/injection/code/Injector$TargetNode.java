package org.spongepowered.asm.mixin.injection.code;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.mixin.injection.InjectionPoint;

public final class Injector$TargetNode {
    final AbstractInsnNode insn;
    final Set<InjectionPoint> nominators = new HashSet<InjectionPoint>();

    Injector$TargetNode(AbstractInsnNode insn) {
        this.insn = insn;
    }

    public AbstractInsnNode getNode() {
        return this.insn;
    }

    public Set<InjectionPoint> getNominators() {
        return Collections.unmodifiableSet(this.nominators);
    }

    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != Injector$TargetNode.class) {
            return false;
        }
        return ((Injector$TargetNode)obj).insn == this.insn;
    }

    public int hashCode() {
        return this.insn.hashCode();
    }
}
