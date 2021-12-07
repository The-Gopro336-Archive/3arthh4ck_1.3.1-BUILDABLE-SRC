package org.spongepowered.tools.agent;

import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.lib.ClassWriter;
import org.spongepowered.asm.lib.MethodVisitor;
import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.mixin.MixinEnvironment;

class MixinAgentClassLoader
extends ClassLoader {
    private static final Logger logger = LogManager.getLogger("mixin.agent");
    private Map<Class<?>, byte[]> mixins = new HashMap();
    private Map<String, byte[]> targets = new HashMap<String, byte[]>();

    MixinAgentClassLoader() {
    }

    void addMixinClass(String name) {
        logger.debug("Mixin class {} added to class loader", new Object[]{name});
        try {
            byte[] bytes = this.materialise(name);
            Class<?> clazz = this.defineClass(name, bytes, 0, bytes.length);
            clazz.newInstance();
            this.mixins.put(clazz, bytes);
        }
        catch (Throwable e) {
            logger.catching(e);
        }
    }

    void addTargetClass(String name, byte[] bytecode) {
        this.targets.put(name, bytecode);
    }

    byte[] getFakeMixinBytecode(Class<?> clazz) {
        return this.mixins.get(clazz);
    }

    byte[] getOriginalTargetBytecode(String name) {
        return this.targets.get(name);
    }

    private byte[] materialise(String name) {
        ClassWriter cw = new ClassWriter(3);
        cw.visit(MixinEnvironment.getCompatibilityLevel().classVersion(), 1, name.replace('.', '/'), null, Type.getInternalName(Object.class), null);
        MethodVisitor mv = cw.visitMethod(1, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(25, 0);
        mv.visitMethodInsn(183, Type.getInternalName(Object.class), "<init>", "()V", false);
        mv.visitInsn(177);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
        cw.visitEnd();
        return cw.toByteArray();
    }
}
