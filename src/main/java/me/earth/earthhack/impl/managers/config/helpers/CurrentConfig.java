/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package me.earth.earthhack.impl.managers.config.helpers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import me.earth.earthhack.api.config.ConfigHelper;
import me.earth.earthhack.api.config.Jsonable;
import me.earth.earthhack.api.util.interfaces.Nameable;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.config.util.ConfigDeleteException;
import me.earth.earthhack.impl.managers.config.util.JsonPathWriter;

public class CurrentConfig
implements ConfigHelper {
    private static final CurrentConfig INSTANCE = new CurrentConfig();
    private static final String PATH = "earthhack/Configs.json";
    private final Map<ConfigHelper, String> configs = new HashMap<ConfigHelper, String>();
    private final Map<String, String> additional = new HashMap<String, String>();

    private CurrentConfig() {
    }

    public static CurrentConfig getInstance() {
        return INSTANCE;
    }

    public void set(ConfigHelper helper, String config) {
        this.configs.put(helper, config);
    }

    public String get(ConfigHelper helper) {
        return this.configs.get(helper);
    }

    public void set(String additional, String config) {
        this.additional.put(additional, config);
    }

    public String get(String additional) {
        return this.additional.get(additional);
    }

    @Override
    public void save() throws IOException {
        Path file = Paths.get(PATH, new String[0]);
        if (!Files.exists(file, new LinkOption[0])) {
            Files.createFile(file, new FileAttribute[0]);
        }
        JsonObject object = new JsonObject();
        for (Map.Entry<ConfigHelper, String> entry : this.configs.entrySet()) {
            object.add(entry.getKey().getName(), Jsonable.parse(entry.getValue()));
        }
        for (Map.Entry<Object, String> entry : this.additional.entrySet()) {
            object.add((String)entry.getKey(), Jsonable.parse(entry.getValue()));
        }
        JsonPathWriter.write(file, object);
    }

    @Override
    public void refresh() throws IOException {
        try (InputStream stream = Files.newInputStream(Paths.get(PATH, new String[0]), new OpenOption[0]);){
            JsonObject object = Jsonable.PARSER.parse((Reader)new InputStreamReader(stream)).getAsJsonObject();
            for (Map.Entry entry : object.entrySet()) {
                ConfigHelper helper = (ConfigHelper)Managers.CONFIG.getObject((String)entry.getKey());
                if (helper != null) {
                    this.set(helper, ((JsonElement)entry.getValue()).getAsString());
                    continue;
                }
                this.additional.put((String)entry.getKey(), ((JsonElement)entry.getValue()).getAsString());
            }
        }
    }

    @Override
    public void save(String name) throws IOException {
        throw new UnsupportedOperationException("CurrentConfig doesn't support multiple configs.");
    }

    @Override
    public void load(String name) {
        throw new UnsupportedOperationException("CurrentConfig doesn't support multiple configs.");
    }

    @Override
    public void refresh(String name) throws IOException {
        throw new UnsupportedOperationException("CurrentConfig doesn't support multiple configs.");
    }

    @Override
    public void delete(String name) throws ConfigDeleteException {
        throw new ConfigDeleteException("CurrentConfig doesn't support multiple configs.");
    }

    public Collection<? extends Nameable> getConfigs() {
        return this.configs.keySet();
    }

    @Override
    public String getName() {
        return "current";
    }
}

