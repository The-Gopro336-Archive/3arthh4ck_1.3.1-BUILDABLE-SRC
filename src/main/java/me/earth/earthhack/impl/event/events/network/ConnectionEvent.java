package me.earth.earthhack.impl.event.events.network;

import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;

public class ConnectionEvent {
    private final EntityPlayer player;
    private final String name;
    private final UUID uuid;

    private ConnectionEvent(String name, UUID uuid, EntityPlayer player) {
        this.player = player;
        this.name = name;
        this.uuid = uuid;
    }

    public EntityPlayer getPlayer() {
        return this.player;
    }

    public String getName() {
        if (this.name == null && this.player != null) {
            return this.player.getName();
        }
        return this.name;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public static class Leave
    extends ConnectionEvent {
        public Leave(String name, UUID uuid, EntityPlayer player) {
            super(name, uuid, player);
        }
    }

    public static class Join
    extends ConnectionEvent {
        public Join(String name, UUID uuid, EntityPlayer player) {
            super(name, uuid, player);
        }
    }
}
