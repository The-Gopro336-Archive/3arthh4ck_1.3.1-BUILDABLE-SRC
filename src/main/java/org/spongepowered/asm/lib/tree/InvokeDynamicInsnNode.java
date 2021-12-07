package org.spongepowered.asm.lib.tree;

import java.util.Map;
import org.spongepowered.asm.lib.Handle;
import org.spongepowered.asm.lib.MethodVisitor;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.LabelNode;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class InvokeDynamicInsnNode
extends AbstractInsnNode {
    public String name;
    public String desc;
    public Handle bsm;
    public Object[] bsmArgs;

    public InvokeDynamicInsnNode(String name, String desc, Handle bsm, Object ... bsmArgs) {
        super(186);
        this.name = name;
        this.desc = desc;
        this.bsm = bsm;
        this.bsmArgs = bsmArgs;
    }

    @Override
    public int getType() {
        return 6;
    }

    @Override
    public void accept(MethodVisitor mv) {
        mv.visitInvokeDynamicInsn(this.name, this.desc, this.bsm, this.bsmArgs);
        this.acceptAnnotations(mv);
    }

    @Override
    public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels) {
        return new InvokeDynamicInsnNode(this.name, this.desc, this.bsm, this.bsmArgs).cloneAnnotations(this);
    }
}
