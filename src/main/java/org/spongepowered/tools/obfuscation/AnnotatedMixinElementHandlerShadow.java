package org.spongepowered.tools.obfuscation;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import org.spongepowered.asm.obfuscation.mapping.IMapping;
import org.spongepowered.asm.obfuscation.mapping.common.MappingField;
import org.spongepowered.asm.obfuscation.mapping.common.MappingMethod;
import org.spongepowered.tools.obfuscation.AnnotatedMixin;
import org.spongepowered.tools.obfuscation.AnnotatedMixinElementHandler;
import org.spongepowered.tools.obfuscation.Mappings;
import org.spongepowered.tools.obfuscation.ObfuscationData;
import org.spongepowered.tools.obfuscation.ObfuscationType;
import org.spongepowered.tools.obfuscation.interfaces.IMixinAnnotationProcessor;
import org.spongepowered.tools.obfuscation.interfaces.IObfuscationDataProvider;
import org.spongepowered.tools.obfuscation.mirror.AnnotationHandle;
import org.spongepowered.tools.obfuscation.mirror.TypeHandle;

class AnnotatedMixinElementHandlerShadow
extends AnnotatedMixinElementHandler {
    AnnotatedMixinElementHandlerShadow(IMixinAnnotationProcessor ap, AnnotatedMixin mixin) {
        super(ap, mixin);
    }

    public void registerShadow(AnnotatedElementShadow<?, ?> elem) {
        this.validateTarget((Element)elem.getElement(), elem.getAnnotation(), elem.getName(), "@Shadow");
        if (!elem.shouldRemap()) {
            return;
        }
        for (TypeHandle target : this.mixin.getTargets()) {
            this.registerShadowForTarget(elem, target);
        }
    }

    private void registerShadowForTarget(AnnotatedElementShadow<?, ?> elem, TypeHandle target) {
        ObfuscationData<?> obfData = elem.getObfuscationData(this.obf.getDataProvider(), target);
        if (obfData.isEmpty()) {
            String info;
            String string = info = this.mixin.isMultiTarget() ? " in target " + target : "";
            if (target.isSimulated()) {
                elem.printMessage(this.ap, Diagnostic.Kind.WARNING, "Unable to locate obfuscation mapping" + info + " for @Shadow " + elem);
            } else {
                elem.printMessage(this.ap, Diagnostic.Kind.WARNING, "Unable to locate obfuscation mapping" + info + " for @Shadow " + elem);
            }
            return;
        }
        for (ObfuscationType type : obfData) {
            try {
                elem.addMapping(type, (IMapping)obfData.get(type));
            }
            catch (Mappings.MappingConflictException ex) {
                elem.printMessage(this.ap, Diagnostic.Kind.ERROR, "Mapping conflict for @Shadow " + elem + ": " + ex.getNew().getSimpleName() + " for target " + target + " conflicts with existing mapping " + ex.getOld().getSimpleName());
            }
        }
    }

    class AnnotatedElementShadowMethod
    extends AnnotatedElementShadow<ExecutableElement, MappingMethod> {
        public AnnotatedElementShadowMethod(ExecutableElement element, AnnotationHandle annotation, boolean shouldRemap) {
            super(element, annotation, shouldRemap, IMapping.Type.METHOD);
        }

        @Override
        public MappingMethod getMapping(TypeHandle owner, String name, String desc) {
            return owner.getMappingMethod(name, desc);
        }

        @Override
        public void addMapping(ObfuscationType type, IMapping<?> remapped) {
            AnnotatedMixinElementHandlerShadow.this.addMethodMapping(type, this.setObfuscatedName(remapped), this.getDesc(), remapped.getDesc());
        }
    }

    class AnnotatedElementShadowField
    extends AnnotatedElementShadow<VariableElement, MappingField> {
        public AnnotatedElementShadowField(VariableElement element, AnnotationHandle annotation, boolean shouldRemap) {
            super(element, annotation, shouldRemap, IMapping.Type.FIELD);
        }

        @Override
        public MappingField getMapping(TypeHandle owner, String name, String desc) {
            return new MappingField(owner.getName(), name, desc);
        }

        @Override
        public void addMapping(ObfuscationType type, IMapping<?> remapped) {
            AnnotatedMixinElementHandlerShadow.this.addFieldMapping(type, this.setObfuscatedName(remapped), this.getDesc(), remapped.getDesc());
        }
    }

    static abstract class AnnotatedElementShadow<E extends Element, M extends IMapping<M>>
    extends AnnotatedMixinElementHandler.AnnotatedElement<E> {
        private final boolean shouldRemap;
        private final AnnotatedMixinElementHandler.ShadowElementName name;
        private final IMapping.Type type;

        protected AnnotatedElementShadow(E element, AnnotationHandle annotation, boolean shouldRemap, IMapping.Type type) {
            super(element, annotation);
            this.shouldRemap = shouldRemap;
            this.name = new AnnotatedMixinElementHandler.ShadowElementName((Element)element, annotation);
            this.type = type;
        }

        public boolean shouldRemap() {
            return this.shouldRemap;
        }

        public AnnotatedMixinElementHandler.ShadowElementName getName() {
            return this.name;
        }

        public IMapping.Type getElementType() {
            return this.type;
        }

        public String toString() {
            return this.getElementType().name().toLowerCase();
        }

        public AnnotatedMixinElementHandler.ShadowElementName setObfuscatedName(IMapping<?> name) {
            return this.setObfuscatedName(name.getSimpleName());
        }

        public AnnotatedMixinElementHandler.ShadowElementName setObfuscatedName(String name) {
            return this.getName().setObfuscatedName(name);
        }

        public ObfuscationData<M> getObfuscationData(IObfuscationDataProvider provider, TypeHandle owner) {
            return provider.getObfEntry(this.getMapping(owner, this.getName().toString(), this.getDesc()));
        }

        public abstract M getMapping(TypeHandle var1, String var2, String var3);

        public abstract void addMapping(ObfuscationType var1, IMapping<?> var2);
    }
}
