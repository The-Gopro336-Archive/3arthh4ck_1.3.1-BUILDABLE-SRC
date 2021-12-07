/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.server.SPacketSpawnObject
 *  net.minecraft.network.play.server.SPacketTimeUpdate
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.managers.minecraft;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.DisconnectEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.Timer;
import me.earth.earthhack.impl.util.network.ServerUtil;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.util.math.BlockPos;

public class ServerTickManager
extends SubscriberImpl
implements Globals {
    private int serverTicks;
    private Map<BlockPos, Long> timeMap = new HashMap<BlockPos, Long>();
    private final Timer serverTickTimer = new Timer();
    private boolean flag = true;
    private boolean initialized = false;
    private final ArrayDeque<Integer> spawnObjectTimes = new ArrayDeque();
    private int averageSpawnObjectTime;

    public ServerTickManager() {
        this.listeners.add(new EventListener<WorldClientEvent>(WorldClientEvent.class){

            @Override
            public void invoke(WorldClientEvent event) {
                if (event.getClient().isRemote) {
                    ServerTickManager.this.reset();
                }
            }
        });
        this.listeners.add(new EventListener<PacketEvent.Receive<SPacketSpawnObject>>(PacketEvent.Receive.class, Integer.MAX_VALUE, SPacketSpawnObject.class){

            @Override
            public void invoke(PacketEvent.Receive<SPacketSpawnObject> event) {
                if (Globals.mc.world != null && Globals.mc.world.isRemote) {
                    ServerTickManager.this.onSpawnObject();
                }
            }
        });
        this.listeners.add(new EventListener<PacketEvent.Receive<SPacketTimeUpdate>>(PacketEvent.Receive.class, Integer.MAX_VALUE, SPacketTimeUpdate.class){

            @Override
            public void invoke(PacketEvent.Receive<SPacketTimeUpdate> event) {
                if (Globals.mc.world != null && Globals.mc.world.isRemote) {
                    ServerTickManager.this.reset();
                }
            }
        });
        this.listeners.add(new EventListener<DisconnectEvent>(DisconnectEvent.class){

            @Override
            public void invoke(DisconnectEvent event) {
                ServerTickManager.this.initialized = false;
            }
        });
    }

    public int getTickTime() {
        if (this.serverTickTimer.getTime() < 50L) {
            return (int)this.serverTickTimer.getTime();
        }
        return (int)(this.serverTickTimer.getTime() % (long)this.getServerTickLengthMS());
    }

    public int getTickTimeAdjusted() {
        int time = this.getTickTime() + ServerUtil.getPingNoPingSpoof() / 2;
        if (time < this.getServerTickLengthMS()) {
            return time;
        }
        return time % this.getServerTickLengthMS();
    }

    public int getTickTimeAdjustedForServerPackets() {
        int time = this.getTickTime() - ServerUtil.getPingNoPingSpoof() / 2;
        if (time < this.getServerTickLengthMS() && time > 0) {
            return time;
        }
        if (time < 0) {
            return time + this.getServerTickLengthMS();
        }
        return time % this.getServerTickLengthMS();
    }

    public void reset() {
        this.serverTickTimer.reset();
        this.serverTickTimer.adjust(ServerUtil.getPingNoPingSpoof() / 2);
        this.initialized = true;
    }

    public int getServerTickLengthMS() {
        if (Managers.TPS.getTps() == 0.0f) {
            return 50;
        }
        return (int)(50.0f * (20.0f / Managers.TPS.getTps()));
    }

    public void onSpawnObject() {
        int time = this.getTickTimeAdjustedForServerPackets();
        if (this.spawnObjectTimes.size() > 10) {
            this.spawnObjectTimes.poll();
        }
        this.spawnObjectTimes.add(time);
        int totalTime = 0;
        for (int spawnTime : this.spawnObjectTimes) {
            totalTime += spawnTime;
        }
        this.averageSpawnObjectTime = totalTime / this.spawnObjectTimes.size();
    }

    public int normalize(int toNormalize) {
        while (toNormalize < 0) {
            toNormalize += this.getServerTickLengthMS();
        }
        while (toNormalize > this.getServerTickLengthMS()) {
            toNormalize -= this.getServerTickLengthMS();
        }
        return toNormalize;
    }

    public boolean valid(int currentTime, int minTime, int maxTime) {
        if (minTime > maxTime) {
            return currentTime >= minTime || currentTime <= maxTime;
        }
        return currentTime >= minTime && currentTime <= maxTime;
    }

    public int getSpawnTime() {
        return this.averageSpawnObjectTime;
    }
}

