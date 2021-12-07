package me.earth.earthhack.impl.util.helpers.blocks.modes;

import me.earth.earthhack.impl.util.helpers.blocks.modes.Pop;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;

final class Pop$1
extends Pop {
    @Override
    public boolean shouldPop(float damage, int popTime) {
        return (double)damage < (double)EntityUtil.getHealth(Pop$1.mc.player) + 1.0;
    }
}
