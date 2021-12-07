package org.spongepowered.tools.obfuscation.mirror;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import org.spongepowered.asm.util.SignaturePrinter;
import org.spongepowered.tools.obfuscation.mirror.Visibility;

public abstract class TypeUtils {
    private static final int MAX_GENERIC_RECURSION_DEPTH = 5;
    private static final String OBJECT_SIG = "java.lang.Object";
    private static final String OBJECT_REF = "java/lang/Object";

    private TypeUtils() {
    }

    public static PackageElement getPackage(TypeMirror type) {
        if (!(type instanceof DeclaredType)) {
            return null;
        }
        return TypeUtils.getPackage((TypeElement)((DeclaredType)type).asElement());
    }

    public static PackageElement getPackage(TypeElement type) {
        Element parent;
        for (parent = type.getEnclosingElement(); parent != null && !(parent instanceof PackageElement); parent = parent.getEnclosingElement()) {
        }
        return (PackageElement)parent;
    }

    public static String getElementType(Element element) {
        if (element instanceof TypeElement) {
            return "TypeElement";
        }
        if (element instanceof ExecutableElement) {
            return "ExecutableElement";
        }
        if (element instanceof VariableElement) {
            return "VariableElement";
        }
        if (element instanceof PackageElement) {
            return "PackageElement";
        }
        if (element instanceof TypeParameterElement) {
            return "TypeParameterElement";
        }
        return element.getClass().getSimpleName();
    }

    public static String stripGenerics(String type) {
        StringBuilder sb = new StringBuilder();
        int depth = 0;
        for (int pos = 0; pos < type.length(); ++pos) {
            char c = type.charAt(pos);
            if (c == '<') {
                ++depth;
            }
            if (depth == 0) {
                sb.append(c);
                continue;
            }
            if (c != '>') continue;
            --depth;
        }
        return sb.toString();
    }

    public static String getName(VariableElement field) {
        return field != null ? field.getSimpleName().toString() : null;
    }

    public static String getName(ExecutableElement method) {
        return method != null ? method.getSimpleName().toString() : null;
    }

    public static String getJavaSignature(Element element) {
        if (element instanceof ExecutableElement) {
            ExecutableElement method = (ExecutableElement)element;
            StringBuilder desc = new StringBuilder().append("(");
            boolean extra = false;
            for (VariableElement variableElement : method.getParameters()) {
                if (extra) {
                    desc.append(',');
                }
                desc.append(TypeUtils.getTypeName(variableElement.asType()));
                extra = true;
            }
            desc.append(')').append(TypeUtils.getTypeName(method.getReturnType()));
            return desc.toString();
        }
        return TypeUtils.getTypeName(element.asType());
    }

    public static String getJavaSignature(String descriptor) {
        return new SignaturePrinter("", descriptor).setFullyQualified(true).toDescriptor();
    }

    public static String getTypeName(TypeMirror type) {
        switch (type.getKind()) {
            case ARRAY: {
                return TypeUtils.getTypeName(((ArrayType)type).getComponentType()) + "[]";
            }
            case DECLARED: {
                return TypeUtils.getTypeName((DeclaredType)type);
            }
            case TYPEVAR: {
                return TypeUtils.getTypeName(TypeUtils.getUpperBound(type));
            }
            case ERROR: {
                return OBJECT_SIG;
            }
        }
        return type.toString();
    }

    public static String getTypeName(DeclaredType type) {
        if (type == null) {
            return OBJECT_SIG;
        }
        return TypeUtils.getInternalName((TypeElement)type.asElement()).replace('/', '.');
    }

    public static String getDescriptor(Element element) {
        if (element instanceof ExecutableElement) {
            return TypeUtils.getDescriptor((ExecutableElement)element);
        }
        if (element instanceof VariableElement) {
            return TypeUtils.getInternalName((VariableElement)element);
        }
        return TypeUtils.getInternalName(element.asType());
    }

    public static String getDescriptor(ExecutableElement method) {
        if (method == null) {
            return null;
        }
        StringBuilder signature = new StringBuilder();
        for (VariableElement variableElement : method.getParameters()) {
            signature.append(TypeUtils.getInternalName(variableElement));
        }
        String returnType = TypeUtils.getInternalName(method.getReturnType());
        return String.format("(%s)%s", signature, returnType);
    }

    public static String getInternalName(VariableElement field) {
        if (field == null) {
            return null;
        }
        return TypeUtils.getInternalName(field.asType());
    }

