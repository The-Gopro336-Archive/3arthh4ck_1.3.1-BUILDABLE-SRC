package org.spongepowered.asm.mixin.injection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.InsnList;
import org.spongepowered.asm.mixin.injection.InjectionPoint;

final class InjectionPoint$Shift
extends InjectionPoint {
    private final InjectionPoint input;
    private final int shift;

    public InjectionPoint$Shift(InjectionPoint input, int shift) {
        if (input == null) {
            throw new IllegalArgumentException("Must supply an input injection point for SHIFT");
        }
        this.input = input;
        this.shift = shift;
    }

    @Override
    public String toString() {
        return "InjectionPoint(" + this.getClass().getSimpleName() + ")[" + this.input + "]";
    }

    @Override
    public boolean find(String desc, InsnList insns, Collection<AbstractInsnNode> nodes) {
        List<Object> list = nodes instanceof List ? (List<Object>)nodes : new ArrayList<AbstractInsnNode>(nodes);
        this.input.find(desc, insns, nodes);
        for (int i = 0; i < list.size(); ++i) {
            list.set(i, insns.get(insns.indexOf((AbstractInsnNode)list.get(i)) + this.shift));
        }
        if (nodes != list) {
            nodes.clear();
            nodes.addAll((Collection<AbstractInsnNode>)list);
        }
        return nodes.size() > 0;
    }
}
