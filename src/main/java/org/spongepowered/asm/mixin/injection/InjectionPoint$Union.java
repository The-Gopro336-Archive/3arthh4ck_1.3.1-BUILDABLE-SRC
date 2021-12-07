package org.spongepowered.asm.mixin.injection;

import java.util.Collection;
import java.util.LinkedHashSet;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.InsnList;
import org.spongepowered.asm.mixin.injection.InjectionPoint;

final class InjectionPoint$Union
extends InjectionPoint.CompositeInjectionPoint {
    public InjectionPoint$Union(InjectionPoint ... points) {
        super(points);
    }

    @Override
    public boolean find(String desc, InsnList insns, Collection<AbstractInsnNode> nodes) {
        LinkedHashSet<AbstractInsnNode> allNodes = new LinkedHashSet<AbstractInsnNode>();
        for (int i = 0; i < this.components.length; ++i) {
            this.components[i].find(desc, insns, allNodes);
        }
        nodes.addAll(allNodes);
        return allNodes.size() > 0;
    }
}
