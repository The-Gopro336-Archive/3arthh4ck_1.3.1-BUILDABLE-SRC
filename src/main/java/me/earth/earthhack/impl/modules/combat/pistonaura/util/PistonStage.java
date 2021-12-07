/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.modules.combat.pistonaura.util;

import java.util.function.Function;
import me.earth.earthhack.impl.modules.combat.pistonaura.util.PistonData;
import net.minecraft.util.math.BlockPos;

public enum PistonStage {
    CRYSTAL(PistonData::getCrystalPos),
    PISTON(PistonData::getPistonPos),
    REDSTONE(PistonData::getRedstonePos),
    BREAK(data -> data.getCrystalPos().up());

    private final Function<PistonData, BlockPos> function;

    private PistonStage(Function<PistonData, BlockPos> function) {
        this.function = function;
    }

    public BlockPos getPos(PistonData data) {
        return this.function.apply(data);
    }
}

