package org.spongepowered.asm.util;

public class ReEntranceLock {
    private final int maxDepth;
    private int depth = 0;
    private boolean semaphore = false;

    public ReEntranceLock(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public int getMaxDepth() {
        return this.maxDepth;
    }

    public int getDepth() {
        return this.depth;
    }

    public ReEntranceLock push() {
        ++this.depth;
        this.checkAndSet();
        return this;
    }

    public ReEntranceLock pop() {
        if (this.depth == 0) {
            throw new IllegalStateException("ReEntranceLock pop() with zero depth");
        }
        --this.depth;
        return this;
    }

    public boolean check() {
        return this.depth > this.maxDepth;
    }

    public boolean checkAndSet() {
        return this.semaphore |= this.check();
    }

    public ReEntranceLock set() {
        this.semaphore = true;
        return this;
    }

    public boolean isSet() {
        return this.semaphore;
    }

    public ReEntranceLock clear() {
        this.semaphore = false;
        return this;
    }
}
