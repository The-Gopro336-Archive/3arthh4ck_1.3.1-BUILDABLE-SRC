/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.network.play.server.SPacketEntityProperties
 *  net.minecraft.network.play.server.SPacketEntityProperties$Snapshot
 */
package me.earth.earthhack.impl.modules.combat.autoarmor;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.autoarmor.AutoArmor;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.network.play.server.SPacketEntityProperties;

final class ListenerEntityProperties
extends ModuleListener<AutoArmor, PacketEvent.Receive<SPacketEntityProperties>> {
    public ListenerEntityProperties(AutoArmor module) {
        super(module, PacketEvent.Receive.class, SPacketEntityProperties.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntityProperties> event) {
        EntityPlayerSP player = ListenerEntityProperties.mc.player;
        if (player != null && ((SPacketEntityProperties)event.getPacket()).getEntityId() == player.getEntityId()) {
            for (SPacketEntityProperties.Snapshot snapShot : ((SPacketEntityProperties)event.getPacket()).getSnapshots()) {
                if (!snapShot.getName().equals(SharedMonsterAttributes.ARMOR.getName())) continue;
                ((AutoArmor)this.module).propertyTimer.reset();
                break;
            }
        }
    }
}

