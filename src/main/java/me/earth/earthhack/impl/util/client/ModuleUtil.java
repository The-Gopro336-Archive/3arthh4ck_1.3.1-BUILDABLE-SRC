/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.util.client;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Hidden;
import me.earth.earthhack.impl.managers.Managers;

public class ModuleUtil {
    public static String getHudName(Module module) {
        return module.getDisplayName() + (module.getDisplayInfo() == null || module.isHidden() == Hidden.Info ? "" : "\u00a77 [\u00a7f" + module.getDisplayInfo() + "\u00a77" + "]");
    }

    public static void disableRed(Module module, String message) {
        ModuleUtil.disable(module, "\u00a7c" + message);
    }

    public static void disable(Module module, String message) {
        module.disable();
        ModuleUtil.sendMessage(module, message);
    }

    public static void sendMessage(Module module, String message) {
        ModuleUtil.sendMessage(module, message, "");
    }

    public static void sendMessage(Module module, String message, String append) {
        Managers.CHAT.sendDeleteMessage("<" + module.getDisplayName() + "> " + message, module.getName() + append, 2000);
    }
}

