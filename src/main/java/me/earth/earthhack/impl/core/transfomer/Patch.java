/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.objectweb.asm.tree.ClassNode
 */
package me.earth.earthhack.impl.core.transfomer;

import org.objectweb.asm.tree.ClassNode;

public interface Patch {
    public String getName();

    public String getTransformedName();

    public void apply(ClassNode var1);

    public boolean isFinished();
}

