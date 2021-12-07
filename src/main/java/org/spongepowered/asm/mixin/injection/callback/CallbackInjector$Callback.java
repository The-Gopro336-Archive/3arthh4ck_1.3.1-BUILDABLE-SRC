package org.spongepowered.asm.mixin.injection.callback;

import java.util.ArrayList;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.InsnList;
import org.spongepowered.asm.lib.tree.InsnNode;
import org.spongepowered.asm.lib.tree.LocalVariableNode;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInjector;
import org.spongepowered.asm.mixin.injection.code.Injector;
import org.spongepowered.asm.mixin.injection.struct.InjectionNodes;
import org.spongepowered.asm.mixin.injection.struct.Target;
import org.spongepowered.asm.util.Annotations;
import org.spongepowered.asm.util.Bytecode;

class CallbackInjector$Callback
extends InsnList {
    private final MethodNode handler;
    private final AbstractInsnNode head;
    final Target target;
    final InjectionNodes.InjectionNode node;
    final LocalVariableNode[] locals;
    final Type[] localTypes;
    final int frameSize;
    final int extraArgs;
    final boolean canCaptureLocals;
    final boolean isAtReturn;
    final String desc;
    final String descl;
    final String[] argNames;
    int ctor;
    int invoke;
    private int marshalVar = -1;
    private boolean captureArgs = true;

    CallbackInjector$Callback(MethodNode handler, Target target, InjectionNodes.InjectionNode node, LocalVariableNode[] locals, boolean captureLocals) {
        this.handler = handler;
        this.target = target;
        this.head = target.insns.getFirst();
        this.node = node;
        this.locals = locals;
        this.localTypes = locals != null ? new Type[locals.length] : null;
        this.frameSize = Bytecode.getFirstNonArgLocalIndex(target.arguments, !CallbackInjector.this.isStatic());
        ArrayList<String> argNames = null;
        if (locals != null) {
            int baseArgIndex = CallbackInjector.this.isStatic() ? 0 : 1;
            argNames = new ArrayList<String>();
            for (int l = 0; l <= locals.length; ++l) {
                if (l == this.frameSize) {
                    argNames.add(target.returnType == Type.VOID_TYPE ? "ci" : "cir");
                }
                if (l >= locals.length || locals[l] == null) continue;
                this.localTypes[l] = Type.getType(locals[l].desc);
                if (l < baseArgIndex) continue;
                argNames.add(CallbackInjector.meltSnowman(l, locals[l].name));
            }
        }
        this.extraArgs = Math.max(0, Bytecode.getFirstNonArgLocalIndex(this.handler) - (this.frameSize + 1));
        this.argNames = argNames != null ? argNames.toArray(new String[argNames.size()]) : null;
        this.canCaptureLocals = captureLocals && locals != null && locals.length > this.frameSize;
        this.isAtReturn = this.node.getCurrentTarget() instanceof InsnNode && this.isValueReturnOpcode(this.node.getCurrentTarget().getOpcode());
        this.desc = target.getCallbackDescriptor(this.localTypes, target.arguments);
        this.descl = target.getCallbackDescriptor(true, this.localTypes, target.arguments, this.frameSize, this.extraArgs);
        this.invoke = target.arguments.length + (this.canCaptureLocals ? this.localTypes.length - this.frameSize : 0);
    }

    private boolean isValueReturnOpcode(int opcode) {
        return opcode >= 172 && opcode < 177;
    }

    String getDescriptor() {
        return this.canCaptureLocals ? this.descl : this.desc;
    }

    String getDescriptorWithAllLocals() {
        return this.target.getCallbackDescriptor(true, this.localTypes, this.target.arguments, this.frameSize, Short.MAX_VALUE);
    }

    String getCallbackInfoConstructorDescriptor() {
        return this.isAtReturn ? CallbackInfo.getConstructorDescriptor(this.target.returnType) : CallbackInfo.getConstructorDescriptor();
    }

    void add(AbstractInsnNode insn, boolean ctorStack, boolean invokeStack) {
        this.add(insn, ctorStack, invokeStack, false);
    }

    void add(AbstractInsnNode insn, boolean ctorStack, boolean invokeStack, boolean head) {
        if (head) {
            this.target.insns.insertBefore(this.head, insn);
        } else {
            this.add(insn);
        }
        this.ctor += ctorStack ? 1 : 0;
        this.invoke += invokeStack ? 1 : 0;
    }

    void inject() {
        this.target.insertBefore(this.node, (InsnList)this);
        this.target.addToStack(Math.max(this.invoke, this.ctor));
    }

    boolean checkDescriptor(String desc) {
        Type[] myTypes;
        if (this.getDescriptor().equals(desc)) {
            return true;
        }
        if (this.target.getSimpleCallbackDescriptor().equals(desc) && !this.canCaptureLocals) {
            this.captureArgs = false;
            return true;
        }
        Type[] inTypes = Type.getArgumentTypes(desc);
        if (inTypes.length != (myTypes = Type.getArgumentTypes(this.descl)).length) {
            return false;
        }
        for (int arg = 0; arg < myTypes.length; ++arg) {
            Type type = inTypes[arg];
            if (type.equals(myTypes[arg])) continue;
            if (type.getSort() == 9) {
                return false;
            }
            if (Annotations.getInvisibleParameter(this.handler, Coerce.class, arg) == null) {
                return false;
            }
            if (Injector.canCoerce(inTypes[arg], myTypes[arg])) continue;
            return false;
        }
        return true;
    }

    boolean captureArgs() {
        return this.captureArgs;
    }

    int marshalVar() {
        if (this.marshalVar < 0) {
            this.marshalVar = this.target.allocateLocal();
        }
        return this.marshalVar;
    }
}
