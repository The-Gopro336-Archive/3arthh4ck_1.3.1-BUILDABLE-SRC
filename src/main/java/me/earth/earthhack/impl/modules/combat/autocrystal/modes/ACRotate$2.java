package me.earth.earthhack.impl.modules.combat.autocrystal.modes;

import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;

final class ACRotate$2
extends ACRotate {
    @Override
    public boolean noRotate(ACRotate rotate) {
        return rotate == Place || rotate == None;
    }
}
