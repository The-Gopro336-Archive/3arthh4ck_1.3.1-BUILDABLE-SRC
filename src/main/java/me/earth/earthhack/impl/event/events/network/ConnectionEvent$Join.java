package me.earth.earthhack.impl.event.events.network;

import java.util.UUID;
import me.earth.earthhack.impl.event.events.network.ConnectionEvent;
import net.minecraft.entity.player.EntityPlayer;

public class ConnectionEvent$Join
extends ConnectionEvent {
    public ConnectionEvent$Join(String name, UUID uuid, EntityPlayer player) {
        super(name, uuid, player, null);
    }
}
