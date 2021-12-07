package org.spongepowered.asm.mixin.injection.code;

import com.google.common.base.Strings;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.AnnotationNode;
import org.spongepowered.asm.lib.tree.InsnList;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.IInjectionPointContext;
import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.code.ISliceContext;
import org.spongepowered.asm.mixin.injection.code.ReadOnlyInsnList;
import org.spongepowered.asm.mixin.injection.throwables.InjectionError;
import org.spongepowered.asm.mixin.injection.throwables.InvalidSliceException;
import org.spongepowered.asm.util.Annotations;
import org.spongepowered.asm.util.Bytecode;

public final class MethodSlice {
    private static final Logger logger = LogManager.getLogger("mixin");
    private final ISliceContext owner;
    private final String id;
    private final InjectionPoint from;
    private final InjectionPoint to;
    private final String name;

    private MethodSlice(ISliceContext owner, String id, InjectionPoint from, InjectionPoint to) {
        if (from == null && to == null) {
            throw new InvalidSliceException(owner, String.format("%s is redundant. No 'from' or 'to' value specified", this));
        }
        this.owner = owner;
        this.id = Strings.nullToEmpty(id);
        this.from = from;
        this.to = to;
        this.name = MethodSlice.getSliceName(id);
    }

    public String getId() {
        return this.id;
    }

    public ReadOnlyInsnList getSlice(MethodNode method) {
        int end;
        int max = method.instructions.size() - 1;
        int start = this.find(method, this.from, 0, 0, this.name + "(from)");
        if (start > (end = this.find(method, this.to, max, start, this.name + "(to)"))) {
            throw new InvalidSliceException(this.owner, String.format("%s is negative size. Range(%d -> %d)", this.describe(), start, end));
        }
        if (start < 0 || end < 0 || start > max || end > max) {
            throw new InjectionError("Unexpected critical error in " + this + ": out of bounds start=" + start + " end=" + end + " lim=" + max);
        }
        if (start == 0 && end == max) {
            return new ReadOnlyInsnList(method.instructions);
        }
        return new InsnListSlice(method.instructions, start, end);
    }

    private int find(MethodNode method, InjectionPoint injectionPoint, int defaultValue, int failValue, String description) {
        if (injectionPoint == null) {
            return defaultValue;
        }
        LinkedList<AbstractInsnNode> nodes = new LinkedList<AbstractInsnNode>();
        ReadOnlyInsnList insns = new ReadOnlyInsnList(method.instructions);
        boolean result = injectionPoint.find(method.desc, insns, nodes);
        InjectionPoint.Selector select = injectionPoint.getSelector();
        if (nodes.size() != 1 && select == InjectionPoint.Selector.ONE) {
            throw new InvalidSliceException(this.owner, String.format("%s requires 1 result but found %d", this.describe(description), nodes.size()));
        }
        if (!result) {
            if (this.owner.getContext().getOption(MixinEnvironment.Option.DEBUG_VERBOSE)) {
                logger.warn("{} did not match any instructions", new Object[]{this.describe(description)});
            }
            return failValue;
        }
        return method.instructions.indexOf(select == InjectionPoint.Selector.FIRST ? (AbstractInsnNode)nodes.getFirst() : (AbstractInsnNode)nodes.getLast());
    }

    public String toString() {
        return this.describe();
    }

    private String describe() {
        return this.describe(this.name);
    }

    private String describe(String description) {
        return MethodSlice.describeSlice(description, this.owner);
    }

    private static String describeSlice(String description, ISliceContext owner) {
        String annotation = Bytecode.getSimpleName(owner.getAnnotation());
        MethodNode method = owner.getMethod();
        return String.format("%s->%s(%s)::%s%s", owner.getContext(), annotation, description, method.name, method.desc);
    }

    private static String getSliceName(String id) {
        return String.format("@Slice[%s]", Strings.nullToEmpty(id));
    }

