/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.client.management;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.management.Management;
import me.earth.earthhack.impl.modules.client.media.Media;

final class ListenerTick
extends ModuleListener<Management, TickEvent> {
    private static final ModuleCache<Media> MEDIA = Caches.getModule(Media.class);

    public ListenerTick(Management module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        if (((Management)this.module).friend.getValue().booleanValue() && ((Management)this.module).lastProfile != null && !((Management)this.module).lastProfile.equals((Object)mc.getSession().getProfile())) {
            ((Management)this.module).lastProfile = mc.getSession().getProfile();
            Managers.FRIENDS.add(((Management)this.module).lastProfile.getName(), ((Management)this.module).lastProfile.getId());
            MEDIA.computeIfPresent(Media::reload);
        }
    }
}

