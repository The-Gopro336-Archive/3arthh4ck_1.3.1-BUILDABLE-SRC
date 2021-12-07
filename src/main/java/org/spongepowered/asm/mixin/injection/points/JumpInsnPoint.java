package org.spongepowered.asm.mixin.injection.points;

import java.util.Collection;
import java.util.ListIterator;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.InsnList;
import org.spongepowered.asm.lib.tree.JumpInsnNode;
import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.struct.InjectionPointData;

@InjectionPoint.AtCode(value="JUMP")
public class JumpInsnPoint
extends InjectionPoint {
    private final int opCode;
    private final int ordinal;

    public JumpInsnPoint(InjectionPointData data) {
        this.opCode = data.getOpcode(-1, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 198, 199, -1);
        this.ordinal = data.getOrdinal();
    }

    @Override
    public boolean find(String desc, InsnList insns, Collection<AbstractInsnNode> nodes) {
        boolean found = false;
        int ordinal = 0;
        ListIterator<AbstractInsnNode> iter = insns.iterator();
        while (iter.hasNext()) {
            AbstractInsnNode insn = iter.next();
            if (!(insn instanceof JumpInsnNode) || this.opCode != -1 && insn.getOpcode() != this.opCode) continue;
            if (this.ordinal == -1 || this.ordinal == ordinal) {
                nodes.add(insn);
                found = true;
            }
            ++ordinal;
        }
        return found;
    }
}
