/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 */
package me.earth.earthhack.impl.modules.client.notifications;

import me.earth.earthhack.impl.event.events.misc.DeathEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.notifications.Notifications;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

final class ListenerDeath
extends ModuleListener<Notifications, DeathEvent> {
    public ListenerDeath(Notifications module) {
        super(module, DeathEvent.class);
    }

    @Override
    public void invoke(DeathEvent event) {
        int pops;
        if (event.getEntity() instanceof EntityPlayer && (pops = Managers.COMBAT.getPops((Entity)event.getEntity())) > 0) {
            ((Notifications)this.module).onDeath((Entity)event.getEntity(), Managers.COMBAT.getPops((Entity)event.getEntity()));
        }
    }
}

