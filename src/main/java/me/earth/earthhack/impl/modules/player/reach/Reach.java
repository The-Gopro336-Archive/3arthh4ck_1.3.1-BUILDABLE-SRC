/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.player.reach;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.player.reach.ListenerReach;
import me.earth.earthhack.impl.util.client.SimpleData;

public class Reach
extends Module {
    protected final Setting<Float> reach = this.register(new NumberSetting<Float>("Add", Float.valueOf(1.5f), Float.valueOf(0.0f), Float.valueOf(6.0f)));
    protected final Setting<Float> hitBox = this.register(new NumberSetting<Float>("HitBox", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(6.0f)));

    public Reach() {
        super("Reach", Category.Player);
        this.listeners.add(new ListenerReach(this));
        SimpleData data = new SimpleData(this, "Allows you to interact with blocks and entities outside your normal range.");
        data.register(this.reach, "Range in blocks that you want to add to your normal reach.");
        data.register(this.hitBox, "Makes entities hitboxes bigger.");
        this.setData(data);
    }
}

