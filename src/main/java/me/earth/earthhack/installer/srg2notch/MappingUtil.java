/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.objectweb.asm.Handle
 *  org.objectweb.asm.Type
 */
package me.earth.earthhack.installer.srg2notch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.earth.earthhack.impl.util.misc.collections.ArrayUtil;
import me.earth.earthhack.installer.srg2notch.Mapping;
import me.earth.earthhack.installer.srg2notch.MethodMapping;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Type;

public class MappingUtil {
    public static String map(String owner, String name, String desc, Mapping mapping) {
        String methodMapping = MappingUtil.getMethodMapping(owner, name, desc, mapping);
        return methodMapping == null ? name : methodMapping;
    }

    private static String getMethodMapping(String owner, String name, String desc, Mapping mapping) {
        List<MethodMapping> mappings = mapping.getMethods().get(name);
        if (mappings == null) {
            return null;
        }
        if (mappings.size() == 1) {
            return mappings.get(0).getName();
        }
        double bestFactor = 0.0;
        MethodMapping best = null;
        for (MethodMapping m : mappings) {
            double matchFactor = 0.0;
            if (m.getDesc().equals(desc)) {
                matchFactor += 1.0;
            }
            if (m.getOwner().equals(owner)) {
                matchFactor += 1.0;
            }
            if (name.contains("_" + m.getName())) {
                matchFactor += 0.5;
            }
            if (best != null && !(matchFactor > bestFactor)) continue;
            bestFactor = matchFactor;
            best = m;
        }
        return best == null ? null : best.getName();
    }

    public static List<Object> map(List<Object> objects, Mapping mapping) {
        if (objects == null) {
            return null;
        }
        ArrayList<Object> local = new ArrayList<Object>(objects.size());
        for (Object o : objects) {
            if (o instanceof String) {
                String s = (String)o;
                o = s.startsWith("[") ? MappingUtil.mapDescription(s, mapping) : mapping.getClasses().getOrDefault(s, s);
            }
            local.add(o);
        }
        return local;
    }

    public static Handle map(Handle h, Mapping mapping) {
        String desc;
        String name = h.getName();
        String owner = h.getOwner();
        name = MappingUtil.getMethodMapping(owner, name, desc = h.getDesc(), mapping);
        if (name == null) {
            name = mapping.getFields().getOrDefault(h.getName(), h.getName());
        }
        owner = mapping.getClasses().getOrDefault(owner, owner);
        desc = MappingUtil.mapDescription(desc, mapping);
        return new Handle(h.getTag(), owner, name, desc, h.isInterface());
    }

    public static Type map(Type type, Mapping mapping) {
        return Type.getType((String)MappingUtil.mapDescription(type.getDescriptor(), mapping));
    }

    public static String[] splitField(String field) {
        int i = field.lastIndexOf("/");
        String owner = field.substring(0, i);
        String name = field.substring(i + 1);
        return new String[]{owner, name};
    }

    public static String[] splitMethod(String method) {
        String[] split = method.split("(\\()");
        int i = split[0].lastIndexOf("/");
        String owner = split[0].substring(0, i);
        String name = split[0].substring(i + 1);
        String desc = "(" + split[1];
        return new String[]{owner, name, desc};
    }

    public static String mapDescription(String desc, Mapping mapping) {
        Set<String> classes = MappingUtil.matchClasses(desc, ';');
        return MappingUtil.map(desc, mapping, classes);
    }

    public static String mapSignature(String signature, Mapping mapping) {
        Set<String> classes = MappingUtil.matchClasses(signature, '<', ';');
        return MappingUtil.map(signature, mapping, classes);
    }

    private static Set<String> matchClasses(String s, char ... separators) {
        boolean collect = false;
        HashSet<String> matched = new HashSet<String>();
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (collect && ArrayUtil.contains(c, separators)) {
                current.append(c);
                matched.add(current.toString());
                current = new StringBuilder();
                collect = false;
            } else if (c == 'L' && !collect) {
                collect = true;
                continue;
            }
            if (!collect) continue;
            current.append(c);
        }
        return matched;
    }

    private static String map(String s, Mapping mapping, Set<String> classes) {
        String result = s;
        for (String name : classes) {
            String clazz = name.substring(0, name.length() - 1);
            String separator = name.substring(name.length() - 1);
            String replace = mapping.getClasses().get(clazz);
            if (replace == null) continue;
            result = result.replace(name, replace + separator);
        }
        return result;
    }
}

