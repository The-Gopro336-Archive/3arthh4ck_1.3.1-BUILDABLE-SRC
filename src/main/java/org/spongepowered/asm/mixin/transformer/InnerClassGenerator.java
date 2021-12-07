package org.spongepowered.asm.mixin.transformer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.lib.AnnotationVisitor;
import org.spongepowered.asm.lib.ClassReader;
import org.spongepowered.asm.lib.ClassVisitor;
import org.spongepowered.asm.lib.commons.ClassRemapper;
import org.spongepowered.asm.lib.commons.Remapper;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.ClassInfo;
import org.spongepowered.asm.mixin.transformer.MixinInfo;
import org.spongepowered.asm.mixin.transformer.MixinTargetContext;
import org.spongepowered.asm.mixin.transformer.ext.IClassGenerator;
import org.spongepowered.asm.mixin.transformer.throwables.InvalidMixinException;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.transformers.MixinClassWriter;

final class InnerClassGenerator
implements IClassGenerator {
    private static final Logger logger = LogManager.getLogger("mixin");
    private final Map<String, String> innerClassNames = new HashMap<String, String>();
    private final Map<String, InnerClassInfo> innerClasses = new HashMap<String, InnerClassInfo>();

    InnerClassGenerator() {
    }

    public String registerInnerClass(MixinInfo owner, String originalName, MixinTargetContext context) {
        String id = String.format("%s%s", originalName, context);
        String ref = this.innerClassNames.get(id);
        if (ref == null) {
            ref = InnerClassGenerator.getUniqueReference(originalName, context);
            this.innerClassNames.put(id, ref);
            this.innerClasses.put(ref, new InnerClassInfo(ref, originalName, owner, context));
            logger.debug("Inner class {} in {} on {} gets unique name {}", new Object[]{originalName, owner.getClassRef(), context.getTargetClassRef(), ref});
        }
        return ref;
    }

    @Override
    public byte[] generate(String name) {
        String ref = name.replace('.', '/');
        InnerClassInfo info = this.innerClasses.get(ref);
        if (info != null) {
            return this.generate(info);
        }
        return null;
    }

    private byte[] generate(InnerClassInfo info) {
        try {
            logger.debug("Generating mapped inner class {} (originally {})", new Object[]{info.getName(), info.getOriginalName()});
            ClassReader cr = new ClassReader(info.getClassBytes());
            MixinClassWriter cw = new MixinClassWriter(cr, 0);
            cr.accept(new InnerClassAdapter((ClassVisitor)cw, info), 8);
            return cw.toByteArray();
        }
        catch (InvalidMixinException ex) {
            throw ex;
        }
        catch (Exception ex) {
            logger.catching(ex);
            return null;
        }
    }

    private static String getUniqueReference(String originalName, MixinTargetContext context) {
        String name = originalName.substring(originalName.lastIndexOf(36) + 1);
        if (name.matches("^[0-9]+$")) {
            name = "Anonymous";
        }
        return String.format("%s$%s$%s", context.getTargetClassRef(), name, UUID.randomUUID().toString().replace("-", ""));
    }

    static class InnerClassAdapter
    extends ClassRemapper {
        private final InnerClassInfo info;

        public InnerClassAdapter(ClassVisitor cv, InnerClassInfo info) {
            super(327680, cv, info);
            this.info = info;
        }

        @Override
        public void visitSource(String source, String debug) {
            super.visitSource(source, debug);
            AnnotationVisitor av = this.cv.visitAnnotation("Lorg/spongepowered/asm/mixin/transformer/meta/MixinInner;", false);
            av.visit("mixin", this.info.getOwner().toString());
            av.visit("name", this.info.getOriginalName().substring(this.info.getOriginalName().lastIndexOf(47) + 1));
            av.visitEnd();
        }

        @Override
        public void visitInnerClass(String name, String outerName, String innerName, int access) {
            if (name.startsWith(this.info.getOriginalName() + "$")) {
                throw new InvalidMixinException((IMixinInfo)this.info.getOwner(), "Found unsupported nested inner class " + name + " in " + this.info.getOriginalName());
            }
            super.visitInnerClass(name, outerName, innerName, access);
        }
    }

    static class InnerClassInfo
    extends Remapper {
        private final String name;
        private final String originalName;
        private final MixinInfo owner;
        private final MixinTargetContext target;
        private final String ownerName;
        private final String targetName;

        InnerClassInfo(String name, String originalName, MixinInfo owner, MixinTargetContext target) {
            this.name = name;
            this.originalName = originalName;
            this.owner = owner;
            this.ownerName = owner.getClassRef();
            this.target = target;
            this.targetName = target.getTargetClassRef();
        }

        String getName() {
            return this.name;
        }

        String getOriginalName() {
            return this.originalName;
        }

        MixinInfo getOwner() {
            return this.owner;
        }

        MixinTargetContext getTarget() {
            return this.target;
        }

        String getOwnerName() {
            return this.ownerName;
        }

        String getTargetName() {
            return this.targetName;
        }

        byte[] getClassBytes() throws ClassNotFoundException, IOException {
            return MixinService.getService().getBytecodeProvider().getClassBytes(this.originalName, true);
        }

        @Override
        public String mapMethodName(String owner, String name, String desc) {
            ClassInfo.Method method;
            if (this.ownerName.equalsIgnoreCase(owner) && (method = this.owner.getClassInfo().findMethod(name, desc, 10)) != null) {
                return method.getName();
            }
            return super.mapMethodName(owner, name, desc);
        }

        @Override
        public String map(String key) {
            if (this.originalName.equals(key)) {
                return this.name;
            }
            if (this.ownerName.equals(key)) {
                return this.targetName;
            }
            return key;
        }

        public String toString() {
            return this.name;
        }
    }
}
