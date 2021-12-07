package org.spongepowered.asm.service.mojang;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import net.minecraft.launchwrapper.LaunchClassLoader;

final class LaunchClassLoaderUtil {
    private static final String CACHED_CLASSES_FIELD = "cachedClasses";
    private static final String INVALID_CLASSES_FIELD = "invalidClasses";
    private static final String CLASS_LOADER_EXCEPTIONS_FIELD = "classLoaderExceptions";
    private static final String TRANSFORMER_EXCEPTIONS_FIELD = "transformerExceptions";
    private final LaunchClassLoader classLoader;
    private final Map<String, Class<?>> cachedClasses;
    private final Set<String> invalidClasses;
    private final Set<String> classLoaderExceptions;
    private final Set<String> transformerExceptions;

    LaunchClassLoaderUtil(LaunchClassLoader classLoader) {
        this.classLoader = classLoader;
        this.cachedClasses = (Map)LaunchClassLoaderUtil.getField(classLoader, CACHED_CLASSES_FIELD);
        this.invalidClasses = (Set)LaunchClassLoaderUtil.getField(classLoader, INVALID_CLASSES_FIELD);
        this.classLoaderExceptions = (Set)LaunchClassLoaderUtil.getField(classLoader, CLASS_LOADER_EXCEPTIONS_FIELD);
        this.transformerExceptions = (Set)LaunchClassLoaderUtil.getField(classLoader, TRANSFORMER_EXCEPTIONS_FIELD);
    }

    LaunchClassLoader getClassLoader() {
        return this.classLoader;
    }

    boolean isClassLoaded(String name) {
        return this.cachedClasses.containsKey(name);
    }

    boolean isClassExcluded(String name, String transformedName) {
        return this.isClassClassLoaderExcluded(name, transformedName) || this.isClassTransformerExcluded(name, transformedName);
    }

    boolean isClassClassLoaderExcluded(String name, String transformedName) {
        for (String exception : this.getClassLoaderExceptions()) {
            if ((transformedName == null || !transformedName.startsWith(exception)) && !name.startsWith(exception)) continue;
            return true;
        }
        return false;
    }

    boolean isClassTransformerExcluded(String name, String transformedName) {
        for (String exception : this.getTransformerExceptions()) {
            if ((transformedName == null || !transformedName.startsWith(exception)) && !name.startsWith(exception)) continue;
            return true;
        }
        return false;
    }

    void registerInvalidClass(String name) {
        if (this.invalidClasses != null) {
            this.invalidClasses.add(name);
        }
    }

    Set<String> getClassLoaderExceptions() {
        if (this.classLoaderExceptions != null) {
            return this.classLoaderExceptions;
        }
        return Collections.emptySet();
    }

    Set<String> getTransformerExceptions() {
        if (this.transformerExceptions != null) {
            return this.transformerExceptions;
        }
        return Collections.emptySet();
    }

    private static <T> T getField(LaunchClassLoader classLoader, String fieldName) {
        try {
            Field field = LaunchClassLoader.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T)field.get(classLoader);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
