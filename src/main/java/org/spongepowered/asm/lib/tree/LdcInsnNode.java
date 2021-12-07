package org.spongepowered.asm.lib.tree;

import java.util.Map;
import org.spongepowered.asm.lib.MethodVisitor;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.LabelNode;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class LdcInsnNode
extends AbstractInsnNode {
    public Object cst;

    public LdcInsnNode(Object cst) {
        super(18);
        this.cst = cst;
    }

    @Override
    public int getType() {
        return 9;
    }

    @Override
    public void accept(MethodVisitor mv) {
        mv.visitLdcInsn(this.cst);
        this.acceptAnnotations(mv);
    }

    @Override
    public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels) {
        return new LdcInsnNode(this.cst).cloneAnnotations(this);
    }
}
