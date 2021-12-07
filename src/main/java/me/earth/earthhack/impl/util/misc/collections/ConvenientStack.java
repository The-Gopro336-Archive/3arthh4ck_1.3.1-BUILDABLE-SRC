/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.util.misc.collections;

import java.util.Collection;
import java.util.Stack;

public class ConvenientStack<E>
extends Stack<E> {
    public ConvenientStack() {
    }

    public ConvenientStack(Collection<E> collection) {
        this.addAll(collection);
    }

    @Override
    public synchronized E pop() {
        if (this.empty()) {
            return null;
        }
        return super.pop();
    }

    @Override
    public synchronized E peek() {
        if (this.empty()) {
            return null;
        }
        return super.peek();
    }
}

