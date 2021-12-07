/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.util.misc;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.function.Consumer;

public class ReflectionUtil {
    public static void addToClassPath(URLClassLoader classLoader, File file) throws Exception {
        URL url = file.toURI().toURL();
        ReflectionUtil.addToClassPath(classLoader, url);
    }

    public static void addToClassPath(URLClassLoader classLoader, URL url) throws Exception {
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(classLoader, url);
    }

    public static void iterateSuperClasses(Class<?> clazz, Consumer<Class<?>> consumer) {
        while (clazz != Object.class) {
            consumer.accept(clazz);
            clazz = clazz.getSuperclass();
        }
    }

    public static <T> T getField(Class<?> clazz, Object instance, int index) {
        try {
            Field field = clazz.getDeclaredFields()[index];
            field.setAccessible(true);
            return (T)field.get(instance);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void setField(Class<?> clazz, Object instance, int index, Object value) {
        try {
            Field field = clazz.getDeclaredFields()[index];
            field.setAccessible(true);
            field.set(instance, value);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Field getField(Class<?> clazz, String ... mappings) throws NoSuchFieldException {
        for (String s : mappings) {
            try {
                return clazz.getDeclaredField(s);
            }
            catch (NoSuchFieldException noSuchFieldException) {
            }
        }
        throw new NoSuchFieldException("No Such field: " + clazz.getName() + "-> " + Arrays.toString(mappings));
    }

    public static Method getMethodNoParameters(Class<?> clazz, String ... mappings) {
        for (String s : mappings) {
            try {
                return clazz.getDeclaredMethod(s, new Class[0]);
            }
            catch (NoSuchMethodException noSuchMethodException) {
            }
        }
        throw new RuntimeException("Couldn't find: " + Arrays.toString(mappings));
    }

    public static Method getMethod(Class<?> clazz, String notch, String searge, String mcp, Class<?> ... parameterTypes) {
        try {
            return clazz.getMethod(searge, parameterTypes);
        }
        catch (NoSuchMethodException e) {
            try {
                return clazz.getMethod(notch, parameterTypes);
            }
            catch (NoSuchMethodException ex) {
                try {
                    return clazz.getMethod(mcp, parameterTypes);
                }
                catch (NoSuchMethodException exc) {
                    throw new RuntimeException(exc);
                }
            }
        }
    }

    public static String getSimpleName(String name) {
        return name.substring(name.lastIndexOf(".") + 1);
    }
}

