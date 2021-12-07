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
public class LookupSwitchInsnNode
extends AbstractInsnNode {
    public LabelNode dflt;
    public List<Integer> keys;
    public List<LabelNode> labels;

    public LookupSwitchInsnNode(LabelNode dflt, int[] keys, LabelNode[] labels) {
        super(171);
        this.dflt = dflt;
        this.keys = new ArrayList<Integer>(keys == null ? 0 : keys.length);
        this.labels = new ArrayList<LabelNode>(labels == null ? 0 : labels.length);
        if (keys != null) {
            for (int i = 0; i < keys.length; ++i) {
                this.keys.add(keys[i]);
            }
        }
        if (labels != null) {
            this.labels.addAll(Arrays.asList(labels));
        }
    }

    @Override
    public int getType() {
        return 12;
    }

    @Override
    public void accept(MethodVisitor mv) {
        int[] keys = new int[this.keys.size()];
        for (int i = 0; i < keys.length; ++i) {
            keys[i] = this.keys.get(i);
        }
        Label[] labels = new Label[this.labels.size()];
        for (int i = 0; i < labels.length; ++i) {
            labels[i] = this.labels.get(i).getLabel();
        }
        mv.visitLookupSwitchInsn(this.dflt.getLabel(), keys, labels);
        this.acceptAnnotations(mv);
    }

    @Override
    public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels) {
        LookupSwitchInsnNode clone = new LookupSwitchInsnNode(LookupSwitchInsnNode.clone(this.dflt, labels), null, LookupSwitchInsnNode.clone(this.labels, labels));
        clone.keys.addAll(this.keys);
        return clone.cloneAnnotations(this);
    }
}
