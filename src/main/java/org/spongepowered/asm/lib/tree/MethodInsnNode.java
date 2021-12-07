package org.spongepowered.asm.lib.tree;

import java.util.Map;
import org.spongepowered.asm.lib.MethodVisitor;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.LabelNode;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MethodInsnNode
extends AbstractInsnNode {
    public String owner;
    public String name;
    public String desc;
    public boolean itf;

    @Deprecated
    public MethodInsnNode(int opcode, String owner, String name, String desc) {
        this(opcode, owner, name, desc, opcode == 185);
    }

    public MethodInsnNode(int opcode, String owner, String name, String desc, boolean itf) {
        super(opcode);
        this.owner = owner;
        this.name = name;
        this.desc = desc;
        this.itf = itf;
    }

    public void setOpcode(int opcode) {
        this.opcode = opcode;
    }

    @Override
    public int getType() {
        return 5;
    }

    @Override
    public void accept(MethodVisitor mv) {
        mv.visitMethodInsn(this.opcode, this.owner, this.name, this.desc, this.itf);
        this.acceptAnnotations(mv);
    }

    @Override
    public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels) {
        return new MethodInsnNode(this.opcode, this.owner, this.name, this.desc, this.itf);
    }
}
