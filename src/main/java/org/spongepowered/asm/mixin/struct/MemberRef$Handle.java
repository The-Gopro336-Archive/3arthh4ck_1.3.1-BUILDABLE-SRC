package org.spongepowered.asm.mixin.struct;

import org.spongepowered.asm.lib.Handle;
import org.spongepowered.asm.mixin.struct.MemberRef;
import org.spongepowered.asm.mixin.transformer.throwables.MixinTransformerError;
import org.spongepowered.asm.util.Bytecode;

public final class MemberRef$Handle
extends MemberRef {
    private Handle handle;

    public MemberRef$Handle(Handle handle) {
        this.handle = handle;
    }

    public Handle getMethodHandle() {
        return this.handle;
    }

    @Override
    public boolean isField() {
        switch (this.handle.getTag()) {
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 9: {
                return false;
            }
            case 1: 
            case 2: 
            case 3: 
            case 4: {
                return true;
            }
        }
        throw new MixinTransformerError("Invalid tag " + this.handle.getTag() + " for method handle " + this.handle + ".");
    }

    @Override
    public int getOpcode() {
        int opcode = MemberRef.opcodeFromTag(this.handle.getTag());
        if (opcode == 0) {
            throw new MixinTransformerError("Invalid tag " + this.handle.getTag() + " for method handle " + this.handle + ".");
        }
        return opcode;
    }

    @Override
    public void setOpcode(int opcode) {
        int tag = MemberRef.tagFromOpcode(opcode);
        if (tag == 0) {
            throw new MixinTransformerError("Invalid opcode " + Bytecode.getOpcodeName(opcode) + " for method handle " + this.handle + ".");
        }
        boolean itf = tag == 9;
        this.handle = new Handle(tag, this.handle.getOwner(), this.handle.getName(), this.handle.getDesc(), itf);
    }

    @Override
    public String getOwner() {
        return this.handle.getOwner();
    }

    @Override
    public void setOwner(String owner) {
        boolean itf = this.handle.getTag() == 9;
        this.handle = new Handle(this.handle.getTag(), owner, this.handle.getName(), this.handle.getDesc(), itf);
    }

    @Override
    public String getName() {
        return this.handle.getName();
    }

    @Override
    public void setName(String name) {
        boolean itf = this.handle.getTag() == 9;
        this.handle = new Handle(this.handle.getTag(), this.handle.getOwner(), name, this.handle.getDesc(), itf);
    }

    @Override
    public String getDesc() {
        return this.handle.getDesc();
    }

    @Override
    public void setDesc(String desc) {
        boolean itf = this.handle.getTag() == 9;
        this.handle = new Handle(this.handle.getTag(), this.handle.getOwner(), this.handle.getName(), desc, itf);
    }
}
