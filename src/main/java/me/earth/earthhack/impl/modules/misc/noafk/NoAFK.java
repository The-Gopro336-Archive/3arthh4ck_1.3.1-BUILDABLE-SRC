/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.misc.noafk;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.modules.misc.noafk.ListenerChat;
import me.earth.earthhack.impl.modules.misc.noafk.ListenerGameLoop;
import me.earth.earthhack.impl.modules.misc.noafk.ListenerInput;
import me.earth.earthhack.impl.modules.misc.noafk.NoAFKData;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.text.TextColor;

public class NoAFK
extends Module {
    private static final String DEFAULT = "I'm AFK! This message was brought to you by 3arthh4ck.";
    protected final Setting<Boolean> rotate = this.register(new BooleanSetting("Rotate", true));
    protected final Setting<Boolean> swing = this.register(new BooleanSetting("Swing", true));
    protected final Setting<Boolean> sneak = this.register(new BooleanSetting("Sneak", true));
    protected final Setting<Boolean> autoReply = this.register(new BooleanSetting("AutoReply", false));
    protected final Setting<String> message = this.register(new StringSetting("Message", "I'm AFK! This message was brought to you by 3arthh4ck."));
    protected final Setting<String> indicator = this.register(new StringSetting("Indicator", " whispers: "));
    protected final Setting<String> reply = this.register(new StringSetting("Reply", "/r "));
    protected final Setting<TextColor> color = this.register(new EnumSetting<TextColor>("Color", TextColor.LightPurple));
    protected final StopWatch swing_timer = new StopWatch();
    protected final StopWatch sneak_timer = new StopWatch();
    protected boolean sneaking;

    public NoAFK() {
        super("NoAFK", Category.Misc);
        this.listeners.add(new ListenerGameLoop(this));
        this.listeners.add(new ListenerChat(this));
        this.listeners.add(new ListenerInput(this));
        this.setData(new NoAFKData(this));
    }
}

