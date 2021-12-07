package org.spongepowered.asm.lib.tree.analysis;

import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.analysis.Value;

public class AnalyzerException
extends Exception {
    public final AbstractInsnNode node;

    public AnalyzerException(AbstractInsnNode node, String msg) {
        super(msg);
        this.node = node;
    }

    public AnalyzerException(AbstractInsnNode node, String msg, Throwable exception) {
        super(msg, exception);
        this.node = node;
    }

    public AnalyzerException(AbstractInsnNode node, String msg, Object expected, Value encountered) {
        super((msg == null ? "Expected " : msg + ": expected ") + expected + ", but found " + encountered);
        this.node = node;
    }
}