    public static MethodSlice parse(ISliceContext owner, Slice slice) {
        String id = slice.id();
        At from = slice.from();
        At to = slice.to();
        InjectionPoint fromPoint = from != null ? InjectionPoint.parse((IInjectionPointContext)owner, from) : null;
        InjectionPoint toPoint = to != null ? InjectionPoint.parse((IInjectionPointContext)owner, to) : null;
        return new MethodSlice(owner, id, fromPoint, toPoint);
    }

    public static MethodSlice parse(ISliceContext info, AnnotationNode node) {
        String id = (String)Annotations.getValue(node, "id");
        AnnotationNode from = (AnnotationNode)Annotations.getValue(node, "from");
        AnnotationNode to = (AnnotationNode)Annotations.getValue(node, "to");
        InjectionPoint fromPoint = from != null ? InjectionPoint.parse((IInjectionPointContext)info, from) : null;
        InjectionPoint toPoint = to != null ? InjectionPoint.parse((IInjectionPointContext)info, to) : null;
        return new MethodSlice(info, id, fromPoint, toPoint);
    }

    static final class InsnListSlice
    extends ReadOnlyInsnList {
        private final int start;
        private final int end;

        protected InsnListSlice(InsnList inner, int start, int end) {
            super(inner);
            this.start = start;
            this.end = end;
        }

        @Override
        public ListIterator<AbstractInsnNode> iterator() {
            return this.iterator(0);
        }

        @Override
        public ListIterator<AbstractInsnNode> iterator(int index) {
            return new SliceIterator(super.iterator(this.start + index), this.start, this.end, this.start + index);
        }

        @Override
        public AbstractInsnNode[] toArray() {
            AbstractInsnNode[] all = super.toArray();
            AbstractInsnNode[] subset = new AbstractInsnNode[this.size()];
            System.arraycopy(all, this.start, subset, 0, subset.length);
            return subset;
        }

        @Override
        public int size() {
            return this.end - this.start + 1;
        }

        @Override
        public AbstractInsnNode getFirst() {
            return super.get(this.start);
        }

        @Override
        public AbstractInsnNode getLast() {
            return super.get(this.end);
        }

        @Override
        public AbstractInsnNode get(int index) {
            return super.get(this.start + index);
        }

        @Override
        public boolean contains(AbstractInsnNode insn) {
            for (AbstractInsnNode node : this.toArray()) {
                if (node != insn) continue;
                return true;
            }
            return false;
        }

        @Override
        public int indexOf(AbstractInsnNode insn) {
            int index = super.indexOf(insn);
            return index >= this.start && index <= this.end ? index - this.start : -1;
        }

        public int realIndexOf(AbstractInsnNode insn) {
            return super.indexOf(insn);
        }

        static class SliceIterator
        implements ListIterator<AbstractInsnNode> {
            private final ListIterator<AbstractInsnNode> iter;
            private int start;
            private int end;
            private int index;

            public SliceIterator(ListIterator<AbstractInsnNode> iter, int start, int end, int index) {
                this.iter = iter;
                this.start = start;
                this.end = end;
                this.index = index;
            }

            @Override
            public boolean hasNext() {
                return this.index <= this.end && this.iter.hasNext();
            }

            @Override
            public AbstractInsnNode next() {
                if (this.index > this.end) {
                    throw new NoSuchElementException();
                }
                ++this.index;
                return this.iter.next();
            }

            @Override
            public boolean hasPrevious() {
                return this.index > this.start;
            }

            @Override
            public AbstractInsnNode previous() {
                if (this.index <= this.start) {
                    throw new NoSuchElementException();
                }
                --this.index;
                return this.iter.previous();
            }

            @Override
            public int nextIndex() {
                return this.index - this.start;
            }

            @Override
            public int previousIndex() {
                return this.index - this.start - 1;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Cannot remove insn from slice");
            }

            @Override
            public void set(AbstractInsnNode e) {
                throw new UnsupportedOperationException("Cannot set insn using slice");
            }

            @Override
            public void add(AbstractInsnNode e) {
                throw new UnsupportedOperationException("Cannot add insn using slice");
            }
        }
    }
}
