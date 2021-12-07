package org.spongepowered.tools.obfuscation;

import java.util.Iterator;
import java.util.List;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import org.spongepowered.asm.mixin.injection.struct.MemberInfo;
import org.spongepowered.asm.obfuscation.mapping.IMapping;
import org.spongepowered.asm.obfuscation.mapping.common.MappingField;
import org.spongepowered.asm.obfuscation.mapping.common.MappingMethod;
import org.spongepowered.asm.util.ConstraintParser;
import org.spongepowered.asm.util.throwables.ConstraintViolationException;
import org.spongepowered.asm.util.throwables.InvalidConstraintException;
import org.spongepowered.tools.obfuscation.AnnotatedMixin;
import org.spongepowered.tools.obfuscation.Mappings;
import org.spongepowered.tools.obfuscation.ObfuscationData;
import org.spongepowered.tools.obfuscation.ObfuscationType;
import org.spongepowered.tools.obfuscation.interfaces.IMixinAnnotationProcessor;
import org.spongepowered.tools.obfuscation.interfaces.IObfuscationManager;
import org.spongepowered.tools.obfuscation.mapping.IMappingConsumer;
import org.spongepowered.tools.obfuscation.mirror.AnnotationHandle;
import org.spongepowered.tools.obfuscation.mirror.FieldHandle;
import org.spongepowered.tools.obfuscation.mirror.MethodHandle;
import org.spongepowered.tools.obfuscation.mirror.TypeHandle;
import org.spongepowered.tools.obfuscation.mirror.TypeUtils;
import org.spongepowered.tools.obfuscation.mirror.Visibility;

abstract class AnnotatedMixinElementHandler {
    protected final AnnotatedMixin mixin;
    protected final String classRef;
    protected final IMixinAnnotationProcessor ap;
    protected final IObfuscationManager obf;
    private IMappingConsumer mappings;

    AnnotatedMixinElementHandler(IMixinAnnotationProcessor ap, AnnotatedMixin mixin) {
        this.ap = ap;
        this.mixin = mixin;
        this.classRef = mixin.getClassRef();
        this.obf = ap.getObfuscationManager();
    }

    private IMappingConsumer getMappings() {
        if (this.mappings == null) {
            IMappingConsumer mappingConsumer = this.mixin.getMappings();
            this.mappings = mappingConsumer instanceof Mappings ? ((Mappings)mappingConsumer).asUnique() : mappingConsumer;
        }
        return this.mappings;
    }

    protected final void addFieldMappings(String mcpName, String mcpSignature, ObfuscationData<MappingField> obfData) {
        for (ObfuscationType type : obfData) {
            MappingField obfField = obfData.get(type);
            this.addFieldMapping(type, mcpName, obfField.getSimpleName(), mcpSignature, obfField.getDesc());
        }
    }

    protected final void addFieldMapping(ObfuscationType type, ShadowElementName name, String mcpSignature, String obfSignature) {
        this.addFieldMapping(type, name.name(), name.obfuscated(), mcpSignature, obfSignature);
    }

    protected final void addFieldMapping(ObfuscationType type, String mcpName, String obfName, String mcpSignature, String obfSignature) {
        MappingField from = new MappingField(this.classRef, mcpName, mcpSignature);
        MappingField to = new MappingField(this.classRef, obfName, obfSignature);
        this.getMappings().addFieldMapping(type, from, to);
    }

    protected final void addMethodMappings(String mcpName, String mcpSignature, ObfuscationData<MappingMethod> obfData) {
        for (ObfuscationType type : obfData) {
            MappingMethod obfMethod = obfData.get(type);
            this.addMethodMapping(type, mcpName, obfMethod.getSimpleName(), mcpSignature, obfMethod.getDesc());
        }
    }

    protected final void addMethodMapping(ObfuscationType type, ShadowElementName name, String mcpSignature, String obfSignature) {
        this.addMethodMapping(type, name.name(), name.obfuscated(), mcpSignature, obfSignature);
    }

    protected final void addMethodMapping(ObfuscationType type, String mcpName, String obfName, String mcpSignature, String obfSignature) {
        MappingMethod from = new MappingMethod(this.classRef, mcpName, mcpSignature);
        MappingMethod to = new MappingMethod(this.classRef, obfName, obfSignature);
        this.getMappings().addMethodMapping(type, from, to);
    }

