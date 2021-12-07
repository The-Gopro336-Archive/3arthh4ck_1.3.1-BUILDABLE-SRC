/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.client.colors;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.colors.ListenerTick;

public class Colors
extends Module {
    public Colors() {
        super("Colors", Category.Client);
        this.register(Managers.COLOR.getColorSetting());
        this.register(Managers.COLOR.getRainbowSpeed());
        Bus.EVENT_BUS.register(new ListenerTick(this));
    }
}

