package me.earth.earthhack.impl.modules.movement.jesus;

import java.util.Objects;
import me.earth.earthhack.impl.event.events.misc.CollisionEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.jesus.Jesus;
import me.earth.earthhack.impl.modules.movement.jesus.mode.JesusMode;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import net.minecraft.block.BlockLiquid;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

final class ListenerCollision
extends ModuleListener<Jesus, CollisionEvent> {
    public ListenerCollision(Jesus module) {
        super(module, CollisionEvent.class);
    }

    @Override
    public void invoke(CollisionEvent event) {
        if (event.getEntity() != null && ListenerCollision.mc.player != null && (event.getEntity().equals(ListenerCollision.mc.player) && ((Jesus)this.module).mode.getValue() != JesusMode.Dolphin || event.getEntity().getControllingPassenger() != null && Objects.equals(event.getEntity().getControllingPassenger(), ListenerCollision.mc.player)) && event.getBlock() instanceof BlockLiquid && !ListenerCollision.mc.player.isSneaking() && ListenerCollision.mc.player.fallDistance < 3.0f && !PositionUtil.inLiquid() && PositionUtil.inLiquid(false) && PositionUtil.isAbove(event.getPos())) {
            BlockPos pos = event.getPos();
            event.setBB(new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, (double)pos.getY() + 0.99, pos.getZ() + 1));
        }
    }
}
