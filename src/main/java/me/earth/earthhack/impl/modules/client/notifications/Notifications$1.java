package me.earth.earthhack.impl.modules.client.notifications;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.impl.event.events.client.PostInitEvent;

class Notifications$1
extends EventListener<PostInitEvent> {
    Notifications$1(Class target) {
        super(target);
    }

    @Override
    public void invoke(PostInitEvent event) {
        Notifications.this.createSettings();
    }
}
