package me.earth.earthhack.impl.util.helpers.addable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.CommandSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.util.helpers.addable.ListType;
import me.earth.earthhack.impl.util.helpers.command.AddableCommandModule;

public class AddableModule
extends AddableCommandModule {
    public final Set<String> strings = new HashSet<String>();
    public final String descriptor;
    public final Setting<ListType> listType = this.register(new EnumSetting<ListType>("List-Type", ListType.WhiteList));
    public final Setting<String> commandSetting;

    public AddableModule(String name, Category category, String command, String descriptor) {
        super(name, category);
        this.descriptor = descriptor;
        this.commandSetting = this.register(new CommandSetting(command, this::onSettingInput));
    }

    @Override
    public <T, S extends Setting<T>> S register(S setting) {
        if (setting.getName().equalsIgnoreCase("add") || setting.getName().equalsIgnoreCase("del")) {
            Earthhack.getLogger().error(this.getName() + " Can't register the setting: " + setting.getName() + " in AddableModules these names (add/del) are reserved for the module command!");
            return setting;
        }
        return super.register(setting);
    }

    @Override
    public void add(String string) {
        this.strings.add(this.formatString(string));
    }

    @Override
    public void del(String string) {
        this.strings.remove(this.formatString(string));
    }

    @Override
    public PossibleInputs getSettingInput(String input, String[] args) {
        if (input == null || input.isEmpty()) {
            return new PossibleInputs("<add/del> <" + this.descriptor + ">", "");
        }
        return this.getInput(input, args);
    }

    public boolean isValid(String string) {
        if (string == null) {
            return false;
        }
        if (this.listType.getValue() == ListType.WhiteList) {
            return this.strings.contains(this.formatString(string));
        }
        return !this.strings.contains(this.formatString(string));
    }

    public Collection<String> getList() {
        return this.strings;
    }

    public String getInput(String input, boolean add) {
        if (!add) {
            for (String s : this.strings) {
                if (!TextUtil.startsWith(s, input)) continue;
                return TextUtil.substring(s, input.length());
            }
        }
        return "";
    }

    protected void onSettingInput(String input) {
        this.add(this.formatString(input));
    }

    protected PossibleInputs getInput(String input, String[] args) {
        PossibleInputs inputs = PossibleInputs.empty().setRest(" <" + this.descriptor + ">");
        if (args.length == 1) {
            if ("add".startsWith(args[0].toLowerCase())) {
                return inputs.setCompletion(TextUtil.substring("add", args[0].length()));
            }
            if ("del".startsWith(args[0].toLowerCase())) {
                return inputs.setCompletion(TextUtil.substring("del", args[0].length()));
            }
            return inputs;
        }
        if (args.length > 1) {
            inputs.setRest("");
            if (args[0].equalsIgnoreCase("add")) {
                return inputs.setCompletion(this.getInput(input.substring(4), true));
            }
            if (args[0].equalsIgnoreCase("del")) {
                return inputs.setCompletion(this.getInput(input.substring(4), false));
            }
            return inputs;
        }
        return inputs.setRest("");
    }

    protected String formatString(String string) {
        return string.toLowerCase();
    }
}
