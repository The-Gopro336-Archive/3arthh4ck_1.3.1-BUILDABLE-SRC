/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.network.play.server.SPacketEntityVelocity
 */
package me.earth.earthhack.impl.modules.movement.speed;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.speed.Speed;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.server.SPacketEntityVelocity;

final class ListenerVelocity
extends ModuleListener<Speed, PacketEvent.Receive<SPacketEntityVelocity>> {
    public ListenerVelocity(Speed module) {
        super(module, PacketEvent.Receive.class, SPacketEntityVelocity.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntityVelocity> event) {
        SPacketEntityVelocity packet = (SPacketEntityVelocity)event.getPacket();
        EntityPlayerSP player = ListenerVelocity.mc.player;
        if (player != null && packet.getEntityID() == player.getEntityId() && !((Speed)this.module).directional.getValue().booleanValue() && ((Speed)this.module).velocity.getValue().booleanValue()) {
            double speed = Math.sqrt(packet.getMotionX() * packet.getMotionX() + packet.getMotionZ() * packet.getMotionZ()) / 8000.0;
            double d = ((Speed)this.module).lastExp = ((Speed)this.module).expTimer.passed(((Speed)this.module).coolDown.getValue().intValue()) ? speed : speed - ((Speed)this.module).lastExp;
            if (((Speed)this.module).lastExp > 0.0) {
                ((Speed)this.module).expTimer.reset();
                mc.addScheduledTask(() -> {
                    ((Speed)this.module).speed += ((Speed)this.module).lastExp * (double)((Speed)this.module).multiplier.getValue().floatValue();
                    ((Speed)this.module).distance += ((Speed)this.module).lastExp * (double)((Speed)this.module).multiplier.getValue().floatValue();
                    if (ListenerVelocity.mc.player.motionY > 0.0 && ((Speed)this.module).vertical.getValue().floatValue() != 0.0f) {
                        ListenerVelocity.mc.player.motionY *= (double)((Speed)this.module).vertical.getValue().floatValue();
                    }
                });
            }
        }
    }
}

