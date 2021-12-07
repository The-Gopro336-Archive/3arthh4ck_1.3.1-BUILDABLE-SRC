package org.spongepowered.asm.mixin.injection.modify;

import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.InsnList;
import org.spongepowered.asm.mixin.injection.modify.LocalVariableDiscriminator;
import org.spongepowered.asm.mixin.injection.struct.Target;

class ModifyVariableInjector$Context
extends LocalVariableDiscriminator.Context {
    final InsnList insns = new InsnList();

    public ModifyVariableInjector$Context(Type returnType, boolean argsOnly, Target target, AbstractInsnNode node) {
        super(returnType, argsOnly, target, node);
    }
}
