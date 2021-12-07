package org.spongepowered.asm.mixin;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.GlobalProperties;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.Config;

public final class Mixins {
    private static final Logger logger = LogManager.getLogger("mixin");
    private static final String CONFIGS_KEY = "mixin.configs.queue";
    private static final Set<String> errorHandlers = new LinkedHashSet<String>();

    private Mixins() {
    }

    public static void addConfigurations(String ... configFiles) {
        MixinEnvironment fallback = MixinEnvironment.getDefaultEnvironment();
        for (String configFile : configFiles) {
            Mixins.createConfiguration(configFile, fallback);
        }
    }

    public static void addConfiguration(String configFile) {
        Mixins.createConfiguration(configFile, MixinEnvironment.getDefaultEnvironment());
    }

    @Deprecated
    static void addConfiguration(String configFile, MixinEnvironment fallback) {
        Mixins.createConfiguration(configFile, fallback);
    }

    private static void createConfiguration(String configFile, MixinEnvironment fallback) {
        Config config = null;
        try {
            config = Config.create(configFile, fallback);
        }
        catch (Exception ex) {
            logger.error("Error encountered reading mixin config " + configFile + ": " + ex.getClass().getName() + " " + ex.getMessage(), ex);
        }
        Mixins.registerConfiguration(config);
    }

    private static void registerConfiguration(Config config) {
        if (config == null) {
            return;
        }
        MixinEnvironment env = config.getEnvironment();
        if (env != null) {
            env.registerConfig(config.getName());
        }
        Mixins.getConfigs().add(config);
    }

    public static int getUnvisitedCount() {
        int count = 0;
        for (Config config : Mixins.getConfigs()) {
            if (config.isVisited()) continue;
            ++count;
        }
        return count;
    }

    public static Set<Config> getConfigs() {
        LinkedHashSet mixinConfigs = (LinkedHashSet)GlobalProperties.get(CONFIGS_KEY);
        if (mixinConfigs == null) {
            mixinConfigs = new LinkedHashSet();
            GlobalProperties.put(CONFIGS_KEY, mixinConfigs);
        }
        return mixinConfigs;
    }

    public static void registerErrorHandlerClass(String handlerName) {
        if (handlerName != null) {
            errorHandlers.add(handlerName);
        }
    }

    public static Set<String> getErrorHandlerClasses() {
        return Collections.unmodifiableSet(errorHandlers);
    }
}
