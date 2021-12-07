/*
 * Decompiled with CFR 0.150.
 */
package org.spongepowered.asm.lib.util;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.spongepowered.asm.lib.Attribute;
import org.spongepowered.asm.lib.Handle;
import org.spongepowered.asm.lib.Label;
import org.spongepowered.asm.lib.TypePath;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class Printer {
    public static final String[] OPCODES;
    public static final String[] TYPES;
    public static final String[] HANDLE_TAG;
    protected final int api;
    protected final StringBuffer buf;
    public final List<Object> text;

    protected Printer(int api) {
        this.api = api;
        this.buf = new StringBuffer();
        this.text = new ArrayList<Object>();
    }

    public abstract void visit(int var1, int var2, String var3, String var4, String var5, String[] var6);

    public abstract void visitSource(String var1, String var2);

    public abstract void visitOuterClass(String var1, String var2, String var3);

    public abstract Printer visitClassAnnotation(String var1, boolean var2);

    public Printer visitClassTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        throw new RuntimeException("Must be overriden");
    }

    public abstract void visitClassAttribute(Attribute var1);

    public abstract void visitInnerClass(String var1, String var2, String var3, int var4);

    public abstract Printer visitField(int var1, String var2, String var3, String var4, Object var5);

    public abstract Printer visitMethod(int var1, String var2, String var3, String var4, String[] var5);

    public abstract void visitClassEnd();

    public abstract void visit(String var1, Object var2);

    public abstract void visitEnum(String var1, String var2, String var3);

    public abstract Printer visitAnnotation(String var1, String var2);

    public abstract Printer visitArray(String var1);

    public abstract void visitAnnotationEnd();

    public abstract Printer visitFieldAnnotation(String var1, boolean var2);

    public Printer visitFieldTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        throw new RuntimeException("Must be overriden");
    }

    public abstract void visitFieldAttribute(Attribute var1);

    public abstract void visitFieldEnd();

    public void visitParameter(String name, int access) {
        throw new RuntimeException("Must be overriden");
    }

    public abstract Printer visitAnnotationDefault();

    public abstract Printer visitMethodAnnotation(String var1, boolean var2);

    public Printer visitMethodTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        throw new RuntimeException("Must be overriden");
    }

    public abstract Printer visitParameterAnnotation(int var1, String var2, boolean var3);

    public abstract void visitMethodAttribute(Attribute var1);

    public abstract void visitCode();

    public abstract void visitFrame(int var1, int var2, Object[] var3, int var4, Object[] var5);

    public abstract void visitInsn(int var1);

    public abstract void visitIntInsn(int var1, int var2);

    public abstract void visitVarInsn(int var1, int var2);

    public abstract void visitTypeInsn(int var1, String var2);

    public abstract void visitFieldInsn(int var1, String var2, String var3, String var4);

    @Deprecated
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        if (this.api >= 327680) {
            boolean itf = opcode == 185;
            this.visitMethodInsn(opcode, owner, name, desc, itf);
            return;
        }
        throw new RuntimeException("Must be overriden");
    }

    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        if (this.api < 327680) {
            if (itf != (opcode == 185)) {
                throw new IllegalArgumentException("INVOKESPECIAL/STATIC on interfaces require ASM 5");
            }
            this.visitMethodInsn(opcode, owner, name, desc);
            return;
        }
        throw new RuntimeException("Must be overriden");
    }

    public abstract void visitInvokeDynamicInsn(String var1, String var2, Handle var3, Object ... var4);

    public abstract void visitJumpInsn(int var1, Label var2);

    public abstract void visitLabel(Label var1);

    public abstract void visitLdcInsn(Object var1);

    public abstract void visitIincInsn(int var1, int var2);

    public abstract void visitTableSwitchInsn(int var1, int var2, Label var3, Label ... var4);

    public abstract void visitLookupSwitchInsn(Label var1, int[] var2, Label[] var3);

    public abstract void visitMultiANewArrayInsn(String var1, int var2);

    public Printer visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        throw new RuntimeException("Must be overriden");
    }

    public abstract void visitTryCatchBlock(Label var1, Label var2, Label var3, String var4);

    public Printer visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        throw new RuntimeException("Must be overriden");
    }

    public abstract void visitLocalVariable(String var1, String var2, String var3, Label var4, Label var5, int var6);

    public Printer visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible) {
        throw new RuntimeException("Must be overriden");
    }

    public abstract void visitLineNumber(int var1, Label var2);

    public abstract void visitMaxs(int var1, int var2);

    public abstract void visitMethodEnd();

    public List<Object> getText() {
        return this.text;
    }

    public void print(PrintWriter pw) {
        Printer.printList(pw, this.text);
    }

    public static void appendString(StringBuffer buf, String s) {
        buf.append('\"');
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c == '\n') {
                buf.append("\\n");
                continue;
            }
            if (c == '\r') {
                buf.append("\\r");
                continue;
            }
            if (c == '\\') {
                buf.append("\\\\");
                continue;
            }
            if (c == '\"') {
                buf.append("\\\"");
                continue;
            }
            if (c < ' ' || c > '\u007f') {
                buf.append("\\u");
                if (c < '\u0010') {
                    buf.append("000");
                } else if (c < '\u0100') {
                    buf.append("00");
                } else if (c < '\u1000') {
                    buf.append('0');
                }
                buf.append(Integer.toString(c, 16));
                continue;
            }
            buf.append(c);
        }
        buf.append('\"');
    }

    static void printList(PrintWriter pw, List<?> l) {
        for (int i = 0; i < l.size(); ++i) {
            Object o = l.get(i);
            if (o instanceof List) {
                Printer.printList(pw, (List)o);
                continue;
            }
            pw.print(o.toString());
        }
    }

    static {
        int l;
        String s = "NOP,ACONST_NULL,ICONST_M1,ICONST_0,ICONST_1,ICONST_2,ICONST_3,ICONST_4,ICONST_5,LCONST_0,LCONST_1,FCONST_0,FCONST_1,FCONST_2,DCONST_0,DCONST_1,BIPUSH,SIPUSH,LDC,,,ILOAD,LLOAD,FLOAD,DLOAD,ALOAD,,,,,,,,,,,,,,,,,,,,,IALOAD,LALOAD,FALOAD,DALOAD,AALOAD,BALOAD,CALOAD,SALOAD,ISTORE,LSTORE,FSTORE,DSTORE,ASTORE,,,,,,,,,,,,,,,,,,,,,IASTORE,LASTORE,FASTORE,DASTORE,AASTORE,BASTORE,CASTORE,SASTORE,POP,POP2,DUP,DUP_X1,DUP_X2,DUP2,DUP2_X1,DUP2_X2,SWAP,IADD,LADD,FADD,DADD,ISUB,LSUB,FSUB,DSUB,IMUL,LMUL,FMUL,DMUL,IDIV,LDIV,FDIV,DDIV,IREM,LREM,FREM,DREM,INEG,LNEG,FNEG,DNEG,ISHL,LSHL,ISHR,LSHR,IUSHR,LUSHR,IAND,LAND,IOR,LOR,IXOR,LXOR,IINC,I2L,I2F,I2D,L2I,L2F,L2D,F2I,F2L,F2D,D2I,D2L,D2F,I2B,I2C,I2S,LCMP,FCMPL,FCMPG,DCMPL,DCMPG,IFEQ,IFNE,IFLT,IFGE,IFGT,IFLE,IF_ICMPEQ,IF_ICMPNE,IF_ICMPLT,IF_ICMPGE,IF_ICMPGT,IF_ICMPLE,IF_ACMPEQ,IF_ACMPNE,GOTO,JSR,RET,TABLESWITCH,LOOKUPSWITCH,IRETURN,LRETURN,FRETURN,DRETURN,ARETURN,RETURN,GETSTATIC,PUTSTATIC,GETFIELD,PUTFIELD,INVOKEVIRTUAL,INVOKESPECIAL,INVOKESTATIC,INVOKEINTERFACE,INVOKEDYNAMIC,NEW,NEWARRAY,ANEWARRAY,ARRAYLENGTH,ATHROW,CHECKCAST,INSTANCEOF,MONITORENTER,MONITOREXIT,,MULTIANEWARRAY,IFNULL,IFNONNULL,";
        OPCODES = new String[200];
        int i = 0;
        int j = 0;
        while ((l = s.indexOf(44, j)) > 0) {
            Printer.OPCODES[i++] = j + 1 == l ? null : s.substring(j, l);
            j = l + 1;
        }
        s = "T_BOOLEAN,T_CHAR,T_FLOAT,T_DOUBLE,T_BYTE,T_SHORT,T_INT,T_LONG,";
        TYPES = new String[12];
        j = 0;
        i = 4;
        while ((l = s.indexOf(44, j)) > 0) {
            Printer.TYPES[i++] = s.substring(j, l);
            j = l + 1;
        }
        s = "H_GETFIELD,H_GETSTATIC,H_PUTFIELD,H_PUTSTATIC,H_INVOKEVIRTUAL,H_INVOKESTATIC,H_INVOKESPECIAL,H_NEWINVOKESPECIAL,H_INVOKEINTERFACE,";
        HANDLE_TAG = new String[10];
        j = 0;
        i = 1;
        while ((l = s.indexOf(44, j)) > 0) {
            Printer.HANDLE_TAG[i++] = s.substring(j, l);
            j = l + 1;
        }
    }
}

