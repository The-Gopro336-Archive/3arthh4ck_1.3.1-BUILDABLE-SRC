/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.projectile.EntityFishHook
 *  net.minecraft.network.play.INetHandlerPlayClient
 *  net.minecraft.network.play.server.SPacketEntityStatus
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.modules.movement.velocity;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.velocity.Velocity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.world.World;

final class ListenerBobber
extends ModuleListener<Velocity, PacketEvent.Receive<SPacketEntityStatus>> {
    public ListenerBobber(Velocity module) {
        super(module, PacketEvent.Receive.class, Integer.MIN_VALUE, SPacketEntityStatus.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntityStatus> event) {
        SPacketEntityStatus packet;
        if (((Velocity)this.module).bobbers.getValue().booleanValue() && (packet = (SPacketEntityStatus)event.getPacket()).getOpCode() == 31 && !event.isCancelled()) {
            event.setCancelled(true);
            mc.addScheduledTask(() -> {
                if (mc.getConnection() == null) {
                    return;
                }
                Entity entity = packet.getEntity((World)ListenerBobber.mc.world);
                if (entity instanceof EntityFishHook) {
                    EntityFishHook fishHook = (EntityFishHook)entity;
                    if (fishHook.caughtEntity != null && mc.getConnection() != null && !fishHook.caughtEntity.equals((Object)ListenerBobber.mc.player)) {
                        packet.processPacket((INetHandlerPlayClient)mc.getConnection());
                    }
                } else {
                    packet.processPacket((INetHandlerPlayClient)mc.getConnection());
                }
            });
        }
    }
}

