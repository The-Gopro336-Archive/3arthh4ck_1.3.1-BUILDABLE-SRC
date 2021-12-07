package org.spongepowered.asm.lib;

import org.spongepowered.asm.lib.Label;

class Edge {
    static final int NORMAL = 0;
    static final int EXCEPTION = Integer.MAX_VALUE;
    int info;
    Label successor;
    Edge next;

    Edge() {
    }
}
