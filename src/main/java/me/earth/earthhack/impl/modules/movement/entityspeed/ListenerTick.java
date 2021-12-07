package me.earth.earthhack.impl.modules.movement.entityspeed;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.boatfly.BoatFly;
import me.earth.earthhack.impl.modules.movement.entityspeed.EntitySpeed;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

final class ListenerTick
extends ModuleListener<EntitySpeed, TickEvent> {
    private static final ModuleCache<BoatFly> BOAT_FLY = Caches.getModule(BoatFly.class);

    public ListenerTick(EntitySpeed module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        if (!event.isSafe()) {
            return;
        }
        Entity riding = ListenerTick.mc.player.getRidingEntity();
        if (riding == null) {
            return;
        }
        double cosYaw = Math.cos(Math.toRadians(ListenerTick.mc.player.rotationYaw + 90.0f));
        double sinYaw = Math.sin(Math.toRadians(ListenerTick.mc.player.rotationYaw + 90.0f));
        BlockPos pos = new BlockPos(ListenerTick.mc.player.posX + 2.0 * cosYaw + 0.0 * sinYaw, ListenerTick.mc.player.posY, ListenerTick.mc.player.posZ + (2.0 * sinYaw - 0.0 * cosYaw));
        BlockPos down = new BlockPos(ListenerTick.mc.player.posX + 2.0 * cosYaw + 0.0 * sinYaw, ListenerTick.mc.player.posY - 1.0, ListenerTick.mc.player.posZ + (2.0 * sinYaw - 0.0 * cosYaw));
        if (!riding.onGround && !ListenerTick.mc.world.getBlockState(pos).func_185904_a().blocksMovement() && !ListenerTick.mc.world.getBlockState(down).func_185904_a().blocksMovement() && ((EntitySpeed)this.module).noStuck.getValue().booleanValue()) {
            EntitySpeed.strafe(0.0);
            ((EntitySpeed)this.module).stuckTimer.reset();
            return;
        }
        pos = new BlockPos(ListenerTick.mc.player.posX + 2.0 * cosYaw + 0.0 * sinYaw, ListenerTick.mc.player.posY, ListenerTick.mc.player.posZ + (2.0 * sinYaw - 0.0 * cosYaw));
        if (ListenerTick.mc.world.getBlockState(pos).func_185904_a().blocksMovement() && ((EntitySpeed)this.module).noStuck.getValue().booleanValue()) {
            EntitySpeed.strafe(0.0);
            ((EntitySpeed)this.module).stuckTimer.reset();
            return;
        }
        pos = new BlockPos(ListenerTick.mc.player.posX + cosYaw + 0.0 * sinYaw, ListenerTick.mc.player.posY + 1.0, ListenerTick.mc.player.posZ + (sinYaw - 0.0 * cosYaw));
        if (ListenerTick.mc.world.getBlockState(pos).func_185904_a().blocksMovement() && ((EntitySpeed)this.module).noStuck.getValue().booleanValue()) {
            EntitySpeed.strafe(0.0);
            ((EntitySpeed)this.module).stuckTimer.reset();
            return;
        }
        if (ListenerTick.mc.player.movementInput.jump) {
            ((EntitySpeed)this.module).jumpTimer.reset();
        }
        if (((EntitySpeed)this.module).stuckTimer.passed(((EntitySpeed)this.module).stuckTime.getValue().intValue()) || !((EntitySpeed)this.module).noStuck.getValue().booleanValue()) {
            if (!(riding.isInWater() || BOAT_FLY.isEnabled() || ListenerTick.mc.player.movementInput.jump || !((EntitySpeed)this.module).jumpTimer.passed(1000L) || PositionUtil.inLiquid())) {
                if (riding.onGround) {
                    riding.motionY = 0.4;
                }
                riding.motionY = -0.4;
            }
            EntitySpeed.strafe(((EntitySpeed)this.module).speed.getValue().floatValue());
            if (((EntitySpeed)this.module).resetStuck.getValue().booleanValue()) {
                ((EntitySpeed)this.module).stuckTimer.reset();
            }
        }
    }
}
