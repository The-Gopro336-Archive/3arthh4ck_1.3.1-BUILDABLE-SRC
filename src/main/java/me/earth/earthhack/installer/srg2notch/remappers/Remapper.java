/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.objectweb.asm.tree.ClassNode
 */
package me.earth.earthhack.installer.srg2notch.remappers;

import me.earth.earthhack.installer.srg2notch.Mapping;
import org.objectweb.asm.tree.ClassNode;

public interface Remapper {
    public void remap(ClassNode var1, Mapping var2);
}

