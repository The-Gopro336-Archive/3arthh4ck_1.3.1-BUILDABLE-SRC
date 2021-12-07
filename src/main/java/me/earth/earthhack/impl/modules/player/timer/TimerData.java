/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.player.timer;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.player.timer.Timer;

final class TimerData
extends DefaultData<Timer> {
    public TimerData(Timer module) {
        super(module);
        this.register(module.mode, "-Normal normal Timer\n-Physics a timer that doesn't make all the other entities in the world move quickly.\n-Switch a timer that switches between different values quickly.\n-Blink (BETA)");
        this.register(module.autoOff, "Turns the module off after this value in milliseconds passed.");
        this.register(module.lagTime, "Waits for this delay in milliseconds after we got lagged back by the server before attempting to timer again.");
        this.register(module.speed, "Speed for the standard timer.");
        this.register(module.updates, "Updates for the Physics timer.");
        this.register(module.fast, "Fast Speed for Mode-Switch.");
        this.register(module.fastTime, "Fast Speed will be active for this time in milliseconds when using Mode-Switch.");
        this.register(module.slow, "Slow Speed for Mode-Switch.");
        this.register(module.slowTime, "Slow Speed will be active for this time in milliseconds when using Mode-Switch.");
        this.register(module.maxPackets, "Maximum Packets send by Mode-Blink.");
        this.register(module.offset, "Packet Offset for Mode-Blink.");
        this.register(module.letThrough, "Lets through every n-th packet. A value of 0 means no packets will be let through.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Makes you move faster by sending more packets to the server.";
    }
}

