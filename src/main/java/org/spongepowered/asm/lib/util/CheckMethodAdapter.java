/*
 * Decompiled with CFR 0.150.
 */
package org.spongepowered.asm.lib.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.spongepowered.asm.lib.AnnotationVisitor;
import org.spongepowered.asm.lib.Attribute;
import org.spongepowered.asm.lib.Handle;
import org.spongepowered.asm.lib.Label;
import org.spongepowered.asm.lib.MethodVisitor;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.TypePath;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.lib.tree.analysis.Analyzer;
import org.spongepowered.asm.lib.tree.analysis.BasicValue;
import org.spongepowered.asm.lib.tree.analysis.BasicVerifier;
import org.spongepowered.asm.lib.util.CheckAnnotationAdapter;
import org.spongepowered.asm.lib.util.CheckClassAdapter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CheckMethodAdapter
extends MethodVisitor {
    public int version;
    private int access;
    private boolean startCode;
    private boolean endCode;
    private boolean endMethod;
    private int insnCount;
    private final Map<Label, Integer> labels;
    private Set<Label> usedLabels;
    private int expandedFrames;
    private int compressedFrames;
    private int lastFrame = -1;
    private List<Label> handlers;
    private static final int[] TYPE;
    private static Field labelStatusField;

    public CheckMethodAdapter(MethodVisitor mv) {
        this(mv, new HashMap<Label, Integer>());
    }

    public CheckMethodAdapter(MethodVisitor mv, Map<Label, Integer> labels) {
        this(327680, mv, labels);
        if (this.getClass() != CheckMethodAdapter.class) {
            throw new IllegalStateException();
        }
    }

    protected CheckMethodAdapter(int api, MethodVisitor mv, Map<Label, Integer> labels) {
        super(api, mv);
        this.labels = labels;
        this.usedLabels = new HashSet<Label>();
        this.handlers = new ArrayList<Label>();
    }

    public CheckMethodAdapter(int access, String name, String desc, final MethodVisitor cmv, Map<Label, Integer> labels) {
        this(new MethodNode(327680, access, name, desc, null, null){

            public void visitEnd() {
                Analyzer<BasicValue> a = new Analyzer<BasicValue>(new BasicVerifier());
                try {
                    a.analyze("dummy", this);
                }
                catch (Exception e) {
                    if (e instanceof IndexOutOfBoundsException && this.maxLocals == 0 && this.maxStack == 0) {
                        throw new RuntimeException("Data flow checking option requires valid, non zero maxLocals and maxStack values.");
                    }
                    e.printStackTrace();
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw, true);
                    CheckClassAdapter.printAnalyzerResult(this, a, pw);
                    pw.close();
                    throw new RuntimeException(e.getMessage() + ' ' + sw.toString());
                }
                this.accept(cmv);
            }
        }, labels);
        this.access = access;
    }

    @Override
    public void visitParameter(String name, int access) {
        if (name != null) {
            CheckMethodAdapter.checkUnqualifiedName(this.version, name, "name");
        }
        CheckClassAdapter.checkAccess(access, 36880);
        super.visitParameter(name, access);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        this.checkEndMethod();
        CheckMethodAdapter.checkDesc(desc, false);
        return new CheckAnnotationAdapter(super.visitAnnotation(desc, visible));
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        this.checkEndMethod();
        int sort = typeRef >>> 24;
        if (sort != 1 && sort != 18 && sort != 20 && sort != 21 && sort != 22 && sort != 23) {
            throw new IllegalArgumentException("Invalid type reference sort 0x" + Integer.toHexString(sort));
        }
        CheckClassAdapter.checkTypeRefAndPath(typeRef, typePath);
        CheckMethodAdapter.checkDesc(desc, false);
        return new CheckAnnotationAdapter(super.visitTypeAnnotation(typeRef, typePath, desc, visible));
    }

    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        this.checkEndMethod();
        return new CheckAnnotationAdapter(super.visitAnnotationDefault(), false);
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
        this.checkEndMethod();
        CheckMethodAdapter.checkDesc(desc, false);
        return new CheckAnnotationAdapter(super.visitParameterAnnotation(parameter, desc, visible));
    }

    @Override
    public void visitAttribute(Attribute attr) {
        this.checkEndMethod();
        if (attr == null) {
            throw new IllegalArgumentException("Invalid attribute (must not be null)");
        }
        super.visitAttribute(attr);
    }

    @Override
    public void visitCode() {
        if ((this.access & 0x400) != 0) {
            throw new RuntimeException("Abstract methods cannot have code");
        }
        this.startCode = true;
        super.visitCode();
    }

    @Override
    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
        int i;
        int mStack;
        int mLocal;
        if (this.insnCount == this.lastFrame) {
            throw new IllegalStateException("At most one frame can be visited at a given code location.");
        }
        this.lastFrame = this.insnCount;
        switch (type) {
            case -1: 
            case 0: {
                mLocal = Integer.MAX_VALUE;
                mStack = Integer.MAX_VALUE;
                break;
            }
            case 3: {
                mLocal = 0;
                mStack = 0;
                break;
            }
            case 4: {
                mLocal = 0;
                mStack = 1;
                break;
            }
            case 1: 
            case 2: {
                mLocal = 3;
                mStack = 0;
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid frame type " + type);
            }
        }
        if (nLocal > mLocal) {
            throw new IllegalArgumentException("Invalid nLocal=" + nLocal + " for frame type " + type);
        }
        if (nStack > mStack) {
            throw new IllegalArgumentException("Invalid nStack=" + nStack + " for frame type " + type);
        }
        if (type != 2) {
            if (nLocal > 0 && (local == null || local.length < nLocal)) {
                throw new IllegalArgumentException("Array local[] is shorter than nLocal");
            }
            for (i = 0; i < nLocal; ++i) {
                this.checkFrameValue(local[i]);
            }
        }
        if (nStack > 0 && (stack == null || stack.length < nStack)) {
            throw new IllegalArgumentException("Array stack[] is shorter than nStack");
        }
        for (i = 0; i < nStack; ++i) {
            this.checkFrameValue(stack[i]);
        }
        if (type == -1) {
            ++this.expandedFrames;
        } else {
            ++this.compressedFrames;
        }
        if (this.expandedFrames > 0 && this.compressedFrames > 0) {
            throw new RuntimeException("Expanded and compressed frames must not be mixed.");
        }
        super.visitFrame(type, nLocal, local, nStack, stack);
    }

    @Override
    public void visitInsn(int opcode) {
        this.checkStartCode();
        this.checkEndCode();
        CheckMethodAdapter.checkOpcode(opcode, 0);
        super.visitInsn(opcode);
        ++this.insnCount;
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        this.checkStartCode();
        this.checkEndCode();
        CheckMethodAdapter.checkOpcode(opcode, 1);
        switch (opcode) {
            case 16: {
                CheckMethodAdapter.checkSignedByte(operand, "Invalid operand");
                break;
            }
            case 17: {
                CheckMethodAdapter.checkSignedShort(operand, "Invalid operand");
                break;
            }
            default: {
                if (operand >= 4 && operand <= 11) break;
                throw new IllegalArgumentException("Invalid operand (must be an array type code T_...): " + operand);
            }
        }
        super.visitIntInsn(opcode, operand);
        ++this.insnCount;
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        this.checkStartCode();
        this.checkEndCode();
        CheckMethodAdapter.checkOpcode(opcode, 2);
        CheckMethodAdapter.checkUnsignedShort(var, "Invalid variable index");
        super.visitVarInsn(opcode, var);
        ++this.insnCount;
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        this.checkStartCode();
        this.checkEndCode();
        CheckMethodAdapter.checkOpcode(opcode, 3);
        CheckMethodAdapter.checkInternalName(type, "type");
        if (opcode == 187 && type.charAt(0) == '[') {
            throw new IllegalArgumentException("NEW cannot be used to create arrays: " + type);
        }
        super.visitTypeInsn(opcode, type);
        ++this.insnCount;
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        this.checkStartCode();
        this.checkEndCode();
        CheckMethodAdapter.checkOpcode(opcode, 4);
        CheckMethodAdapter.checkInternalName(owner, "owner");
        CheckMethodAdapter.checkUnqualifiedName(this.version, name, "name");
        CheckMethodAdapter.checkDesc(desc, false);
        super.visitFieldInsn(opcode, owner, name, desc);
        ++this.insnCount;
    }

    @Override
    @Deprecated
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        if (this.api >= 327680) {
            super.visitMethodInsn(opcode, owner, name, desc);
            return;
        }
        this.doVisitMethodInsn(opcode, owner, name, desc, opcode == 185);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        if (this.api < 327680) {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
            return;
        }
        this.doVisitMethodInsn(opcode, owner, name, desc, itf);
    }

    private void doVisitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        this.checkStartCode();
        this.checkEndCode();
        CheckMethodAdapter.checkOpcode(opcode, 5);
        if (opcode != 183 || !"<init>".equals(name)) {
            CheckMethodAdapter.checkMethodIdentifier(this.version, name, "name");
        }
        CheckMethodAdapter.checkInternalName(owner, "owner");
        CheckMethodAdapter.checkMethodDesc(desc);
        if (opcode == 182 && itf) {
            throw new IllegalArgumentException("INVOKEVIRTUAL can't be used with interfaces");
        }
        if (opcode == 185 && !itf) {
            throw new IllegalArgumentException("INVOKEINTERFACE can't be used with classes");
        }
        if (opcode == 183 && itf && (this.version & 0xFFFF) < 52) {
            throw new IllegalArgumentException("INVOKESPECIAL can't be used with interfaces prior to Java 8");
        }
        if (this.mv != null) {
            this.mv.visitMethodInsn(opcode, owner, name, desc, itf);
        }
        ++this.insnCount;
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object ... bsmArgs) {
        this.checkStartCode();
        this.checkEndCode();
        CheckMethodAdapter.checkMethodIdentifier(this.version, name, "name");
        CheckMethodAdapter.checkMethodDesc(desc);
        if (bsm.getTag() != 6 && bsm.getTag() != 8) {
            throw new IllegalArgumentException("invalid handle tag " + bsm.getTag());
        }
        for (int i = 0; i < bsmArgs.length; ++i) {
            this.checkLDCConstant(bsmArgs[i]);
        }
        super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
        ++this.insnCount;
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        this.checkStartCode();
        this.checkEndCode();
        CheckMethodAdapter.checkOpcode(opcode, 6);
        this.checkLabel(label, false, "label");
        CheckMethodAdapter.checkNonDebugLabel(label);
        super.visitJumpInsn(opcode, label);
        this.usedLabels.add(label);
        ++this.insnCount;
    }

    @Override
    public void visitLabel(Label label) {
        this.checkStartCode();
        this.checkEndCode();
        this.checkLabel(label, false, "label");
        if (this.labels.get(label) != null) {
            throw new IllegalArgumentException("Already visited label");
        }
        this.labels.put(label, this.insnCount);
        super.visitLabel(label);
    }

    @Override
    public void visitLdcInsn(Object cst) {
        this.checkStartCode();
        this.checkEndCode();
        this.checkLDCConstant(cst);
        super.visitLdcInsn(cst);
        ++this.insnCount;
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        this.checkStartCode();
        this.checkEndCode();
        CheckMethodAdapter.checkUnsignedShort(var, "Invalid variable index");
        CheckMethodAdapter.checkSignedShort(increment, "Invalid increment");
        super.visitIincInsn(var, increment);
        ++this.insnCount;
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label ... labels) {
        int i;
        this.checkStartCode();
        this.checkEndCode();
        if (max < min) {
            throw new IllegalArgumentException("Max = " + max + " must be greater than or equal to min = " + min);
        }
        this.checkLabel(dflt, false, "default label");
        CheckMethodAdapter.checkNonDebugLabel(dflt);
        if (labels == null || labels.length != max - min + 1) {
            throw new IllegalArgumentException("There must be max - min + 1 labels");
        }
        for (i = 0; i < labels.length; ++i) {
            this.checkLabel(labels[i], false, "label at index " + i);
            CheckMethodAdapter.checkNonDebugLabel(labels[i]);
        }
        super.visitTableSwitchInsn(min, max, dflt, labels);
        for (i = 0; i < labels.length; ++i) {
            this.usedLabels.add(labels[i]);
        }
        ++this.insnCount;
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        int i;
        this.checkEndCode();
        this.checkStartCode();
        this.checkLabel(dflt, false, "default label");
        CheckMethodAdapter.checkNonDebugLabel(dflt);
        if (keys == null || labels == null || keys.length != labels.length) {
            throw new IllegalArgumentException("There must be the same number of keys and labels");
        }
        for (i = 0; i < labels.length; ++i) {
            this.checkLabel(labels[i], false, "label at index " + i);
            CheckMethodAdapter.checkNonDebugLabel(labels[i]);
        }
        super.visitLookupSwitchInsn(dflt, keys, labels);
        this.usedLabels.add(dflt);
        for (i = 0; i < labels.length; ++i) {
            this.usedLabels.add(labels[i]);
        }
        ++this.insnCount;
    }

    @Override
    public void visitMultiANewArrayInsn(String desc, int dims) {
        this.checkStartCode();
        this.checkEndCode();
        CheckMethodAdapter.checkDesc(desc, false);
        if (desc.charAt(0) != '[') {
            throw new IllegalArgumentException("Invalid descriptor (must be an array type descriptor): " + desc);
        }
        if (dims < 1) {
            throw new IllegalArgumentException("Invalid dimensions (must be greater than 0): " + dims);
        }
        if (dims > desc.lastIndexOf(91) + 1) {
            throw new IllegalArgumentException("Invalid dimensions (must not be greater than dims(desc)): " + dims);
        }
        super.visitMultiANewArrayInsn(desc, dims);
        ++this.insnCount;
    }

    @Override
    public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        this.checkStartCode();
        this.checkEndCode();
        int sort = typeRef >>> 24;
        if (sort != 67 && sort != 68 && sort != 69 && sort != 70 && sort != 71 && sort != 72 && sort != 73 && sort != 74 && sort != 75) {
            throw new IllegalArgumentException("Invalid type reference sort 0x" + Integer.toHexString(sort));
        }
        CheckClassAdapter.checkTypeRefAndPath(typeRef, typePath);
        CheckMethodAdapter.checkDesc(desc, false);
        return new CheckAnnotationAdapter(super.visitInsnAnnotation(typeRef, typePath, desc, visible));
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        this.checkStartCode();
        this.checkEndCode();
        this.checkLabel(start, false, "start label");
        this.checkLabel(end, false, "end label");
        this.checkLabel(handler, false, "handler label");
        CheckMethodAdapter.checkNonDebugLabel(start);
        CheckMethodAdapter.checkNonDebugLabel(end);
        CheckMethodAdapter.checkNonDebugLabel(handler);
        if (this.labels.get(start) != null || this.labels.get(end) != null || this.labels.get(handler) != null) {
            throw new IllegalStateException("Try catch blocks must be visited before their labels");
        }
        if (type != null) {
            CheckMethodAdapter.checkInternalName(type, "type");
        }
        super.visitTryCatchBlock(start, end, handler, type);
        this.handlers.add(start);
        this.handlers.add(end);
    }

    @Override
    public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        this.checkStartCode();
        this.checkEndCode();
        int sort = typeRef >>> 24;
        if (sort != 66) {
            throw new IllegalArgumentException("Invalid type reference sort 0x" + Integer.toHexString(sort));
        }
        CheckClassAdapter.checkTypeRefAndPath(typeRef, typePath);
        CheckMethodAdapter.checkDesc(desc, false);
        return new CheckAnnotationAdapter(super.visitTryCatchAnnotation(typeRef, typePath, desc, visible));
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        this.checkStartCode();
        this.checkEndCode();
        CheckMethodAdapter.checkUnqualifiedName(this.version, name, "name");
        CheckMethodAdapter.checkDesc(desc, false);
        this.checkLabel(start, true, "start label");
        this.checkLabel(end, true, "end label");
        CheckMethodAdapter.checkUnsignedShort(index, "Invalid variable index");
        int s = this.labels.get(start);
        int e = this.labels.get(end);
        if (e < s) {
            throw new IllegalArgumentException("Invalid start and end labels (end must be greater than start)");
        }
        super.visitLocalVariable(name, desc, signature, start, end, index);
    }

    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible) {
        this.checkStartCode();
        this.checkEndCode();
        int sort = typeRef >>> 24;
        if (sort != 64 && sort != 65) {
            throw new IllegalArgumentException("Invalid type reference sort 0x" + Integer.toHexString(sort));
        }
        CheckClassAdapter.checkTypeRefAndPath(typeRef, typePath);
        CheckMethodAdapter.checkDesc(desc, false);
        if (start == null || end == null || index == null || end.length != start.length || index.length != start.length) {
            throw new IllegalArgumentException("Invalid start, end and index arrays (must be non null and of identical length");
        }
        for (int i = 0; i < start.length; ++i) {
            this.checkLabel(start[i], true, "start label");
            this.checkLabel(end[i], true, "end label");
            CheckMethodAdapter.checkUnsignedShort(index[i], "Invalid variable index");
            int s = this.labels.get(start[i]);
            int e = this.labels.get(end[i]);
            if (e >= s) continue;
            throw new IllegalArgumentException("Invalid start and end labels (end must be greater than start)");
        }
        return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, desc, visible);
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        this.checkStartCode();
        this.checkEndCode();
        CheckMethodAdapter.checkUnsignedShort(line, "Invalid line number");
        this.checkLabel(start, true, "start label");
        super.visitLineNumber(line, start);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        this.checkStartCode();
        this.checkEndCode();
        this.endCode = true;
        for (Label l : this.usedLabels) {
            if (this.labels.get(l) != null) continue;
            throw new IllegalStateException("Undefined label used");
        }
        int i = 0;
        while (i < this.handlers.size()) {
            Integer start = this.labels.get(this.handlers.get(i++));
            Integer end = this.labels.get(this.handlers.get(i++));
            if (start == null || end == null) {
                throw new IllegalStateException("Undefined try catch block labels");
            }
            if (end > start) continue;
            throw new IllegalStateException("Emty try catch block handler range");
        }
        CheckMethodAdapter.checkUnsignedShort(maxStack, "Invalid max stack");
        CheckMethodAdapter.checkUnsignedShort(maxLocals, "Invalid max locals");
        super.visitMaxs(maxStack, maxLocals);
    }

    @Override
    public void visitEnd() {
        this.checkEndMethod();
        this.endMethod = true;
        super.visitEnd();
    }

    void checkStartCode() {
        if (!this.startCode) {
            throw new IllegalStateException("Cannot visit instructions before visitCode has been called.");
        }
    }

    void checkEndCode() {
        if (this.endCode) {
            throw new IllegalStateException("Cannot visit instructions after visitMaxs has been called.");
        }
    }

    void checkEndMethod() {
        if (this.endMethod) {
            throw new IllegalStateException("Cannot visit elements after visitEnd has been called.");
        }
    }

    void checkFrameValue(Object value) {
        if (value == Opcodes.TOP || value == Opcodes.INTEGER || value == Opcodes.FLOAT || value == Opcodes.LONG || value == Opcodes.DOUBLE || value == Opcodes.NULL || value == Opcodes.UNINITIALIZED_THIS) {
            return;
        }
        if (value instanceof String) {
            CheckMethodAdapter.checkInternalName((String)value, "Invalid stack frame value");
            return;
        }
        if (!(value instanceof Label)) {
            throw new IllegalArgumentException("Invalid stack frame value: " + value);
        }
        this.usedLabels.add((Label)value);
    }

    static void checkOpcode(int opcode, int type) {
        if (opcode < 0 || opcode > 199 || TYPE[opcode] != type) {
            throw new IllegalArgumentException("Invalid opcode: " + opcode);
        }
    }

    static void checkSignedByte(int value, String msg) {
        if (value < -128 || value > 127) {
            throw new IllegalArgumentException(msg + " (must be a signed byte): " + value);
        }
    }

    static void checkSignedShort(int value, String msg) {
        if (value < -32768 || value > 32767) {
            throw new IllegalArgumentException(msg + " (must be a signed short): " + value);
        }
    }

    static void checkUnsignedShort(int value, String msg) {
        if (value < 0 || value > 65535) {
            throw new IllegalArgumentException(msg + " (must be an unsigned short): " + value);
        }
    }

    static void checkConstant(Object cst) {
        if (!(cst instanceof Integer || cst instanceof Float || cst instanceof Long || cst instanceof Double || cst instanceof String)) {
            throw new IllegalArgumentException("Invalid constant: " + cst);
        }
    }

    void checkLDCConstant(Object cst) {
        if (cst instanceof Type) {
            int s = ((Type)cst).getSort();
            if (s != 10 && s != 9 && s != 11) {
                throw new IllegalArgumentException("Illegal LDC constant value");
            }
            if (s != 11 && (this.version & 0xFFFF) < 49) {
                throw new IllegalArgumentException("ldc of a constant class requires at least version 1.5");
            }
            if (s == 11 && (this.version & 0xFFFF) < 51) {
                throw new IllegalArgumentException("ldc of a method type requires at least version 1.7");
            }
        } else if (cst instanceof Handle) {
            if ((this.version & 0xFFFF) < 51) {
                throw new IllegalArgumentException("ldc of a handle requires at least version 1.7");
            }
            int tag = ((Handle)cst).getTag();
            if (tag < 1 || tag > 9) {
                throw new IllegalArgumentException("invalid handle tag " + tag);
            }
        } else {
            CheckMethodAdapter.checkConstant(cst);
        }
    }

    static void checkUnqualifiedName(int version, String name, String msg) {
        if ((version & 0xFFFF) < 49) {
            CheckMethodAdapter.checkIdentifier(name, msg);
        } else {
            for (int i = 0; i < name.length(); ++i) {
                if (".;[/".indexOf(name.charAt(i)) == -1) continue;
                throw new IllegalArgumentException("Invalid " + msg + " (must be a valid unqualified name): " + name);
            }
        }
    }

    static void checkIdentifier(String name, String msg) {
        CheckMethodAdapter.checkIdentifier(name, 0, -1, msg);
    }

    static void checkIdentifier(String name, int start, int end, String msg) {
        if (name == null || (end == -1 ? name.length() <= start : end <= start)) {
            throw new IllegalArgumentException("Invalid " + msg + " (must not be null or empty)");
        }
        if (!Character.isJavaIdentifierStart(name.charAt(start))) {
            throw new IllegalArgumentException("Invalid " + msg + " (must be a valid Java identifier): " + name);
        }
        int max = end == -1 ? name.length() : end;
        for (int i = start + 1; i < max; ++i) {
            if (Character.isJavaIdentifierPart(name.charAt(i))) continue;
            throw new IllegalArgumentException("Invalid " + msg + " (must be a valid Java identifier): " + name);
        }
    }

    static void checkMethodIdentifier(int version, String name, String msg) {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("Invalid " + msg + " (must not be null or empty)");
        }
        if ((version & 0xFFFF) >= 49) {
            for (int i = 0; i < name.length(); ++i) {
                if (".;[/<>".indexOf(name.charAt(i)) == -1) continue;
                throw new IllegalArgumentException("Invalid " + msg + " (must be a valid unqualified name): " + name);
            }
            return;
        }
        if (!Character.isJavaIdentifierStart(name.charAt(0))) {
            throw new IllegalArgumentException("Invalid " + msg + " (must be a '<init>', '<clinit>' or a valid Java identifier): " + name);
        }
        for (int i = 1; i < name.length(); ++i) {
            if (Character.isJavaIdentifierPart(name.charAt(i))) continue;
            throw new IllegalArgumentException("Invalid " + msg + " (must be '<init>' or '<clinit>' or a valid Java identifier): " + name);
        }
    }

    static void checkInternalName(String name, String msg) {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("Invalid " + msg + " (must not be null or empty)");
        }
        if (name.charAt(0) == '[') {
            CheckMethodAdapter.checkDesc(name, false);
        } else {
            CheckMethodAdapter.checkInternalName(name, 0, -1, msg);
        }
    }

    static void checkInternalName(String name, int start, int end, String msg) {
        int max = end == -1 ? name.length() : end;
        try {
            int slash;
            int begin = start;
            do {
                if ((slash = name.indexOf(47, begin + 1)) == -1 || slash > max) {
                    slash = max;
                }
                CheckMethodAdapter.checkIdentifier(name, begin, slash, null);
                begin = slash + 1;
            } while (slash != max);
        }
        catch (IllegalArgumentException unused) {
            throw new IllegalArgumentException("Invalid " + msg + " (must be a fully qualified class name in internal form): " + name);
        }
    }

    static void checkDesc(String desc, boolean canBeVoid) {
        int end = CheckMethodAdapter.checkDesc(desc, 0, canBeVoid);
        if (end != desc.length()) {
            throw new IllegalArgumentException("Invalid descriptor: " + desc);
        }
    }

    static int checkDesc(String desc, int start, boolean canBeVoid) {
        if (desc == null || start >= desc.length()) {
            throw new IllegalArgumentException("Invalid type descriptor (must not be null or empty)");
        }
        switch (desc.charAt(start)) {
            case 'V': {
                if (canBeVoid) {
                    return start + 1;
                }
                throw new IllegalArgumentException("Invalid descriptor: " + desc);
            }
            case 'B': 
            case 'C': 
            case 'D': 
            case 'F': 
            case 'I': 
            case 'J': 
            case 'S': 
            case 'Z': {
                return start + 1;
            }
            case '[': {
                int index;
                for (index = start + 1; index < desc.length() && desc.charAt(index) == '['; ++index) {
                }
                if (index < desc.length()) {
                    return CheckMethodAdapter.checkDesc(desc, index, false);
                }
                throw new IllegalArgumentException("Invalid descriptor: " + desc);
            }
            case 'L': {
                int index = desc.indexOf(59, start);
                if (index == -1 || index - start < 2) {
                    throw new IllegalArgumentException("Invalid descriptor: " + desc);
                }
                try {
                    CheckMethodAdapter.checkInternalName(desc, start + 1, index, null);
                }
                catch (IllegalArgumentException unused) {
                    throw new IllegalArgumentException("Invalid descriptor: " + desc);
                }
                return index + 1;
            }
        }
        throw new IllegalArgumentException("Invalid descriptor: " + desc);
    }

    static void checkMethodDesc(String desc) {
        if (desc == null || desc.length() == 0) {
            throw new IllegalArgumentException("Invalid method descriptor (must not be null or empty)");
        }
        if (desc.charAt(0) != '(' || desc.length() < 3) {
            throw new IllegalArgumentException("Invalid descriptor: " + desc);
        }
        int start = 1;
        if (desc.charAt(start) != ')') {
            do {
                if (desc.charAt(start) != 'V') continue;
                throw new IllegalArgumentException("Invalid descriptor: " + desc);
            } while ((start = CheckMethodAdapter.checkDesc(desc, start, false)) < desc.length() && desc.charAt(start) != ')');
        }
        if ((start = CheckMethodAdapter.checkDesc(desc, start + 1, true)) != desc.length()) {
            throw new IllegalArgumentException("Invalid descriptor: " + desc);
        }
    }

    void checkLabel(Label label, boolean checkVisited, String msg) {
        if (label == null) {
            throw new IllegalArgumentException("Invalid " + msg + " (must not be null)");
        }
        if (checkVisited && this.labels.get(label) == null) {
            throw new IllegalArgumentException("Invalid " + msg + " (must be visited first)");
        }
    }

    private static void checkNonDebugLabel(Label label) {
        Field f = CheckMethodAdapter.getLabelStatusField();
        int status = 0;
        try {
            status = f == null ? 0 : (Integer)f.get(label);
        }
        catch (IllegalAccessException e) {
            throw new Error("Internal error");
        }
        if ((status & 1) != 0) {
            throw new IllegalArgumentException("Labels used for debug info cannot be reused for control flow");
        }
    }

    private static Field getLabelStatusField() {
        if (labelStatusField == null && (labelStatusField = CheckMethodAdapter.getLabelField("a")) == null) {
            labelStatusField = CheckMethodAdapter.getLabelField("status");
        }
        return labelStatusField;
    }

    private static Field getLabelField(String name) {
        try {
            Field f = Label.class.getDeclaredField(name);
            f.setAccessible(true);
            return f;
        }
        catch (NoSuchFieldException e) {
            return null;
        }
    }

    static {
        String s = "BBBBBBBBBBBBBBBBCCIAADDDDDAAAAAAAAAAAAAAAAAAAABBBBBBBBDDDDDAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBJBBBBBBBBBBBBBBBBBBBBHHHHHHHHHHHHHHHHDKLBBBBBBFFFFGGGGAECEBBEEBBAMHHAA";
        TYPE = new int[s.length()];
        for (int i = 0; i < TYPE.length; ++i) {
            CheckMethodAdapter.TYPE[i] = s.charAt(i) - 65 - 1;
        }
    }
}

