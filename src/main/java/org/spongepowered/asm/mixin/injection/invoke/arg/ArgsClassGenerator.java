package org.spongepowered.asm.mixin.injection.invoke.arg;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.HashMap;
import java.util.Map;
import org.spongepowered.asm.lib.ClassVisitor;
import org.spongepowered.asm.lib.ClassWriter;
import org.spongepowered.asm.lib.Label;
import org.spongepowered.asm.lib.MethodVisitor;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.lib.util.CheckClassAdapter;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import org.spongepowered.asm.mixin.transformer.ext.IClassGenerator;
import org.spongepowered.asm.util.Bytecode;
import org.spongepowered.asm.util.SignaturePrinter;
import org.spongepowered.asm.util.asm.MethodVisitorEx;

public final class ArgsClassGenerator
implements IClassGenerator {
    public static final String ARGS_NAME = Args.class.getName();
    public static final String ARGS_REF = ARGS_NAME.replace('.', '/');
    public static final String GETTER_PREFIX = "$";
    private static final String CLASS_NAME_BASE = "org.spongepowered.asm.synthetic.args.Args$";
    private static final String OBJECT = "java/lang/Object";
    private static final String OBJECT_ARRAY = "[Ljava/lang/Object;";
    private static final String VALUES_FIELD = "values";
    private static final String CTOR_DESC = "([Ljava/lang/Object;)V";
    private static final String SET = "set";
    private static final String SET_DESC = "(ILjava/lang/Object;)V";
    private static final String SETALL = "setAll";
    private static final String SETALL_DESC = "([Ljava/lang/Object;)V";
    private static final String NPE = "java/lang/NullPointerException";
    private static final String NPE_CTOR_DESC = "(Ljava/lang/String;)V";
    private static final String AIOOBE = "org/spongepowered/asm/mixin/injection/invoke/arg/ArgumentIndexOutOfBoundsException";
    private static final String AIOOBE_CTOR_DESC = "(I)V";
    private static final String ACE = "org/spongepowered/asm/mixin/injection/invoke/arg/ArgumentCountException";
    private static final String ACE_CTOR_DESC = "(IILjava/lang/String;)V";
    private int nextIndex = 1;
    private final BiMap<String, String> classNames = HashBiMap.create();
    private final Map<String, byte[]> classBytes = new HashMap<String, byte[]>();

    public String getClassName(String desc) {
        String voidDesc = Bytecode.changeDescriptorReturnType(desc, "V");
        String name = (String)this.classNames.get(voidDesc);
        if (name == null) {
            name = String.format("%s%d", CLASS_NAME_BASE, this.nextIndex++);
            this.classNames.put(voidDesc, name);
        }
        return name;
    }

    public String getClassRef(String desc) {
        return this.getClassName(desc).replace('.', '/');
    }

    @Override
    public byte[] generate(String name) {
        return this.getBytes(name);
    }

    public byte[] getBytes(String name) {
        byte[] bytes = this.classBytes.get(name);
        if (bytes == null) {
            String desc = (String)this.classNames.inverse().get(name);
            if (desc == null) {
                return null;
            }
            bytes = this.generateClass(name, desc);
            this.classBytes.put(name, bytes);
        }
        return bytes;
    }

    private byte[] generateClass(String name, String desc) {
        String ref = name.replace('.', '/');
        Type[] args = Type.getArgumentTypes(desc);
        ClassWriter writer = new ClassWriter(2);
        ClassVisitor visitor = writer;
        if (MixinEnvironment.getCurrentEnvironment().getOption(MixinEnvironment.Option.DEBUG_VERIFY)) {
            visitor = new CheckClassAdapter(writer);
        }
        visitor.visit(50, 4129, ref, null, ARGS_REF, null);
        visitor.visitSource(name.substring(name.lastIndexOf(46) + 1) + ".java", null);
        this.generateCtor(ref, desc, args, visitor);
        this.generateToString(ref, desc, args, visitor);
        this.generateFactory(ref, desc, args, visitor);
        this.generateSetters(ref, desc, args, visitor);
        this.generateGetters(ref, desc, args, visitor);
        visitor.visitEnd();
        return writer.toByteArray();
    }

    private void generateCtor(String ref, String desc, Type[] args, ClassVisitor writer) {
        MethodVisitor ctor = writer.visitMethod(2, "<init>", "([Ljava/lang/Object;)V", null, null);
        ctor.visitCode();
        ctor.visitVarInsn(25, 0);
        ctor.visitVarInsn(25, 1);
        ctor.visitMethodInsn(183, ARGS_REF, "<init>", "([Ljava/lang/Object;)V", false);
        ctor.visitInsn(177);
        ctor.visitMaxs(2, 2);
        ctor.visitEnd();
    }

    private void generateToString(String ref, String desc, Type[] args, ClassVisitor writer) {
        MethodVisitor toString = writer.visitMethod(1, "toString", "()Ljava/lang/String;", null, null);
        toString.visitCode();
        toString.visitLdcInsn("Args" + ArgsClassGenerator.getSignature(args));
        toString.visitInsn(176);
        toString.visitMaxs(1, 1);
        toString.visitEnd();
    }

    private void generateFactory(String ref, String desc, Type[] args, ClassVisitor writer) {
        String factoryDesc = Bytecode.changeDescriptorReturnType(desc, "L" + ref + ";");
        MethodVisitorEx of = new MethodVisitorEx(writer.visitMethod(9, "of", factoryDesc, null, null));
        of.visitCode();
        of.visitTypeInsn(187, ref);
        of.visitInsn(89);
        of.visitConstant((byte)args.length);
        of.visitTypeInsn(189, OBJECT);
        byte argIndex = 0;
        for (Type arg : args) {
            of.visitInsn(89);
            of.visitConstant(argIndex);
            of.visitVarInsn(arg.getOpcode(21), argIndex);
            ArgsClassGenerator.box(of, arg);
            of.visitInsn(83);
            argIndex = (byte)(argIndex + arg.getSize());
        }
        of.visitMethodInsn(183, ref, "<init>", "([Ljava/lang/Object;)V", false);
        of.visitInsn(176);
        of.visitMaxs(6, Bytecode.getArgsSize(args));
        of.visitEnd();
    }

    private void generateGetters(String ref, String desc, Type[] args, ClassVisitor writer) {
        byte argIndex = 0;
        for (Type arg : args) {
            String name = GETTER_PREFIX + argIndex;
            String sig = "()" + arg.getDescriptor();
            MethodVisitorEx get = new MethodVisitorEx(writer.visitMethod(1, name, sig, null, null));
            get.visitCode();
            get.visitVarInsn(25, 0);
            get.visitFieldInsn(180, ref, VALUES_FIELD, OBJECT_ARRAY);
            get.visitConstant(argIndex);
            get.visitInsn(50);
            ArgsClassGenerator.unbox(get, arg);
            get.visitInsn(arg.getOpcode(172));
            get.visitMaxs(2, 1);
            get.visitEnd();
            argIndex = (byte)(argIndex + 1);
        }
    }

    private void generateSetters(String ref, String desc, Type[] args, ClassVisitor writer) {
        this.generateIndexedSetter(ref, desc, args, writer);
        this.generateMultiSetter(ref, desc, args, writer);
    }

    private void generateIndexedSetter(String ref, String desc, Type[] args, ClassVisitor writer) {
        int index;
        MethodVisitorEx set = new MethodVisitorEx(writer.visitMethod(1, SET, SET_DESC, null, null));
        set.visitCode();
        Label store = new Label();
        Label checkNull = new Label();
        Label[] labels = new Label[args.length];
        for (int label = 0; label < labels.length; ++label) {
            labels[label] = new Label();
        }
        set.visitVarInsn(25, 0);
        set.visitFieldInsn(180, ref, VALUES_FIELD, OBJECT_ARRAY);
        for (index = 0; index < args.length; index = (byte)(index + 1)) {
            set.visitVarInsn(21, 1);
            set.visitConstant((byte)index);
            set.visitJumpInsn(159, labels[index]);
        }
        ArgsClassGenerator.throwAIOOBE(set, 1);
        for (index = 0; index < args.length; ++index) {
            String boxingType = Bytecode.getBoxingType(args[index]);
            set.visitLabel(labels[index]);
            set.visitVarInsn(21, 1);
            set.visitVarInsn(25, 2);
            set.visitTypeInsn(192, boxingType != null ? boxingType : args[index].getInternalName());
            set.visitJumpInsn(167, boxingType != null ? checkNull : store);
        }
        set.visitLabel(checkNull);
        set.visitInsn(89);
        set.visitJumpInsn(199, store);
        ArgsClassGenerator.throwNPE(set, "Argument with primitive type cannot be set to NULL");
        set.visitLabel(store);
        set.visitInsn(83);
        set.visitInsn(177);
        set.visitMaxs(6, 3);
        set.visitEnd();
    }

    private void generateMultiSetter(String ref, String desc, Type[] args, ClassVisitor writer) {
        MethodVisitorEx set = new MethodVisitorEx(writer.visitMethod(1, SETALL, "([Ljava/lang/Object;)V", null, null));
        set.visitCode();
        Label lengthOk = new Label();
        Label nullPrimitive = new Label();
        int maxStack = 6;
        set.visitVarInsn(25, 1);
        set.visitInsn(190);
        set.visitInsn(89);
        set.visitConstant((byte)args.length);
        set.visitJumpInsn(159, lengthOk);
        set.visitTypeInsn(187, ACE);
        set.visitInsn(89);
        set.visitInsn(93);
        set.visitInsn(88);
        set.visitConstant((byte)args.length);
        set.visitLdcInsn(ArgsClassGenerator.getSignature(args));
        set.visitMethodInsn(183, ACE, "<init>", ACE_CTOR_DESC, false);
        set.visitInsn(191);
        set.visitLabel(lengthOk);
        set.visitInsn(87);
        set.visitVarInsn(25, 0);
        set.visitFieldInsn(180, ref, VALUES_FIELD, OBJECT_ARRAY);
        for (byte index = 0; index < args.length; index = (byte)(index + 1)) {
            set.visitInsn(89);
            set.visitConstant(index);
            set.visitVarInsn(25, 1);
            set.visitConstant(index);
            set.visitInsn(50);
            String boxingType = Bytecode.getBoxingType(args[index]);
            set.visitTypeInsn(192, boxingType != null ? boxingType : args[index].getInternalName());
            if (boxingType != null) {
                set.visitInsn(89);
                set.visitJumpInsn(198, nullPrimitive);
                maxStack = 7;
            }
            set.visitInsn(83);
        }
        set.visitInsn(177);
        set.visitLabel(nullPrimitive);
        ArgsClassGenerator.throwNPE(set, "Argument with primitive type cannot be set to NULL");
        set.visitInsn(177);
        set.visitMaxs(maxStack, 2);
        set.visitEnd();
    }

    private static void throwNPE(MethodVisitorEx method, String message) {
        method.visitTypeInsn(187, NPE);
        method.visitInsn(89);
        method.visitLdcInsn(message);
        method.visitMethodInsn(183, NPE, "<init>", NPE_CTOR_DESC, false);
        method.visitInsn(191);
    }

    private static void throwAIOOBE(MethodVisitorEx method, int arg) {
        method.visitTypeInsn(187, AIOOBE);
        method.visitInsn(89);
        method.visitVarInsn(21, arg);
        method.visitMethodInsn(183, AIOOBE, "<init>", AIOOBE_CTOR_DESC, false);
        method.visitInsn(191);
    }

    private static void box(MethodVisitor method, Type var) {
        String boxingType = Bytecode.getBoxingType(var);
        if (boxingType != null) {
            String desc = String.format("(%s)L%s;", var.getDescriptor(), boxingType);
            method.visitMethodInsn(184, boxingType, "valueOf", desc, false);
        }
    }

    private static void unbox(MethodVisitor method, Type var) {
        String boxingType = Bytecode.getBoxingType(var);
        if (boxingType != null) {
            String unboxingMethod = Bytecode.getUnboxingMethod(var);
            String desc = "()" + var.getDescriptor();
            method.visitTypeInsn(192, boxingType);
            method.visitMethodInsn(182, boxingType, unboxingMethod, desc, false);
        } else {
            method.visitTypeInsn(192, var.getInternalName());
        }
    }

    private static String getSignature(Type[] args) {
        return new SignaturePrinter("", null, args).setFullyQualified(true).getFormattedArgs();
    }
}
