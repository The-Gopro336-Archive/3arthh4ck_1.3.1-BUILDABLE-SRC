package me.earth.earthhack.impl.event.events.network;

import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import net.minecraft.client.multiplayer.WorldClient;

public class WorldClientEvent$Load
extends WorldClientEvent {
    public WorldClientEvent$Load(WorldClient client) {
        super(client, null);
    }
}
