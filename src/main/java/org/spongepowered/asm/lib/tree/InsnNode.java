package org.spongepowered.asm.lib.tree;

import java.util.Map;
import org.spongepowered.asm.lib.MethodVisitor;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.LabelNode;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class InsnNode
extends AbstractInsnNode {
    public InsnNode(int opcode) {
        super(opcode);
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public void accept(MethodVisitor mv) {
        mv.visitInsn(this.opcode);
        this.acceptAnnotations(mv);
    }

    @Override
    public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels) {
        return new InsnNode(this.opcode).cloneAnnotations(this);
    }
}
