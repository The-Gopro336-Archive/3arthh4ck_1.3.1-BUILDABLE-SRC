/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package me.earth.earthhack.impl.managers.client.macro;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.earth.earthhack.api.config.Jsonable;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.managers.chat.CommandManager;
import me.earth.earthhack.impl.managers.client.macro.Macro;
import me.earth.earthhack.impl.managers.client.macro.MacroManager;
import me.earth.earthhack.impl.managers.client.macro.MacroType;

public class DelegateMacro
extends Macro {
    private MacroType delegated = MacroType.DELEGATE;

    public DelegateMacro(String name, String macro) {
        super(name, Bind.none(), new String[]{"macro use " + macro});
    }

    public boolean isReferenced(MacroManager macros) {
        return this.isReferenced(macros, new HashSet<Macro>());
    }

    private boolean isReferenced(MacroManager macros, Set<Macro> checked) {
        checked.add(this);
        for (Macro m : macros.getRegistered()) {
            if (checked.contains(m)) continue;
            for (String command : m.commands) {
                if (!command.toLowerCase().contains(this.getName().toLowerCase())) continue;
                if (m instanceof DelegateMacro) {
                    if (!((DelegateMacro)m).isReferenced(macros, checked)) continue;
                    return true;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void execute(CommandManager manager) throws Error {
        this.delegated.execute(manager, this);
    }

    @Override
    public MacroType getType() {
        return MacroType.DELEGATE;
    }

    @Override
    public void fromJson(JsonElement element) {
        JsonObject object = element.getAsJsonObject();
        ArrayList<String> commands = new ArrayList<String>();
        block12: for (Map.Entry entry : object.entrySet()) {
            switch (((String)entry.getKey()).toLowerCase()) {
                case "bind": {
                    this.bind = Bind.fromString(((JsonElement)entry.getValue()).getAsString());
                    continue block12;
                }
                case "delegated": {
                    this.delegated = MacroType.fromString(((JsonElement)entry.getValue()).getAsString());
                    continue block12;
                }
                case "release": {
                    this.release = Boolean.parseBoolean(((JsonElement)entry.getValue()).getAsString());
                }
                case "type": {
                    continue block12;
                }
            }
            commands.add(((JsonElement)entry.getValue()).getAsString());
        }
        this.commands = commands.toArray(new String[0]);
    }

    @Override
    public String toJson() {
        return DelegateMacro.delegateToJson(this.delegated, this.commands);
    }

    public static DelegateMacro delegate(String name, final Macro macro) {
        return new DelegateMacro(name, ""){

            @Override
            public void execute(CommandManager manager) throws Error {
                macro.execute(manager);
            }

            @Override
            public String[] getCommands() {
                return macro.getCommands();
            }

            @Override
            public void fromJson(JsonElement element) {
                Earthhack.getLogger().info("Anonymous delegates " + this.getName() + " fromJson method was called. This shouldn't happen.");
            }

            @Override
            public String toJson() {
                return DelegateMacro.delegateToJson(macro.getType(), macro.commands);
            }
        };
    }

    private static String delegateToJson(MacroType type, String[] commands) {
        JsonObject object = new JsonObject();
        object.add("bind", PARSER.parse("NONE"));
        object.add("type", PARSER.parse("DELEGATE"));
        object.add("delegated", PARSER.parse(type.name()));
        for (int i = 0; i < commands.length; ++i) {
            object.add("command" + (i == 0 ? "" : Integer.valueOf(i)), Jsonable.parse(commands[i]));
        }
        return object.toString();
    }
}

