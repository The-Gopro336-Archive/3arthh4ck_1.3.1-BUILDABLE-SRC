package org.spongepowered.asm.mixin.injection;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.InsnList;
import org.spongepowered.asm.mixin.injection.InjectionPoint;

final class InjectionPoint$Intersection
extends InjectionPoint.CompositeInjectionPoint {
    public InjectionPoint$Intersection(InjectionPoint ... points) {
        super(points);
    }

    @Override
    public boolean find(String desc, InsnList insns, Collection<AbstractInsnNode> nodes) {
        boolean found = false;
        ArrayList[] allNodes = (ArrayList[])Array.newInstance(ArrayList.class, this.components.length);
        for (int i = 0; i < this.components.length; ++i) {
            allNodes[i] = new ArrayList();
            this.components[i].find(desc, insns, allNodes[i]);
        }
        ArrayList alpha = allNodes[0];
        for (int nodeIndex = 0; nodeIndex < alpha.size(); ++nodeIndex) {
            AbstractInsnNode node = (AbstractInsnNode)alpha.get(nodeIndex);
            boolean in = true;
            for (int b = 1; b < allNodes.length && allNodes[b].contains(node); ++b) {
            }
            if (!in) continue;
            nodes.add(node);
            found = true;
        }
        return found;
    }
}
