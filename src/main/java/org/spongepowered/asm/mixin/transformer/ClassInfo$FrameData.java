package org.spongepowered.asm.mixin.transformer;

import org.spongepowered.asm.lib.tree.FrameNode;

public class ClassInfo$FrameData {
    private static final String[] FRAMETYPES = new String[]{"NEW", "FULL", "APPEND", "CHOP", "SAME", "SAME1"};
    public final int index;
    public final int type;
    public final int locals;

    ClassInfo$FrameData(int index, int type, int locals) {
        this.index = index;
        this.type = type;
        this.locals = locals;
    }

    ClassInfo$FrameData(int index, FrameNode frameNode) {
        this.index = index;
        this.type = frameNode.type;
        this.locals = frameNode.local != null ? frameNode.local.size() : 0;
    }

    public String toString() {
        return String.format("FrameData[index=%d, type=%s, locals=%d]", this.index, FRAMETYPES[this.type + 1], this.locals);
    }
}
