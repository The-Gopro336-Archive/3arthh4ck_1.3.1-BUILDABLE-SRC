package org.spongepowered.asm.mixin.injection.code;

import java.util.ListIterator;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.InsnList;

class ReadOnlyInsnList
extends InsnList {
    private InsnList insnList;

    public ReadOnlyInsnList(InsnList insns) {
        this.insnList = insns;
    }

    void dispose() {
        this.insnList = null;
    }

    @Override
    public final void set(AbstractInsnNode location, AbstractInsnNode insn) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void add(AbstractInsnNode insn) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void add(InsnList insns) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void insert(AbstractInsnNode insn) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void insert(InsnList insns) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void insert(AbstractInsnNode location, AbstractInsnNode insn) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void insert(AbstractInsnNode location, InsnList insns) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void insertBefore(AbstractInsnNode location, AbstractInsnNode insn) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void insertBefore(AbstractInsnNode location, InsnList insns) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void remove(AbstractInsnNode insn) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AbstractInsnNode[] toArray() {
        return this.insnList.toArray();
    }

    @Override
    public int size() {
        return this.insnList.size();
    }

    @Override
    public AbstractInsnNode getFirst() {
        return this.insnList.getFirst();
    }

    @Override
    public AbstractInsnNode getLast() {
        return this.insnList.getLast();
    }

    @Override
    public AbstractInsnNode get(int index) {
        return this.insnList.get(index);
    }

    @Override
    public boolean contains(AbstractInsnNode insn) {
        return this.insnList.contains(insn);
    }

    @Override
    public int indexOf(AbstractInsnNode insn) {
        return this.insnList.indexOf(insn);
    }

    @Override
    public ListIterator<AbstractInsnNode> iterator() {
        return this.insnList.iterator();
    }

    @Override
    public ListIterator<AbstractInsnNode> iterator(int index) {
        return this.insnList.iterator(index);
    }

    @Override
    public final void resetLabels() {
        this.insnList.resetLabels();
    }
}
