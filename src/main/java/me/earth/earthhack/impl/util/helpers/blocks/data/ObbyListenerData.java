package me.earth.earthhack.impl.util.helpers.blocks.data;

import me.earth.earthhack.impl.util.helpers.blocks.ObbyListenerModule;
import me.earth.earthhack.impl.util.helpers.blocks.data.ObbyData;

public class ObbyListenerData<T extends ObbyListenerModule<?>>
extends ObbyData<T> {
    public ObbyListenerData(T module) {
        super(module);
        this.register(((ObbyListenerModule)module).confirm, "Time from placing a block until it's confirmed by the server.");
    }
}
