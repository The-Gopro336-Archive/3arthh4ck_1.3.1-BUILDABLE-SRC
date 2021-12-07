/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.network.play.client.CPacketPlayer$PositionRotation
 *  net.minecraft.network.play.client.CPacketPlayer$Rotation
 */
package me.earth.earthhack.impl.modules.movement.nofall;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.mixins.network.client.ICPacketPlayer;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import me.earth.earthhack.impl.modules.movement.nofall.NoFall;
import net.minecraft.network.play.client.CPacketPlayer;

final class ListenerPlayerPackets
extends CPacketPlayerListener
implements Globals {
    public final NoFall module;

    protected ListenerPlayerPackets(NoFall module) {
        this.module = module;
    }

    @Override
    protected void onPacket(PacketEvent.Send<CPacketPlayer> event) {
        this.onPacket((CPacketPlayer)event.getPacket());
    }

    @Override
    protected void onPosition(PacketEvent.Send<CPacketPlayer.Position> event) {
        this.onPacket((CPacketPlayer)event.getPacket());
    }

    @Override
    protected void onRotation(PacketEvent.Send<CPacketPlayer.Rotation> event) {
        this.onPacket((CPacketPlayer)event.getPacket());
    }

    @Override
    protected void onPositionRotation(PacketEvent.Send<CPacketPlayer.PositionRotation> event) {
        this.onPacket((CPacketPlayer)event.getPacket());
    }

    private void onPacket(CPacketPlayer packet) {
        switch (this.module.mode.getValue()) {
            case Packet: {
                if (!(ListenerPlayerPackets.mc.player.fallDistance > 3.0f)) break;
                ((ICPacketPlayer)packet).setOnGround(true);
                return;
            }
            case Anti: {
                if (!(ListenerPlayerPackets.mc.player.fallDistance > 3.0f)) break;
                ((ICPacketPlayer)packet).setY(ListenerPlayerPackets.mc.player.posY + (double)0.1f);
                return;
            }
            case AAC: {
                if (!(ListenerPlayerPackets.mc.player.fallDistance > 3.0f)) break;
                ListenerPlayerPackets.mc.player.onGround = true;
                ListenerPlayerPackets.mc.player.capabilities.isFlying = true;
                ListenerPlayerPackets.mc.player.capabilities.allowFlying = true;
                ((ICPacketPlayer)packet).setOnGround(false);
                ListenerPlayerPackets.mc.player.velocityChanged = true;
                ListenerPlayerPackets.mc.player.capabilities.isFlying = false;
                ListenerPlayerPackets.mc.player.jump();
            }
        }
    }
}

