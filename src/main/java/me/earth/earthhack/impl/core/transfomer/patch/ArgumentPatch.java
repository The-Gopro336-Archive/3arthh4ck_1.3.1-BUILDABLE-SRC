/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.objectweb.asm.tree.ClassNode
 */
package me.earth.earthhack.impl.core.transfomer.patch;

import me.earth.earthhack.impl.core.transfomer.patch.FinishingPatch;
import me.earth.earthhack.tweaker.launch.Argument;
import me.earth.earthhack.tweaker.launch.DevArguments;
import org.objectweb.asm.tree.ClassNode;

public abstract class ArgumentPatch
extends FinishingPatch {
    private final String argument;

    public ArgumentPatch(String name, String transformed, String argument) {
        super(name, transformed);
        this.argument = argument;
    }

    protected abstract void applyPatch(ClassNode var1);

    @Override
    public void apply(ClassNode node) {
        DevArguments dev = DevArguments.getInstance();
        Argument arg = dev.getArgument(this.argument);
        if (arg != null && !((Boolean)arg.getValue()).booleanValue()) {
            this.setFinished(true);
            return;
        }
        this.applyPatch(node);
    }
}

