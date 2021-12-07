package org.spongepowered.asm.lib.tree;

import org.spongepowered.asm.lib.MethodVisitor;

public class ParameterNode {
    public String name;
    public int access;

    public ParameterNode(String name, int access) {
        this.name = name;
        this.access = access;
    }

    public void accept(MethodVisitor mv) {
        mv.visitParameter(this.name, this.access);
    }
}
