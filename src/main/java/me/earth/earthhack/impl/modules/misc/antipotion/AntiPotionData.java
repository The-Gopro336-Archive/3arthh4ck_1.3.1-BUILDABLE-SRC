package me.earth.earthhack.impl.modules.misc.antipotion;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.misc.antipotion.AntiPotion;

final class AntiPotionData
extends DefaultData<AntiPotion> {
    public AntiPotionData(AntiPotion module) {
        super(module);
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Allows you to remove potions on the client side. Only works for some potions like levitation.";
    }
}
