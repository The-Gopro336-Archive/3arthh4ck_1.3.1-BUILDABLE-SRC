package me.earth.earthhack.impl.managers.client.macro;

import com.google.gson.JsonElement;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.managers.chat.CommandManager;
import me.earth.earthhack.impl.managers.client.macro.DelegateMacro;
import me.earth.earthhack.impl.managers.client.macro.Macro;

final class DelegateMacro$1
extends DelegateMacro {
    final Macro val$macro;

    DelegateMacro$1(String name, String macro, Macro macro2) {
        this.val$macro = macro2;
        super(name, macro);
    }

    @Override
    public void execute(CommandManager manager) throws Error {
        this.val$macro.execute(manager);
    }

    @Override
    public String[] getCommands() {
        return this.val$macro.getCommands();
    }

    @Override
    public void fromJson(JsonElement element) {
        Earthhack.getLogger().info("Anonymous delegates " + this.getName() + " fromJson method was called. This shouldn't happen.");
    }

    @Override
    public String toJson() {
        return DelegateMacro.delegateToJson(this.val$macro.getType(), this.val$macro.commands);
    }
}
