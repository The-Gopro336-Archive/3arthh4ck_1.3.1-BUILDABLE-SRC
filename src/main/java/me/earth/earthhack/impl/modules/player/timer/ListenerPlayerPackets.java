/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.network.play.client.CPacketPlayer$PositionRotation
 *  net.minecraft.network.play.client.CPacketPlayer$Rotation
 */
package me.earth.earthhack.impl.modules.player.timer;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.player.timer.Timer;
import me.earth.earthhack.impl.modules.player.timer.mode.TimerMode;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.network.play.client.CPacketPlayer;

final class ListenerPlayerPackets
extends CPacketPlayerListener
implements Globals {
    private final Timer timer;

    public ListenerPlayerPackets(Timer timer) {
        this.timer = timer;
    }

    @Override
    protected void onPacket(PacketEvent.Send<CPacketPlayer> event) {
        this.onEvent(event);
    }

    @Override
    protected void onPosition(PacketEvent.Send<CPacketPlayer.Position> event) {
        if (!Managers.POSITION.isBlocking()) {
            this.onEvent(event);
        }
    }

    @Override
    protected void onRotation(PacketEvent.Send<CPacketPlayer.Rotation> event) {
        if (!Managers.ROTATION.isBlocking()) {
            this.onEvent(event);
        }
    }

    @Override
    protected void onPositionRotation(PacketEvent.Send<CPacketPlayer.PositionRotation> event) {
        if (!Managers.ROTATION.isBlocking() && !Managers.POSITION.isBlocking()) {
            this.onEvent(event);
        }
    }

    private void onEvent(PacketEvent<?> event) {
        if (this.timer.mode.getValue() == TimerMode.Blink && Managers.NCP.passed(this.timer.lagTime.getValue())) {
            if (this.timer.packets != 0 && this.timer.letThrough.getValue() != 0 && this.timer.packets % this.timer.letThrough.getValue() == 0) {
                ++this.timer.packets;
                return;
            }
            if (MovementUtil.noMovementKeys() && ListenerPlayerPackets.mc.player.motionX < 0.001 && ListenerPlayerPackets.mc.player.motionY < 0.001 && ListenerPlayerPackets.mc.player.motionZ < 0.001) {
                event.setCancelled(true);
                this.timer.pSpeed = 1.0f;
                ++this.timer.packets;
                return;
            }
            if (this.timer.packets > this.timer.offset.getValue() && this.timer.sent < this.timer.maxPackets.getValue()) {
                this.timer.pSpeed = this.timer.speed.getValue().floatValue();
                --this.timer.packets;
                ++this.timer.sent;
                return;
            }
        }
        this.timer.pSpeed = 1.0f;
        this.timer.sent = 0;
        this.timer.packets = 0;
    }
}

