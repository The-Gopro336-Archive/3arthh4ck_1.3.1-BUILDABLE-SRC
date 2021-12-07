package org.spongepowered.asm.mixin.injection.struct;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.spongepowered.asm.lib.Label;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.lib.tree.InsnList;
import org.spongepowered.asm.lib.tree.LabelNode;
import org.spongepowered.asm.lib.tree.LocalVariableNode;
import org.spongepowered.asm.lib.tree.MethodInsnNode;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.lib.tree.TypeInsnNode;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.struct.InjectionNodes;
import org.spongepowered.asm.mixin.transformer.ClassInfo;
import org.spongepowered.asm.util.Bytecode;

public class Target
implements Comparable<Target>,
Iterable<AbstractInsnNode> {
    public final ClassNode classNode;
    public final MethodNode method;
    public final InsnList insns;
    public final boolean isStatic;
    public final boolean isCtor;
    public final Type[] arguments;
    public final Type returnType;
    private final int maxStack;
    private final int maxLocals;
    private final InjectionNodes injectionNodes = new InjectionNodes();
    private String callbackInfoClass;
    private String callbackDescriptor;
    private int[] argIndices;
    private List<Integer> argMapVars;
    private LabelNode start;
    private LabelNode end;

    public Target(ClassNode classNode, MethodNode method) {
        this.classNode = classNode;
        this.method = method;
        this.insns = method.instructions;
        this.isStatic = Bytecode.methodIsStatic(method);
        this.isCtor = method.name.equals("<init>");
        this.arguments = Type.getArgumentTypes(method.desc);
        this.returnType = Type.getReturnType(method.desc);
        this.maxStack = method.maxStack;
        this.maxLocals = method.maxLocals;
    }

    public InjectionNodes.InjectionNode addInjectionNode(AbstractInsnNode node) {
        return this.injectionNodes.add(node);
    }

    public InjectionNodes.InjectionNode getInjectionNode(AbstractInsnNode node) {
        return this.injectionNodes.get(node);
    }

    public int getMaxLocals() {
        return this.maxLocals;
    }

    public int getMaxStack() {
        return this.maxStack;
    }

    public int getCurrentMaxLocals() {
        return this.method.maxLocals;
    }

    public int getCurrentMaxStack() {
        return this.method.maxStack;
    }

    public int allocateLocal() {
        return this.allocateLocals(1);
    }

    public int allocateLocals(int locals) {
        int nextLocal = this.method.maxLocals;
        this.method.maxLocals += locals;
        return nextLocal;
    }

    public void addToLocals(int locals) {
        this.setMaxLocals(this.maxLocals + locals);
    }

    public void setMaxLocals(int maxLocals) {
        if (maxLocals > this.method.maxLocals) {
            this.method.maxLocals = maxLocals;
        }
    }

    public void addToStack(int stack) {
        this.setMaxStack(this.maxStack + stack);
    }

    public void setMaxStack(int maxStack) {
        if (maxStack > this.method.maxStack) {
            this.method.maxStack = maxStack;
        }
    }

    public int[] generateArgMap(Type[] args, int start) {
        if (this.argMapVars == null) {
            this.argMapVars = new ArrayList<Integer>();
        }
        int[] argMap = new int[args.length];
        int index = 0;
        for (int arg = start; arg < args.length; ++arg) {
            int size = args[arg].getSize();
            argMap[arg] = this.allocateArgMapLocal(index, size);
            index += size;
        }
        return argMap;
    }

    private int allocateArgMapLocal(int index, int size) {
        if (index >= this.argMapVars.size()) {
            int base = this.allocateLocals(size);
            for (int offset = 0; offset < size; ++offset) {
                this.argMapVars.add(base + offset);
            }
            return base;
        }
        int local = this.argMapVars.get(index);
        if (size > 1 && index + size > this.argMapVars.size()) {
            int nextLocal = this.allocateLocals(1);
            if (nextLocal == local + 1) {
                this.argMapVars.add(nextLocal);
                return local;
            }
            this.argMapVars.set(index, nextLocal);
            this.argMapVars.add(this.allocateLocals(1));
            return nextLocal;
        }
        return local;
    }

    public int[] getArgIndices() {
        if (this.argIndices == null) {
            this.argIndices = this.calcArgIndices(this.isStatic ? 0 : 1);
        }
        return this.argIndices;
    }

    private int[] calcArgIndices(int local) {
        int[] argIndices = new int[this.arguments.length];
        for (int arg = 0; arg < this.arguments.length; ++arg) {
            argIndices[arg] = local;
            local += this.arguments[arg].getSize();
        }
        return argIndices;
    }

    public String getCallbackInfoClass() {
        if (this.callbackInfoClass == null) {
            this.callbackInfoClass = CallbackInfo.getCallInfoClassName(this.returnType);
        }
        return this.callbackInfoClass;
    }

    public String getSimpleCallbackDescriptor() {
        return String.format("(L%s;)V", this.getCallbackInfoClass());
    }

    public String getCallbackDescriptor(Type[] locals, Type[] argumentTypes) {
        return this.getCallbackDescriptor(false, locals, argumentTypes, 0, Short.MAX_VALUE);
    }

    public String getCallbackDescriptor(boolean captureLocals, Type[] locals, Type[] argumentTypes, int startIndex, int extra) {
        if (this.callbackDescriptor == null) {
            this.callbackDescriptor = String.format("(%sL%s;)V", this.method.desc.substring(1, this.method.desc.indexOf(41)), this.getCallbackInfoClass());
        }
        if (!captureLocals || locals == null) {
            return this.callbackDescriptor;
        }
        StringBuilder descriptor = new StringBuilder(this.callbackDescriptor.substring(0, this.callbackDescriptor.indexOf(41)));
        for (int l = startIndex; l < locals.length && extra > 0; ++l) {
            if (locals[l] == null) continue;
            descriptor.append(locals[l].getDescriptor());
            --extra;
        }
        return descriptor.append(")V").toString();
    }

    public String toString() {
        return String.format("%s::%s%s", this.classNode.name, this.method.name, this.method.desc);
    }

    @Override
    public int compareTo(Target o) {
        if (o == null) {
            return Integer.MAX_VALUE;
        }
        return this.toString().compareTo(o.toString());
    }

    public int indexOf(InjectionNodes.InjectionNode node) {
        return this.insns.indexOf(node.getCurrentTarget());
    }

    public int indexOf(AbstractInsnNode insn) {
        return this.insns.indexOf(insn);
    }

    public AbstractInsnNode get(int index) {
        return this.insns.get(index);
    }

    @Override
    public Iterator<AbstractInsnNode> iterator() {
        return this.insns.iterator();
    }

    public MethodInsnNode findInitNodeFor(TypeInsnNode newNode) {
        int start = this.indexOf(newNode);
        ListIterator<AbstractInsnNode> iter = this.insns.iterator(start);
        while (iter.hasNext()) {
            AbstractInsnNode insn = (AbstractInsnNode)iter.next();
            if (!(insn instanceof MethodInsnNode) || insn.getOpcode() != 183) continue;
            MethodInsnNode methodNode = (MethodInsnNode)insn;
            if (!"<init>".equals(methodNode.name) || !methodNode.owner.equals(newNode.desc)) continue;
            return methodNode;
        }
        return null;
    }

    public MethodInsnNode findSuperInitNode() {
        if (!this.isCtor) {
            return null;
        }
        return Bytecode.findSuperInit(this.method, ClassInfo.forName(this.classNode.name).getSuperName());
    }

    public void insertBefore(InjectionNodes.InjectionNode location, InsnList insns) {
        this.insns.insertBefore(location.getCurrentTarget(), insns);
    }

    public void insertBefore(AbstractInsnNode location, InsnList insns) {
        this.insns.insertBefore(location, insns);
    }

    public void replaceNode(AbstractInsnNode location, AbstractInsnNode insn) {
        this.insns.insertBefore(location, insn);
        this.insns.remove(location);
        this.injectionNodes.replace(location, insn);
    }

    public void replaceNode(AbstractInsnNode location, AbstractInsnNode champion, InsnList insns) {
        this.insns.insertBefore(location, insns);
        this.insns.remove(location);
        this.injectionNodes.replace(location, champion);
    }

    public void wrapNode(AbstractInsnNode location, AbstractInsnNode champion, InsnList before, InsnList after) {
        this.insns.insertBefore(location, before);
        this.insns.insert(location, after);
        this.injectionNodes.replace(location, champion);
    }

    public void replaceNode(AbstractInsnNode location, InsnList insns) {
        this.insns.insertBefore(location, insns);
        this.removeNode(location);
    }

    public void removeNode(AbstractInsnNode insn) {
        this.insns.remove(insn);
        this.injectionNodes.remove(insn);
    }

    public void addLocalVariable(int index, String name, String desc) {
        if (this.start == null) {
            this.start = new LabelNode(new Label());
            this.end = new LabelNode(new Label());
            this.insns.insert(this.start);
            this.insns.add(this.end);
        }
        this.addLocalVariable(index, name, desc, this.start, this.end);
    }

    private void addLocalVariable(int index, String name, String desc, LabelNode start, LabelNode end) {
        if (this.method.localVariables == null) {
            this.method.localVariables = new ArrayList<LocalVariableNode>();
        }
        this.method.localVariables.add(new LocalVariableNode(name, desc, null, start, end, index));
    }
}
