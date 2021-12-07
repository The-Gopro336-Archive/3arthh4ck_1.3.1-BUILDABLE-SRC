package me.earth.earthhack.impl.event.events.misc;

import me.earth.earthhack.impl.event.events.misc.ClickBlockEvent;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ClickBlockEvent$Right
extends ClickBlockEvent {
    private final Vec3d vec;
    private final EnumHand hand;

    public ClickBlockEvent$Right(BlockPos pos, EnumFacing facing, Vec3d vec, EnumHand hand) {
        super(pos, facing);
        this.vec = vec;
        this.hand = hand;
    }

    public EnumHand getHand() {
        return this.hand;
    }

    public Vec3d getVec() {
        return this.vec;
    }
}
