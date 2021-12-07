package org.spongepowered.asm.lib.tree;

import org.spongepowered.asm.lib.TypePath;
import org.spongepowered.asm.lib.tree.AnnotationNode;

public class TypeAnnotationNode
extends AnnotationNode {
    public int typeRef;
    public TypePath typePath;

    public TypeAnnotationNode(int typeRef, TypePath typePath, String desc) {
        this(327680, typeRef, typePath, desc);
        if (this.getClass() != TypeAnnotationNode.class) {
            throw new IllegalStateException();
        }
    }

    public TypeAnnotationNode(int api, int typeRef, TypePath typePath, String desc) {
        super(api, desc);
        this.typeRef = typeRef;
        this.typePath = typePath;
    }
}
