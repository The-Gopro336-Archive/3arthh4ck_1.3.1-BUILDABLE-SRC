package me.earth.earthhack.impl.modules.player.arrows;

import net.minecraft.potion.Potion;

final class Arrows$SpecialPot
extends Potion {
    public static final Arrows$SpecialPot SPECTRAL = new Arrows$SpecialPot();
    public static final Arrows$SpecialPot NONE = new Arrows$SpecialPot();

    private Arrows$SpecialPot() {
        super(false, 0);
    }
}
