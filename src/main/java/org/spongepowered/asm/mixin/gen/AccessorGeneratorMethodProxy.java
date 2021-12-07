package org.spongepowered.asm.mixin.gen;

import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.tree.InsnNode;
import org.spongepowered.asm.lib.tree.MethodInsnNode;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.lib.tree.VarInsnNode;
import org.spongepowered.asm.mixin.gen.AccessorGenerator;
import org.spongepowered.asm.mixin.gen.AccessorInfo;
import org.spongepowered.asm.util.Bytecode;

public class AccessorGeneratorMethodProxy
extends AccessorGenerator {
    private final MethodNode targetMethod;
    private final Type[] argTypes;
    private final Type returnType;
    private final boolean isInstanceMethod;

    public AccessorGeneratorMethodProxy(AccessorInfo info) {
        super(info);
        this.targetMethod = info.getTargetMethod();
        this.argTypes = info.getArgTypes();
        this.returnType = info.getReturnType();
        this.isInstanceMethod = !Bytecode.hasFlag(this.targetMethod, 8);
    }

    @Override
    public MethodNode generate() {
        int size = Bytecode.getArgsSize(this.argTypes) + this.returnType.getSize() + (this.isInstanceMethod ? 1 : 0);
        MethodNode method = this.createMethod(size, size);
        if (this.isInstanceMethod) {
            method.instructions.add(new VarInsnNode(25, 0));
        }
        Bytecode.loadArgs(this.argTypes, method.instructions, this.isInstanceMethod ? 1 : 0);
        boolean isPrivate = Bytecode.hasFlag(this.targetMethod, 2);
        int opcode = this.isInstanceMethod ? (isPrivate ? 183 : 182) : 184;
        method.instructions.add(new MethodInsnNode(opcode, this.info.getClassNode().name, this.targetMethod.name, this.targetMethod.desc, false));
        method.instructions.add(new InsnNode(this.returnType.getOpcode(172)));
        return method;
    }
}
