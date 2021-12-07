/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.util.internal.ConcurrentSet
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.network.play.server.SPacketDestroyEntities
 *  net.minecraft.network.play.server.SPacketSoundEffect
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.Vec3d
 */
package me.earth.earthhack.impl.managers.minecraft.combat;

import io.netty.util.internal.ConcurrentSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.managers.minecraft.combat.util.CustomEntityTime;
import me.earth.earthhack.impl.managers.minecraft.combat.util.EntityTime;
import me.earth.earthhack.impl.managers.minecraft.combat.util.SoundObserver;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.management.Management;
import me.earth.earthhack.impl.util.math.MathUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;

public class SetDeadManager
extends SubscriberImpl
implements Globals {
    private static final SettingCache<Integer, NumberSetting<Integer>, Management> DEATH_TIME = Caches.getSetting(Management.class, Setting.class, "DeathTime", 500);
    private static final SettingCache<Boolean, BooleanSetting, Management> SOUND_REMOVE = Caches.getSetting(Management.class, BooleanSetting.class, "SoundRemove", true);
    private final Map<Integer, EntityTime> killed;
    private final Set<SoundObserver> observers = new ConcurrentSet();

    public SetDeadManager() {
        this.killed = new ConcurrentHashMap<Integer, EntityTime>();
        this.listeners.add(new EventListener<PacketEvent.Receive<SPacketSoundEffect>>(PacketEvent.Receive.class, Integer.MAX_VALUE, SPacketSoundEffect.class){

            @Override
            public void invoke(PacketEvent.Receive<SPacketSoundEffect> event) {
                SPacketSoundEffect p = (SPacketSoundEffect)event.getPacket();
                if (p.getCategory() == SoundCategory.BLOCKS && p.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE && SetDeadManager.this.shouldRemove()) {
                    Vec3d pos = new Vec3d(p.getX(), p.getY(), p.getZ());
                    Globals.mc.addScheduledTask(() -> {
                        SetDeadManager.this.removeCrystals(pos, 11.0f, Globals.mc.world.loadedEntityList);
                        for (SoundObserver observer : SetDeadManager.this.observers) {
                            if (!observer.shouldBeNotified()) continue;
                            observer.onChange(p);
                        }
                    });
                }
            }
        });
        this.listeners.add(new EventListener<PacketEvent.Receive<SPacketDestroyEntities>>(PacketEvent.Receive.class, Integer.MAX_VALUE, SPacketDestroyEntities.class){

            @Override
            public void invoke(PacketEvent.Receive<SPacketDestroyEntities> event) {
                Globals.mc.addScheduledTask(() -> {
                    for (int id : ((SPacketDestroyEntities)event.getPacket()).getEntityIDs()) {
                        SetDeadManager.this.confirmKill(id);
                    }
                });
            }
        });
        this.listeners.add(new EventListener<UpdateEvent>(UpdateEvent.class){

            @Override
            public void invoke(UpdateEvent event) {
                SetDeadManager.this.updateKilled();
            }
        });
        this.listeners.add(new EventListener<WorldClientEvent.Load>(WorldClientEvent.Load.class){

            @Override
            public void invoke(WorldClientEvent.Load event) {
                SetDeadManager.this.clear();
            }
        });
        this.listeners.add(new EventListener<WorldClientEvent.Unload>(WorldClientEvent.Unload.class){

            @Override
            public void invoke(WorldClientEvent.Unload event) {
                SetDeadManager.this.clear();
            }
        });
    }

    public Entity getEntity(int id) {
        EntityTime time = this.killed.get(id);
        if (time != null) {
            return time.getEntity();
        }
        return null;
    }

    public void setDeadCustom(Entity entity, long t) {
        EntityTime time = this.killed.get(entity.getEntityId());
        if (time instanceof CustomEntityTime) {
            time.getEntity().setDead();
            time.reset();
        } else {
            entity.setDead();
            this.killed.put(entity.getEntityId(), new CustomEntityTime(entity, t));
        }
    }

    public void revive(int id) {
        EntityTime time = this.killed.remove(id);
        if (time != null && time.isValid()) {
            Entity entity = time.getEntity();
            entity.isDead = false;
            SetDeadManager.mc.world.addEntityToWorld(entity.getEntityId(), entity);
            entity.isDead = false;
        }
    }

    public void updateKilled() {
        for (Map.Entry<Integer, EntityTime> entry : this.killed.entrySet()) {
            if (!entry.getValue().isValid()) {
                entry.getValue().getEntity().setDead();
                this.killed.remove(entry.getKey());
                continue;
            }
            if (!entry.getValue().passed(DEATH_TIME.getValue().intValue())) continue;
            Entity entity = entry.getValue().getEntity();
            entity.isDead = false;
            if (SetDeadManager.mc.world.loadedEntityList.contains((Object)entity)) continue;
            SetDeadManager.mc.world.addEntityToWorld(entry.getKey().intValue(), entity);
            entity.isDead = false;
            this.killed.remove(entry.getKey());
        }
    }

    public void removeCrystals(Vec3d pos, float range, List<Entity> entities) {
        for (Entity entity : entities) {
            if (!(entity instanceof EntityEnderCrystal) || !(entity.getDistanceSq(pos.x, pos.y, pos.z) <= (double)MathUtil.square(range))) continue;
            this.setDead(entity);
        }
    }

    public void setDead(Entity entity) {
        EntityTime time = this.killed.get(entity.getEntityId());
        if (time != null) {
            time.getEntity().setDead();
            time.reset();
        } else if (!entity.isDead) {
            entity.setDead();
            this.killed.put(entity.getEntityId(), new EntityTime(entity));
        }
    }

    public void confirmKill(int id) {
        EntityTime time = this.killed.get(id);
        if (time != null) {
            time.setValid(false);
            time.getEntity().setDead();
        }
    }

    public boolean passedDeathTime(Entity entity, long deathTime) {
        return this.passedDeathTime(entity.getEntityId(), deathTime);
    }

    public boolean passedDeathTime(int id, long deathTime) {
        if (deathTime <= 0L) {
            return true;
        }
        EntityTime time = this.killed.get(id);
        if (time != null && time.isValid()) {
            return time.passed(deathTime);
        }
        return true;
    }

    public void clear() {
        this.killed.clear();
    }

    public void addObserver(SoundObserver observer) {
        this.observers.add(observer);
    }

    public void removeObserver(SoundObserver observer) {
        this.observers.remove(observer);
    }

    private boolean shouldRemove() {
        if (!SOUND_REMOVE.getValue().booleanValue()) {
            return false;
        }
        for (SoundObserver soundObserver : this.observers) {
            if (!soundObserver.shouldRemove()) continue;
            return true;
        }
        return false;
    }
}