    protected final void checkConstraints(ExecutableElement method, AnnotationHandle annotation) {
        try {
            ConstraintParser.Constraint constraint = ConstraintParser.parse((String)annotation.getValue("constraints"));
            try {
                constraint.check(this.ap.getTokenProvider());
            }
            catch (ConstraintViolationException ex) {
                this.ap.printMessage(Diagnostic.Kind.ERROR, ex.getMessage(), method, annotation.asMirror());
            }
        }
        catch (InvalidConstraintException ex) {
            this.ap.printMessage(Diagnostic.Kind.WARNING, ex.getMessage(), method, annotation.asMirror());
        }
    }

    protected final void validateTarget(Element element, AnnotationHandle annotation, AliasedElementName name, String type) {
        if (element instanceof ExecutableElement) {
            this.validateTargetMethod((ExecutableElement)element, annotation, name, type, false, false);
        } else if (element instanceof VariableElement) {
            this.validateTargetField((VariableElement)element, annotation, name, type);
        }
    }

    protected final void validateTargetMethod(ExecutableElement method, AnnotationHandle annotation, AliasedElementName name, String type, boolean overwrite, boolean merge) {
        String signature = TypeUtils.getJavaSignature(method);
        for (TypeHandle target : this.mixin.getTargets()) {
            if (target.isImaginary()) continue;
            MethodHandle targetMethod = target.findMethod(method);
            if (targetMethod == null && name.hasPrefix()) {
                targetMethod = target.findMethod(name.baseName(), signature);
            }
            if (targetMethod == null && name.hasAliases()) {
                String alias;
                Iterator<String> iterator = name.getAliases().iterator();
                while (iterator.hasNext() && (targetMethod = target.findMethod(alias = iterator.next(), signature)) == null) {
                }
            }
            if (targetMethod != null) {
                if (!overwrite) continue;
                this.validateMethodVisibility(method, annotation, type, target, targetMethod);
                continue;
            }
            if (merge) continue;
            this.printMessage(Diagnostic.Kind.WARNING, "Cannot find target for " + type + " method in " + target, method, annotation);
        }
    }

    private void validateMethodVisibility(ExecutableElement method, AnnotationHandle annotation, String type, TypeHandle target, MethodHandle targetMethod) {
        Visibility visTarget = targetMethod.getVisibility();
        if (visTarget == null) {
            return;
        }
        Visibility visMethod = TypeUtils.getVisibility(method);
        String visibility = "visibility of " + (Object)((Object)visTarget) + " method in " + target;
        if (visTarget.ordinal() > visMethod.ordinal()) {
            this.printMessage(Diagnostic.Kind.WARNING, (Object)((Object)visMethod) + " " + type + " method cannot reduce " + visibility, method, annotation);
        } else if (visTarget == Visibility.PRIVATE && visMethod.ordinal() > visTarget.ordinal()) {
            this.printMessage(Diagnostic.Kind.WARNING, (Object)((Object)visMethod) + " " + type + " method will upgrade " + visibility, method, annotation);
        }
    }

    protected final void validateTargetField(VariableElement field, AnnotationHandle annotation, AliasedElementName name, String type) {
        String fieldType = field.asType().toString();
        for (TypeHandle target : this.mixin.getTargets()) {
            String alias;
            FieldHandle targetField;
            if (target.isImaginary() || (targetField = target.findField(field)) != null) continue;
            List<String> aliases = name.getAliases();
            Iterator<String> iterator = aliases.iterator();
            while (iterator.hasNext() && (targetField = target.findField(alias = iterator.next(), fieldType)) == null) {
            }
            if (targetField != null) continue;
            this.ap.printMessage(Diagnostic.Kind.WARNING, "Cannot find target for " + type + " field in " + target, field, annotation.asMirror());
        }
    }

    protected final void validateReferencedTarget(ExecutableElement method, AnnotationHandle inject, MemberInfo reference, String type) {
        String signature = reference.toDescriptor();
        for (TypeHandle target : this.mixin.getTargets()) {
            MethodHandle targetMethod;
            if (target.isImaginary() || (targetMethod = target.findMethod(reference.name, signature)) != null) continue;
            this.ap.printMessage(Diagnostic.Kind.WARNING, "Cannot find target method for " + type + " in " + target, method, inject.asMirror());
        }
    }

