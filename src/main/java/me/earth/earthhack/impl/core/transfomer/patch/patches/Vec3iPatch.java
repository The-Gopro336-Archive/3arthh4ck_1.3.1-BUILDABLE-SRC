/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.objectweb.asm.tree.ClassNode
 *  org.objectweb.asm.tree.MethodNode
 */
package me.earth.earthhack.impl.core.transfomer.patch.patches;

import me.earth.earthhack.impl.core.Core;
import me.earth.earthhack.impl.core.transfomer.patch.ArgumentPatch;
import me.earth.earthhack.impl.core.util.AsmUtil;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class Vec3iPatch
extends ArgumentPatch {
    public Vec3iPatch() {
        super("fq", "net.minecraft.util.math.Vec3i", "leijurvpos");
    }

    @Override
    protected void applyPatch(ClassNode cn) {
        MethodNode x = AsmUtil.findMappedMethod(cn, "p", "()I", "func_177958_n", "getX", "()I");
        MethodNode y = AsmUtil.findMappedMethod(cn, "q", "()I", "func_177956_o", "getY", "()I");
        MethodNode z = AsmUtil.findMappedMethod(cn, "r", "()I", "func_177952_p", "getZ", "()I");
        MethodNode hashCode = AsmUtil.findMethod(cn, "hashCode", "()I");
        if (x == null || y == null || z == null || hashCode == null) {
            Core.LOGGER.error("Vec3i is missing one of: " + (Object)x + ", " + (Object)y + ", " + (Object)z + ", " + (Object)hashCode);
            return;
        }
        hashCode.visitCode();
        hashCode.visitLdcInsn((Object)11206370049L);
        hashCode.visitVarInsn(25, 0);
        hashCode.visitMethodInsn(182, cn.name, x.name, x.desc, false);
        hashCode.visitInsn(133);
        hashCode.visitInsn(97);
        hashCode.visitLdcInsn((Object)8734625L);
        hashCode.visitInsn(105);
        hashCode.visitVarInsn(25, 0);
        hashCode.visitMethodInsn(182, cn.name, y.name, y.desc, false);
        hashCode.visitInsn(133);
        hashCode.visitInsn(97);
        hashCode.visitLdcInsn((Object)2873465L);
        hashCode.visitInsn(105);
        hashCode.visitVarInsn(25, 0);
        hashCode.visitMethodInsn(182, cn.name, z.name, z.desc, false);
        hashCode.visitInsn(133);
        hashCode.visitInsn(97);
        hashCode.visitInsn(136);
        hashCode.visitInsn(172);
        hashCode.visitMaxs(4, 1);
        hashCode.visitEnd();
    }
}

