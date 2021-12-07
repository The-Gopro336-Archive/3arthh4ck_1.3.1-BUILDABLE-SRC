/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.network.play.server.SPacketEntityStatus
 *  net.minecraft.network.play.server.SPacketUpdateHealth
 */
package me.earth.earthhack.impl.managers.minecraft.combat;

import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.event.bus.api.EventBus;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.core.mixins.network.server.ISPacketEntityStatus;
import me.earth.earthhack.impl.event.listeners.ReceiveListener;
import me.earth.earthhack.tweaker.launch.Argument;
import me.earth.earthhack.tweaker.launch.DevArguments;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketUpdateHealth;

public class TotemDebugService
extends SubscriberImpl
implements Globals {
    private volatile long time;

    public TotemDebugService() {
        this.listeners.add(new ReceiveListener<SPacketEntityStatus>(SPacketEntityStatus.class, e -> {
            EntityPlayerSP player = TotemDebugService.mc.player;
            ISPacketEntityStatus packet = (ISPacketEntityStatus)e.getPacket();
            if (player != null && packet.getLogicOpcode() == 35 && packet.getEntityId() == player.getEntityId()) {
                long t = System.currentTimeMillis();
                Earthhack.getLogger().info("Pop, last pop: " + (t - this.time) + "ms");
                this.time = t;
            }
        }));
        this.listeners.add(new ReceiveListener<SPacketUpdateHealth>(SPacketUpdateHealth.class, e -> {
            if (((SPacketUpdateHealth)e.getPacket()).getHealth() <= 0.0f) {
                long t = System.currentTimeMillis();
                Earthhack.getLogger().info("Death, last pop: " + (t - this.time) + "ms");
                this.time = t;
            }
        }));
    }

    public static void trySubscribe(EventBus eventBus) {
        Argument a = DevArguments.getInstance().getArgument("totems");
        if (a == null || ((Boolean)a.getValue()).booleanValue()) {
            Earthhack.getLogger().info("TotemDebugger loaded.");
            eventBus.subscribe(new TotemDebugService());
        }
    }
}

