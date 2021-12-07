package me.earth.earthhack.impl.managers.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.earthhack.api.plugin.Plugin;

public class PluginDescriptions {
    private static final Map<Plugin, String> DESCRIPTIONS = new ConcurrentHashMap<Plugin, String>();

    public static void register(Plugin plugin, String description) {
        DESCRIPTIONS.put(plugin, description);
    }

    public static String getDescription(Plugin plugin) {
        return DESCRIPTIONS.get(plugin);
    }
}
