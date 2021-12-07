/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.movement.longjump;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.movement.longjump.LongJump;

final class LongJumpData
extends DefaultData<LongJump> {
    public LongJumpData(LongJump module) {
        super(module);
        this.register(module.mode, "-Normal best more for Anarchy\n-Cowabunga ...");
        this.register(module.boost, "Amount your jump will be boosted by.");
        this.register(module.noKick, "Prevents you from getting kicked by disabling this module automatically.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Makes you jump far.";
    }
}

