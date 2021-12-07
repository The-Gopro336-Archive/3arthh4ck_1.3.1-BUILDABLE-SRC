package me.earth.earthhack.impl.managers.client;

import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import me.earth.earthhack.api.config.Jsonable;
import me.earth.earthhack.api.plugin.Plugin;
import me.earth.earthhack.api.plugin.PluginConfig;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.core.Core;
import me.earth.earthhack.impl.managers.client.PluginRemapper;
import me.earth.earthhack.impl.managers.client.exception.BadPluginException;
import me.earth.earthhack.impl.util.misc.ReflectionUtil;
import me.earth.earthhack.vanilla.Environment;

public class PluginManager {
    private static final PluginManager INSTANCE = new PluginManager();
    private static final String PATH = "earthhack/plugins";
    private final Map<PluginConfig, Plugin> plugins = new HashMap<PluginConfig, Plugin>();
    private final Map<String, PluginConfig> configs = new HashMap<String, PluginConfig>();
    private final PluginRemapper remapper = new PluginRemapper();
    private ClassLoader classLoader;

    private PluginManager() {
    }

    public static PluginManager getInstance() {
        return INSTANCE;
    }

    public void createPluginConfigs(ClassLoader pluginClassLoader) {
        if (!(pluginClassLoader instanceof URLClassLoader)) {
            throw new IllegalArgumentException("PluginClassLoader was not an URLClassLoader, but: " + pluginClassLoader.getClass().getName());
        }
        this.classLoader = pluginClassLoader;
        Core.LOGGER.info("PluginManager: Scanning for PluginConfigs.");
        File d = new File(PATH);
        Map<String, File> remap = this.scanPlugins(d.listFiles(), pluginClassLoader);
        remap.keySet().removeAll(this.configs.keySet());
        try {
            File[] remappedPlugins = this.remapper.remap(remap.values());
            this.scanPlugins(remappedPlugins, pluginClassLoader);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, File> scanPlugins(File[] files, ClassLoader pluginClassLoader) {
        HashMap<String, File> remap = new HashMap<String, File>();
        try {
            for (File file : Objects.requireNonNull(files)) {
                if (!file.getName().endsWith(".jar")) continue;
                Core.LOGGER.info("PluginManager: Scanning " + file.getName());
                try {
                    this.scanJarFile(file, pluginClassLoader, remap);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return remap;
    }

    public void instantiatePlugins() {
        for (PluginConfig config : this.configs.values()) {
            if (this.plugins.containsKey(config)) {
                Earthhack.getLogger().error("Can't register Plugin " + config.getName() + ", a plugin with that name is already registered.");
                continue;
            }
            Earthhack.getLogger().info("Instantiating: " + config.getName() + ", MainClass: " + config.getMainClass());
            try {
                Class<?> clazz = Class.forName(config.getMainClass());
                Constructor<?> constructor = clazz.getConstructor(new Class[0]);
                constructor.setAccessible(true);
                Plugin plugin = (Plugin)constructor.newInstance(new Object[0]);
                this.plugins.put(config, plugin);
            }
            catch (Throwable e) {
                Earthhack.getLogger().error("Error instantiating : " + config.getName() + ", caused by:");
                e.printStackTrace();
            }
        }
    }

    private void scanJarFile(File file, ClassLoader pluginClassLoader, Map<String, File> remap) throws Exception {
        JarFile jarFile = new JarFile(file);
        Manifest manifest = jarFile.getManifest();
        Attributes attributes = manifest.getMainAttributes();
        String configName = attributes.getValue("3arthh4ckConfig");
        if (configName == null) {
            throw new BadPluginException(jarFile.getName() + ": Manifest doesn't provide a 3arthh4ckConfig!");
        }
        String vanilla = attributes.getValue("3arthh4ckVanilla");
        switch (Environment.getEnvironment()) {
            case VANILLA: {
                if (vanilla != null && !vanilla.equals("false")) break;
                Core.LOGGER.info("Found Plugin to remap!");
                remap.put(configName, file);
                return;
            }
            case SEARGE: 
            case MCP: {
                if (vanilla == null || !vanilla.equals("true")) break;
                return;
            }
        }
        ReflectionUtil.addToClassPath((URLClassLoader)pluginClassLoader, file);
        PluginConfig config = (PluginConfig)Jsonable.GSON.fromJson(new InputStreamReader(Objects.requireNonNull(pluginClassLoader.getResourceAsStream(configName))), PluginConfig.class);
        if (config == null) {
            throw new BadPluginException(jarFile.getName() + ": Found a PluginConfig, but couldn't instantiate it.");
        }
        Core.LOGGER.info("Found PluginConfig: " + config.getName() + ", MainClass: " + config.getMainClass() + ", Mixins: " + config.getMixinConfig());
        this.configs.put(configName, config);
    }

    public Map<String, PluginConfig> getConfigs() {
        return this.configs;
    }

    public Map<PluginConfig, Plugin> getPlugins() {
        return this.plugins;
    }

    public ClassLoader getPluginClassLoader() {
        return this.classLoader;
    }
}
