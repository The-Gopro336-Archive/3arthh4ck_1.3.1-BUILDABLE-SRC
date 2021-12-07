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
public class VarInsnNode
extends AbstractInsnNode {
    public int var;

    public VarInsnNode(int opcode, int var) {
        super(opcode);
        this.var = var;
    }

    public void setOpcode(int opcode) {
        this.opcode = opcode;
    }

    @Override
    public int getType() {
        return 2;
    }

    @Override
    public void accept(MethodVisitor mv) {
        mv.visitVarInsn(this.opcode, this.var);
        this.acceptAnnotations(mv);
    }

    @Override
    public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels) {
        return new VarInsnNode(this.opcode, this.var).cloneAnnotations(this);
    }
}

