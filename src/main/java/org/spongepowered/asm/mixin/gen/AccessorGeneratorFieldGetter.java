package org.spongepowered.asm.mixin.gen;

import org.spongepowered.asm.lib.tree.FieldInsnNode;
import org.spongepowered.asm.lib.tree.InsnNode;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.lib.tree.VarInsnNode;
import org.spongepowered.asm.mixin.gen.AccessorGeneratorField;
import org.spongepowered.asm.mixin.gen.AccessorInfo;

public class AccessorGeneratorFieldGetter
extends AccessorGeneratorField {
    public AccessorGeneratorFieldGetter(AccessorInfo info) {
        super(info);
    }

    @Override
    public MethodNode generate() {
        MethodNode method = this.createMethod(this.targetType.getSize(), this.targetType.getSize());
        if (this.isInstanceField) {
            method.instructions.add(new VarInsnNode(25, 0));
        }
        int opcode = this.isInstanceField ? 180 : 178;
        method.instructions.add(new FieldInsnNode(opcode, this.info.getClassNode().name, this.targetField.name, this.targetField.desc));
        method.instructions.add(new InsnNode(this.targetType.getOpcode(172)));
        return method;
    }
}
