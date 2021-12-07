/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.misc.tooltips;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.misc.tooltips.ToolTips;

final class ToolTipsData
extends DefaultData<ToolTips> {
    public ToolTipsData(ToolTips module) {
        super(module);
        this.register(module.shulkers, "Displays tooltips for shulkers.");
        this.register(module.shulkerSpy, "Displays and saves shulkers held by other players.");
        this.register(module.own, "With Shulkerspy: displays your own held shulker.");
        this.register(module.peekBind, "While hovering over a shulker in the gui, press this bind to peek into it.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Tooltips for shulkers etc.";
    }
}

