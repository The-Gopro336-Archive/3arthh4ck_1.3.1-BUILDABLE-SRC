/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.combat.autoarmor;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.combat.autoarmor.AutoArmor;

final class AutoArmorData
extends DefaultData<AutoArmor> {
    public AutoArmorData(AutoArmor module) {
        super(module);
        this.register(module.delay, "Delay for for moving around items in your inventory. Low values might cause inventory desync.");
        this.register(module.autoMend, "Takes armor of while you're mending and the armor reaches a certain threshold.");
        this.register(module.helmet, "The automend threshold to take the helmet off.");
        this.register(module.chest, "The automend threshold to take the chestplate off.");
        this.register(module.legs, "The automend threshold to take the leggings off.");
        this.register(module.boots, "The automend threshold to take the boots off.");
        this.register(module.curse, "If you want to allow Curse of Binding armor to be put on.");
        this.register(module.closest, "Automend calculates if its safe to take of armor. If an enemy player is closer than this value and can damage you armor won't be taken off.");
        this.register(module.maxDmg, "If more damage than this can be dealt to you armor won't be taken off.");
        this.register(module.newVer, "Takes 1.13+ mechanics into account while calculating your safety.");
        this.register(module.bedCheck, "Takes beds into account while calculating your safety.");
        this.register(module.noDesync, "Attempts to resync your inventory (BETA).");
        this.register(module.screenCheck, "Doesn't allow taking off armor while in a gui.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Automatically puts Armor on.";
    }
}

