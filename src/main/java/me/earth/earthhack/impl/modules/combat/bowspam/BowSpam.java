/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.combat.bowspam;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.combat.bowspam.BowSpamData;
import me.earth.earthhack.impl.modules.combat.bowspam.ListenerMotion;
import me.earth.earthhack.impl.modules.combat.bowspam.ListenerMove;

public class BowSpam
extends Module {
    protected final Setting<Integer> delay = this.register(new NumberSetting<Integer>("Delay", 10, 0, 20));
    protected final Setting<Boolean> tpsSync = this.register(new BooleanSetting("TPS-Sync", true));
    protected final Setting<Boolean> bowBomb = this.register(new BooleanSetting("BowBomb", false));
    protected final Setting<Boolean> rape = this.register(new BooleanSetting("Rape", false));

    public BowSpam() {
        super("BowSpam", Category.Combat);
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerMove(this));
        this.setData(new BowSpamData(this));
    }
}

