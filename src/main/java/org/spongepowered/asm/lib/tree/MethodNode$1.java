package org.spongepowered.asm.lib.tree;

import java.util.ArrayList;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class MethodNode$1
extends ArrayList<Object> {
    MethodNode$1(int x0) {
        super(x0);
    }

    @Override
    public boolean add(Object o) {
        MethodNode.this.annotationDefault = o;
        return super.add(o);
    }
}
