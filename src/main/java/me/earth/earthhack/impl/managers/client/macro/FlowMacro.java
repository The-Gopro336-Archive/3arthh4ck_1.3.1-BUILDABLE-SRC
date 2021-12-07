/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.managers.client.macro;

import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.managers.client.macro.Macro;
import me.earth.earthhack.impl.managers.client.macro.MacroType;
import me.earth.earthhack.impl.managers.client.macro.MacroUtil;

public class FlowMacro
extends Macro {
    public FlowMacro(String name, Bind bind, Macro ... macros) {
        super(name, bind, MacroUtil.concatenateCommands(macros));
    }

    @Override
    public MacroType getType() {
        return MacroType.FLOW;
    }
}

