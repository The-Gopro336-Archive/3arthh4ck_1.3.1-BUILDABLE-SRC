/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.misc.autoreconnect;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.misc.autoreconnect.AutoReconnect;

final class AutoReconnectData
extends DefaultData<AutoReconnect> {
    public AutoReconnectData(AutoReconnect module) {
        super(module);
        this.register(module.delay, "After this delay in seconds passed you will be reconnected.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Automatically reconnects you after you got kicked.";
    }
}

