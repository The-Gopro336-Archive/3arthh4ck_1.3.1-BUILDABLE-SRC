package org.spongepowered.asm.mixin.transformer;

import org.spongepowered.asm.mixin.transformer.ClassInfo;

public enum ClassInfo$Traversal {
    NONE(null, false, ClassInfo.SearchType.SUPER_CLASSES_ONLY),
    ALL(null, true, ClassInfo.SearchType.ALL_CLASSES),
    IMMEDIATE(NONE, true, ClassInfo.SearchType.SUPER_CLASSES_ONLY),
    SUPER(ALL, false, ClassInfo.SearchType.SUPER_CLASSES_ONLY);

    private final ClassInfo$Traversal next;
    private final boolean traverse;
    private final ClassInfo.SearchType searchType;

    private ClassInfo$Traversal(ClassInfo$Traversal next, boolean traverse, ClassInfo.SearchType searchType) {
        this.next = next != null ? next : this;
        this.traverse = traverse;
        this.searchType = searchType;
    }

    public ClassInfo$Traversal next() {
        return this.next;
    }

    public boolean canTraverse() {
        return this.traverse;
    }

    public ClassInfo.SearchType getSearchType() {
        return this.searchType;
    }
}
