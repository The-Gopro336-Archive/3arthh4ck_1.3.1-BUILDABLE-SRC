package org.spongepowered.asm.lib.tree;

import java.util.Map;
import org.spongepowered.asm.lib.MethodVisitor;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.LabelNode;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class IntInsnNode
extends AbstractInsnNode {
    public int operand;

    public IntInsnNode(int opcode, int operand) {
        super(opcode);
        this.operand = operand;
    }

    public void setOpcode(int opcode) {
        this.opcode = opcode;
    }

    @Override
    public int getType() {
        return 1;
    }

    @Override
    public void accept(MethodVisitor mv) {
        mv.visitIntInsn(this.opcode, this.operand);
        this.acceptAnnotations(mv);
    }

    @Override
    public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels) {
        return new IntInsnNode(this.opcode, this.operand).cloneAnnotations(this);
    }
}
