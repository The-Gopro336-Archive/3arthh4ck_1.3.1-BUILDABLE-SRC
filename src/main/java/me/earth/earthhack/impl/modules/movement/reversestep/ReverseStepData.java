/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.movement.reversestep;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.movement.reversestep.ReverseStep;

final class ReverseStepData
extends DefaultData<ReverseStep> {
    public ReverseStepData(ReverseStep module) {
        super(module);
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "";
    }
}

