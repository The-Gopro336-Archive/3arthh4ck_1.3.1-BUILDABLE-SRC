package me.earth.earthhack.impl.util.minecraft;

import me.earth.earthhack.impl.util.minecraft.ArmUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import net.minecraft.util.EnumHand;

final class Swing$4
extends Swing {
    @Override
    public void swing(EnumHand hand) {
        ArmUtil.swingArmNoPacket(hand);
    }
}
