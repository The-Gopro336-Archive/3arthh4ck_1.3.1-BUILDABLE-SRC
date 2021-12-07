/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 */
package me.earth.earthhack.impl.commands;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.config.Config;
import me.earth.earthhack.api.config.ConfigHelper;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.gui.YesNoNonPausing;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.config.helpers.CurrentConfig;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.util.misc.io.IORunnable;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.client.gui.GuiScreen;

public class ConfigCommand
extends Command
implements Globals {
    public ConfigCommand() {
        super(new String[][]{{"config"}, {"config"}, {"save", "delete", "load", "refresh"}, {"name..."}});
        CommandDescriptions.register(this, "Manage your configs.");
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 1) {
            Managers.CHAT.sendDeleteMessage("Use this command to save/load your config.", "config", 3000);
            return;
        }
        ConfigHelper helper = (ConfigHelper)Managers.CONFIG.getObject(args[1]);
        if (helper == null) {
            Managers.CHAT.sendDeleteMessage("\u00a7b" + args[1] + "\u00a7c" + " unknown. Use: " + this.getConcatenatedHelpers(), "config1", 3000);
            return;
        }
        block3 : switch (args.length) {
            case 2: {
                StringBuilder message = new StringBuilder("Use this command").append(" to save/delete/load the ").append("\u00a7b").append(helper.getName()).append("\u00a7f").append(" config. Currently active: ").append("\u00a7a").append(CurrentConfig.getInstance().get(helper)).append("\u00a7f").append(". Available: ");
                Iterator itr = helper.getConfigs().iterator();
                while (itr.hasNext()) {
                    Config config = (Config)itr.next();
                    message.append("\u00a7b").append(config.getName()).append("\u00a7f");
                    if (!itr.hasNext()) continue;
                    message.append(", ");
                }
                message.append(".");
                Managers.CHAT.sendDeleteMessage(message.toString(), "config2", 3000);
                break;
            }
            case 3: {
                switch (args[2].toLowerCase()) {
                    case "save": {
                        try {
                            Managers.CONFIG.save(helper, new String[0]);
                            Managers.CHAT.sendDeleteMessage("\u00a7aSaved the " + helper.getName() + " config.", "config5", 3000);
                        }
                        catch (IOException e) {
                            Managers.CHAT.sendDeleteMessage("\u00a7cAn error occurred while saving " + helper.getName() + ": " + "\u00a7f" + e.getMessage() + "\u00a7c" + ".", "config6", 3000);
                            e.printStackTrace();
                        }
                        return;
                    }
                    case "delete": {
                        Managers.CHAT.sendDeleteMessage("\u00a7cPlease specify a \u00a7f" + helper.getName() + "\u00a7c" + " config to delete!", "config6", 3000);
                        return;
                    }
                    case "load": {
                        Managers.CHAT.sendDeleteMessage("\u00a7cPlease specify a config to load.", "config6", 3000);
                        return;
                    }
                    case "refresh": {
                        GuiScreen before = ConfigCommand.mc.currentScreen;
                        Scheduler.getInstance().schedule(() -> mc.displayGuiScreen((GuiScreen)new YesNoNonPausing((result, id) -> {
                            mc.displayGuiScreen(before);
                            if (!result) {
                                return;
                            }
                            try {
                                helper.refresh();
                                Managers.CHAT.sendDeleteMessage("\u00a7aRefreshed the " + helper.getName() + " config.", "config7", 3000);
                            }
                            catch (IOException e) {
                                Managers.CHAT.sendDeleteMessage("\u00a7cAn error occurred while saving " + helper.getName() + ": " + "\u00a7f" + e.getMessage() + "\u00a7c" + ".", "config6", 3000);
                                e.printStackTrace();
                            }
                        }, "\u00a7cReload the " + helper.getName() + " config from the disk.", "This action will override your current " + helper.getName() + " configs. Continue?", 1337)));
                        return;
                    }
                }
                Managers.CHAT.sendDeleteMessage("\u00a7cCan't recognize option " + args[2] + ".", "config4", 3000);
                break;
            }
            default: {
                Object[] configs = Arrays.copyOfRange(args, 3, args.length);
                String cString = "config" + (configs.length > 1 ? "s" : "");
                switch (args[2].toLowerCase()) {
                    case "save": {
                        this.displayYesNo("Sav", "Save the  " + helper.getName() + " - " + Arrays.toString(configs) + " " + cString + "?", helper, () -> ConfigCommand.lambda$execute$2(helper, (String[])configs, cString));
                        break block3;
                    }
                    case "delete": {
                        try {
                            helper.delete(args[3]);
                            ChatUtil.sendMessage("\u00a7aDeleted \u00a7c" + args[3] + "\u00a7a" + " from the " + helper.getName() + "s config.");
                        }
                        catch (Exception e) {
                            ChatUtil.sendMessage("\u00a7cCan't delete \u00a7f" + args[3] + "\u00a7c" + ": " + e.getMessage());
                            e.printStackTrace();
                        }
                        break block3;
                    }
                    case "load": {
                        try {
                            Managers.CONFIG.load(helper, args[3]);
                            ChatUtil.sendMessage("\u00a7aLoaded the \u00a7f" + helper.getName() + "\u00a7a" + " : " + "\u00a7f" + args[3] + "\u00a7a" + " config.");
                        }
                        catch (IOException e) {
                            ChatUtil.sendMessage("\u00a7cAn error occurred while loading the \u00a7f" + helper.getName() + "\u00a7c" + " : " + "\u00a7f" + args[3] + "\u00a7c" + " config.");
                            e.printStackTrace();
                        }
                        break block3;
                    }
                    case "refresh": {
                        this.displayYesNo("Refresh", "This action will override your current " + helper.getName() + " - " + Arrays.toString(configs) + " " + cString + ". Continue?", helper, () -> ConfigCommand.lambda$execute$3(helper, (String[])configs, cString));
                        break block3;
                    }
                }
                ChatUtil.sendMessage("\u00a7cCan't recognize option \u00a7f" + args[2] + "\u00a7c" + ".");
            }
        }
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args) {
        PossibleInputs inputs = super.getPossibleInputs(args);
        if (args.length == 1) {
            return inputs.setRest(" <" + this.getConcatenatedHelpers() + "> <save/delete/load/refresh> <name>");
        }
        ConfigHelper helper = (ConfigHelper)CommandUtil.getNameableStartingWith(args[1], Managers.CONFIG.getRegistered());
        if (helper == null) {
            return inputs.setCompletion("").setRest("\u00a7c config type not found");
        }
        switch (args.length) {
            case 2: {
                return inputs.setCompletion(TextUtil.substring(helper.getName(), args[1].length())).setRest(" <save/delete/load/refresh> <name>");
            }
            case 3: {
                return inputs;
            }
        }
        Object nameable = CommandUtil.getNameableStartingWith(args[args.length - 1], helper.getConfigs());
        if (nameable != null) {
            return inputs.setRest("").setCompletion(TextUtil.substring(nameable.getName(), args[args.length - 1].length()));
        }
        return inputs;
    }

    @Override
    public Completer onTabComplete(Completer completer) {
        return super.onTabComplete(completer);
    }

    private String getConcatenatedHelpers() {
        StringBuilder builder = new StringBuilder();
        Iterator it = Managers.CONFIG.getRegistered().iterator();
        while (it.hasNext()) {
            ConfigHelper helper = (ConfigHelper)it.next();
            builder.append(helper.getName());
            if (!it.hasNext()) continue;
            builder.append("/");
        }
        return builder.toString();
    }

    private void displayYesNo(String action, String message2, ConfigHelper<?> helper, IORunnable runnable) {
        GuiScreen before = ConfigCommand.mc.currentScreen;
        Scheduler.getInstance().schedule(() -> mc.displayGuiScreen((GuiScreen)new YesNoNonPausing((result, id) -> {
            mc.displayGuiScreen(before);
            if (!result) {
                return;
            }
            try {
                runnable.run();
            }
            catch (IOException e) {
                Managers.CHAT.sendDeleteMessage("\u00a7cAn error occurred while " + action.toLowerCase() + "ing " + helper.getName() + ": " + "\u00a7f" + e.getMessage() + "\u00a7c" + ".", "config6", 3000);
                e.printStackTrace();
            }
        }, "\u00a7c" + action + "ing the " + "\u00a7f" + helper.getName() + "\u00a7c" + " config.", message2, 1337)));
    }

    private static /* synthetic */ void lambda$execute$3(ConfigHelper helper, String[] configs, String cString) throws IOException {
        Managers.CONFIG.load(helper, configs);
        ChatUtil.sendMessage("\u00a7aRefreshed the \u00a7f" + helper.getName() + "\u00a7a" + " : " + "\u00a7f" + Arrays.toString(configs) + "\u00a7a" + " " + cString + ".");
    }

    private static /* synthetic */ void lambda$execute$2(ConfigHelper helper, String[] configs, String cString) throws IOException {
        Managers.CONFIG.save(helper, configs);
        ChatUtil.sendMessage("\u00a7aSaved the \u00a7f" + helper.getName() + "\u00a7a" + " : " + "\u00a7f" + Arrays.toString(configs) + "\u00a7a" + " " + cString + ".");
    }
}

