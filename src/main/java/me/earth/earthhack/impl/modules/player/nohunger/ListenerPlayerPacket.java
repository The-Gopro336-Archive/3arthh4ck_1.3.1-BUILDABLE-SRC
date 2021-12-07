/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.network.play.client.CPacketPlayer$PositionRotation
 *  net.minecraft.network.play.client.CPacketPlayer$Rotation
 */
package me.earth.earthhack.impl.modules.player.nohunger;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.mixins.network.client.ICPacketPlayer;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import me.earth.earthhack.impl.modules.player.nohunger.NoHunger;
import net.minecraft.network.play.client.CPacketPlayer;

final class ListenerPlayerPacket
extends CPacketPlayerListener
implements Globals {
    private final NoHunger module;

    public ListenerPlayerPacket(NoHunger module) {
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
        if (this.module.ground.getValue().booleanValue() && this.module.onGround && ListenerPlayerPacket.mc.player.onGround && packet.getY(0.0) == (!((ICPacketPlayer)packet).isMoving() ? 0.0 : ListenerPlayerPacket.mc.player.posY)) {
            ((ICPacketPlayer)packet).setOnGround(false);
        }
        this.module.onGround = ListenerPlayerPacket.mc.player.onGround;
    }
}

