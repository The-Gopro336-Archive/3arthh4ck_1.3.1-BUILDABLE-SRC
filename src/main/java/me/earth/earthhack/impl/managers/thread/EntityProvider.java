/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 */
package me.earth.earthhack.impl.managers.thread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class EntityProvider
extends SubscriberImpl
implements Globals {
    private volatile List<EntityPlayer> players = Collections.emptyList();
    private volatile List<Entity> entities = Collections.emptyList();

    public EntityProvider() {
        this.listeners.add(new EventListener<TickEvent.PostWorldTick>(TickEvent.PostWorldTick.class){

            @Override
            public void invoke(TickEvent.PostWorldTick event) {
                EntityProvider.this.update();
            }
        });
    }

    private void update() {
        if (EntityProvider.mc.world != null) {
            this.setLists(new ArrayList<Entity>(EntityProvider.mc.world.loadedEntityList), new ArrayList<EntityPlayer>(EntityProvider.mc.world.playerEntities));
        } else {
            this.setLists(Collections.emptyList(), Collections.emptyList());
        }
    }

    private void setLists(List<Entity> loadedEntities, List<EntityPlayer> playerEntities) {
        this.entities = loadedEntities;
        this.players = playerEntities;
    }

    public List<Entity> getEntities() {
        return this.entities;
    }

    public List<EntityPlayer> getPlayers() {
        return this.players;
    }

    public List<Entity> getEntitiesAsync() {
        return this.getEntities(!mc.isCallingFromMinecraftThread());
    }

    public List<EntityPlayer> getPlayersAsync() {
        return this.getPlayers(!mc.isCallingFromMinecraftThread());
    }

    public List<Entity> getEntities(boolean async) {
        return async ? this.entities : EntityProvider.mc.world.loadedEntityList;
    }

    public List<EntityPlayer> getPlayers(boolean async) {
        return async ? this.players : EntityProvider.mc.world.playerEntities;
    }

    public Entity getEntity(int id) {
        List<Entity> entities = this.getEntitiesAsync();
        if (entities != null) {
            return entities.stream().filter(e -> e != null && e.getEntityId() == id).findFirst().orElse(null);
        }
        return null;
    }
}

