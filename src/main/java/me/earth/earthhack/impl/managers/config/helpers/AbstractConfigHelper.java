/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 */
package me.earth.earthhack.impl.managers.config.helpers;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import me.earth.earthhack.api.config.Config;
import me.earth.earthhack.api.config.ConfigHelper;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.managers.config.helpers.CurrentConfig;
import me.earth.earthhack.impl.managers.config.util.ConfigDeleteException;
import me.earth.earthhack.impl.managers.config.util.JsonPathWriter;
import me.earth.earthhack.impl.util.misc.FileUtil;

public abstract class AbstractConfigHelper<C extends Config>
implements ConfigHelper<C> {
    protected final Map<String, C> configs = new HashMap<String, C>();
    protected final String name;
    protected final String path;

    public AbstractConfigHelper(String name, String path) {
        this.name = name;
        this.path = "earthhack/" + path;
    }

    protected abstract C create(String var1);

    protected abstract JsonObject toJson(C var1);

    protected abstract C readFile(InputStream var1, String var2) throws IOException;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void save() throws IOException {
        this.ensureDir(this.path);
        if (this.registerDefaultIfNotPresent()) {
            C config = this.create("default");
            this.configs.put("default", config);
        }
        String current = CurrentConfig.getInstance().get(this);
        try {
            for (String s : this.configs.keySet()) {
                if (s.equalsIgnoreCase(current)) {
                    this.ensureDir(this.path);
                    C config = this.create(s);
                    this.configs.put(s, config);
                    JsonObject object = this.toJson(config);
                    JsonPathWriter.write(Paths.get(this.path + "/" + s + ".json", new String[0]), object);
                    continue;
                }
                this.save(s);
            }
        }
        finally {
            CurrentConfig.getInstance().set(this, current);
        }
    }

    @Override
    public void refresh() throws IOException {
        this.ensureDir(this.path);
        HashMap configMap = new HashMap();
        Files.walk(Paths.get(this.path, new String[0]), new FileVisitOption[0]).forEach(p -> {
            if (p.getFileName().toString().endsWith(".json")) {
                try {
                    Earthhack.getLogger().info(this.getName() + " config found : " + p);
                    C config = this.read((Path)p);
                    configMap.put(config.getName().toLowerCase(), config);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        this.configs.clear();
        this.configs.putAll(configMap);
    }

    @Override
    public void save(String name) throws IOException {
        name = name.toLowerCase();
        this.ensureDir(this.path);
        Config config = (Config)this.configs.get(name);
        if (config == null || name.equalsIgnoreCase(CurrentConfig.getInstance().get(this))) {
            config = this.create(name);
            this.configs.put(name, config);
        }
        JsonObject object = this.toJson(config);
        JsonPathWriter.write(Paths.get(this.path + "/" + name + ".json", new String[0]), object);
        CurrentConfig.getInstance().set(this, name);
    }

    @Override
    public void load(String name) {
        Config c = (Config)this.configs.get(name = name.toLowerCase());
        if (c != null) {
            c.apply();
            CurrentConfig.getInstance().set(this, name);
        }
    }

    @Override
    public void refresh(String name) throws IOException {
        this.ensureDir(this.path);
        name = name.toLowerCase();
        Path path = Paths.get(name, new String[0]);
        C config = this.read(path);
        this.configs.put(name, config);
    }

    @Override
    public void delete(String name) throws ConfigDeleteException, IOException {
        if ("default".equalsIgnoreCase(name = name.toLowerCase())) {
            throw new ConfigDeleteException("Can't delete the Default config!");
        }
        if (name.equalsIgnoreCase(CurrentConfig.getInstance().get(this))) {
            throw new ConfigDeleteException("This config is currently active. Please switch to another config before deleting this.");
        }
        this.configs.remove(name);
        Path deletePath = Paths.get(this.path + "/" + name + ".json", new String[0]);
        Files.delete(deletePath);
    }

    @Override
    public Collection<C> getConfigs() {
        return this.configs.values();
    }

    @Override
    public String getName() {
        return this.name;
    }

    protected C read(Path path) throws IOException {
        String name = path.getFileName().toString();
        try (InputStream stream = Files.newInputStream(path, new OpenOption[0]);){
            C c = this.readFile(stream, name.substring(0, name.length() - 5));
            return c;
        }
    }

    protected boolean registerDefaultIfNotPresent() {
        String current = CurrentConfig.getInstance().get(this);
        if (current == null || current.equals("default")) {
            CurrentConfig.getInstance().set(this, "default");
            return true;
        }
        return false;
    }

    protected void ensureDir(String path) {
        FileUtil.createDirectory(Paths.get(path, new String[0]));
    }
}

