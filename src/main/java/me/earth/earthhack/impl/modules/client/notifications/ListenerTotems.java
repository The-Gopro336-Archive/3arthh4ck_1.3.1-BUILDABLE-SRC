package me.earth.earthhack.impl.modules.client.notifications;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.notifications.Notifications;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;

final class ListenerTotems
extends ModuleListener<Notifications, PacketEvent.Receive<SPacketEntityStatus>> {
    public ListenerTotems(Notifications module) {
        super(module, PacketEvent.Receive.class, SPacketEntityStatus.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntityStatus> event) {
        switch (((SPacketEntityStatus)event.getPacket()).getOpCode()) {
            case 3: {
                mc.addScheduledTask(() -> {
                    int pops;
                    Entity entity;
                    if (ListenerTotems.mc.world != null && (entity = ((SPacketEntityStatus)event.getPacket()).getEntity(ListenerTotems.mc.world)) instanceof EntityPlayer && (pops = Managers.COMBAT.getPops(entity)) > 0) {
                        ((Notifications)this.module).onDeath(entity, Managers.COMBAT.getPops(entity));
                    }
                });
                break;
            }
            case 35: {
                mc.addScheduledTask(() -> {
                    Entity entity = ((SPacketEntityStatus)event.getPacket()).getEntity(ListenerTotems.mc.world);
                    if (entity instanceof EntityPlayer) {
                        ((Notifications)this.module).onPop(entity, Managers.COMBAT.getPops(entity) + 1);
                    }
                });
                break;
            }
        }
    }
}
