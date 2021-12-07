package org.spongepowered.asm.mixin.injection.points;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.AnnotationNode;
import org.spongepowered.asm.lib.tree.FrameNode;
import org.spongepowered.asm.lib.tree.InsnList;
import org.spongepowered.asm.lib.tree.LabelNode;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.struct.InjectionPointData;
import org.spongepowered.asm.mixin.injection.throwables.InvalidInjectionException;
import org.spongepowered.asm.mixin.refmap.IMixinContext;
import org.spongepowered.asm.util.Annotations;
import org.spongepowered.asm.util.Bytecode;

@InjectionPoint.AtCode(value="CONSTANT")
public class BeforeConstant
extends InjectionPoint {
    private static final Logger logger = LogManager.getLogger("mixin");
    private final int ordinal;
    private final boolean nullValue;
    private final Integer intValue;
    private final Float floatValue;
    private final Long longValue;
    private final Double doubleValue;
    private final String stringValue;
    private final Type typeValue;
    private final int[] expandOpcodes;
    private final boolean expand;
    private final String matchByType;
    private final boolean log;

    public BeforeConstant(IMixinContext context, AnnotationNode node, String returnType) {
        super(Annotations.getValue(node, "slice", ""), InjectionPoint.Selector.DEFAULT, null);
        Boolean empty = Annotations.getValue(node, "nullValue", (Boolean)null);
        this.ordinal = Annotations.getValue(node, "ordinal", -1);
        this.nullValue = empty != null && empty != false;
        this.intValue = Annotations.getValue(node, "intValue", (Integer)null);
        this.floatValue = Annotations.getValue(node, "floatValue", (Float)null);
        this.longValue = Annotations.getValue(node, "longValue", (Long)null);
        this.doubleValue = Annotations.getValue(node, "doubleValue", (Double)null);
        this.stringValue = Annotations.getValue(node, "stringValue", (String)null);
        this.typeValue = Annotations.getValue(node, "classValue", (Type)null);
        this.matchByType = this.validateDiscriminator(context, returnType, empty, "on @Constant annotation");
        this.expandOpcodes = this.parseExpandOpcodes(Annotations.getValue(node, "expandZeroConditions", true, Constant.Condition.class));
        this.expand = this.expandOpcodes.length > 0;
        this.log = Annotations.getValue(node, "log", Boolean.FALSE);
    }

    public BeforeConstant(InjectionPointData data) {
        super(data);
        String strNullValue = data.get("nullValue", null);
        Boolean empty = strNullValue != null ? Boolean.valueOf(Boolean.parseBoolean(strNullValue)) : null;
        this.ordinal = data.getOrdinal();
        this.nullValue = empty != null && empty != false;
        this.intValue = Ints.tryParse(data.get("intValue", ""));
        this.floatValue = Floats.tryParse(data.get("floatValue", ""));
        this.longValue = Longs.tryParse(data.get("longValue", ""));
        this.doubleValue = Doubles.tryParse(data.get("doubleValue", ""));
        this.stringValue = data.get("stringValue", null);
        String strClassValue = data.get("classValue", null);
        this.typeValue = strClassValue != null ? Type.getObjectType(strClassValue.replace('.', '/')) : null;
        this.matchByType = this.validateDiscriminator(data.getContext(), "V", empty, "in @At(\"CONSTANT\") args");
        if ("V".equals(this.matchByType)) {
            throw new InvalidInjectionException(data.getContext(), "No constant discriminator could be parsed in @At(\"CONSTANT\") args");
        }
        ArrayList<Constant.Condition> conditions = new ArrayList<Constant.Condition>();
        String strConditions = data.get("expandZeroConditions", "").toLowerCase();
        for (Constant.Condition condition : Constant.Condition.values()) {
            if (!strConditions.contains(condition.name().toLowerCase())) continue;
            conditions.add(condition);
        }
        this.expandOpcodes = this.parseExpandOpcodes(conditions);
        this.expand = this.expandOpcodes.length > 0;
        this.log = data.get("log", false);
    }

    private String validateDiscriminator(IMixinContext context, String returnType, Boolean empty, String type) {
        int c = BeforeConstant.count(empty, this.intValue, this.floatValue, this.longValue, this.doubleValue, this.stringValue, this.typeValue);
        if (c == 1) {
            returnType = null;
        } else if (c > 1) {
            throw new InvalidInjectionException(context, "Conflicting constant discriminators specified " + type + " for " + context);
        }
        return returnType;
    }

    private int[] parseExpandOpcodes(List<Constant.Condition> conditions) {
        HashSet<Integer> opcodes = new HashSet<Integer>();
        for (Constant.Condition condition : conditions) {
            Constant.Condition actual = condition.getEquivalentCondition();
            for (int opcode : actual.getOpcodes()) {
                opcodes.add(opcode);
            }
        }
        return Ints.toArray(opcodes);
    }

    @Override
    public boolean find(String desc, InsnList insns, Collection<AbstractInsnNode> nodes) {
        boolean found = false;
        this.log("BeforeConstant is searching for constants in method with descriptor {}", desc);
        ListIterator<AbstractInsnNode> iter = insns.iterator();
        int ordinal = 0;
        int last = 0;
        while (iter.hasNext()) {
            boolean matchesInsn;
            AbstractInsnNode insn = iter.next();
            boolean bl = matchesInsn = this.expand ? this.matchesConditionalInsn(last, insn) : this.matchesConstantInsn(insn);
            if (matchesInsn) {
                this.log("    BeforeConstant found a matching constant{} at ordinal {}", this.matchByType != null ? " TYPE" : " value", ordinal);
                if (this.ordinal == -1 || this.ordinal == ordinal) {
                    this.log("      BeforeConstant found {}", Bytecode.describeNode(insn).trim());
                    nodes.add(insn);
                    found = true;
                }
                ++ordinal;
            }
            if (insn instanceof LabelNode || insn instanceof FrameNode) continue;
            last = insn.getOpcode();
        }
        return found;
    }

    private boolean matchesConditionalInsn(int last, AbstractInsnNode insn) {
        for (int conditionalOpcode : this.expandOpcodes) {
            int opcode = insn.getOpcode();
            if (opcode != conditionalOpcode) continue;
            if (last == 148 || last == 149 || last == 150 || last == 151 || last == 152) {
                this.log("  BeforeConstant is ignoring {} following {}", Bytecode.getOpcodeName(opcode), Bytecode.getOpcodeName(last));
                return false;
            }
            this.log("  BeforeConstant found {} instruction", Bytecode.getOpcodeName(opcode));
            return true;
        }
        if (this.intValue != null && this.intValue == 0 && Bytecode.isConstant(insn)) {
            Object value = Bytecode.getConstant(insn);
            this.log("  BeforeConstant found INTEGER constant: value = {}", value);
            return value instanceof Integer && (Integer)value == 0;
        }
        return false;
    }

    private boolean matchesConstantInsn(AbstractInsnNode insn) {
        if (!Bytecode.isConstant(insn)) {
            return false;
        }
        Object value = Bytecode.getConstant(insn);
        if (value == null) {
            this.log("  BeforeConstant found NULL constant: nullValue = {}", this.nullValue);
            return this.nullValue || "Ljava/lang/Object;".equals(this.matchByType);
        }
        if (value instanceof Integer) {
            this.log("  BeforeConstant found INTEGER constant: value = {}, intValue = {}", value, this.intValue);
            return value.equals(this.intValue) || "I".equals(this.matchByType);
        }
        if (value instanceof Float) {
            this.log("  BeforeConstant found FLOAT constant: value = {}, floatValue = {}", value, this.floatValue);
            return value.equals(this.floatValue) || "F".equals(this.matchByType);
        }
        if (value instanceof Long) {
            this.log("  BeforeConstant found LONG constant: value = {}, longValue = {}", value, this.longValue);
            return value.equals(this.longValue) || "J".equals(this.matchByType);
        }
        if (value instanceof Double) {
            this.log("  BeforeConstant found DOUBLE constant: value = {}, doubleValue = {}", value, this.doubleValue);
            return value.equals(this.doubleValue) || "D".equals(this.matchByType);
        }
        if (value instanceof String) {
            this.log("  BeforeConstant found STRING constant: value = {}, stringValue = {}", value, this.stringValue);
            return value.equals(this.stringValue) || "Ljava/lang/String;".equals(this.matchByType);
        }
        if (value instanceof Type) {
            this.log("  BeforeConstant found CLASS constant: value = {}, typeValue = {}", value, this.typeValue);
            return value.equals(this.typeValue) || "Ljava/lang/Class;".equals(this.matchByType);
        }
        return false;
    }

    protected void log(String message, Object ... params) {
        if (this.log) {
            logger.info(message, params);
        }
    }

    private static int count(Object ... values) {
        int counter = 0;
        for (Object value : values) {
            if (value == null) continue;
            ++counter;
        }
        return counter;
    }
}
