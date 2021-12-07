package me.earth.earthhack.impl.event.events.misc;

import me.earth.earthhack.api.util.interfaces.Globals;

public class TickEvent
implements Globals {
    public boolean isSafe() {
        return TickEvent.mc.player != null && TickEvent.mc.world != null;
    }

    public static final class Post
    extends TickEvent {
    }

    public static final class PostWorldTick
    extends TickEvent {
    }
}
