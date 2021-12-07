package me.earth.earthhack.impl.managers.client;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.earthhack.api.observable.Observable;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.client.event.PlayerEvent;
import me.earth.earthhack.impl.managers.client.event.PlayerEventType;
import me.earth.earthhack.impl.managers.thread.lookup.LookUp;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerManager
extends Observable<PlayerEvent> {
    private final Map<String, UUID> players = new ConcurrentHashMap<String, UUID>();

    public boolean contains(Entity player) {
        if (player instanceof EntityPlayer) {
            return this.contains(player.getName());
        }
        return false;
    }

    public boolean contains(EntityPlayer player) {
        return this.contains(player.getName());
    }

    public boolean contains(String name) {
        return this.players.containsKey(name);
    }

    public void add(EntityPlayer player) {
        this.add(player.getName(), player.getUniqueID());
    }

    public void add(String name) {
        Managers.LOOK_UP.doLookUp(new LookUp(LookUp.Type.UUID, name){

            @Override
            public void onSuccess() {
                PlayerManager.this.add(this.name, this.uuid);
            }

            @Override
            public void onFailure() {
            }
        });
    }

    public void add(String name, UUID uuid) {
        PlayerEvent event = new PlayerEvent(PlayerEventType.ADD, name, uuid);
        this.onChange(event);
        if (!event.isCancelled()) {
            this.players.put(name, uuid);
        }
    }

    public void remove(Entity player) {
        if (player instanceof EntityPlayer) {
            this.remove(player.getName());
        }
    }

    public void remove(String name) {
        UUID uuid = this.players.get(name);
        PlayerEvent event = new PlayerEvent(PlayerEventType.DEL, name, uuid);
        this.onChange(event);
        if (!event.isCancelled()) {
            this.players.remove(name);
        }
    }

    public Collection<String> getPlayers() {
        return this.players.keySet();
    }

    public Map<String, UUID> getPlayersWithUUID() {
        return this.players;
    }

    public void clear() {
        this.players.clear();
    }
}
