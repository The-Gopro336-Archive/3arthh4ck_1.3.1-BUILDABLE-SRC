/*
 * Decompiled with CFR 0.150.
 */
package org.spongepowered.asm.lib;

import org.spongepowered.asm.lib.AnnotationVisitor;
import org.spongepowered.asm.lib.AnnotationWriter;
import org.spongepowered.asm.lib.Attribute;
import org.spongepowered.asm.lib.ByteVector;
import org.spongepowered.asm.lib.ClassWriter;
import org.spongepowered.asm.lib.CurrentFrame;
import org.spongepowered.asm.lib.Edge;
import org.spongepowered.asm.lib.Frame;
import org.spongepowered.asm.lib.Handle;
import org.spongepowered.asm.lib.Handler;
import org.spongepowered.asm.lib.Item;
import org.spongepowered.asm.lib.Label;
import org.spongepowered.asm.lib.MethodVisitor;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.TypePath;

class MethodWriter
extends MethodVisitor {
    static final int ACC_CONSTRUCTOR = 524288;
    static final int SAME_FRAME = 0;
    static final int SAME_LOCALS_1_STACK_ITEM_FRAME = 64;
    static final int RESERVED = 128;
    static final int SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED = 247;
    static final int CHOP_FRAME = 248;
    static final int SAME_FRAME_EXTENDED = 251;
    static final int APPEND_FRAME = 252;
    static final int FULL_FRAME = 255;
    static final int FRAMES = 0;
    static final int INSERTED_FRAMES = 1;
    static final int MAXS = 2;
    static final int NOTHING = 3;
    final ClassWriter cw;
    private int access;
    private final int name;
    private final int desc;
    private final String descriptor;
    String signature;
    int classReaderOffset;
    int classReaderLength;
    int exceptionCount;
    int[] exceptions;
    private ByteVector annd;
    private AnnotationWriter anns;
    private AnnotationWriter ianns;
    private AnnotationWriter tanns;
    private AnnotationWriter itanns;
    private AnnotationWriter[] panns;
    private AnnotationWriter[] ipanns;
    private int synthetics;
    private Attribute attrs;
    private ByteVector code = new ByteVector();
    private int maxStack;
    private int maxLocals;
    private int currentLocals;
    private int frameCount;
    private ByteVector stackMap;
    private int previousFrameOffset;
    private int[] previousFrame;
    private int[] frame;
    private int handlerCount;
    private Handler firstHandler;
    private Handler lastHandler;
    private int methodParametersCount;
    private ByteVector methodParameters;
    private int localVarCount;
    private ByteVector localVar;
    private int localVarTypeCount;
    private ByteVector localVarType;
    private int lineNumberCount;
    private ByteVector lineNumber;
    private int lastCodeOffset;
    private AnnotationWriter ctanns;
    private AnnotationWriter ictanns;
    private Attribute cattrs;
    private int subroutines;
    private final int compute;
    private Label labels;
    private Label previousBlock;
    private Label currentBlock;
    private int stackSize;
    private int maxStackSize;

    MethodWriter(ClassWriter cw, int access, String name, String desc, String signature, String[] exceptions, int compute) {
        super(327680);
        if (cw.firstMethod == null) {
            cw.firstMethod = this;
        } else {
            cw.lastMethod.mv = this;
        }
        cw.lastMethod = this;
        this.cw = cw;
        this.access = access;
        if ("<init>".equals(name)) {
            this.access |= 0x80000;
        }
        this.name = cw.newUTF8(name);
        this.desc = cw.newUTF8(desc);
        this.descriptor = desc;
        this.signature = signature;
        if (exceptions != null && exceptions.length > 0) {
            this.exceptionCount = exceptions.length;
            this.exceptions = new int[this.exceptionCount];
            for (int i = 0; i < this.exceptionCount; ++i) {
                this.exceptions[i] = cw.newClass(exceptions[i]);
            }
        }
        this.compute = compute;
        if (compute != 3) {
            int size = Type.getArgumentsAndReturnSizes(this.descriptor) >> 2;
            if ((access & 8) != 0) {
                --size;
            }
            this.maxLocals = size;
            this.currentLocals = size;
            this.labels = new Label();
            this.labels.status |= 8;
            this.visitLabel(this.labels);
        }
    }

    public void visitParameter(String name, int access) {
        if (this.methodParameters == null) {
            this.methodParameters = new ByteVector();
        }
        ++this.methodParametersCount;
        this.methodParameters.putShort(name == null ? 0 : this.cw.newUTF8(name)).putShort(access);
    }

    public AnnotationVisitor visitAnnotationDefault() {
        this.annd = new ByteVector();
        return new AnnotationWriter(this.cw, false, this.annd, null, 0);
    }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        ByteVector bv = new ByteVector();
        bv.putShort(this.cw.newUTF8(desc)).putShort(0);
        AnnotationWriter aw = new AnnotationWriter(this.cw, true, bv, bv, 2);
        if (visible) {
            aw.next = this.anns;
            this.anns = aw;
        } else {
            aw.next = this.ianns;
            this.ianns = aw;
        }
        return aw;
    }

    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        ByteVector bv = new ByteVector();
        AnnotationWriter.putTarget(typeRef, typePath, bv);
        bv.putShort(this.cw.newUTF8(desc)).putShort(0);
        AnnotationWriter aw = new AnnotationWriter(this.cw, true, bv, bv, bv.length - 2);
        if (visible) {
            aw.next = this.tanns;
            this.tanns = aw;
        } else {
            aw.next = this.itanns;
            this.itanns = aw;
        }
        return aw;
    }

    public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
        ByteVector bv = new ByteVector();
        if ("Ljava/lang/Synthetic;".equals(desc)) {
            this.synthetics = Math.max(this.synthetics, parameter + 1);
            return new AnnotationWriter(this.cw, false, bv, null, 0);
        }
        bv.putShort(this.cw.newUTF8(desc)).putShort(0);
        AnnotationWriter aw = new AnnotationWriter(this.cw, true, bv, bv, 2);
        if (visible) {
            if (this.panns == null) {
                this.panns = new AnnotationWriter[Type.getArgumentTypes(this.descriptor).length];
            }
            aw.next = this.panns[parameter];
            this.panns[parameter] = aw;
        } else {
            if (this.ipanns == null) {
                this.ipanns = new AnnotationWriter[Type.getArgumentTypes(this.descriptor).length];
            }
            aw.next = this.ipanns[parameter];
            this.ipanns[parameter] = aw;
        }
        return aw;
    }

    public void visitAttribute(Attribute attr) {
        if (attr.isCodeAttribute()) {
            attr.next = this.cattrs;
            this.cattrs = attr;
        } else {
            attr.next = this.attrs;
            this.attrs = attr;
        }
    }

    public void visitCode() {
    }

    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
        if (this.compute == 0) {
            return;
        }
        if (this.compute == 1) {
            if (this.currentBlock.frame == null) {
                this.currentBlock.frame = new CurrentFrame();
                this.currentBlock.frame.owner = this.currentBlock;
                this.currentBlock.frame.initInputFrame(this.cw, this.access, Type.getArgumentTypes(this.descriptor), nLocal);
                this.visitImplicitFirstFrame();
            } else {
                if (type == -1) {
                    this.currentBlock.frame.set(this.cw, nLocal, local, nStack, stack);
                }
                this.visitFrame(this.currentBlock.frame);
            }
        } else if (type == -1) {
            int i;
            if (this.previousFrame == null) {
                this.visitImplicitFirstFrame();
            }
            this.currentLocals = nLocal;
            int frameIndex = this.startFrame(this.code.length, nLocal, nStack);
            for (i = 0; i < nLocal; ++i) {
                this.frame[frameIndex++] = local[i] instanceof String ? 0x1700000 | this.cw.addType((String)local[i]) : (local[i] instanceof Integer ? (Integer)local[i] : 0x1800000 | this.cw.addUninitializedType("", ((Label)local[i]).position));
            }
            for (i = 0; i < nStack; ++i) {
                this.frame[frameIndex++] = stack[i] instanceof String ? 0x1700000 | this.cw.addType((String)stack[i]) : (stack[i] instanceof Integer ? (Integer)stack[i] : 0x1800000 | this.cw.addUninitializedType("", ((Label)stack[i]).position));
            }
            this.endFrame();
        } else {
            int delta;
            if (this.stackMap == null) {
                this.stackMap = new ByteVector();
                delta = this.code.length;
            } else {
                delta = this.code.length - this.previousFrameOffset - 1;
                if (delta < 0) {
                    if (type == 3) {
                        return;
                    }
                    throw new IllegalStateException();
                }
            }
            switch (type) {
                case 0: {
                    int i;
                    this.currentLocals = nLocal;
                    this.stackMap.putByte(255).putShort(delta).putShort(nLocal);
                    for (i = 0; i < nLocal; ++i) {
                        this.writeFrameType(local[i]);
                    }
                    this.stackMap.putShort(nStack);
                    for (i = 0; i < nStack; ++i) {
                        this.writeFrameType(stack[i]);
                    }
                    break;
                }
                case 1: {
                    this.currentLocals += nLocal;
                    this.stackMap.putByte(251 + nLocal).putShort(delta);
                    for (int i = 0; i < nLocal; ++i) {
                        this.writeFrameType(local[i]);
                    }
                    break;
                }
                case 2: {
                    this.currentLocals -= nLocal;
                    this.stackMap.putByte(251 - nLocal).putShort(delta);
                    break;
                }
                case 3: {
                    if (delta < 64) {
                        this.stackMap.putByte(delta);
                        break;
                    }
                    this.stackMap.putByte(251).putShort(delta);
                    break;
                }
                case 4: {
                    if (delta < 64) {
                        this.stackMap.putByte(64 + delta);
                    } else {
                        this.stackMap.putByte(247).putShort(delta);
                    }
                    this.writeFrameType(stack[0]);
                }
            }
            this.previousFrameOffset = this.code.length;
            ++this.frameCount;
        }
        this.maxStack = Math.max(this.maxStack, nStack);
        this.maxLocals = Math.max(this.maxLocals, this.currentLocals);
    }

    public void visitInsn(int opcode) {
        this.lastCodeOffset = this.code.length;
        this.code.putByte(opcode);
        if (this.currentBlock != null) {
            if (this.compute == 0 || this.compute == 1) {
                this.currentBlock.frame.execute(opcode, 0, null, null);
            } else {
                int size = this.stackSize + Frame.SIZE[opcode];
                if (size > this.maxStackSize) {
                    this.maxStackSize = size;
                }
                this.stackSize = size;
            }
            if (opcode >= 172 && opcode <= 177 || opcode == 191) {
                this.noSuccessor();
            }
        }
    }

    public void visitIntInsn(int opcode, int operand) {
        this.lastCodeOffset = this.code.length;
        if (this.currentBlock != null) {
            if (this.compute == 0 || this.compute == 1) {
                this.currentBlock.frame.execute(opcode, operand, null, null);
            } else if (opcode != 188) {
                int size = this.stackSize + 1;
                if (size > this.maxStackSize) {
                    this.maxStackSize = size;
                }
                this.stackSize = size;
            }
        }
        if (opcode == 17) {
            this.code.put12(opcode, operand);
        } else {
            this.code.put11(opcode, operand);
        }
    }

    public void visitVarInsn(int opcode, int var) {
        int n;
        this.lastCodeOffset = this.code.length;
        if (this.currentBlock != null) {
            if (this.compute == 0 || this.compute == 1) {
                this.currentBlock.frame.execute(opcode, var, null, null);
            } else if (opcode == 169) {
                this.currentBlock.status |= 0x100;
                this.currentBlock.inputStackTop = this.stackSize;
                this.noSuccessor();
            } else {
                int size = this.stackSize + Frame.SIZE[opcode];
                if (size > this.maxStackSize) {
                    this.maxStackSize = size;
                }
                this.stackSize = size;
            }
        }
        if (this.compute != 3 && (n = opcode == 22 || opcode == 24 || opcode == 55 || opcode == 57 ? var + 2 : var + 1) > this.maxLocals) {
            this.maxLocals = n;
        }
        if (var < 4 && opcode != 169) {
            int opt = opcode < 54 ? 26 + (opcode - 21 << 2) + var : 59 + (opcode - 54 << 2) + var;
            this.code.putByte(opt);
        } else if (var >= 256) {
            this.code.putByte(196).put12(opcode, var);
        } else {
            this.code.put11(opcode, var);
        }
        if (opcode >= 54 && this.compute == 0 && this.handlerCount > 0) {
            this.visitLabel(new Label());
        }
    }

    public void visitTypeInsn(int opcode, String type) {
        this.lastCodeOffset = this.code.length;
        Item i = this.cw.newClassItem(type);
        if (this.currentBlock != null) {
            if (this.compute == 0 || this.compute == 1) {
                this.currentBlock.frame.execute(opcode, this.code.length, this.cw, i);
            } else if (opcode == 187) {
                int size = this.stackSize + 1;
                if (size > this.maxStackSize) {
                    this.maxStackSize = size;
                }
                this.stackSize = size;
            }
        }
        this.code.put12(opcode, i.index);
    }

    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        this.lastCodeOffset = this.code.length;
        Item i = this.cw.newFieldItem(owner, name, desc);
        if (this.currentBlock != null) {
            if (this.compute == 0 || this.compute == 1) {
                this.currentBlock.frame.execute(opcode, 0, this.cw, i);
            } else {
                int size;
                char c = desc.charAt(0);
                switch (opcode) {
                    case 178: {
                        size = this.stackSize + (c == 'D' || c == 'J' ? 2 : 1);
                        break;
                    }
                    case 179: {
                        size = this.stackSize + (c == 'D' || c == 'J' ? -2 : -1);
                        break;
                    }
                    case 180: {
                        size = this.stackSize + (c == 'D' || c == 'J' ? 1 : 0);
                        break;
                    }
                    default: {
                        size = this.stackSize + (c == 'D' || c == 'J' ? -3 : -2);
                    }
                }
                if (size > this.maxStackSize) {
                    this.maxStackSize = size;
                }
                this.stackSize = size;
            }
        }
        this.code.put12(opcode, i.index);
    }

    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        this.lastCodeOffset = this.code.length;
        Item i = this.cw.newMethodItem(owner, name, desc, itf);
        int argSize = i.intVal;
        if (this.currentBlock != null) {
            if (this.compute == 0 || this.compute == 1) {
                this.currentBlock.frame.execute(opcode, 0, this.cw, i);
            } else {
                int size;
                if (argSize == 0) {
                    i.intVal = argSize = Type.getArgumentsAndReturnSizes(desc);
                }
                if ((size = opcode == 184 ? this.stackSize - (argSize >> 2) + (argSize & 3) + 1 : this.stackSize - (argSize >> 2) + (argSize & 3)) > this.maxStackSize) {
                    this.maxStackSize = size;
                }
                this.stackSize = size;
            }
        }
        if (opcode == 185) {
            if (argSize == 0) {
                i.intVal = argSize = Type.getArgumentsAndReturnSizes(desc);
            }
            this.code.put12(185, i.index).put11(argSize >> 2, 0);
        } else {
            this.code.put12(opcode, i.index);
        }
    }

    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object ... bsmArgs) {
        this.lastCodeOffset = this.code.length;
        Item i = this.cw.newInvokeDynamicItem(name, desc, bsm, bsmArgs);
        int argSize = i.intVal;
        if (this.currentBlock != null) {
            if (this.compute == 0 || this.compute == 1) {
                this.currentBlock.frame.execute(186, 0, this.cw, i);
            } else {
                int size;
                if (argSize == 0) {
                    i.intVal = argSize = Type.getArgumentsAndReturnSizes(desc);
                }
                if ((size = this.stackSize - (argSize >> 2) + (argSize & 3) + 1) > this.maxStackSize) {
                    this.maxStackSize = size;
                }
                this.stackSize = size;
            }
        }
        this.code.put12(186, i.index);
        this.code.putShort(0);
    }

    public void visitJumpInsn(int opcode, Label label) {
        boolean isWide = opcode >= 200;
        opcode = isWide ? opcode - 33 : opcode;
        this.lastCodeOffset = this.code.length;
        Label nextInsn = null;
        if (this.currentBlock != null) {
            if (this.compute == 0) {
                this.currentBlock.frame.execute(opcode, 0, null, null);
                label.getFirst().status |= 0x10;
                this.addSuccessor(0, label);
                if (opcode != 167) {
                    nextInsn = new Label();
                }
            } else if (this.compute == 1) {
                this.currentBlock.frame.execute(opcode, 0, null, null);
            } else if (opcode == 168) {
                if ((label.status & 0x200) == 0) {
                    label.status |= 0x200;
                    ++this.subroutines;
                }
                this.currentBlock.status |= 0x80;
                this.addSuccessor(this.stackSize + 1, label);
                nextInsn = new Label();
            } else {
                this.stackSize += Frame.SIZE[opcode];
                this.addSuccessor(this.stackSize, label);
            }
        }
        if ((label.status & 2) != 0 && label.position - this.code.length < -32768) {
            if (opcode == 167) {
                this.code.putByte(200);
            } else if (opcode == 168) {
                this.code.putByte(201);
            } else {
                if (nextInsn != null) {
                    nextInsn.status |= 0x10;
                }
                this.code.putByte(opcode <= 166 ? (opcode + 1 ^ 1) - 1 : opcode ^ 1);
                this.code.putShort(8);
                this.code.putByte(200);
            }
            label.put(this, this.code, this.code.length - 1, true);
        } else if (isWide) {
            this.code.putByte(opcode + 33);
            label.put(this, this.code, this.code.length - 1, true);
        } else {
            this.code.putByte(opcode);
            label.put(this, this.code, this.code.length - 1, false);
        }
        if (this.currentBlock != null) {
            if (nextInsn != null) {
                this.visitLabel(nextInsn);
            }
            if (opcode == 167) {
                this.noSuccessor();
            }
        }
    }

    public void visitLabel(Label label) {
        this.cw.hasAsmInsns |= label.resolve(this, this.code.length, this.code.data);
        if ((label.status & 1) != 0) {
            return;
        }
        if (this.compute == 0) {
            if (this.currentBlock != null) {
                if (label.position == this.currentBlock.position) {
                    this.currentBlock.status |= label.status & 0x10;
                    label.frame = this.currentBlock.frame;
                    return;
                }
                this.addSuccessor(0, label);
            }
            this.currentBlock = label;
            if (label.frame == null) {
                label.frame = new Frame();
                label.frame.owner = label;
            }
            if (this.previousBlock != null) {
                if (label.position == this.previousBlock.position) {
                    this.previousBlock.status |= label.status & 0x10;
                    label.frame = this.previousBlock.frame;
                    this.currentBlock = this.previousBlock;
                    return;
                }
                this.previousBlock.successor = label;
            }
            this.previousBlock = label;
        } else if (this.compute == 1) {
            if (this.currentBlock == null) {
                this.currentBlock = label;
            } else {
                this.currentBlock.frame.owner = label;
            }
        } else if (this.compute == 2) {
            if (this.currentBlock != null) {
                this.currentBlock.outputStackMax = this.maxStackSize;
                this.addSuccessor(this.stackSize, label);
            }
            this.currentBlock = label;
            this.stackSize = 0;
            this.maxStackSize = 0;
            if (this.previousBlock != null) {
                this.previousBlock.successor = label;
            }
            this.previousBlock = label;
        }
    }

    public void visitLdcInsn(Object cst) {
        this.lastCodeOffset = this.code.length;
        Item i = this.cw.newConstItem(cst);
        if (this.currentBlock != null) {
            if (this.compute == 0 || this.compute == 1) {
                this.currentBlock.frame.execute(18, 0, this.cw, i);
            } else {
                int size = i.type == 5 || i.type == 6 ? this.stackSize + 2 : this.stackSize + 1;
                if (size > this.maxStackSize) {
                    this.maxStackSize = size;
                }
                this.stackSize = size;
            }
        }
        int index = i.index;
        if (i.type == 5 || i.type == 6) {
            this.code.put12(20, index);
        } else if (index >= 256) {
            this.code.put12(19, index);
        } else {
            this.code.put11(18, index);
        }
    }

    public void visitIincInsn(int var, int increment) {
        int n;
        this.lastCodeOffset = this.code.length;
        if (this.currentBlock != null && (this.compute == 0 || this.compute == 1)) {
            this.currentBlock.frame.execute(132, var, null, null);
        }
        if (this.compute != 3 && (n = var + 1) > this.maxLocals) {
            this.maxLocals = n;
        }
        if (var > 255 || increment > 127 || increment < -128) {
            this.code.putByte(196).put12(132, var).putShort(increment);
        } else {
            this.code.putByte(132).put11(var, increment);
        }
    }

    public void visitTableSwitchInsn(int min, int max, Label dflt, Label ... labels) {
        this.lastCodeOffset = this.code.length;
        int source = this.code.length;
        this.code.putByte(170);
        this.code.putByteArray(null, 0, (4 - this.code.length % 4) % 4);
        dflt.put(this, this.code, source, true);
        this.code.putInt(min).putInt(max);
        for (int i = 0; i < labels.length; ++i) {
            labels[i].put(this, this.code, source, true);
        }
        this.visitSwitchInsn(dflt, labels);
    }

    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        this.lastCodeOffset = this.code.length;
        int source = this.code.length;
        this.code.putByte(171);
        this.code.putByteArray(null, 0, (4 - this.code.length % 4) % 4);
        dflt.put(this, this.code, source, true);
        this.code.putInt(labels.length);
        for (int i = 0; i < labels.length; ++i) {
            this.code.putInt(keys[i]);
            labels[i].put(this, this.code, source, true);
        }
        this.visitSwitchInsn(dflt, labels);
    }

    private void visitSwitchInsn(Label dflt, Label[] labels) {
        if (this.currentBlock != null) {
            if (this.compute == 0) {
                this.currentBlock.frame.execute(171, 0, null, null);
                this.addSuccessor(0, dflt);
                dflt.getFirst().status |= 0x10;
                for (int i = 0; i < labels.length; ++i) {
                    this.addSuccessor(0, labels[i]);
                    labels[i].getFirst().status |= 0x10;
                }
            } else {
                --this.stackSize;
                this.addSuccessor(this.stackSize, dflt);
                for (int i = 0; i < labels.length; ++i) {
                    this.addSuccessor(this.stackSize, labels[i]);
                }
            }
            this.noSuccessor();
        }
    }

    public void visitMultiANewArrayInsn(String desc, int dims) {
        this.lastCodeOffset = this.code.length;
        Item i = this.cw.newClassItem(desc);
        if (this.currentBlock != null) {
            if (this.compute == 0 || this.compute == 1) {
                this.currentBlock.frame.execute(197, dims, this.cw, i);
            } else {
                this.stackSize += 1 - dims;
            }
        }
        this.code.put12(197, i.index).putByte(dims);
    }

    public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        ByteVector bv = new ByteVector();
        typeRef = typeRef & 0xFF0000FF | this.lastCodeOffset << 8;
        AnnotationWriter.putTarget(typeRef, typePath, bv);
        bv.putShort(this.cw.newUTF8(desc)).putShort(0);
        AnnotationWriter aw = new AnnotationWriter(this.cw, true, bv, bv, bv.length - 2);
        if (visible) {
            aw.next = this.ctanns;
            this.ctanns = aw;
        } else {
            aw.next = this.ictanns;
            this.ictanns = aw;
        }
        return aw;
    }

    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        ++this.handlerCount;
        Handler h = new Handler();
        h.start = start;
        h.end = end;
        h.handler = handler;
        h.desc = type;
        int n = h.type = type != null ? this.cw.newClass(type) : 0;
        if (this.lastHandler == null) {
            this.firstHandler = h;
        } else {
            this.lastHandler.next = h;
        }
        this.lastHandler = h;
    }

    public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        ByteVector bv = new ByteVector();
        AnnotationWriter.putTarget(typeRef, typePath, bv);
        bv.putShort(this.cw.newUTF8(desc)).putShort(0);
        AnnotationWriter aw = new AnnotationWriter(this.cw, true, bv, bv, bv.length - 2);
        if (visible) {
            aw.next = this.ctanns;
            this.ctanns = aw;
        } else {
            aw.next = this.ictanns;
            this.ictanns = aw;
        }
        return aw;
    }

    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        char c;
        int n;
        if (signature != null) {
            if (this.localVarType == null) {
                this.localVarType = new ByteVector();
            }
            ++this.localVarTypeCount;
            this.localVarType.putShort(start.position).putShort(end.position - start.position).putShort(this.cw.newUTF8(name)).putShort(this.cw.newUTF8(signature)).putShort(index);
        }
        if (this.localVar == null) {
            this.localVar = new ByteVector();
        }
        ++this.localVarCount;
        this.localVar.putShort(start.position).putShort(end.position - start.position).putShort(this.cw.newUTF8(name)).putShort(this.cw.newUTF8(desc)).putShort(index);
        if (this.compute != 3 && (n = index + ((c = desc.charAt(0)) == 'J' || c == 'D' ? 2 : 1)) > this.maxLocals) {
            this.maxLocals = n;
        }
    }

    public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible) {
        ByteVector bv = new ByteVector();
        bv.putByte(typeRef >>> 24).putShort(start.length);
        for (int i = 0; i < start.length; ++i) {
            bv.putShort(start[i].position).putShort(end[i].position - start[i].position).putShort(index[i]);
        }
        if (typePath == null) {
            bv.putByte(0);
        } else {
            int length = typePath.b[typePath.offset] * 2 + 1;
            bv.putByteArray(typePath.b, typePath.offset, length);
        }
        bv.putShort(this.cw.newUTF8(desc)).putShort(0);
        AnnotationWriter aw = new AnnotationWriter(this.cw, true, bv, bv, bv.length - 2);
        if (visible) {
            aw.next = this.ctanns;
            this.ctanns = aw;
        } else {
            aw.next = this.ictanns;
            this.ictanns = aw;
        }
        return aw;
    }

    public void visitLineNumber(int line, Label start) {
        if (this.lineNumber == null) {
            this.lineNumber = new ByteVector();
        }
        ++this.lineNumberCount;
        this.lineNumber.putShort(start.position);
        this.lineNumber.putShort(line);
    }

    public void visitMaxs(int maxStack, int maxLocals) {
        if (this.compute == 0) {
            Label l;
            Handler handler = this.firstHandler;
            while (handler != null) {
                Label l2 = handler.start.getFirst();
                Label h = handler.handler.getFirst();
                Label e = handler.end.getFirst();
                String t = handler.desc == null ? "java/lang/Throwable" : handler.desc;
                int kind = 0x1700000 | this.cw.addType(t);
                h.status |= 0x10;
                while (l2 != e) {
                    Edge b = new Edge();
                    b.info = kind;
                    b.successor = h;
                    b.next = l2.successors;
                    l2.successors = b;
                    l2 = l2.successor;
                }
                handler = handler.next;
            }
            Frame f = this.labels.frame;
            f.initInputFrame(this.cw, this.access, Type.getArgumentTypes(this.descriptor), this.maxLocals);
            this.visitFrame(f);
            int max = 0;
            Label changed = this.labels;
            while (changed != null) {
                l = changed;
                changed = changed.next;
                l.next = null;
                f = l.frame;
                if ((l.status & 0x10) != 0) {
                    l.status |= 0x20;
                }
                l.status |= 0x40;
                int blockMax = f.inputStack.length + l.outputStackMax;
                if (blockMax > max) {
                    max = blockMax;
                }
                Edge e = l.successors;
                while (e != null) {
                    Label n = e.successor.getFirst();
                    boolean change = f.merge(this.cw, n.frame, e.info);
                    if (change && n.next == null) {
                        n.next = changed;
                        changed = n;
                    }
                    e = e.next;
                }
            }
            l = this.labels;
            while (l != null) {
                int start;
                Label k;
                int end;
                f = l.frame;
                if ((l.status & 0x20) != 0) {
                    this.visitFrame(f);
                }
                if ((l.status & 0x40) == 0 && (end = ((k = l.successor) == null ? this.code.length : k.position) - 1) >= (start = l.position)) {
                    max = Math.max(max, 1);
                    for (int i = start; i < end; ++i) {
                        this.code.data[i] = 0;
                    }
                    this.code.data[end] = -65;
                    int frameIndex = this.startFrame(start, 0, 1);
                    this.frame[frameIndex] = 0x1700000 | this.cw.addType("java/lang/Throwable");
                    this.endFrame();
                    this.firstHandler = Handler.remove(this.firstHandler, l, k);
                }
                l = l.successor;
            }
            handler = this.firstHandler;
            this.handlerCount = 0;
            while (handler != null) {
                ++this.handlerCount;
                handler = handler.next;
            }
            this.maxStack = max;
        } else if (this.compute == 2) {
            Handler handler = this.firstHandler;
            while (handler != null) {
                Label l = handler.start;
                Label h = handler.handler;
                Label e = handler.end;
                while (l != e) {
                    Edge b = new Edge();
                    b.info = Integer.MAX_VALUE;
                    b.successor = h;
                    if ((l.status & 0x80) == 0) {
                        b.next = l.successors;
                        l.successors = b;
                    } else {
                        b.next = l.successors.next.next;
                        l.successors.next.next = b;
                    }
                    l = l.successor;
                }
                handler = handler.next;
            }
            if (this.subroutines > 0) {
                int id = 0;
                this.labels.visitSubroutine(null, 1L, this.subroutines);
                Label l = this.labels;
                while (l != null) {
                    if ((l.status & 0x80) != 0) {
                        Label subroutine = l.successors.next.successor;
                        if ((subroutine.status & 0x400) == 0) {
                            subroutine.visitSubroutine(null, (long)(++id) / 32L << 32 | 1L << id % 32, this.subroutines);
                        }
                    }
                    l = l.successor;
                }
                l = this.labels;
                while (l != null) {
                    if ((l.status & 0x80) != 0) {
                        Label L = this.labels;
                        while (L != null) {
                            L.status &= 0xFFFFF7FF;
                            L = L.successor;
                        }
                        Label subroutine = l.successors.next.successor;
                        subroutine.visitSubroutine(l, 0L, this.subroutines);
                    }
                    l = l.successor;
                }
            }
            int max = 0;
            Label stack = this.labels;
            while (stack != null) {
                Label l = stack;
                stack = stack.next;
                int start = l.inputStackTop;
                int blockMax = start + l.outputStackMax;
                if (blockMax > max) {
                    max = blockMax;
                }
                Edge b = l.successors;
                if ((l.status & 0x80) != 0) {
                    b = b.next;
                }
                while (b != null) {
                    l = b.successor;
                    if ((l.status & 8) == 0) {
                        l.inputStackTop = b.info == Integer.MAX_VALUE ? 1 : start + b.info;
                        l.status |= 8;
                        l.next = stack;
                        stack = l;
                    }
                    b = b.next;
                }
            }
            this.maxStack = Math.max(maxStack, max);
        } else {
            this.maxStack = maxStack;
            this.maxLocals = maxLocals;
        }
    }

    public void visitEnd() {
    }

    private void addSuccessor(int info, Label successor) {
        Edge b = new Edge();
        b.info = info;
        b.successor = successor;
        b.next = this.currentBlock.successors;
        this.currentBlock.successors = b;
    }

    private void noSuccessor() {
        if (this.compute == 0) {
            Label l = new Label();
            l.frame = new Frame();
            l.frame.owner = l;
            l.resolve(this, this.code.length, this.code.data);
            this.previousBlock.successor = l;
            this.previousBlock = l;
        } else {
            this.currentBlock.outputStackMax = this.maxStackSize;
        }
        if (this.compute != 1) {
            this.currentBlock = null;
        }
    }

    private void visitFrame(Frame f) {
        int t;
        int i;
        int nTop = 0;
        int nLocal = 0;
        int nStack = 0;
        int[] locals = f.inputLocals;
        int[] stacks = f.inputStack;
        for (i = 0; i < locals.length; ++i) {
            t = locals[i];
            if (t == 0x1000000) {
                ++nTop;
            } else {
                nLocal += nTop + 1;
                nTop = 0;
            }
            if (t != 0x1000004 && t != 0x1000003) continue;
            ++i;
        }
        for (i = 0; i < stacks.length; ++i) {
            t = stacks[i];
            ++nStack;
            if (t != 0x1000004 && t != 0x1000003) continue;
            ++i;
        }
        int frameIndex = this.startFrame(f.owner.position, nLocal, nStack);
        i = 0;
        while (nLocal > 0) {
            t = locals[i];
            this.frame[frameIndex++] = t;
            if (t == 0x1000004 || t == 0x1000003) {
                ++i;
            }
            ++i;
            --nLocal;
        }
        for (i = 0; i < stacks.length; ++i) {
            t = stacks[i];
            this.frame[frameIndex++] = t;
            if (t != 0x1000004 && t != 0x1000003) continue;
            ++i;
        }
        this.endFrame();
    }

    private void visitImplicitFirstFrame() {
        int frameIndex = this.startFrame(0, this.descriptor.length() + 1, 0);
        if ((this.access & 8) == 0) {
            this.frame[frameIndex++] = (this.access & 0x80000) == 0 ? 0x1700000 | this.cw.addType(this.cw.thisName) : 6;
        }
        int i = 1;
        block8: while (true) {
            int j = i;
            switch (this.descriptor.charAt(i++)) {
                case 'B': 
                case 'C': 
                case 'I': 
                case 'S': 
                case 'Z': {
                    this.frame[frameIndex++] = 1;
                    continue block8;
                }
                case 'F': {
                    this.frame[frameIndex++] = 2;
                    continue block8;
                }
                case 'J': {
                    this.frame[frameIndex++] = 4;
                    continue block8;
                }
                case 'D': {
                    this.frame[frameIndex++] = 3;
                    continue block8;
                }
                case '[': {
                    while (this.descriptor.charAt(i) == '[') {
                        ++i;
                    }
                    if (this.descriptor.charAt(i) == 'L') {
                        ++i;
                        while (this.descriptor.charAt(i) != ';') {
                            ++i;
                        }
                    }
                    this.frame[frameIndex++] = 0x1700000 | this.cw.addType(this.descriptor.substring(j, ++i));
                    continue block8;
                }
                case 'L': {
                    while (this.descriptor.charAt(i) != ';') {
                        ++i;
                    }
                    this.frame[frameIndex++] = 0x1700000 | this.cw.addType(this.descriptor.substring(j + 1, i++));
                    continue block8;
                }
            }
            break;
        }
        this.frame[1] = frameIndex - 3;
        this.endFrame();
    }

    private int startFrame(int offset, int nLocal, int nStack) {
        int n = 3 + nLocal + nStack;
        if (this.frame == null || this.frame.length < n) {
            this.frame = new int[n];
        }
        this.frame[0] = offset;
        this.frame[1] = nLocal;
        this.frame[2] = nStack;
        return 3;
    }

    private void endFrame() {
        if (this.previousFrame != null) {
            if (this.stackMap == null) {
                this.stackMap = new ByteVector();
            }
            this.writeFrame();
            ++this.frameCount;
        }
        this.previousFrame = this.frame;
        this.frame = null;
    }

    private void writeFrame() {
        int clocalsSize = this.frame[1];
        int cstackSize = this.frame[2];
        if ((this.cw.version & 0xFFFF) < 50) {
            this.stackMap.putShort(this.frame[0]).putShort(clocalsSize);
            this.writeFrameTypes(3, 3 + clocalsSize);
            this.stackMap.putShort(cstackSize);
            this.writeFrameTypes(3 + clocalsSize, 3 + clocalsSize + cstackSize);
            return;
        }
        int localsSize = this.previousFrame[1];
        int type = 255;
        int k = 0;
        int delta = this.frameCount == 0 ? this.frame[0] : this.frame[0] - this.previousFrame[0] - 1;
        if (cstackSize == 0) {
            k = clocalsSize - localsSize;
            switch (k) {
                case -3: 
                case -2: 
                case -1: {
                    type = 248;
                    localsSize = clocalsSize;
                    break;
                }
                case 0: {
                    type = delta < 64 ? 0 : 251;
                    break;
                }
                case 1: 
                case 2: 
                case 3: {
                    type = 252;
                }
            }
        } else if (clocalsSize == localsSize && cstackSize == 1) {
            int n = type = delta < 63 ? 64 : 247;
        }
        if (type != 255) {
            int l = 3;
            for (int j = 0; j < localsSize; ++j) {
                if (this.frame[l] != this.previousFrame[l]) {
                    type = 255;
                    break;
                }
                ++l;
            }
        }
        switch (type) {
            case 0: {
                this.stackMap.putByte(delta);
                break;
            }
            case 64: {
                this.stackMap.putByte(64 + delta);
                this.writeFrameTypes(3 + clocalsSize, 4 + clocalsSize);
                break;
            }
            case 247: {
                this.stackMap.putByte(247).putShort(delta);
                this.writeFrameTypes(3 + clocalsSize, 4 + clocalsSize);
                break;
            }
            case 251: {
                this.stackMap.putByte(251).putShort(delta);
                break;
            }
            case 248: {
                this.stackMap.putByte(251 + k).putShort(delta);
                break;
            }
            case 252: {
                this.stackMap.putByte(251 + k).putShort(delta);
                this.writeFrameTypes(3 + localsSize, 3 + clocalsSize);
                break;
            }
            default: {
                this.stackMap.putByte(255).putShort(delta).putShort(clocalsSize);
                this.writeFrameTypes(3, 3 + clocalsSize);
                this.stackMap.putShort(cstackSize);
                this.writeFrameTypes(3 + clocalsSize, 3 + clocalsSize + cstackSize);
            }
        }
    }

    private void writeFrameTypes(int start, int end) {
        for (int i = start; i < end; ++i) {
            int t = this.frame[i];
            int d = t & 0xF0000000;
            if (d == 0) {
                int v = t & 0xFFFFF;
                switch (t & 0xFF00000) {
                    case 0x1700000: {
                        this.stackMap.putByte(7).putShort(this.cw.newClass(this.cw.typeTable[v].strVal1));
                        break;
                    }
                    case 0x1800000: {
                        this.stackMap.putByte(8).putShort(this.cw.typeTable[v].intVal);
                        break;
                    }
                    default: {
                        this.stackMap.putByte(v);
                        break;
                    }
                }
                continue;
            }
            StringBuilder sb = new StringBuilder();
            d >>= 28;
            while (d-- > 0) {
                sb.append('[');
            }
            if ((t & 0xFF00000) == 0x1700000) {
                sb.append('L');
                sb.append(this.cw.typeTable[t & 1048575].strVal1);
                sb.append(';');
            } else {
                switch (t & 0xF) {
                    case 1: {
                        sb.append('I');
                        break;
                    }
                    case 2: {
                        sb.append('F');
                        break;
                    }
                    case 3: {
                        sb.append('D');
                        break;
                    }
                    case 9: {
                        sb.append('Z');
                        break;
                    }
                    case 10: {
                        sb.append('B');
                        break;
                    }
                    case 11: {
                        sb.append('C');
                        break;
                    }
                    case 12: {
                        sb.append('S');
                        break;
                    }
                    default: {
                        sb.append('J');
                    }
                }
            }
            this.stackMap.putByte(7).putShort(this.cw.newClass(sb.toString()));
        }
    }

    private void writeFrameType(Object type) {
        if (type instanceof String) {
            this.stackMap.putByte(7).putShort(this.cw.newClass((String)type));
        } else if (type instanceof Integer) {
            this.stackMap.putByte((Integer)type);
        } else {
            this.stackMap.putByte(8).putShort(((Label)type).position);
        }
    }

    final int getSize() {
        int i;
        if (this.classReaderOffset != 0) {
            return 6 + this.classReaderLength;
        }
        int size = 8;
        if (this.code.length > 0) {
            if (this.code.length > 65535) {
                throw new RuntimeException("Method code too large!");
            }
            this.cw.newUTF8("Code");
            size += 18 + this.code.length + 8 * this.handlerCount;
            if (this.localVar != null) {
                this.cw.newUTF8("LocalVariableTable");
                size += 8 + this.localVar.length;
            }
            if (this.localVarType != null) {
                this.cw.newUTF8("LocalVariableTypeTable");
                size += 8 + this.localVarType.length;
            }
            if (this.lineNumber != null) {
                this.cw.newUTF8("LineNumberTable");
                size += 8 + this.lineNumber.length;
            }
            if (this.stackMap != null) {
                boolean zip = (this.cw.version & 0xFFFF) >= 50;
                this.cw.newUTF8(zip ? "StackMapTable" : "StackMap");
                size += 8 + this.stackMap.length;
            }
            if (this.ctanns != null) {
                this.cw.newUTF8("RuntimeVisibleTypeAnnotations");
                size += 8 + this.ctanns.getSize();
            }
            if (this.ictanns != null) {
                this.cw.newUTF8("RuntimeInvisibleTypeAnnotations");
                size += 8 + this.ictanns.getSize();
            }
            if (this.cattrs != null) {
                size += this.cattrs.getSize(this.cw, this.code.data, this.code.length, this.maxStack, this.maxLocals);
            }
        }
        if (this.exceptionCount > 0) {
            this.cw.newUTF8("Exceptions");
            size += 8 + 2 * this.exceptionCount;
        }
        if ((this.access & 0x1000) != 0 && ((this.cw.version & 0xFFFF) < 49 || (this.access & 0x40000) != 0)) {
            this.cw.newUTF8("Synthetic");
            size += 6;
        }
        if ((this.access & 0x20000) != 0) {
            this.cw.newUTF8("Deprecated");
            size += 6;
        }
        if (this.signature != null) {
            this.cw.newUTF8("Signature");
            this.cw.newUTF8(this.signature);
            size += 8;
        }
        if (this.methodParameters != null) {
            this.cw.newUTF8("MethodParameters");
            size += 7 + this.methodParameters.length;
        }
        if (this.annd != null) {
            this.cw.newUTF8("AnnotationDefault");
            size += 6 + this.annd.length;
        }
        if (this.anns != null) {
            this.cw.newUTF8("RuntimeVisibleAnnotations");
            size += 8 + this.anns.getSize();
        }
        if (this.ianns != null) {
            this.cw.newUTF8("RuntimeInvisibleAnnotations");
            size += 8 + this.ianns.getSize();
        }
        if (this.tanns != null) {
            this.cw.newUTF8("RuntimeVisibleTypeAnnotations");
            size += 8 + this.tanns.getSize();
        }
        if (this.itanns != null) {
            this.cw.newUTF8("RuntimeInvisibleTypeAnnotations");
            size += 8 + this.itanns.getSize();
        }
        if (this.panns != null) {
            this.cw.newUTF8("RuntimeVisibleParameterAnnotations");
            size += 7 + 2 * (this.panns.length - this.synthetics);
            for (i = this.panns.length - 1; i >= this.synthetics; --i) {
                size += this.panns[i] == null ? 0 : this.panns[i].getSize();
            }
        }
        if (this.ipanns != null) {
            this.cw.newUTF8("RuntimeInvisibleParameterAnnotations");
            size += 7 + 2 * (this.ipanns.length - this.synthetics);
            for (i = this.ipanns.length - 1; i >= this.synthetics; --i) {
                size += this.ipanns[i] == null ? 0 : this.ipanns[i].getSize();
            }
        }
        if (this.attrs != null) {
            size += this.attrs.getSize(this.cw, null, 0, -1, -1);
        }
        return size;
    }

    final void put(ByteVector out) {
        int FACTOR = 64;
        int mask = 0xE0000 | (this.access & 0x40000) / 64;
        out.putShort(this.access & ~mask).putShort(this.name).putShort(this.desc);
        if (this.classReaderOffset != 0) {
            out.putByteArray(this.cw.cr.b, this.classReaderOffset, this.classReaderLength);
            return;
        }
        int attributeCount = 0;
        if (this.code.length > 0) {
            ++attributeCount;
        }
        if (this.exceptionCount > 0) {
            ++attributeCount;
        }
        if ((this.access & 0x1000) != 0 && ((this.cw.version & 0xFFFF) < 49 || (this.access & 0x40000) != 0)) {
            ++attributeCount;
        }
        if ((this.access & 0x20000) != 0) {
            ++attributeCount;
        }
        if (this.signature != null) {
            ++attributeCount;
        }
        if (this.methodParameters != null) {
            ++attributeCount;
        }
        if (this.annd != null) {
            ++attributeCount;
        }
        if (this.anns != null) {
            ++attributeCount;
        }
        if (this.ianns != null) {
            ++attributeCount;
        }
        if (this.tanns != null) {
            ++attributeCount;
        }
        if (this.itanns != null) {
            ++attributeCount;
        }
        if (this.panns != null) {
            ++attributeCount;
        }
        if (this.ipanns != null) {
            ++attributeCount;
        }
        if (this.attrs != null) {
            attributeCount += this.attrs.getCount();
        }
        out.putShort(attributeCount);
        if (this.code.length > 0) {
            int size = 12 + this.code.length + 8 * this.handlerCount;
            if (this.localVar != null) {
                size += 8 + this.localVar.length;
            }
            if (this.localVarType != null) {
                size += 8 + this.localVarType.length;
            }
            if (this.lineNumber != null) {
                size += 8 + this.lineNumber.length;
            }
            if (this.stackMap != null) {
                size += 8 + this.stackMap.length;
            }
            if (this.ctanns != null) {
                size += 8 + this.ctanns.getSize();
            }
            if (this.ictanns != null) {
                size += 8 + this.ictanns.getSize();
            }
            if (this.cattrs != null) {
                size += this.cattrs.getSize(this.cw, this.code.data, this.code.length, this.maxStack, this.maxLocals);
            }
            out.putShort(this.cw.newUTF8("Code")).putInt(size);
            out.putShort(this.maxStack).putShort(this.maxLocals);
            out.putInt(this.code.length).putByteArray(this.code.data, 0, this.code.length);
            out.putShort(this.handlerCount);
            if (this.handlerCount > 0) {
                Handler h = this.firstHandler;
                while (h != null) {
                    out.putShort(h.start.position).putShort(h.end.position).putShort(h.handler.position).putShort(h.type);
                    h = h.next;
                }
            }
            attributeCount = 0;
            if (this.localVar != null) {
                ++attributeCount;
            }
            if (this.localVarType != null) {
                ++attributeCount;
            }
            if (this.lineNumber != null) {
                ++attributeCount;
            }
            if (this.stackMap != null) {
                ++attributeCount;
            }
            if (this.ctanns != null) {
                ++attributeCount;
            }
            if (this.ictanns != null) {
                ++attributeCount;
            }
            if (this.cattrs != null) {
                attributeCount += this.cattrs.getCount();
            }
            out.putShort(attributeCount);
            if (this.localVar != null) {
                out.putShort(this.cw.newUTF8("LocalVariableTable"));
                out.putInt(this.localVar.length + 2).putShort(this.localVarCount);
                out.putByteArray(this.localVar.data, 0, this.localVar.length);
            }
            if (this.localVarType != null) {
                out.putShort(this.cw.newUTF8("LocalVariableTypeTable"));
                out.putInt(this.localVarType.length + 2).putShort(this.localVarTypeCount);
                out.putByteArray(this.localVarType.data, 0, this.localVarType.length);
            }
            if (this.lineNumber != null) {
                out.putShort(this.cw.newUTF8("LineNumberTable"));
                out.putInt(this.lineNumber.length + 2).putShort(this.lineNumberCount);
                out.putByteArray(this.lineNumber.data, 0, this.lineNumber.length);
            }
            if (this.stackMap != null) {
                boolean zip = (this.cw.version & 0xFFFF) >= 50;
                out.putShort(this.cw.newUTF8(zip ? "StackMapTable" : "StackMap"));
                out.putInt(this.stackMap.length + 2).putShort(this.frameCount);
                out.putByteArray(this.stackMap.data, 0, this.stackMap.length);
            }
            if (this.ctanns != null) {
                out.putShort(this.cw.newUTF8("RuntimeVisibleTypeAnnotations"));
                this.ctanns.put(out);
            }
            if (this.ictanns != null) {
                out.putShort(this.cw.newUTF8("RuntimeInvisibleTypeAnnotations"));
                this.ictanns.put(out);
            }
            if (this.cattrs != null) {
                this.cattrs.put(this.cw, this.code.data, this.code.length, this.maxLocals, this.maxStack, out);
            }
        }
        if (this.exceptionCount > 0) {
            out.putShort(this.cw.newUTF8("Exceptions")).putInt(2 * this.exceptionCount + 2);
            out.putShort(this.exceptionCount);
            for (int i = 0; i < this.exceptionCount; ++i) {
                out.putShort(this.exceptions[i]);
            }
        }
        if ((this.access & 0x1000) != 0 && ((this.cw.version & 0xFFFF) < 49 || (this.access & 0x40000) != 0)) {
            out.putShort(this.cw.newUTF8("Synthetic")).putInt(0);
        }
        if ((this.access & 0x20000) != 0) {
            out.putShort(this.cw.newUTF8("Deprecated")).putInt(0);
        }
        if (this.signature != null) {
            out.putShort(this.cw.newUTF8("Signature")).putInt(2).putShort(this.cw.newUTF8(this.signature));
        }
        if (this.methodParameters != null) {
            out.putShort(this.cw.newUTF8("MethodParameters"));
            out.putInt(this.methodParameters.length + 1).putByte(this.methodParametersCount);
            out.putByteArray(this.methodParameters.data, 0, this.methodParameters.length);
        }
        if (this.annd != null) {
            out.putShort(this.cw.newUTF8("AnnotationDefault"));
            out.putInt(this.annd.length);
            out.putByteArray(this.annd.data, 0, this.annd.length);
        }
        if (this.anns != null) {
            out.putShort(this.cw.newUTF8("RuntimeVisibleAnnotations"));
            this.anns.put(out);
        }
        if (this.ianns != null) {
            out.putShort(this.cw.newUTF8("RuntimeInvisibleAnnotations"));
            this.ianns.put(out);
        }
        if (this.tanns != null) {
            out.putShort(this.cw.newUTF8("RuntimeVisibleTypeAnnotations"));
            this.tanns.put(out);
        }
        if (this.itanns != null) {
            out.putShort(this.cw.newUTF8("RuntimeInvisibleTypeAnnotations"));
            this.itanns.put(out);
        }
        if (this.panns != null) {
            out.putShort(this.cw.newUTF8("RuntimeVisibleParameterAnnotations"));
            AnnotationWriter.put(this.panns, this.synthetics, out);
        }
        if (this.ipanns != null) {
            out.putShort(this.cw.newUTF8("RuntimeInvisibleParameterAnnotations"));
            AnnotationWriter.put(this.ipanns, this.synthetics, out);
        }
        if (this.attrs != null) {
            this.attrs.put(this.cw, null, 0, -1, -1, out);
        }
    }
}

