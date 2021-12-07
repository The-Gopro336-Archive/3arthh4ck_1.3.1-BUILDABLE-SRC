package org.spongepowered.asm.lib.tree;

import java.util.Map;
import org.spongepowered.asm.lib.MethodVisitor;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.LabelNode;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class LineNumberNode
extends AbstractInsnNode {
    public int line;
    public LabelNode start;

    public LineNumberNode(int line, LabelNode start) {
        super(-1);
        this.line = line;
        this.start = start;
    }

    @Override
    public int getType() {
        return 15;
    }

    @Override
    public void accept(MethodVisitor mv) {
        mv.visitLineNumber(this.line, this.start.getLabel());
    }

    @Override
    public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels) {
        return new LineNumberNode(this.line, LineNumberNode.clone(this.start, labels));
    }
}
