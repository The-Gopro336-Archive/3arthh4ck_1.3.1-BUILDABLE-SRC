package me.earth.earthhack.impl.modules.combat.criticals;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.combat.criticals.Criticals;

final class CriticalsData
extends DefaultData<Criticals> {
    public CriticalsData(Criticals module) {
        super(module);
        this.register(module.delay, "-Packet will deal criticals silently. \n-Bypass is like packet but works on 2b2t. \n-Jump will automatically jump. \n -MiniJump will jump as well but less.");
        this.register(module.noDesync, "Will not deal criticals against crystals which would otherwise make your AutoCrystal desync you.");
        this.register(module.delay, "The Delay in milliseconds between 2 criticals. Higher delays prevent desync even further.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Automatically deals critical hits when attacking Entities.";
    }
}
