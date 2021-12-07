package org.spongepowered.asm.lib.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.spongepowered.asm.lib.Label;
import org.spongepowered.asm.lib.MethodVisitor;
import org.spongepowered.asm.lib.TypePath;
import org.spongepowered.asm.lib.tree.LabelNode;
import org.spongepowered.asm.lib.tree.TypeAnnotationNode;

public class LocalVariableAnnotationNode
extends TypeAnnotationNode {
    public List<LabelNode> start;
    public List<LabelNode> end;
    public List<Integer> index;

    public LocalVariableAnnotationNode(int typeRef, TypePath typePath, LabelNode[] start, LabelNode[] end, int[] index, String desc) {
        this(327680, typeRef, typePath, start, end, index, desc);
    }

    public LocalVariableAnnotationNode(int api, int typeRef, TypePath typePath, LabelNode[] start, LabelNode[] end, int[] index, String desc) {
        super(api, typeRef, typePath, desc);
        this.start = new ArrayList<LabelNode>(start.length);
        this.start.addAll(Arrays.asList(start));
        this.end = new ArrayList<LabelNode>(end.length);
        this.end.addAll(Arrays.asList(end));
        this.index = new ArrayList<Integer>(index.length);
        for (int i : index) {
            this.index.add(i);
        }
    }

    public void accept(MethodVisitor mv, boolean visible) {
        Label[] start = new Label[this.start.size()];
        Label[] end = new Label[this.end.size()];
        int[] index = new int[this.index.size()];
        for (int i = 0; i < start.length; ++i) {
            start[i] = this.start.get(i).getLabel();
            end[i] = this.end.get(i).getLabel();
            index[i] = this.index.get(i);
        }
        this.accept(mv.visitLocalVariableAnnotation(this.typeRef, this.typePath, start, end, index, this.desc, true));
    }
}
