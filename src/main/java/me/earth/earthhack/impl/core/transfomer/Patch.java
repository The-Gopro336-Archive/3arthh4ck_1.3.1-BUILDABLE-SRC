package me.earth.earthhack.impl.core.transfomer;

import org.objectweb.asm.tree.ClassNode;

public interface Patch {
    public String getName();

    public String getTransformedName();

    public void apply(ClassNode var1);

    public boolean isFinished();
}
