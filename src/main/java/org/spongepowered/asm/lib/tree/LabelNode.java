package org.spongepowered.asm.lib.tree;

import java.util.Map;
import org.spongepowered.asm.lib.Label;
import org.spongepowered.asm.lib.MethodVisitor;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class LabelNode
extends AbstractInsnNode {
    private Label label;

    public LabelNode() {
        super(-1);
    }

    public LabelNode(Label label) {
        super(-1);
        this.label = label;
    }

    @Override
    public int getType() {
        return 8;
    }

    public Label getLabel() {
        if (this.label == null) {
            this.label = new Label();
        }
        return this.label;
    }

    @Override
    public void accept(MethodVisitor cv) {
        cv.visitLabel(this.getLabel());
    }

    @Override
    public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels) {
        return labels.get(this);
    }

    public void resetLabel() {
        this.label = null;
    }
}
