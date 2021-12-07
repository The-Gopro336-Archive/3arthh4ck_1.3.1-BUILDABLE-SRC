package org.spongepowered.asm.mixin.struct;

import org.spongepowered.asm.lib.tree.MethodInsnNode;
import org.spongepowered.asm.mixin.struct.MemberRef;

public final class MemberRef$Method
extends MemberRef {
    private static final int OPCODES = 191;
    public final MethodInsnNode insn;

    public MemberRef$Method(MethodInsnNode insn) {
        this.insn = insn;
    }

    @Override
    public boolean isField() {
        return false;
    }

    @Override
    public int getOpcode() {
        return this.insn.getOpcode();
    }

    @Override
    public void setOpcode(int opcode) {
        if ((opcode & 0xBF) == 0) {
            throw new IllegalArgumentException("Invalid opcode for method instruction: 0x" + Integer.toHexString(opcode));
        }
        this.insn.setOpcode(opcode);
    }

    @Override
    public String getOwner() {
        return this.insn.owner;
    }

    @Override
    public void setOwner(String owner) {
        this.insn.owner = owner;
    }

    @Override
    public String getName() {
        return this.insn.name;
    }

    @Override
    public void setName(String name) {
        this.insn.name = name;
    }

    @Override
    public String getDesc() {
        return this.insn.desc;
    }

    @Override
    public void setDesc(String desc) {
        this.insn.desc = desc;
    }
}
