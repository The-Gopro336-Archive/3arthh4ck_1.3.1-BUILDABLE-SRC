package me.earth.earthhack.impl.modules.combat.autocrystal.modes;

import me.earth.earthhack.impl.modules.combat.autocrystal.modes.AntiFriendPop;

final class AntiFriendPop$2
extends AntiFriendPop {
    @Override
    public boolean shouldCalc(AntiFriendPop type) {
        return type == Place;
    }
}
