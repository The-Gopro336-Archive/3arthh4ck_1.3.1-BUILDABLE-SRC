package org.spongepowered.asm.lib.tree.analysis;

import java.util.ArrayList;
import java.util.List;
import org.spongepowered.asm.lib.tree.JumpInsnNode;
import org.spongepowered.asm.lib.tree.LabelNode;
import org.spongepowered.asm.lib.tree.analysis.AnalyzerException;

class Subroutine {
    LabelNode start;
    boolean[] access;
    List<JumpInsnNode> callers;

    private Subroutine() {
    }

    Subroutine(LabelNode start, int maxLocals, JumpInsnNode caller) {
        this.start = start;
        this.access = new boolean[maxLocals];
        this.callers = new ArrayList<JumpInsnNode>();
        this.callers.add(caller);
    }

    public Subroutine copy() {
        Subroutine result = new Subroutine();
        result.start = this.start;
        result.access = new boolean[this.access.length];
        System.arraycopy(this.access, 0, result.access, 0, this.access.length);
        result.callers = new ArrayList<JumpInsnNode>(this.callers);
        return result;
    }

    public boolean merge(Subroutine subroutine) throws AnalyzerException {
        int i;
        boolean changes = false;
        for (i = 0; i < this.access.length; ++i) {
            if (!subroutine.access[i] || this.access[i]) continue;
            this.access[i] = true;
            changes = true;
        }
        if (subroutine.start == this.start) {
            for (i = 0; i < subroutine.callers.size(); ++i) {
                JumpInsnNode caller = subroutine.callers.get(i);
                if (this.callers.contains(caller)) continue;
                this.callers.add(caller);
                changes = true;
            }
        }
        return changes;
    }
}
