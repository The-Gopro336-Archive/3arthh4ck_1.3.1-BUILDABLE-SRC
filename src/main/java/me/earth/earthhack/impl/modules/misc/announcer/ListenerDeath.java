package me.earth.earthhack.impl.modules.misc.announcer;

import me.earth.earthhack.impl.event.events.misc.DeathEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.announcer.Announcer;
import me.earth.earthhack.impl.modules.misc.announcer.util.Announcement;
import me.earth.earthhack.impl.modules.misc.announcer.util.AnnouncementType;

final class ListenerDeath
extends ModuleListener<Announcer, DeathEvent> {
    public ListenerDeath(Announcer module) {
        super(module, DeathEvent.class);
    }

    @Override
    public void invoke(DeathEvent event) {
        if (((Announcer)this.module).autoEZ.getValue().booleanValue() && ((Announcer)this.module).targets.remove(event.getEntity()) && ListenerDeath.mc.player.getDistanceSq(event.getEntity()) <= 144.0) {
            ((Announcer)this.module).announcements.put(AnnouncementType.Death, new Announcement(event.getEntity().getName(), 0));
            ((Announcer)this.module).announcements.put(AnnouncementType.Totems, null);
        }
    }
}
