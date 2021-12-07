package org.spongepowered.tools.obfuscation.mirror;

import com.google.common.collect.ImmutableList;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

public final class AnnotationHandle {
    public static final AnnotationHandle MISSING = new AnnotationHandle(null);
    private final AnnotationMirror annotation;

    private AnnotationHandle(AnnotationMirror annotation) {
        this.annotation = annotation;
    }

    public AnnotationMirror asMirror() {
        return this.annotation;
    }

    public boolean exists() {
        return this.annotation != null;
    }

    public String toString() {
        if (this.annotation == null) {
            return "@{UnknownAnnotation}";
        }
        return "@" + this.annotation.getAnnotationType().asElement().getSimpleName();
    }

    public <T> T getValue(String key, T defaultValue) {
        if (this.annotation == null) {
            return defaultValue;
        }
        AnnotationValue value = this.getAnnotationValue(key);
        if (defaultValue instanceof Enum && value != null) {
            VariableElement varValue = (VariableElement)value.getValue();
            if (varValue == null) {
                return defaultValue;
            }
            return (T)Enum.valueOf(defaultValue.getClass(), varValue.getSimpleName().toString());
        }
        return (T)(value != null ? value.getValue() : defaultValue);
    }

    public <T> T getValue() {
        return this.getValue("value", null);
    }

    public <T> T getValue(String key) {
        return this.getValue(key, null);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return this.getValue(key, defaultValue);
    }

    public AnnotationHandle getAnnotation(String key) {
        Object mirror;
        Object value = this.getValue(key);
        if (value instanceof AnnotationMirror) {
            return AnnotationHandle.of((AnnotationMirror)value);
        }
        if (value instanceof AnnotationValue && (mirror = ((AnnotationValue)value).getValue()) instanceof AnnotationMirror) {
            return AnnotationHandle.of((AnnotationMirror)mirror);
        }
        return null;
    }

    public <T> List<T> getList() {
        return this.getList("value");
    }

    public <T> List<T> getList(String key) {
        List<AnnotationValue> list = this.getValue(key, Collections.emptyList());
        return AnnotationHandle.unwrapAnnotationValueList(list);
    }

    public List<AnnotationHandle> getAnnotationList(String key) {
        Object val = this.getValue(key, null);
        if (val == null) {
            return Collections.emptyList();
        }
        if (val instanceof AnnotationMirror) {
            return ImmutableList.of(AnnotationHandle.of(val));
        }
        List list = val;
        ArrayList<AnnotationHandle> annotations = new ArrayList<AnnotationHandle>(list.size());
        for (AnnotationValue value : list) {
            annotations.add(new AnnotationHandle((AnnotationMirror)value.getValue()));
        }
        return Collections.unmodifiableList(annotations);
    }

    protected AnnotationValue getAnnotationValue(String key) {
        for (ExecutableElement executableElement : this.annotation.getElementValues().keySet()) {
            if (!executableElement.getSimpleName().contentEquals(key)) continue;
            return this.annotation.getElementValues().get(executableElement);
        }
        return null;
    }

    protected static <T> List<T> unwrapAnnotationValueList(List<AnnotationValue> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        ArrayList<Object> unfolded = new ArrayList<Object>(list.size());
        for (AnnotationValue value : list) {
            unfolded.add(value.getValue());
        }
        return unfolded;
    }

    protected static AnnotationMirror getAnnotation(Element elem, Class<? extends Annotation> annotationClass) {
        if (elem == null) {
            return null;
        }
        List<? extends AnnotationMirror> annotations = elem.getAnnotationMirrors();
        if (annotations == null) {
            return null;
        }
        for (AnnotationMirror annotationMirror : annotations) {
            TypeElement annotationElement;
            Element element = annotationMirror.getAnnotationType().asElement();
            if (!(element instanceof TypeElement) || !(annotationElement = (TypeElement)element).getQualifiedName().contentEquals(annotationClass.getName())) continue;
            return annotationMirror;
        }
        return null;
    }

    public static AnnotationHandle of(AnnotationMirror annotation) {
        return new AnnotationHandle(annotation);
    }

    public static AnnotationHandle of(Element elem, Class<? extends Annotation> annotationClass) {
        return new AnnotationHandle(AnnotationHandle.getAnnotation(elem, annotationClass));
    }
}
