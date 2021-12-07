package org.spongepowered.tools.obfuscation;

import com.google.common.base.Strings;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.gen.AccessorInfo;
import org.spongepowered.asm.mixin.injection.struct.MemberInfo;
import org.spongepowered.asm.mixin.injection.struct.Target;
import org.spongepowered.asm.mixin.refmap.IMixinContext;
import org.spongepowered.asm.mixin.refmap.ReferenceMapper;
import org.spongepowered.asm.mixin.transformer.ext.Extensions;
import org.spongepowered.asm.obfuscation.mapping.common.MappingField;
import org.spongepowered.asm.obfuscation.mapping.common.MappingMethod;
import org.spongepowered.tools.obfuscation.AnnotatedMixin;
import org.spongepowered.tools.obfuscation.AnnotatedMixinElementHandler;
import org.spongepowered.tools.obfuscation.ObfuscationData;
import org.spongepowered.tools.obfuscation.ReferenceManager;
import org.spongepowered.tools.obfuscation.interfaces.IMixinAnnotationProcessor;
import org.spongepowered.tools.obfuscation.mirror.AnnotationHandle;
import org.spongepowered.tools.obfuscation.mirror.FieldHandle;
import org.spongepowered.tools.obfuscation.mirror.MethodHandle;
import org.spongepowered.tools.obfuscation.mirror.TypeHandle;
import org.spongepowered.tools.obfuscation.mirror.TypeUtils;

