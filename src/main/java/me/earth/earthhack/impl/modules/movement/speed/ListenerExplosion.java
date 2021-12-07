package me.earth.earthhack.impl.modules.movement.speed;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.movement.speed.Speed;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.util.math.BlockPos;

final class ListenerExplosion
extends ModuleListener<Speed, PacketEvent.Receive<SPacketExplosion>> {
    public ListenerExplosion(Speed module) {
        super(module, PacketEvent.Receive.class, SPacketExplosion.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketExplosion> event) {
        SPacketExplosion packet;
        BlockPos pos;
        if (((Speed)this.module).explosions.getValue().booleanValue() && MovementUtil.isMoving() && Managers.NCP.passed(((Speed)this.module).lagTime.getValue()) && ListenerExplosion.mc.player.getDistanceSq(pos = new BlockPos((packet = (SPacketExplosion)event.getPacket()).getX(), packet.getY(), packet.getZ())) < 100.0 && (!((Speed)this.module).directional.getValue().booleanValue() || !MovementUtil.isInMovementDirection(packet.getX(), packet.getY(), packet.getZ()))) {
            double speed = Math.sqrt(packet.getMotionX() * packet.getMotionX() + packet.getMotionZ() * packet.getMotionZ());
            double d = ((Speed)this.module).lastExp = ((Speed)this.module).expTimer.passed(((Speed)this.module).coolDown.getValue().intValue()) ? speed : speed - ((Speed)this.module).lastExp;
            if (((Speed)this.module).lastExp > 0.0) {
                ((Speed)this.module).expTimer.reset();
                mc.addScheduledTask(() -> {
                    ((Speed)this.module).speed += ((Speed)this.module).lastExp * (double)((Speed)this.module).multiplier.getValue().floatValue();
                    ((Speed)this.module).distance += ((Speed)this.module).lastExp * (double)((Speed)this.module).multiplier.getValue().floatValue();
                    if (ListenerExplosion.mc.player.motionY > 0.0) {
                        ListenerExplosion.mc.player.motionY *= (double)((Speed)this.module).vertical.getValue().floatValue();
                    }
                });
            }
        }
    }
}
