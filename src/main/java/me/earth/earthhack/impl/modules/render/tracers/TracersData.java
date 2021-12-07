/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.render.tracers;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.render.tracers.Tracers;

final class TracersData
extends DefaultData<Tracers> {
    public TracersData(Tracers module) {
        super(module);
        this.register(module.mode, "-Outline draws an outline around entities.\n-Fill draws a filled box around entities.\n-Stem draws a line at the entity.\n-Off just a tracer.");
        this.register(module.target, "The part of the entities hitbox to trace to.");
        this.register(module.players, "Draw tracers to players.");
        this.register(module.friends, "Draw tracers to friends.");
        this.register(module.invisibles, "Draw tracers to invisible entities.");
        this.register(module.monsters, "Draw tracers to monsters.");
        this.register(module.animals, "Draw tracers to animals.");
        this.register(module.vehicles, "Draw tracers to vehicles.");
        this.register(module.items, "Draw tracers to items.");
        this.register(module.misc, "Draw tracers to other entities.");
        this.register(module.lines, "Turns the actual tracers on and off.");
        this.register(module.lineWidth, "The width of a tracer.");
        this.register(module.tracers, "The maximum amount of tracers to draw.");
        this.register(module.minRange, "Entities have to be at least this far away to get a tracer drawn to them.");
        this.register(module.maxRange, "Entities need to be within this range to get a tracer drawn to them.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Draws lines to Entities.";
    }
}

