package org.spongepowered.asm.mixin.injection.invoke;

import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.InsnList;
import org.spongepowered.asm.lib.tree.InsnNode;
import org.spongepowered.asm.lib.tree.MethodInsnNode;
import org.spongepowered.asm.lib.tree.VarInsnNode;
import org.spongepowered.asm.mixin.injection.invoke.InvokeInjector;
import org.spongepowered.asm.mixin.injection.invoke.arg.ArgsClassGenerator;
import org.spongepowered.asm.mixin.injection.struct.InjectionInfo;
import org.spongepowered.asm.mixin.injection.struct.InjectionNodes;
import org.spongepowered.asm.mixin.injection.struct.Target;
import org.spongepowered.asm.mixin.injection.throwables.InvalidInjectionException;
import org.spongepowered.asm.util.Bytecode;

public class ModifyArgsInjector
extends InvokeInjector {
    private final ArgsClassGenerator argsClassGenerator;

    public ModifyArgsInjector(InjectionInfo info) {
        super(info, "@ModifyArgs");
        this.argsClassGenerator = (ArgsClassGenerator)info.getContext().getExtensions().getGenerator(ArgsClassGenerator.class);
    }

    @Override
    protected void checkTarget(Target target) {
        this.checkTargetModifiers(target, false);
    }

    @Override
    protected void inject(Target target, InjectionNodes.InjectionNode node) {
        this.checkTargetForNode(target, node);
        super.inject(target, node);
    }

    @Override
    protected void injectAtInvoke(Target target, InjectionNodes.InjectionNode node) {
        MethodInsnNode targetMethod = (MethodInsnNode)node.getCurrentTarget();
        Type[] args = Type.getArgumentTypes(targetMethod.desc);
        if (args.length == 0) {
            throw new InvalidInjectionException(this.info, "@ModifyArgs injector " + this + " targets a method invocation " + targetMethod.name + targetMethod.desc + " with no arguments!");
        }
        String clArgs = this.argsClassGenerator.getClassRef(targetMethod.desc);
        boolean withArgs = this.verifyTarget(target);
        InsnList insns = new InsnList();
        target.addToStack(1);
        this.packArgs(insns, clArgs, targetMethod);
        if (withArgs) {
            target.addToStack(Bytecode.getArgsSize(target.arguments));
            Bytecode.loadArgs(target.arguments, insns, target.isStatic ? 0 : 1);
        }
        this.invokeHandler(insns);
        this.unpackArgs(insns, clArgs, args);
        target.insns.insertBefore((AbstractInsnNode)targetMethod, insns);
    }

    private boolean verifyTarget(Target target) {
        String shortDesc = String.format("(L%s;)V", ArgsClassGenerator.ARGS_REF);
        if (!this.methodNode.desc.equals(shortDesc)) {
            String targetDesc = Bytecode.changeDescriptorReturnType(target.method.desc, "V");
            String longDesc = String.format("(L%s;%s", ArgsClassGenerator.ARGS_REF, targetDesc.substring(1));
            if (this.methodNode.desc.equals(longDesc)) {
                return true;
            }
            throw new InvalidInjectionException(this.info, "@ModifyArgs injector " + this + " has an invalid signature " + this.methodNode.desc + ", expected " + shortDesc + " or " + longDesc);
        }
        return false;
    }

    private void packArgs(InsnList insns, String clArgs, MethodInsnNode targetMethod) {
        String factoryDesc = Bytecode.changeDescriptorReturnType(targetMethod.desc, "L" + clArgs + ";");
        insns.add(new MethodInsnNode(184, clArgs, "of", factoryDesc, false));
        insns.add(new InsnNode(89));
        if (!this.isStatic) {
            insns.add(new VarInsnNode(25, 0));
            insns.add(new InsnNode(95));
        }
    }

    private void unpackArgs(InsnList insns, String clArgs, Type[] args) {
        for (int i = 0; i < args.length; ++i) {
            if (i < args.length - 1) {
                insns.add(new InsnNode(89));
            }
            insns.add(new MethodInsnNode(182, clArgs, "$" + i, "()" + args[i].getDescriptor(), false));
            if (i >= args.length - 1) continue;
            if (args[i].getSize() == 1) {
                insns.add(new InsnNode(95));
                continue;
            }
            insns.add(new InsnNode(93));
            insns.add(new InsnNode(88));
        }
    }
}
