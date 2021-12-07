/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.movement.fastswim;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.movement.fastswim.FastSwim;

final class FastSwimData
extends DefaultData<FastSwim> {
    public FastSwimData(FastSwim module) {
        super(module);
        this.register(module.vWater, "Multiplier for moving vertically through water.");
        this.register(module.hWater, "Multiplier for moving horizontally through water.");
        this.register(module.vLava, "Multiplier for moving vertically through lava.");
        this.register(module.hLava, "Multiplier for moving horizontally through lava.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Swim faster.";
    }
}

