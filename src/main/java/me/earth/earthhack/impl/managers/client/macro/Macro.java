package me.earth.earthhack.impl.managers.client.macro;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Map;
import me.earth.earthhack.api.config.Jsonable;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.api.util.interfaces.Nameable;
import me.earth.earthhack.impl.managers.chat.CommandManager;
import me.earth.earthhack.impl.managers.client.macro.MacroType;

public class Macro
implements Jsonable,
Nameable {
    protected boolean release;
    protected final String name;
    protected String[] commands;
    protected int index;
    protected Bind bind;

    public Macro(String name, Bind bind, String[] commands) {
        this.name = name;
        this.bind = bind;
        this.commands = commands;
    }

    public void execute(CommandManager manager) throws Error {
        this.getType().execute(manager, this);
    }

    public String[] getCommands() {
        if (this.commands.length == 0) {
            return new String[]{""};
        }
        return this.commands;
    }

    public Bind getBind() {
        return this.bind;
    }

    public MacroType getType() {
        return MacroType.NORMAL;
    }

    public boolean isRelease() {
        return this.release;
    }

    public void setRelease(boolean release) {
        this.release = release;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public boolean equals(Object o) {
        return o instanceof Macro && ((Macro)o).getName().equalsIgnoreCase(this.getName());
    }

    public int hashCode() {
        return this.name.toLowerCase().hashCode();
    }

    @Override
    public void fromJson(JsonElement element) {
        JsonObject object = element.getAsJsonObject();
        ArrayList<String> commands = new ArrayList<String>();
        block10: for (Map.Entry entry : object.entrySet()) {
            switch (((String)entry.getKey()).toLowerCase()) {
                case "bind": {
                    this.bind = Bind.fromString(((JsonElement)entry.getValue()).getAsString());
                    continue block10;
                }
                case "release": {
                    this.release = Boolean.parseBoolean(((JsonElement)entry.getValue()).getAsString());
                }
                case "type": {
                    continue block10;
                }
            }
            commands.add(((JsonElement)entry.getValue()).getAsString());
        }
        this.commands = commands.toArray(new String[0]);
    }

    @Override
    public String toJson() {
        JsonObject object = new JsonObject();
        object.add("bind", PARSER.parse(this.bind.toString()));
        object.add("type", PARSER.parse(this.getType().name()));
        object.add("release", PARSER.parse(this.release + ""));
        for (int i = 0; i < this.commands.length; ++i) {
            object.add("command" + (i == 0 ? "" : Integer.valueOf(i)), Jsonable.parse(this.commands[i]));
        }
        return object.toString();
    }
}
