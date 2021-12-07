package org.spongepowered.asm.lib.tree;

import java.util.Map;
import org.spongepowered.asm.lib.MethodVisitor;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.LabelNode;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FieldInsnNode
extends AbstractInsnNode {
    public String owner;
    public String name;
    public String desc;

    public FieldInsnNode(int opcode, String owner, String name, String desc) {
        super(opcode);
        this.owner = owner;
        this.name = name;
        this.desc = desc;
    }

    public void setOpcode(int opcode) {
        this.opcode = opcode;
    }

    @Override
    public int getType() {
        return 4;
    }

    @Override
    public void accept(MethodVisitor mv) {
        mv.visitFieldInsn(this.opcode, this.owner, this.name, this.desc);
        this.acceptAnnotations(mv);
    }

    @Override
    public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels) {
        return new FieldInsnNode(this.opcode, this.owner, this.name, this.desc).cloneAnnotations(this);
    }
}
