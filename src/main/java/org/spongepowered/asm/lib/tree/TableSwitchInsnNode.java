package org.spongepowered.asm.lib.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.spongepowered.asm.lib.Label;
import org.spongepowered.asm.lib.MethodVisitor;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.LabelNode;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TableSwitchInsnNode
extends AbstractInsnNode {
    public int min;
    public int max;
    public LabelNode dflt;
    public List<LabelNode> labels;

    public TableSwitchInsnNode(int min, int max, LabelNode dflt, LabelNode ... labels) {
        super(170);
        this.min = min;
        this.max = max;
        this.dflt = dflt;
        this.labels = new ArrayList<LabelNode>();
        if (labels != null) {
            this.labels.addAll(Arrays.asList(labels));
        }
    }

    @Override
    public int getType() {
        return 11;
    }

    @Override
    public void accept(MethodVisitor mv) {
        Label[] labels = new Label[this.labels.size()];
        for (int i = 0; i < labels.length; ++i) {
            labels[i] = this.labels.get(i).getLabel();
        }
        mv.visitTableSwitchInsn(this.min, this.max, this.dflt.getLabel(), labels);
        this.acceptAnnotations(mv);
    }

    @Override
    public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels) {
        return new TableSwitchInsnNode(this.min, this.max, TableSwitchInsnNode.clone(this.dflt, labels), TableSwitchInsnNode.clone(this.labels, labels)).cloneAnnotations(this);
    }
}
