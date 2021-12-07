package org.spongepowered.asm.mixin.injection.modify;

import java.util.Collection;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.VarInsnNode;

class BeforeLoadLocal$SearchState {
    private final boolean print;
    private final int targetOrdinal;
    private int ordinal = 0;
    private boolean pendingCheck = false;
    private boolean found = false;
    private VarInsnNode varNode;

    BeforeLoadLocal$SearchState(int targetOrdinal, boolean print) {
        this.targetOrdinal = targetOrdinal;
        this.print = print;
    }

    boolean success() {
        return this.found;
    }

    boolean isPendingCheck() {
        return this.pendingCheck;
    }

    void setPendingCheck() {
        this.pendingCheck = true;
    }

    void register(VarInsnNode node) {
        this.varNode = node;
    }

    void check(Collection<AbstractInsnNode> nodes, AbstractInsnNode insn, int local) {
        this.pendingCheck = false;
        if (!(local == this.varNode.var || local <= -2 && this.print)) {
            return;
        }
        if (this.targetOrdinal == -1 || this.targetOrdinal == this.ordinal) {
            nodes.add(insn);
            this.found = true;
        }
        ++this.ordinal;
        this.varNode = null;
    }
}
