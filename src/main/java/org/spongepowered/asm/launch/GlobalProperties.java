package org.spongepowered.asm.launch;

import java.util.ServiceLoader;
import org.spongepowered.asm.service.IGlobalPropertyService;

public final class GlobalProperties {
    private static IGlobalPropertyService service;

    private GlobalProperties() {
    }

    private static IGlobalPropertyService getService() {
        if (service == null) {
            ServiceLoader<IGlobalPropertyService> serviceLoader = ServiceLoader.load(IGlobalPropertyService.class, GlobalProperties.class.getClassLoader());
            service = serviceLoader.iterator().next();
        }
        return service;
    }

    public static <T> T get(String key) {
        return GlobalProperties.getService().getProperty(key);
    }

    public static void put(String key, Object value) {
        GlobalProperties.getService().setProperty(key, value);
    }

    public static <T> T get(String key, T defaultValue) {
        return GlobalProperties.getService().getProperty(key, defaultValue);
    }

    public static String getString(String key, String defaultValue) {
        return GlobalProperties.getService().getPropertyString(key, defaultValue);
    }

    public static final class Keys {
        public static final String INIT = "mixin.initialised";
        public static final String AGENTS = "mixin.agents";
        public static final String CONFIGS = "mixin.configs";
        public static final String TRANSFORMER = "mixin.transformer";
        public static final String PLATFORM_MANAGER = "mixin.platform";
        public static final String FML_LOAD_CORE_MOD = "mixin.launch.fml.loadcoremodmethod";
        public static final String FML_GET_REPARSEABLE_COREMODS = "mixin.launch.fml.reparseablecoremodsmethod";
        public static final String FML_CORE_MOD_MANAGER = "mixin.launch.fml.coremodmanagerclass";
        public static final String FML_GET_IGNORED_MODS = "mixin.launch.fml.ignoredmodsmethod";

        private Keys() {
        }
    }
}