    public static String getInternalName(TypeMirror type) {
        switch (type.getKind()) {
            case ARRAY: {
                return "[" + TypeUtils.getInternalName(((ArrayType)type).getComponentType());
            }
            case DECLARED: {
                return "L" + TypeUtils.getInternalName((DeclaredType)type) + ";";
            }
            case TYPEVAR: {
                return "L" + TypeUtils.getInternalName(TypeUtils.getUpperBound(type)) + ";";
            }
            case BOOLEAN: {
                return "Z";
            }
            case BYTE: {
                return "B";
            }
            case CHAR: {
                return "C";
            }
            case DOUBLE: {
                return "D";
            }
            case FLOAT: {
                return "F";
            }
            case INT: {
                return "I";
            }
            case LONG: {
                return "J";
            }
            case SHORT: {
                return "S";
            }
            case VOID: {
                return "V";
            }
            case ERROR: {
                return "Ljava/lang/Object;";
            }
        }
        throw new IllegalArgumentException("Unable to parse type symbol " + type + " with " + (Object)((Object)type.getKind()) + " to equivalent bytecode type");
    }

    public static String getInternalName(DeclaredType type) {
        if (type == null) {
            return OBJECT_REF;
        }
        return TypeUtils.getInternalName((TypeElement)type.asElement());
    }

    public static String getInternalName(TypeElement element) {
        if (element == null) {
            return null;
        }
        StringBuilder reference = new StringBuilder();
        reference.append(element.getSimpleName());
        for (Element parent = element.getEnclosingElement(); parent != null; parent = parent.getEnclosingElement()) {
            if (parent instanceof TypeElement) {
                reference.insert(0, "$").insert(0, parent.getSimpleName());
                continue;
            }
            if (!(parent instanceof PackageElement)) continue;
            reference.insert(0, "/").insert(0, ((PackageElement)parent).getQualifiedName().toString().replace('.', '/'));
        }
        return reference.toString();
    }

    private static DeclaredType getUpperBound(TypeMirror type) {
        try {
            return TypeUtils.getUpperBound0(type, 5);
        }
        catch (IllegalStateException ex) {
            throw new IllegalArgumentException("Type symbol \"" + type + "\" is too complex", ex);
        }
        catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unable to compute upper bound of type symbol " + type, ex);
        }
    }

    private static DeclaredType getUpperBound0(TypeMirror type, int depth) {
        if (depth == 0) {
            throw new IllegalStateException("Generic symbol \"" + type + "\" is too complex, exceeded " + 5 + " iterations attempting to determine upper bound");
        }
        if (type instanceof DeclaredType) {
            return (DeclaredType)type;
        }
        if (type instanceof TypeVariable) {
            try {
                TypeMirror upper = ((TypeVariable)type).getUpperBound();
                return TypeUtils.getUpperBound0(upper, --depth);
            }
            catch (IllegalStateException ex) {
                throw ex;
            }
            catch (IllegalArgumentException ex) {
                throw ex;
            }
            catch (Exception ex) {
                throw new IllegalArgumentException("Unable to compute upper bound of type symbol " + type);
            }
        }
        return null;
    }

    public static boolean isAssignable(ProcessingEnvironment processingEnv, TypeMirror targetType, TypeMirror superClass) {
        boolean assignable = processingEnv.getTypeUtils().isAssignable(targetType, superClass);
        if (!assignable && targetType instanceof DeclaredType && superClass instanceof DeclaredType) {
            TypeMirror rawTargetType = TypeUtils.toRawType(processingEnv, (DeclaredType)targetType);
            TypeMirror rawSuperType = TypeUtils.toRawType(processingEnv, (DeclaredType)superClass);
            return processingEnv.getTypeUtils().isAssignable(rawTargetType, rawSuperType);
        }
        return assignable;
    }

    private static TypeMirror toRawType(ProcessingEnvironment processingEnv, DeclaredType targetType) {
        return processingEnv.getElementUtils().getTypeElement(((TypeElement)targetType.asElement()).getQualifiedName()).asType();
    }

    public static Visibility getVisibility(Element element) {
        if (element == null) {
            return null;
        }
        for (Modifier modifier : element.getModifiers()) {
            switch (modifier) {
                case PUBLIC: {
                    return Visibility.PUBLIC;
                }
                case PROTECTED: {
                    return Visibility.PROTECTED;
                }
                case PRIVATE: {
                    return Visibility.PRIVATE;
                }
            }
        }
        return Visibility.PACKAGE;
    }
}
