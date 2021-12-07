package org.spongepowered.asm.lib.commons;

import java.util.Collections;
import java.util.Map;
import org.spongepowered.asm.lib.commons.Remapper;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SimpleRemapper
extends Remapper {
    private final Map<String, String> mapping;

    public SimpleRemapper(Map<String, String> mapping) {
        this.mapping = mapping;
    }

    public SimpleRemapper(String oldName, String newName) {
        this.mapping = Collections.singletonMap(oldName, newName);
    }

    @Override
    public String mapMethodName(String owner, String name, String desc) {
        String s = this.map(owner + '.' + name + desc);
        return s == null ? name : s;
    }

    @Override
    public String mapInvokeDynamicMethodName(String name, String desc) {
        String s = this.map('.' + name + desc);
        return s == null ? name : s;
    }

    @Override
    public String mapFieldName(String owner, String name, String desc) {
        String s = this.map(owner + '.' + name);
        return s == null ? name : s;
    }

    @Override
    public String map(String key) {
        return this.mapping.get(key);
    }
}
