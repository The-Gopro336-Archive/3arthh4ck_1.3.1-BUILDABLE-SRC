/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package me.earth.earthhack.impl.managers.config.helpers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import me.earth.earthhack.api.config.Jsonable;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.managers.client.macro.CombinedMacro;
import me.earth.earthhack.impl.managers.client.macro.DelegateMacro;
import me.earth.earthhack.impl.managers.client.macro.FlowMacro;
import me.earth.earthhack.impl.managers.client.macro.Macro;
import me.earth.earthhack.impl.managers.client.macro.MacroManager;
import me.earth.earthhack.impl.managers.client.macro.MacroType;
import me.earth.earthhack.impl.managers.config.helpers.AbstractConfigHelper;
import me.earth.earthhack.impl.managers.config.util.MacroConfig;

public class MacroConfigHelper
extends AbstractConfigHelper<MacroConfig> {
    private final MacroManager manager;

    public MacroConfigHelper(MacroManager manager) {
        super("macro", "macros");
        this.manager = manager;
    }

    @Override
    protected MacroConfig create(String name) {
        return MacroConfig.create(name, this.manager);
    }

    @Override
    protected JsonObject toJson(MacroConfig config) {
        JsonObject object = new JsonObject();
        for (Macro macro : config.getMacros()) {
            object.add(macro.getName(), Jsonable.parse(macro.toJson(), false));
        }
        return object;
    }

    @Override
    protected MacroConfig readFile(InputStream stream, String name) {
        MacroConfig config = new MacroConfig(name, this.manager);
        JsonObject object = Jsonable.PARSER.parse((Reader)new InputStreamReader(stream)).getAsJsonObject();
        block6: for (Map.Entry entry : object.entrySet()) {
            Macro macro;
            JsonObject value = ((JsonElement)entry.getValue()).getAsJsonObject();
            MacroType type = MacroType.fromString(value.get("type").getAsString());
            switch (type) {
                case NORMAL: {
                    macro = new Macro((String)entry.getKey(), Bind.none(), new String[0]);
                    break;
                }
                case FLOW: {
                    macro = new FlowMacro((String)entry.getKey(), Bind.none(), new Macro[0]);
                    break;
                }
                case COMBINED: {
                    macro = new CombinedMacro((String)entry.getKey(), Bind.none(), new Macro[0]);
                    break;
                }
                case DELEGATE: {
                    macro = new DelegateMacro((String)entry.getKey(), "");
                    break;
                }
                default: {
                    continue block6;
                }
            }
            macro.fromJson((JsonElement)entry.getValue());
            config.add(macro);
        }
        return config;
    }
}

