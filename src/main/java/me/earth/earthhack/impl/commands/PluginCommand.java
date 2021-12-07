package me.earth.earthhack.impl.commands;

import java.util.Iterator;
import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.plugin.Plugin;
import me.earth.earthhack.api.plugin.PluginConfig;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.gui.chat.util.ChatComponentUtil;
import me.earth.earthhack.impl.managers.client.PluginDescriptions;
import me.earth.earthhack.impl.managers.client.PluginManager;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;

public class PluginCommand
extends Command {
    public PluginCommand() {
        super(new String[][]{{"plugin"}});
        CommandDescriptions.register(this, "Get a list of the currently active plugins.");
    }

    @Override
    public boolean fits(String[] args) {
        return args[0].length() > 0 && TextUtil.startsWith("plugins", args[0]);
    }

    @Override
    public void execute(String[] args) {
        TextComponentString component = new TextComponentString("Active Plugins: ");
        Iterator<PluginConfig> itr = PluginManager.getInstance().getPlugins().keySet().iterator();
        while (itr.hasNext()) {
            PluginConfig config = itr.next();
            Plugin plugin = PluginManager.getInstance().getPlugins().get(config);
            if (plugin == null) continue;
            String description = PluginDescriptions.getDescription(plugin);
            if (description == null) {
                description = "A Plugin.";
            }
            component.appendSibling(new TextComponentString("\u00a7b" + config.getName() + (itr.hasNext() ? "\u00a7f, " : "")).setStyle(new Style().setHoverEvent(ChatComponentUtil.setOffset(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(description))))));
        }
        ChatUtil.sendComponent(component);
    }
}
