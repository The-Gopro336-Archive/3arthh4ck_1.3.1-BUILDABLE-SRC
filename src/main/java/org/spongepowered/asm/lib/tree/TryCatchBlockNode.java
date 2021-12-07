package org.spongepowered.asm.lib.tree;

import java.util.List;
import org.spongepowered.asm.lib.MethodVisitor;
import org.spongepowered.asm.lib.tree.LabelNode;
import org.spongepowered.asm.lib.tree.TypeAnnotationNode;

public class TryCatchBlockNode {
    public LabelNode start;
    public LabelNode end;
    public LabelNode handler;
    public String type;
    public List<TypeAnnotationNode> visibleTypeAnnotations;
    public List<TypeAnnotationNode> invisibleTypeAnnotations;

    public TryCatchBlockNode(LabelNode start, LabelNode end, LabelNode handler, String type) {
        this.start = start;
        this.end = end;
        this.handler = handler;
        this.type = type;
    }

    public void updateIndex(int index) {
        int newTypeRef = 0x42000000 | index << 8;
        if (this.visibleTypeAnnotations != null) {
            for (TypeAnnotationNode tan : this.visibleTypeAnnotations) {
                tan.typeRef = newTypeRef;
            }
        }
        if (this.invisibleTypeAnnotations != null) {
            for (TypeAnnotationNode tan : this.invisibleTypeAnnotations) {
                tan.typeRef = newTypeRef;
            }
        }
    }

    public void accept(MethodVisitor mv) {
        TypeAnnotationNode an;
        int i;
        mv.visitTryCatchBlock(this.start.getLabel(), this.end.getLabel(), this.handler == null ? null : this.handler.getLabel(), this.type);
        int n = this.visibleTypeAnnotations == null ? 0 : this.visibleTypeAnnotations.size();
        for (i = 0; i < n; ++i) {
            an = this.visibleTypeAnnotations.get(i);
            an.accept(mv.visitTryCatchAnnotation(an.typeRef, an.typePath, an.desc, true));
        }
        n = this.invisibleTypeAnnotations == null ? 0 : this.invisibleTypeAnnotations.size();
        for (i = 0; i < n; ++i) {
            an = this.invisibleTypeAnnotations.get(i);
            an.accept(mv.visitTryCatchAnnotation(an.typeRef, an.typePath, an.desc, false));
        }
    }
}
