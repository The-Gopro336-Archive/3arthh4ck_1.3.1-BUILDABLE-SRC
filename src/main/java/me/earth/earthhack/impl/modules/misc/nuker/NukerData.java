/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.misc.nuker;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.misc.nuker.Nuker;

final class NukerData
extends DefaultData<Nuker> {
    public NukerData(Nuker module) {
        super(module);
        this.register(module.nuke, "Classical Nuker. Other options would be the Shulkers and Hoppers setting down below.");
        this.register(module.blocks, "Blocks that should be attacked in one tick.");
        this.register(module.delay, "Helps you not to accidentally spam the Nuker.");
        this.register(module.rotate, "-None no rotations\n-Normal normal legit rotations, you can only break 1 block per tick when using these.\n-Packet spams rotation packets, might lag you back.");
        this.register(module.width, "The width of the nuked selection.");
        this.register(module.height, "The height of the nuked selection.");
        this.register(module.range, "Blocks within this range will be nuked.");
        this.register(module.render, "Renders the selection that should be nuked.");
        this.register(module.color, "Color of the Render.");
        this.register(module.shulkers, "Nukes shulkers around you.");
        this.register(module.hoppers, "Nukes hoppers around you.");
        this.register(module.instant, "For the shulker/hopper nuker. Instantly mines them when they spawn.");
        this.register(module.swing, "Swings your arm.");
        this.register(module.speedMine, "Currently required.");
        this.register(module.autoTool, "Automatically finds the best Tool to nuke with.");
        this.register(module.timeout, "Interval in milliseconds between 2 nukes.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Mines blocks around you.";
    }
}

