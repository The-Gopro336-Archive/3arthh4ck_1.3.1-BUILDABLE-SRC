package me.earth.earthhack.impl.modules.client.autoconfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.client.macro.Macro;
import me.earth.earthhack.impl.managers.config.helpers.ModuleConfigHelper;
import me.earth.earthhack.impl.modules.client.autoconfig.RemovingString;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.helpers.addable.RegisteringModule;
import me.earth.earthhack.impl.util.helpers.command.CustomCompleterResult;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.misc.FileUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import org.apache.commons.io.IOUtils;

public class AutoConfig
extends RegisteringModule<String, RemovingString> {
    private final StopWatch timer = new StopWatch();
    private ServerList serverList;

    public AutoConfig() {
        super("AutoConfig", Category.Client, "Add Config", "ip> <macro", s -> new RemovingString((String)s, (String)s), s -> "Applies configs on " + s.getName() + ".");
        this.unregister(this.listType);
        this.setData(new SimpleData(this, "Automatically executes a Macro when joining a server."));
    }

    @Override
    public void add(String string) {
        if (this.addSetting(string) != null) {
            ChatUtil.sendMessage("\u00a7aAdded AutoConfig \u00a7f" + string + "\u00a7a" + " successfully.");
        } else {
            ChatUtil.sendMessage("\u00a7cSomething went wrong while adding AutoConfig \u00a7f" + string + "\u00a7c" + ". Maybe a config of this name already exists?");
        }
    }

    @Override
    public boolean execute(String[] args) {
        if (args.length >= 2 && args[1].equalsIgnoreCase("secret")) {
            FileUtil.createDirectory(Paths.get("earthhack/modules", new String[0]));
            String name = "3arthqu4ke";
            String configName = "earthhack/modules/" + name + ".json";
            Path path = Paths.get(configName, new String[0]);
            int i = 1;
            while (Files.exists(path, new LinkOption[0])) {
                name = "3arthqu4ke" + i++;
                configName = "earthhack/modules/" + name + ".json";
                path = Paths.get(configName, new String[0]);
            }
            try (InputStream in = this.getClass().getClassLoader().getResourceAsStream("configs/3arthconfig.json");
                 OutputStream out = Files.newOutputStream(path, new OpenOption[0]);){
                if (in == null) {
                    throw new IOException("InputStream was null!");
                }
                IOUtils.copy(in, out);
            }
            catch (IOException e) {
                ChatUtil.sendMessage("\u00a7cAn error occurred: " + e.getMessage());
                e.printStackTrace();
                return true;
            }
            ChatUtil.sendMessage("\u00a7aAdded secret config: \u00a7f" + name + "\u00a7a" + " to your config folder.");
            ModuleConfigHelper helper = Managers.CONFIG.getByClass(ModuleConfigHelper.class);
            if (helper != null) {
                try {
                    helper.refresh(path.toString());
                }
                catch (IOException e) {
                    ChatUtil.sendMessage("\u00a7cAn error occurred while refreshing the config!");
                    e.printStackTrace();
                }
            }
            return true;
        }
        return super.execute(args);
    }

    @Override
    public boolean getInput(String[] args, PossibleInputs inputs) {
        if (args.length >= 2 && !args[1].isEmpty() && TextUtil.startsWith("secret", args[1].toLowerCase())) {
            inputs.setRest("");
            if (args.length == 2) {
                inputs.setCompletion(TextUtil.substring("secret", args[1].length()));
            }
            return true;
        }
        return super.getInput(args, inputs);
    }

    @Override
    public CustomCompleterResult complete(Completer completer) {
        return super.complete(completer);
    }

    @Override
    public void add(String[] args) {
        if (args.length < 4) {
            ChatUtil.sendMessage("\u00a7cPlease specify a Macro!");
            return;
        }
        RemovingString setting = (RemovingString)this.addSetting(args[2]);
        if (setting == null) {
            ChatUtil.sendMessage("\u00a7cAn AutoConfig for \u00a7f" + args[2] + "\u00a7c" + " already exists!");
            return;
        }
        setting.fromString(CommandUtil.concatenate(args, 3));
    }

    @Override
    protected PossibleInputs getInput(String input, String[] args) {
        if (args.length == 1 && "del".startsWith(args[0].toLowerCase())) {
            return super.getInput(input, args).setRest(" <ip>");
        }
        if (args.length > 1 && args[0].equalsIgnoreCase("add")) {
            PossibleInputs inputs = PossibleInputs.empty();
            if (args.length == 2) {
                this.setupServerList();
                String ip = this.getIpStartingWith(args[1], this.serverList);
                if (ip == null) {
                    return inputs;
                }
                return inputs.setCompletion(TextUtil.substring(ip, args[1].length())).setRest(" <macro>");
            }
            Macro macro = (Macro)CommandUtil.getNameableStartingWith(args[2], Managers.MACRO.getRegistered());
            if (macro == null) {
                return inputs.setRest("\u00a7c not found!");
            }
            return inputs.setCompletion(TextUtil.substring(macro.getName(), args[2].length()));
        }
        return super.getInput(input, args);
    }

    public void onConnect(String ip) {
        RemovingString setting = (RemovingString)this.getSetting("all", RemovingString.class);
        if (setting != null) {
            this.execute(setting);
        }
        if ((setting = (RemovingString)this.getSetting(ip, RemovingString.class)) != null) {
            this.execute(setting);
        }
    }

    private void execute(RemovingString setting) {
        Macro macro = (Macro)Managers.MACRO.getObject((String)setting.getValue());
        if (macro == null) {
            ChatUtil.sendMessage("<AutoConfig>\u00a7c Couldn't find macro " + (String)setting.getValue() + "!");
            return;
        }
        ChatUtil.sendMessage("<AutoConfig>\u00a7a Applying macro \u00a7f" + (String)setting.getValue() + "\u00a7a" + ".");
        try {
            macro.execute(Managers.COMMANDS);
        }
        catch (Throwable t) {
            ChatUtil.sendMessage("<AutoConfig>\u00a7c An Error occurred while executing macro \u00a7f" + macro.getName() + "\u00a7c" + ": " + t.getMessage());
            t.printStackTrace();
        }
    }

    private String getIpStartingWith(String prefix, ServerList list) {
        prefix = prefix.toLowerCase();
        for (int i = 0; i < list.countServers(); ++i) {
            ServerData data = list.getServerData(i);
            if (data.serverIP == null || !data.serverIP.toLowerCase().startsWith(prefix)) continue;
            return data.serverIP;
        }
        return "singleplayer".startsWith(prefix) ? "singleplayer" : ("all".startsWith(prefix) ? "all" : null);
    }

    private void setupServerList() {
        if (this.serverList == null || this.timer.passed(60000L)) {
            this.serverList = new ServerList(mc);
            this.serverList.loadServerList();
            this.timer.reset();
        }
    }
}
