/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.commands.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.earthhack.api.command.Command;

public class CommandDescriptions {
    private static final Map<String, String> descriptions = new ConcurrentHashMap<String, String>();

    public static void register(Command command, String description) {
        descriptions.put(command.getName(), description);
    }

    public static String getDescription(Command command) {
        return descriptions.get(command.getName());
    }
}

