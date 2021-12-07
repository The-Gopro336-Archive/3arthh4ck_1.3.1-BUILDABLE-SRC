/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package me.earth.earthhack.impl.modules.misc.spammer;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.misc.spammer.ListenerUpdate;
import me.earth.earthhack.impl.modules.misc.spammer.SpammerData;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.misc.FileUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;

public class Spammer
extends Module {
    private static final String FILE = "earthhack/util/Spammer.txt";
    private static final String DEFAULT = "Good Fight!";
    private static final Random RND = new Random();
    protected final Setting<Integer> delay = this.register(new NumberSetting<Integer>("Delay", 5, 1, 60));
    protected final Setting<Boolean> random = this.register(new BooleanSetting("Random", false));
    protected final Setting<Boolean> antiKick = this.register(new BooleanSetting("AntiKick", false));
    protected final Setting<Boolean> greenText = this.register(new BooleanSetting("GreenText", false));
    protected final Setting<Boolean> refresh = this.register(new BooleanSetting("Refresh", false));
    protected final Setting<Boolean> autoOff = this.register(new BooleanSetting("AutoOff", false));
    protected final List<String> messages = new ArrayList<String>();
    protected final StopWatch timer = new StopWatch();
    protected int currentIndex = 0;

    public Spammer() {
        super("Spammer", Category.Misc);
        this.listeners.add(new ListenerUpdate(this));
        this.refresh.addObserver(event -> {
            ChatUtil.sendMessage("<" + this.getDisplayName() + "> Reloading File...");
            this.loadFile();
            this.currentIndex = 0;
            event.setCancelled(true);
        });
        this.setData(new SpammerData(this));
    }

    @Override
    protected void onLoad() {
        this.loadFile();
    }

    @Override
    protected void onEnable() {
        this.currentIndex = 0;
    }

    private void loadFile() {
        this.messages.clear();
        for (String string : FileUtil.readFile(FILE, true, Lists.newArrayList((Object[])new String[]{DEFAULT}))) {
            if (string.replace("\\s", "").isEmpty()) continue;
            this.messages.add(string);
        }
    }

    protected String getSuffixedMessage() {
        return this.getMessage() + this.getSuffix();
    }

    protected String getSuffix() {
        if (this.antiKick.getValue().booleanValue()) {
            return ChatUtil.generateRandomHexSuffix(2);
        }
        return "";
    }

    private String getMessage() {
        String result;
        if (this.messages.isEmpty()) {
            return DEFAULT;
        }
        if (this.random.getValue().booleanValue() && (result = this.messages.get(RND.nextInt(this.messages.size()))) != null) {
            return result;
        }
        result = this.messages.get(this.currentIndex);
        ++this.currentIndex;
        if (this.currentIndex >= this.messages.size()) {
            this.currentIndex = 0;
        }
        if (result != null) {
            return result;
        }
        return DEFAULT;
    }
}

