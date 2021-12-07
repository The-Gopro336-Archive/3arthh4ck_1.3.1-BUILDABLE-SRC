package me.earth.earthhack.impl.util.helpers.blocks.modes;

import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Pop;
import net.minecraft.init.Items;

final class Pop$2
extends Pop {
    @Override
    public boolean shouldPop(float damage, int popTime) {
        return None.shouldPop(damage, popTime) || Pop$2.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING && Managers.COMBAT.lastPop(Pop$2.mc.player) < (long)popTime;
    }
}
