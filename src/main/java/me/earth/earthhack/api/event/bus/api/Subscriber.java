package me.earth.earthhack.api.event.bus.api;

import java.util.Collection;
import me.earth.earthhack.api.event.bus.api.Listener;

public interface Subscriber {
    public Collection<Listener<?>> getListeners();
}
