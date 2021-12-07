/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 */
package me.earth.earthhack.impl.modules.misc.announcer;

import me.earth.earthhack.impl.event.events.misc.DeathEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.announcer.Announcer;
import me.earth.earthhack.impl.modules.misc.announcer.util.Announcement;
import me.earth.earthhack.impl.modules.misc.announcer.util.AnnouncementType;
import net.minecraft.entity.Entity;

final class ListenerDeath
extends ModuleListener<Announcer, DeathEvent> {
    public ListenerDeath(Announcer module) {
        super(module, DeathEvent.class);
    }

    @Override
    public void invoke(DeathEvent event) {
        if (((Announcer)this.module).autoEZ.getValue().booleanValue() && ((Announcer)this.module).targets.remove((Object)event.getEntity()) && ListenerDeath.mc.player.getDistanceSq((Entity)event.getEntity()) <= 144.0) {
            ((Announcer)this.module).announcements.put(AnnouncementType.Death, new Announcement(event.getEntity().getName(), 0));
            ((Announcer)this.module).announcements.put(AnnouncementType.Totems, null);
        }
    }
}

