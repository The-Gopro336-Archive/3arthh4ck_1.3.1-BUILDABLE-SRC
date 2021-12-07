/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.network.play.client.CPacketUseEntity
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.core.ducks.network.ICPacketUseEntity;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.world.World;

final class ListenerUseEntity
extends ModuleListener<AutoCrystal, PacketEvent.Post<CPacketUseEntity>> {
    public ListenerUseEntity(AutoCrystal module) {
        super(module, PacketEvent.Post.class, Integer.MAX_VALUE, CPacketUseEntity.class);
    }

    @Override
    public void invoke(PacketEvent.Post<CPacketUseEntity> event) {
        Entity entity = ((ICPacketUseEntity)event.getPacket()).getAttackedEntity();
        if (entity == null && (entity = ((CPacketUseEntity)event.getPacket()).getEntityFromWorld((World)ListenerUseEntity.mc.world)) == null) {
            return;
        }
        ((AutoCrystal)this.module).serverTimeHelper.onUseEntity((CPacketUseEntity)event.getPacket(), entity);
    }
}

