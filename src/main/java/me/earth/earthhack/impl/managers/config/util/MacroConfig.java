/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.managers.config.util;

import java.util.ArrayList;
import java.util.List;
import me.earth.earthhack.api.config.Config;
import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.api.register.exception.CantUnregisterException;
import me.earth.earthhack.api.util.IdentifiedNameable;
import me.earth.earthhack.impl.managers.client.macro.Macro;
import me.earth.earthhack.impl.managers.client.macro.MacroManager;

public class MacroConfig
extends IdentifiedNameable
implements Config {
    private final List<Macro> macros = new ArrayList<Macro>();
    private final MacroManager manager;

    public MacroConfig(String name, MacroManager manager) {
        super(name);
        this.manager = manager;
    }

    public void add(Macro macro) {
        this.macros.add(macro);
    }

    public List<Macro> getMacros() {
        return this.macros;
    }

    @Override
    public void apply() {
        for (Macro macro : new ArrayList(this.manager.getRegistered())) {
            try {
                this.manager.unregister(macro);
            }
            catch (CantUnregisterException e) {
                e.printStackTrace();
            }
        }
        for (Macro macro : this.macros) {
            try {
                this.manager.register(macro);
            }
            catch (AlreadyRegisteredException e) {
                e.printStackTrace();
            }
        }
    }

    public static MacroConfig create(String name, MacroManager manager) {
        MacroConfig config = new MacroConfig(name, manager);
        for (Macro macro : manager.getRegistered()) {
            config.add(macro);
        }
        return config;
    }
}

