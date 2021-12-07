/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.network.play.server.SPacketEntityStatus
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.managers.minecraft.combat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.misc.DeathEvent;
import me.earth.earthhack.impl.event.events.misc.TotemPopEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.world.World;

public class CombatManager
extends SubscriberImpl
implements Globals {
    private final Map<EntityPlayer, PopCounter> pops = new ConcurrentHashMap<EntityPlayer, PopCounter>();

    public CombatManager() {
        this.listeners.add(new EventListener<DeathEvent>(DeathEvent.class, Integer.MIN_VALUE){

            @Override
            public void invoke(DeathEvent event) {
                CombatManager.this.onDeath((Entity)event.getEntity());
            }
        });
        this.listeners.add(new EventListener<PacketEvent.Receive<SPacketEntityStatus>>(PacketEvent.Receive.class, Integer.MIN_VALUE, SPacketEntityStatus.class){

            @Override
            public void invoke(PacketEvent.Receive<SPacketEntityStatus> event) {
                switch (((SPacketEntityStatus)event.getPacket()).getOpCode()) {
                    case 3: {
                        Globals.mc.addScheduledTask(() -> CombatManager.this.onDeath(Globals.mc.world == null ? null : ((SPacketEntityStatus)event.getPacket()).getEntity((World)Globals.mc.world)));
                        break;
                    }
                    case 35: {
                        Globals.mc.addScheduledTask(() -> CombatManager.this.onTotemPop((SPacketEntityStatus)event.getPacket()));
                    }
                }
            }
        });
    }

    public void reset() {
        this.pops.clear();
    }

    public int getPops(Entity player) {
        PopCounter popCounter;
        if (player instanceof EntityPlayer && (popCounter = this.pops.get((Object)player)) != null) {
            return popCounter.getPops();
        }
        return 0;
    }

    public long lastPop(Entity player) {
        PopCounter popCounter;
        if (player instanceof EntityPlayer && (popCounter = this.pops.get((Object)player)) != null) {
            return popCounter.lastPop();
        }
        return Integer.MAX_VALUE;
    }

    private void onTotemPop(SPacketEntityStatus packet) {
        Entity player = packet.getEntity((World)CombatManager.mc.world);
        if (player instanceof EntityPlayer) {
            this.pops.computeIfAbsent((EntityPlayer)player, v -> new PopCounter()).pop();
            TotemPopEvent totemPopEvent = new TotemPopEvent((EntityPlayer)player);
            Bus.EVENT_BUS.post(totemPopEvent);
        }
    }

    private void onDeath(Entity entity) {
        if (entity instanceof EntityPlayer) {
            this.pops.remove((Object)entity);
        }
    }

    private static class PopCounter {
        private final StopWatch timer = new StopWatch();
        private int pops;

        private PopCounter() {
        }

        public int getPops() {
            return this.pops;
        }

        public void pop() {
            this.timer.reset();
            ++this.pops;
        }

        public void reset() {
            this.pops = 0;
        }

        public long lastPop() {
            return this.timer.getTime();
        }
    }
}

