package me.earth.earthhack.impl.core.util;

import me.earth.earthhack.impl.core.util.NoSuperClassWriter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class AsmUtil {
    public static ClassNode read(byte[] clazz, int ... flags) {
        ClassNode result = new ClassNode();
        ClassReader reader = new ClassReader(clazz);
        reader.accept(result, AsmUtil.toFlag(flags));
        return result;
    }

    public static byte[] write(ClassNode classNode, int ... flags) {
        ClassWriter writer = new ClassWriter(AsmUtil.toFlag(flags));
        classNode.accept(writer);
        return writer.toByteArray();
    }

    public static byte[] writeNoSuperClass(ClassNode classNode, int ... flags) {
        NoSuperClassWriter writer = new NoSuperClassWriter(AsmUtil.toFlag(flags));
        classNode.accept(writer);
        return writer.toByteArray();
    }

    public static MethodNode findMappedMethod(ClassNode node, String notch, String notchDesc, String searge, String mcp, String srgMcpDesc) {
        MethodNode result = AsmUtil.findMethod(node, notch, notchDesc);
        if (result == null && (result = AsmUtil.findMethod(node, searge, srgMcpDesc)) == null) {
            return AsmUtil.findMethod(node, mcp, srgMcpDesc);
        }
        return result;
    }

    public static MethodNode findMethod(ClassNode node, String name, String description) {
        for (MethodNode mn : node.methods) {
            if (!mn.name.equals(name) || !mn.desc.equals(description)) continue;
            return mn;
        }
        return null;
    }

    public static FieldNode findField(ClassNode node, String ... names) {
        for (String name : names) {
            FieldNode result = AsmUtil.findField(node, name);
            if (result == null) continue;
            return result;
        }
        return null;
    }

    public static FieldNode findField(ClassNode node, String name) {
        for (FieldNode field : node.fields) {
            if (!field.name.equals(name)) continue;
            return field;
        }
        return null;
    }

    public static int toFlag(int ... flags) {
        int flag = 0;
        for (int f : flags) {
            flag |= f;
        }
        return flag;
    }
}
