package me.earth.earthhack.impl.util.helpers.blocks.modes;

import me.earth.earthhack.impl.util.helpers.blocks.modes.Pop;
import net.minecraft.init.Items;

final class Pop$3
extends Pop {
    @Override
    public boolean shouldPop(float damage, int popTime) {
        return None.shouldPop(damage, popTime) || Pop$3.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING;
    }
}
