/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.installer.srg2notch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import me.earth.earthhack.installer.srg2notch.MappingUtil;
import me.earth.earthhack.installer.srg2notch.MethodMapping;

public class Mapping {
    private final Map<String, String> classes;
    private final Map<String, String> fields;
    private final Map<String, List<MethodMapping>> methods;
    private final Map<String, String> constants;

    public Mapping(Map<String, String> classes, Map<String, String> fields, Map<String, List<MethodMapping>> methods, Map<String, String> constants) {
        this.classes = classes;
        this.fields = fields;
        this.methods = methods;
        this.constants = constants;
    }

    public Map<String, String> getClasses() {
        return this.classes;
    }

    public Map<String, String> getFields() {
        return this.fields;
    }

    public Map<String, List<MethodMapping>> getMethods() {
        return this.methods;
    }

    public Map<String, String> getConstants() {
        return this.constants;
    }

    public static Mapping fromResource(String name) throws IOException {
        HashMap<String, List<MethodMapping>> methods = new HashMap<String, List<MethodMapping>>();
        HashMap<String, String> classes = new HashMap<String, String>();
        HashMap<String, String> fields = new HashMap<String, String>();
        HashMap<String, String> constants = new HashMap<String, String>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Mapping.class.getClassLoader().getResourceAsStream(name))));){
            String line;
            while ((line = br.readLine()) != null) {
                String[] mapping = line.split(",");
                switch (mapping[0]) {
                    case "class": {
                        classes.put(mapping[2], mapping[1]);
                        break;
                    }
                    case "field": {
                        if (!mapping[2].startsWith("field")) {
                            String[] notch = MappingUtil.splitField(mapping[1]);
                            String ownerConstant = notch[0] + "/" + mapping[2];
                            constants.put(ownerConstant, notch[1]);
                            break;
                        }
                        String fn = MappingUtil.splitField(mapping[1])[1];
                        fields.put(mapping[2], fn);
                        break;
                    }
                    case "func": {
                        String[] mnn = MappingUtil.splitMethod(mapping[1]);
                        String[] mns = MappingUtil.splitMethod(mapping[2]);
                        if (!mns[1].startsWith("func")) break;
                        methods.computeIfAbsent(mns[1], v -> new ArrayList(1)).add(new MethodMapping(mnn[0], mnn[1], mnn[2]));
                        break;
                    }
                }
            }
        }
        return new Mapping(classes, fields, methods, constants);
    }
}

