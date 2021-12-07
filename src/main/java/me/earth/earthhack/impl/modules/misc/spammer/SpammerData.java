/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.misc.spammer;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.misc.spammer.Spammer;

final class SpammerData
extends DefaultData<Spammer> {
    public SpammerData(Spammer module) {
        super(module);
        this.register(module.delay, "The interval in seconds that messages get send in.");
        this.register(module.random, "Randomly selects a line from your spammer file.");
        this.register(module.antiKick, "Tricks antispams by appending a random suffix.");
        this.register(module.greenText, "Prepends a \">\".");
        this.register(module.refresh, "Refreshes the spammer file under earthhack/util.");
        this.register(module.autoOff, "Turns this module off after it has sent a message.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Spams messages.";
    }
}