    private void printMessage(Diagnostic.Kind kind, String msg, Element e, AnnotationHandle annotation) {
        if (annotation == null) {
            this.ap.printMessage(kind, msg, e);
        } else {
            this.ap.printMessage(kind, msg, e, annotation.asMirror());
        }
    }

    protected static <T extends IMapping<T>> ObfuscationData<T> stripOwnerData(ObfuscationData<T> data) {
        ObfuscationData stripped = new ObfuscationData();
        for (ObfuscationType type : data) {
            IMapping mapping = (IMapping)data.get(type);
            stripped.put(type, mapping.move(null));
        }
        return stripped;
    }

    protected static <T extends IMapping<T>> ObfuscationData<T> stripDescriptors(ObfuscationData<T> data) {
        ObfuscationData stripped = new ObfuscationData();
        for (ObfuscationType type : data) {
            IMapping mapping = (IMapping)data.get(type);
            stripped.put(type, mapping.transform(null));
        }
        return stripped;
    }

    static class ShadowElementName
    extends AliasedElementName {
        private final boolean hasPrefix;
        private final String prefix;
        private final String baseName;
        private String obfuscated;

        ShadowElementName(Element element, AnnotationHandle shadow) {
            super(element, shadow);
            this.prefix = shadow.getValue("prefix", "shadow$");
            boolean hasPrefix = false;
            String name = this.originalName;
            if (name.startsWith(this.prefix)) {
                hasPrefix = true;
                name = name.substring(this.prefix.length());
            }
            this.hasPrefix = hasPrefix;
            this.obfuscated = this.baseName = name;
        }

        public String toString() {
            return this.baseName;
        }

        @Override
        public String baseName() {
            return this.baseName;
        }

        public ShadowElementName setObfuscatedName(IMapping<?> name) {
            this.obfuscated = name.getName();
            return this;
        }

        public ShadowElementName setObfuscatedName(String name) {
            this.obfuscated = name;
            return this;
        }

        @Override
        public boolean hasPrefix() {
            return this.hasPrefix;
        }

        public String prefix() {
            return this.hasPrefix ? this.prefix : "";
        }

        public String name() {
            return this.prefix(this.baseName);
        }

        public String obfuscated() {
            return this.prefix(this.obfuscated);
        }

        public String prefix(String name) {
            return this.hasPrefix ? this.prefix + name : name;
        }
    }

    static class AliasedElementName {
        protected final String originalName;
        private final List<String> aliases;
        private boolean caseSensitive;

        public AliasedElementName(Element element, AnnotationHandle annotation) {
            this.originalName = element.getSimpleName().toString();
            this.aliases = annotation.getList("aliases");
        }

        public AliasedElementName setCaseSensitive(boolean caseSensitive) {
            this.caseSensitive = caseSensitive;
            return this;
        }

        public boolean isCaseSensitive() {
            return this.caseSensitive;
        }

        public boolean hasAliases() {
            return this.aliases.size() > 0;
        }

        public List<String> getAliases() {
            return this.aliases;
        }

        public String elementName() {
            return this.originalName;
        }

        public String baseName() {
            return this.originalName;
        }

        public boolean hasPrefix() {
            return false;
        }
    }

    static abstract class AnnotatedElement<E extends Element> {
        protected final E element;
        protected final AnnotationHandle annotation;
        private final String desc;

        public AnnotatedElement(E element, AnnotationHandle annotation) {
            this.element = element;
            this.annotation = annotation;
            this.desc = TypeUtils.getDescriptor(element);
        }

        public E getElement() {
            return this.element;
        }

        public AnnotationHandle getAnnotation() {
            return this.annotation;
        }

        public String getSimpleName() {
            return this.getElement().getSimpleName().toString();
        }

        public String getDesc() {
            return this.desc;
        }

        public final void printMessage(Messager messager, Diagnostic.Kind kind, CharSequence msg) {
            messager.printMessage(kind, msg, (Element)this.element, this.annotation.asMirror());
        }
    }
}
