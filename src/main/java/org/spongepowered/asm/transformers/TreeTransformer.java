package org.spongepowered.asm.transformers;

import org.spongepowered.asm.lib.ClassReader;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.service.ILegacyClassTransformer;
import org.spongepowered.asm.transformers.MixinClassWriter;

public abstract class TreeTransformer
implements ILegacyClassTransformer {
    private ClassReader classReader;
    private ClassNode classNode;

    protected final ClassNode readClass(byte[] basicClass) {
        return this.readClass(basicClass, true);
    }

    protected final ClassNode readClass(byte[] basicClass, boolean cacheReader) {
        ClassReader classReader = new ClassReader(basicClass);
        if (cacheReader) {
            this.classReader = classReader;
        }
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 8);
        return classNode;
    }

    protected final byte[] writeClass(ClassNode classNode) {
        if (this.classReader != null && this.classNode == classNode) {
            this.classNode = null;
            MixinClassWriter writer = new MixinClassWriter(this.classReader, 3);
            this.classReader = null;
            classNode.accept(writer);
            return writer.toByteArray();
        }
        this.classNode = null;
        MixinClassWriter writer = new MixinClassWriter(3);
        classNode.accept(writer);
        return writer.toByteArray();
    }
}
