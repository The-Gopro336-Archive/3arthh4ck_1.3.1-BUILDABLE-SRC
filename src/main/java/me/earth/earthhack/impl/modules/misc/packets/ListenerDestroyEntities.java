/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.network.play.server.SPacketDestroyEntities
 */
package me.earth.earthhack.impl.modules.misc.packets;

import java.util.List;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.misc.packets.Packets;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.SPacketDestroyEntities;

final class ListenerDestroyEntities
extends ModuleListener<Packets, PacketEvent.Receive<SPacketDestroyEntities>> {
    public ListenerDestroyEntities(Packets module) {
        super(module, PacketEvent.Receive.class, Integer.MIN_VALUE, SPacketDestroyEntities.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketDestroyEntities> event) {
        if (((Packets)this.module).fastDestroyEntities.getValue().booleanValue()) {
            List<Entity> entities = Managers.ENTITIES.getEntities();
            if (entities == null) {
                return;
            }
            for (int id : ((SPacketDestroyEntities)event.getPacket()).getEntityIDs()) {
                for (Entity entity : entities) {
                    if (entity == null || entity.getEntityId() != id) continue;
                    entity.setDead();
                }
            }
        }
    }
}

