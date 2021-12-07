package org.spongepowered.asm.mixin.gen;

import java.util.ArrayList;
import org.spongepowered.asm.lib.tree.AnnotationNode;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.mixin.gen.AccessorInfo;

public abstract class AccessorGenerator {
    protected final AccessorInfo info;

    public AccessorGenerator(AccessorInfo info) {
        this.info = info;
    }

    protected final MethodNode createMethod(int maxLocals, int maxStack) {
        MethodNode method = this.info.getMethod();
        MethodNode accessor = new MethodNode(327680, method.access & 0xFFFFFBFF | 0x1000, method.name, method.desc, null, null);
        accessor.visibleAnnotations = new ArrayList<AnnotationNode>();
        accessor.visibleAnnotations.add(this.info.getAnnotation());
        accessor.maxLocals = maxLocals;
        accessor.maxStack = maxStack;
        return accessor;
    }

    public abstract MethodNode generate();
}
