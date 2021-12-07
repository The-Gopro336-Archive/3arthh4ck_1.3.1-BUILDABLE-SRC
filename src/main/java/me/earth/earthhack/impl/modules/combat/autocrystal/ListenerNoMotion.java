/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.client.CPacketPlayer$Rotation
 */
package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.event.events.network.NoMotionUpdateEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.RotationThread;
import net.minecraft.network.play.client.CPacketPlayer;

final class ListenerNoMotion
extends ModuleListener<AutoCrystal, NoMotionUpdateEvent> {
    private float forward = 0.004f;

    public ListenerNoMotion(AutoCrystal module) {
        super(module, NoMotionUpdateEvent.class);
    }

    @Override
    public void invoke(NoMotionUpdateEvent event) {
        if (((AutoCrystal)this.module).multiThread.getValue().booleanValue() && !((AutoCrystal)this.module).isSpoofing && ((AutoCrystal)this.module).rotate.getValue() != ACRotate.None && ((AutoCrystal)this.module).rotationThread.getValue() == RotationThread.Cancel) {
            this.forward = -this.forward;
            float yaw = Managers.ROTATION.getServerYaw() + this.forward;
            float pitch = Managers.ROTATION.getServerPitch() + this.forward;
            ((AutoCrystal)this.module).rotationCanceller.onPacket(new PacketEvent.Send<CPacketPlayer.Rotation>(new CPacketPlayer.Rotation(yaw, pitch, Managers.POSITION.isOnGround())));
        } else {
            ((AutoCrystal)this.module).rotationCanceller.reset();
        }
    }
}

