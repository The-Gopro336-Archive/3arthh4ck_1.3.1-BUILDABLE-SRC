package org.spongepowered.asm.lib.tree;

import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;

final class InsnList$InsnListIterator
implements ListIterator {
    AbstractInsnNode next;
    AbstractInsnNode prev;
    AbstractInsnNode remove;

    InsnList$InsnListIterator(int index) {
        if (index == InsnList.this.size()) {
            this.next = null;
            this.prev = InsnList.this.getLast();
        } else {
            this.next = InsnList.this.get(index);
            this.prev = this.next.prev;
        }
    }

    public boolean hasNext() {
        return this.next != null;
    }

    public Object next() {
        AbstractInsnNode result;
        if (this.next == null) {
            throw new NoSuchElementException();
        }
        this.prev = result = this.next;
        this.next = result.next;
        this.remove = result;
        return result;
    }

    public void remove() {
        if (this.remove != null) {
            if (this.remove == this.next) {
                this.next = this.next.next;
            } else {
                this.prev = this.prev.prev;
            }
        } else {
            throw new IllegalStateException();
        }
        InsnList.this.remove(this.remove);
        this.remove = null;
    }

    public boolean hasPrevious() {
        return this.prev != null;
    }

    public Object previous() {
        AbstractInsnNode result;
        this.next = result = this.prev;
        this.prev = result.prev;
        this.remove = result;
        return result;
    }

    public int nextIndex() {
        if (this.next == null) {
            return InsnList.this.size();
        }
        if (InsnList.this.cache == null) {
            InsnList.this.cache = InsnList.this.toArray();
        }
        return this.next.index;
    }

    public int previousIndex() {
        if (this.prev == null) {
            return -1;
        }
        if (InsnList.this.cache == null) {
            InsnList.this.cache = InsnList.this.toArray();
        }
        return this.prev.index;
    }

    public void add(Object o) {
        if (this.next != null) {
            InsnList.this.insertBefore(this.next, (AbstractInsnNode)o);
        } else if (this.prev != null) {
            InsnList.this.insert(this.prev, (AbstractInsnNode)o);
        } else {
            InsnList.this.add((AbstractInsnNode)o);
        }
        this.prev = (AbstractInsnNode)o;
        this.remove = null;
    }

    public void set(Object o) {
        if (this.remove != null) {
            InsnList.this.set(this.remove, (AbstractInsnNode)o);
            if (this.remove == this.prev) {
                this.prev = (AbstractInsnNode)o;
            } else {
                this.next = (AbstractInsnNode)o;
            }
        } else {
            throw new IllegalStateException();
        }
    }
}