public class AnnotatedMixinElementHandlerAccessor
extends AnnotatedMixinElementHandler
implements IMixinContext {
    public AnnotatedMixinElementHandlerAccessor(IMixinAnnotationProcessor ap, AnnotatedMixin mixin) {
        super(ap, mixin);
    }

    @Override
    public ReferenceMapper getReferenceMapper() {
        return null;
    }

    @Override
    public String getClassName() {
        return this.mixin.getClassRef().replace('/', '.');
    }

    @Override
    public String getClassRef() {
        return this.mixin.getClassRef();
    }

    @Override
    public String getTargetClassRef() {
        throw new UnsupportedOperationException("Target class not available at compile time");
    }

    @Override
    public IMixinInfo getMixin() {
        throw new UnsupportedOperationException("MixinInfo not available at compile time");
    }

    @Override
    public Extensions getExtensions() {
        throw new UnsupportedOperationException("Mixin Extensions not available at compile time");
    }

    @Override
    public boolean getOption(MixinEnvironment.Option option) {
        throw new UnsupportedOperationException("Options not available at compile time");
    }

    @Override
    public int getPriority() {
        throw new UnsupportedOperationException("Priority not available at compile time");
    }

    @Override
    public Target getTargetMethod(MethodNode into) {
        throw new UnsupportedOperationException("Target not available at compile time");
    }

    public void registerAccessor(AnnotatedElementAccessor elem) {
        if (elem.getAccessorType() == null) {
            elem.printMessage(this.ap, Diagnostic.Kind.WARNING, "Unsupported accessor type");
            return;
        }
        String targetName = this.getAccessorTargetName(elem);
        if (targetName == null) {
            elem.printMessage(this.ap, Diagnostic.Kind.WARNING, "Cannot inflect accessor target name");
            return;
        }
        elem.setTargetName(targetName);
        for (TypeHandle target : this.mixin.getTargets()) {
            if (elem.getAccessorType() == AccessorInfo.AccessorType.METHOD_PROXY) {
                this.registerInvokerForTarget((AnnotatedElementInvoker)elem, target);
                continue;
            }
            this.registerAccessorForTarget(elem, target);
        }
    }

    private void registerAccessorForTarget(AnnotatedElementAccessor elem, TypeHandle target) {
        FieldHandle targetField = target.findField(elem.getTargetName(), elem.getTargetTypeName(), false);
        if (targetField == null) {
            if (!target.isImaginary()) {
                elem.printMessage(this.ap, Diagnostic.Kind.ERROR, "Could not locate @Accessor target " + elem + " in target " + target);
                return;
            }
            targetField = new FieldHandle(target.getName(), elem.getTargetName(), elem.getDesc());
        }
        if (!elem.shouldRemap()) {
            return;
        }
        ObfuscationData<MappingField> obfData = this.obf.getDataProvider().getObfField(targetField.asMapping(false).move(target.getName()));
        if (obfData.isEmpty()) {
            String info = this.mixin.isMultiTarget() ? " in target " + target : "";
            elem.printMessage(this.ap, Diagnostic.Kind.WARNING, "Unable to locate obfuscation mapping" + info + " for @Accessor target " + elem);
            return;
        }
        obfData = AnnotatedMixinElementHandler.stripOwnerData(obfData);
        try {
            this.obf.getReferenceManager().addFieldMapping(this.mixin.getClassRef(), elem.getTargetName(), elem.getContext(), obfData);
        }
        catch (ReferenceManager.ReferenceConflictException ex) {
            elem.printMessage(this.ap, Diagnostic.Kind.ERROR, "Mapping conflict for @Accessor target " + elem + ": " + ex.getNew() + " for target " + target + " conflicts with existing mapping " + ex.getOld());
        }
    }

    private void registerInvokerForTarget(AnnotatedElementInvoker elem, TypeHandle target) {
        MethodHandle targetMethod = target.findMethod(elem.getTargetName(), elem.getTargetTypeName(), false);
        if (targetMethod == null) {
            if (!target.isImaginary()) {
                elem.printMessage(this.ap, Diagnostic.Kind.ERROR, "Could not locate @Invoker target " + elem + " in target " + target);
                return;
            }
            targetMethod = new MethodHandle(target, elem.getTargetName(), elem.getDesc());
        }
        if (!elem.shouldRemap()) {
            return;
        }
        ObfuscationData<MappingMethod> obfData = this.obf.getDataProvider().getObfMethod(targetMethod.asMapping(false).move(target.getName()));
        if (obfData.isEmpty()) {
            String info = this.mixin.isMultiTarget() ? " in target " + target : "";
            elem.printMessage(this.ap, Diagnostic.Kind.WARNING, "Unable to locate obfuscation mapping" + info + " for @Accessor target " + elem);
            return;
        }
        obfData = AnnotatedMixinElementHandler.stripOwnerData(obfData);
        try {
            this.obf.getReferenceManager().addMethodMapping(this.mixin.getClassRef(), elem.getTargetName(), elem.getContext(), obfData);
        }
        catch (ReferenceManager.ReferenceConflictException ex) {
            elem.printMessage(this.ap, Diagnostic.Kind.ERROR, "Mapping conflict for @Invoker target " + elem + ": " + ex.getNew() + " for target " + target + " conflicts with existing mapping " + ex.getOld());
        }
    }

    private String getAccessorTargetName(AnnotatedElementAccessor elem) {
        String value = elem.getAnnotationValue();
        if (Strings.isNullOrEmpty(value)) {
            return this.inflectAccessorTarget(elem);
        }
        return value;
    }

    private String inflectAccessorTarget(AnnotatedElementAccessor elem) {
        return AccessorInfo.inflectTarget(elem.getSimpleName(), elem.getAccessorType(), "", this, false);
    }

    static class AnnotatedElementInvoker
    extends AnnotatedElementAccessor {
        public AnnotatedElementInvoker(ExecutableElement element, AnnotationHandle annotation, boolean shouldRemap) {
            super(element, annotation, shouldRemap);
        }

        @Override
        public String getAccessorDesc() {
            return TypeUtils.getDescriptor((ExecutableElement)this.getElement());
        }

        @Override
        public AccessorInfo.AccessorType getAccessorType() {
            return AccessorInfo.AccessorType.METHOD_PROXY;
        }

        @Override
        public String getTargetTypeName() {
            return TypeUtils.getJavaSignature(this.getElement());
        }
    }

    static class AnnotatedElementAccessor
    extends AnnotatedMixinElementHandler.AnnotatedElement<ExecutableElement> {
        private final boolean shouldRemap;
        private final TypeMirror returnType;
        private String targetName;

        public AnnotatedElementAccessor(ExecutableElement element, AnnotationHandle annotation, boolean shouldRemap) {
            super(element, annotation);
            this.shouldRemap = shouldRemap;
            this.returnType = ((ExecutableElement)this.getElement()).getReturnType();
        }

        public boolean shouldRemap() {
            return this.shouldRemap;
        }

        public String getAnnotationValue() {
            return (String)this.getAnnotation().getValue();
        }

        public TypeMirror getTargetType() {
            switch (this.getAccessorType()) {
                case FIELD_GETTER: {
                    return this.returnType;
                }
                case FIELD_SETTER: {
                    return ((ExecutableElement)this.getElement()).getParameters().get(0).asType();
                }
            }
            return null;
        }

        public String getTargetTypeName() {
            return TypeUtils.getTypeName(this.getTargetType());
        }

        public String getAccessorDesc() {
            return TypeUtils.getInternalName(this.getTargetType());
        }

        public MemberInfo getContext() {
            return new MemberInfo(this.getTargetName(), null, this.getAccessorDesc());
        }

        public AccessorInfo.AccessorType getAccessorType() {
            return this.returnType.getKind() == TypeKind.VOID ? AccessorInfo.AccessorType.FIELD_SETTER : AccessorInfo.AccessorType.FIELD_GETTER;
        }

        public void setTargetName(String targetName) {
            this.targetName = targetName;
        }

        public String getTargetName() {
            return this.targetName;
        }

        public String toString() {
            return this.targetName != null ? this.targetName : "<invalid>";
        }
    }
}
