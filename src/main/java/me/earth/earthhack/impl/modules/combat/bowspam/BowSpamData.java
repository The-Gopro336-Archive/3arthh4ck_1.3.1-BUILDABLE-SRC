/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.combat.bowspam;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.combat.bowspam.BowSpam;

final class BowSpamData
extends DefaultData<BowSpam> {
    public BowSpamData(BowSpam module) {
        super(module);
        this.register(module.delay, "The delay in ticks (~50ms) between each arrow. While low delays feel impressive 10 ticks should already max out your damage.");
        this.register(module.tpsSync, "If the delay should be synced with the servers TPS.");
        this.register(module.bowBomb, "Use this while Flying above the enemy. Will make your arrows deal more damage.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Spam arrows quickly.";
    }
}

