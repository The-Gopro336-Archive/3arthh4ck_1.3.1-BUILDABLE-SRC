/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.network.play.client.CPacketPlayer$PositionRotation
 *  net.minecraft.network.play.client.CPacketPlayer$Rotation
 */
package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.RotationThread;
import net.minecraft.network.play.client.CPacketPlayer;

final class ListenerCPlayers
extends CPacketPlayerListener {
    private final AutoCrystal module;

    public ListenerCPlayers(AutoCrystal module) {
        this.module = module;
    }

    @Override
    protected void onPacket(PacketEvent.Send<CPacketPlayer> event) {
        this.update(event);
    }

    @Override
    protected void onPosition(PacketEvent.Send<CPacketPlayer.Position> event) {
        this.update(event);
    }

    @Override
    protected void onRotation(PacketEvent.Send<CPacketPlayer.Rotation> event) {
        this.update(event);
    }

    @Override
    protected void onPositionRotation(PacketEvent.Send<CPacketPlayer.PositionRotation> event) {
        this.update(event);
    }

    private void update(PacketEvent.Send<? extends CPacketPlayer> event) {
        if (this.module.multiThread.getValue().booleanValue() && !this.module.isSpoofing && this.module.rotate.getValue() != ACRotate.None && this.module.rotationThread.getValue() == RotationThread.Cancel) {
            this.module.rotationCanceller.onPacket(event);
        } else {
            this.module.rotationCanceller.reset();
        }
    }
}

