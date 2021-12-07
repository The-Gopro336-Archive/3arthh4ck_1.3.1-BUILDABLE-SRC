/*
 * Decompiled with CFR 0.150.
 */
package org.spongepowered.asm.lib.tree;

import java.util.Map;
import org.spongepowered.asm.lib.MethodVisitor;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.LabelNode;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class IincInsnNode
extends AbstractInsnNode {
    public int var;
    public int incr;

    public IincInsnNode(int var, int incr) {
        super(132);
        this.var = var;
        this.incr = incr;
    }

    @Override
    public int getType() {
        return 10;
    }

    @Override
    public void accept(MethodVisitor mv) {
        mv.visitIincInsn(this.var, this.incr);
        this.acceptAnnotations(mv);
    }

    @Override
    public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels) {
        return new IincInsnNode(this.var, this.incr).cloneAnnotations(this);
    }
}

