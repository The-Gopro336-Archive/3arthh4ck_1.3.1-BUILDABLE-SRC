/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketClickWindow
 *  net.minecraft.network.play.client.CPacketEntityAction
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 *  net.minecraft.network.play.server.SPacketPlayerPosLook
 */
package me.earth.earthhack.impl.managers.minecraft.movement;

import java.util.concurrent.atomic.AtomicLong;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

public class NCPManager
extends SubscriberImpl
implements Globals {
    private final AtomicLong lagTimer = new AtomicLong();
    private final StopWatch clickTimer = new StopWatch();
    private boolean endedSprint;
    private boolean endedSneak;
    private boolean windowClicks;
    private boolean strict;

    public NCPManager() {
        this.listeners.add(new EventListener<PacketEvent.Receive<SPacketPlayerPosLook>>(PacketEvent.Receive.class, Integer.MAX_VALUE, SPacketPlayerPosLook.class){

            @Override
            public void invoke(PacketEvent.Receive<SPacketPlayerPosLook> event) {
                NCPManager.this.lagTimer.set(System.currentTimeMillis());
            }
        });
        this.listeners.add(new EventListener<WorldClientEvent.Load>(WorldClientEvent.Load.class){

            @Override
            public void invoke(WorldClientEvent.Load event) {
                NCPManager.this.endedSneak = false;
                NCPManager.this.endedSprint = false;
                NCPManager.this.windowClicks = false;
            }
        });
        this.listeners.add(new EventListener<PacketEvent.Send<CPacketClickWindow>>(PacketEvent.Send.class, -1000, CPacketClickWindow.class){

            @Override
            public void invoke(PacketEvent.Send<CPacketClickWindow> event) {
                if (!NCPManager.this.isStrict() || event.isCancelled()) {
                    return;
                }
                if (Globals.mc.player.isActiveItemStackBlocking()) {
                    Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> Globals.mc.playerController.onStoppedUsingItem((EntityPlayer)Globals.mc.player));
                }
                if (Managers.ACTION.isSneaking()) {
                    NCPManager.this.endedSneak = true;
                    Globals.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Globals.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                }
                if (Managers.ACTION.isSprinting()) {
                    NCPManager.this.endedSprint = true;
                    Globals.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Globals.mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
                }
            }
        });
        this.listeners.add(new EventListener<PacketEvent.Post<CPacketClickWindow>>(PacketEvent.Post.class, -1000, CPacketClickWindow.class){

            @Override
            public void invoke(PacketEvent.Post<CPacketClickWindow> event) {
                NCPManager.this.clickTimer.reset();
                if (!NCPManager.this.windowClicks && NCPManager.this.isStrict()) {
                    NCPManager.this.release();
                }
            }
        });
    }

    public StopWatch getClickTimer() {
        return this.clickTimer;
    }

    public boolean isStrict() {
        return this.strict;
    }

    public void setStrict(boolean strict) {
        if (this.strict && !strict) {
            this.releaseMultiClick();
        }
        this.strict = strict;
    }

    public void startMultiClick() {
        this.windowClicks = true;
    }

    public void releaseMultiClick() {
        this.windowClicks = false;
        this.release();
    }

    public boolean passed(int ms) {
        return System.currentTimeMillis() - this.lagTimer.get() >= (long)ms;
    }

    public long getTimeStamp() {
        return this.lagTimer.get();
    }

    public void reset() {
        this.lagTimer.set(System.currentTimeMillis());
    }

    private void release() {
        if (this.endedSneak) {
            this.endedSneak = false;
            NCPManager.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)NCPManager.mc.player, CPacketEntityAction.Action.START_SNEAKING));
        }
        if (this.endedSprint) {
            this.endedSprint = false;
            NCPManager.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)NCPManager.mc.player, CPacketEntityAction.Action.START_SPRINTING));
        }
    }
}

