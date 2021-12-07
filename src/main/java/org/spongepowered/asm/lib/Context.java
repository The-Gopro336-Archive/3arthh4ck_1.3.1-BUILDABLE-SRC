package org.spongepowered.asm.lib;

import org.spongepowered.asm.lib.Attribute;
import org.spongepowered.asm.lib.Label;
import org.spongepowered.asm.lib.TypePath;

class Context {
    Attribute[] attrs;
    int flags;
    char[] buffer;
    int[] bootstrapMethods;
    int access;
    String name;
    String desc;
    Label[] labels;
    int typeRef;
    TypePath typePath;
    int offset;
    Label[] start;
    Label[] end;
    int[] index;
    int mode;
    int localCount;
    int localDiff;
    Object[] local;
    int stackCount;
    Object[] stack;

    Context() {
    }
}
