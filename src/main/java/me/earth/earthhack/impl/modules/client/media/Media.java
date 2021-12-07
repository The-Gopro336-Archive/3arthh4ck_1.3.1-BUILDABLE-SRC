package me.earth.earthhack.impl.modules.client.media;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.autoconfig.RemovingString;
import me.earth.earthhack.impl.modules.client.media.ListenerClearChat;
import me.earth.earthhack.impl.modules.client.media.ListenerTick;
import me.earth.earthhack.impl.modules.client.media.MediaData;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypass;
import me.earth.earthhack.impl.util.helpers.addable.RegisteringModule;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.impl.util.thread.LookUpUtil;

public class Media
extends RegisteringModule<String, RemovingString> {
    protected static final ModuleCache<PingBypass> PING_BYPASS = Caches.getModule(PingBypass.class);
    protected final Setting<String> replacement = this.register(new StringSetting("Replacement", "3arthqu4ke"));
    protected final Setting<Boolean> replaceCustom = this.register(new BooleanSetting("Custom", false));
    protected final Map<String, String> cache = new ConcurrentHashMap<String, String>();
    protected final Map<Setting<String>, Pattern> custom = new ConcurrentHashMap<Setting<String>, Pattern>();
    protected Pattern pattern;
    protected Pattern pingBypass;
    protected boolean pingBypassEnabled;
    protected boolean send;

    public Media() {
        super("Media", Category.Client, "Add_Media", "name> <replace", s -> new RemovingString((String)s, (String)s), s -> "Replaces on " + s.getName() + ".");
        this.listeners.add(new ListenerClearChat(this));
        this.listeners.add(new ListenerTick(this));
        this.pattern = this.compile(mc.getSession().getUsername());
        this.replacement.addObserver(event -> this.cache.clear());
        this.replaceCustom.addObserver(event -> this.cache.clear());
        this.register(new BooleanSetting("Reload", false)).addObserver(event -> this.reload());
        this.setData(new MediaData(this));
    }

    @Override
    public void onEnable() {
        this.pingBypassEnabled = false;
        this.send = false;
    }

    @Override
    public void add(String[] args) {
        if (args.length < 4) {
            ChatUtil.sendMessage("\u00a7cPlease specify a Replacement!");
            return;
        }
        RemovingString setting = this.addSetting(args[2]);
        if (setting == null) {
            ChatUtil.sendMessage("\u00a7cA Replacement for \u00a7f" + args[2] + "\u00a7c" + " already exists!");
            return;
        }
        setting.fromString(CommandUtil.concatenate(args, 3));
    }

    @Override
    protected RemovingString addSetting(String string) {
        RemovingString setting = (RemovingString)super.addSetting(string);
        if (setting != null) {
            this.custom.put(setting, this.compile(setting.getName()));
        }
        return setting;
    }

    @Override
    public Setting<?> unregister(Setting<?> setting) {
        Setting<?> s = super.unregister(setting);
        if (s != null) {
            this.custom.remove(s);
        }
        return s;
    }

    @Override
    public void del(String string) {
        Setting<?> setting = this.getSetting(string);
        if (setting != null) {
            this.custom.remove(setting);
        }
        super.del(string);
        this.cache.clear();
    }

    @Override
    public String getInput(String input, boolean add) {
        if (!add) {
            return super.getInput(input, false);
        }
        String player = LookUpUtil.findNextPlayerName(input);
        if (player != null) {
            return TextUtil.substring(player, input.length());
        }
        return "";
    }

    @Override
    protected String formatString(String string) {
        return string;
    }

    public void reload() {
        this.cache.clear();
        this.pattern = this.compile(mc.getSession().getUsername());
    }

    public String convert(String text) {
        if (!this.isEnabled() || text == null || this.pattern == null) {
            return text;
        }
        return this.cache.computeIfAbsent(text, v -> {
            String toAdd = text;
            if (this.replaceCustom.getValue().booleanValue()) {
                for (Map.Entry<Setting<String>, Pattern> entry : this.custom.entrySet()) {
                    if (this.getSetting(entry.getKey().getName()) == null || !this.getSettings().contains(entry.getKey())) continue;
                    toAdd = entry.getValue().matcher(toAdd).replaceAll(entry.getKey().getValue());
                }
            }
            if (PING_BYPASS.isEnabled() && this.pingBypass != null) {
                toAdd = this.pingBypass.matcher(toAdd).replaceAll(this.replacement.getValue());
            }
            return this.pattern.matcher(toAdd).replaceAll(this.replacement.getValue());
        });
    }

    private Pattern compile(String name) {
        this.cache.clear();
        if (name == null) {
            return null;
        }
        StringBuilder regex = new StringBuilder("(?<!").append('\u00a7').append(")(");
        char[] array = name.toCharArray();
        for (int i = 0; i < array.length; ++i) {
            char c = array[i];
            regex.append(c);
            if (i == array.length - 1) continue;
            for (TextColor textColor : TextColor.values()) {
                if (textColor == TextColor.None) continue;
                String color = textColor.getColor();
                regex.append("[").append(color).append("]").append("*");
            }
        }
        return Pattern.compile(regex.append(")").toString());
    }

    public void setPingBypassName(String name) {
        this.pingBypass = this.compile(name);
    }
}
