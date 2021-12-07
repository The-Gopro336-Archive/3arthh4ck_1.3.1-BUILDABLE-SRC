package me.earth.earthhack.impl.util.mcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import me.earth.earthhack.impl.util.misc.ReflectionUtil;
import me.earth.earthhack.vanilla.Environment;

public class MappingProvider {
    private static final Map<String, String> CLASS_MAPPINGS;
    private static final Map<String, String> FIELD_MAPPINGS;
    private static final boolean VANILLA;

    public static String field(Class<?> clazz, String fieldName) {
        if (VANILLA) {
            return FIELD_MAPPINGS.get(clazz.getName() + "." + fieldName);
        }
        return FIELD_MAPPINGS.get(fieldName);
    }

    public static String simpleName(Class<?> clazz) {
        if (VANILLA) {
            String name = MappingProvider.className(clazz);
            if (name == null) {
                return clazz.getSimpleName();
            }
            return ReflectionUtil.getSimpleName(name);
        }
        return clazz.getSimpleName();
    }

    public static String className(Class<?> clazz) {
        if (VANILLA) {
            return CLASS_MAPPINGS.get(clazz.getName());
        }
        return clazz.getName();
    }

    static {
        VANILLA = Environment.getEnvironment() == Environment.VANILLA;
        int vs = 4458;
        CLASS_MAPPINGS = VANILLA ? new HashMap(vs) : Collections.emptyMap();
        int fs = 15886;
        FIELD_MAPPINGS = new HashMap<String, String>(fs);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(MappingProvider.class.getClassLoader().getResourceAsStream("mappings/mappings.csv"))));){
            String line;
            while ((line = br.readLine()) != null) {
                String[] mapping = line.replace("/", ".").split(",");
                if (mapping.length <= 0) continue;
                if (VANILLA && mapping[0].equals("class")) {
                    CLASS_MAPPINGS.put(mapping[1], mapping[2]);
                    continue;
                }
                if (!mapping[0].equals("field")) continue;
                if (VANILLA) {
                    FIELD_MAPPINGS.put(mapping[1], mapping[3]);
                    continue;
                }
                FIELD_MAPPINGS.put(mapping[2], mapping[3]);
            }
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
