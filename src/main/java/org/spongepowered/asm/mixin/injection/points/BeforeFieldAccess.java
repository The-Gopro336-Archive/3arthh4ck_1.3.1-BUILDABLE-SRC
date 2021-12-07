package org.spongepowered.asm.mixin.injection.points;

import java.util.Collection;
import java.util.ListIterator;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.FieldInsnNode;
import org.spongepowered.asm.lib.tree.InsnList;
import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.points.BeforeInvoke;
import org.spongepowered.asm.mixin.injection.struct.InjectionPointData;
import org.spongepowered.asm.util.Bytecode;

@InjectionPoint.AtCode(value="FIELD")
public class BeforeFieldAccess
extends BeforeInvoke {
    private static final String ARRAY_GET = "get";
    private static final String ARRAY_SET = "set";
    private static final String ARRAY_LENGTH = "length";
    public static final int ARRAY_SEARCH_FUZZ_DEFAULT = 8;
    private final int opcode;
    private final int arrOpcode;
    private final int fuzzFactor;

    public BeforeFieldAccess(InjectionPointData data) {
        super(data);
        this.opcode = data.getOpcode(-1, 180, 181, 178, 179, -1);
        String array = data.get("array", "");
        this.arrOpcode = ARRAY_GET.equalsIgnoreCase(array) ? 46 : (ARRAY_SET.equalsIgnoreCase(array) ? 79 : (ARRAY_LENGTH.equalsIgnoreCase(array) ? 190 : 0));
        this.fuzzFactor = Math.min(Math.max(data.get("fuzz", 8), 1), 32);
    }

    public int getFuzzFactor() {
        return this.fuzzFactor;
    }

    public int getArrayOpcode() {
        return this.arrOpcode;
    }

    private int getArrayOpcode(String desc) {
        if (this.arrOpcode != 190) {
            return Type.getType(desc).getElementType().getOpcode(this.arrOpcode);
        }
        return this.arrOpcode;
    }

    @Override
    protected boolean matchesInsn(AbstractInsnNode insn) {
        if (insn instanceof FieldInsnNode && (((FieldInsnNode)insn).getOpcode() == this.opcode || this.opcode == -1)) {
            if (this.arrOpcode == 0) {
                return true;
            }
            if (insn.getOpcode() != 178 && insn.getOpcode() != 180) {
                return false;
            }
            return Type.getType(((FieldInsnNode)insn).desc).getSort() == 9;
        }
        return false;
    }

    @Override
    protected boolean addInsn(InsnList insns, Collection<AbstractInsnNode> nodes, AbstractInsnNode insn) {
        if (this.arrOpcode > 0) {
            FieldInsnNode fieldInsn = (FieldInsnNode)insn;
            int accOpcode = this.getArrayOpcode(fieldInsn.desc);
            this.log("{} > > > > searching for array access opcode {} fuzz={}", this.className, Bytecode.getOpcodeName(accOpcode), this.fuzzFactor);
            if (BeforeFieldAccess.findArrayNode(insns, fieldInsn, accOpcode, this.fuzzFactor) == null) {
                this.log("{} > > > > > failed to locate matching insn", this.className);
                return false;
            }
        }
        this.log("{} > > > > > adding matching insn", this.className);
        return super.addInsn(insns, nodes, insn);
    }

    public static AbstractInsnNode findArrayNode(InsnList insns, FieldInsnNode fieldNode, int opcode, int searchRange) {
        int pos = 0;
        ListIterator<AbstractInsnNode> iter = insns.iterator(insns.indexOf(fieldNode) + 1);
        while (iter.hasNext()) {
            AbstractInsnNode insn = (AbstractInsnNode)iter.next();
            if (insn.getOpcode() == opcode) {
                return insn;
            }
            if (insn.getOpcode() == 190 && pos == 0) {
                return null;
            }
            if (insn instanceof FieldInsnNode) {
                FieldInsnNode field = (FieldInsnNode)insn;
                if (field.desc.equals(fieldNode.desc) && field.name.equals(fieldNode.name) && field.owner.equals(fieldNode.owner)) {
                    return null;
                }
            }
            if (pos++ <= searchRange) continue;
            return null;
        }
        return null;
    }
}
