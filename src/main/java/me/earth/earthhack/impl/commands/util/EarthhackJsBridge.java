/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.commands.util;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.commands.Commands;

public class EarthhackJsBridge
implements Globals {
    public void command(String command) {
        mc.addScheduledTask(() -> Managers.COMMANDS.applyCommand(Commands.getPrefix() + command));
    }

    public boolean isEnabled(String module) {
        return ((Module)Managers.MODULES.getObject(module)).isEnabled();
    }
}

