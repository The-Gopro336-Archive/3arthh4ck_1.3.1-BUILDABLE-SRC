package me.earth.earthhack.impl.event.events.network;

import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import net.minecraft.client.multiplayer.WorldClient;

public class WorldClientEvent$Unload
extends WorldClientEvent {
    public WorldClientEvent$Unload(WorldClient client) {
        super(client, null);
    }
}
