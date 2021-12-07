/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.client.CPacketPlayer
 */
package me.earth.earthhack.impl.modules.combat.autocrystal.helpers;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.mixins.network.client.ICPacketPlayer;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.RotationFunction;
import me.earth.earthhack.impl.modules.movement.packetfly.PacketFly;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.network.play.client.CPacketPlayer;

public class RotationCanceller
implements Globals {
    private static final ModuleCache<PacketFly> PACKETFLY = Caches.getModule(PacketFly.class);
    private final StopWatch timer = new StopWatch();
    private final Setting<Integer> maxCancel;
    private final AutoCrystal module;
    private volatile CPacketPlayer last;

    public RotationCanceller(AutoCrystal module, Setting<Integer> maxCancel) {
        this.module = module;
        this.maxCancel = maxCancel;
    }

    public void onGameLoop() {
        if (this.last != null && this.timer.passed(this.maxCancel.getValue().intValue())) {
            this.sendLast();
        }
    }

    public synchronized void onPacket(PacketEvent.Send<? extends CPacketPlayer> event) {
        if (event.isCancelled() || PACKETFLY.isEnabled()) {
            return;
        }
        this.reset();
        if (Managers.ROTATION.isBlocking()) {
            return;
        }
        event.setCancelled(true);
        this.last = (CPacketPlayer)event.getPacket();
        this.timer.reset();
    }

    public synchronized boolean setRotations(RotationFunction function) {
        if (this.last == null) {
            return false;
        }
        double x = this.last.getX(Managers.POSITION.getX());
        double y = this.last.getX(Managers.POSITION.getY());
        double z = this.last.getX(Managers.POSITION.getZ());
        float yaw = Managers.ROTATION.getServerYaw();
        float pitch = Managers.ROTATION.getServerPitch();
        boolean onGround = this.last.isOnGround();
        ICPacketPlayer accessor = (ICPacketPlayer)this.last;
        float[] r = function.apply(x, y, z, yaw, pitch);
        if ((double)(r[0] - yaw) == 0.0 || (double)(r[1] - pitch) == 0.0) {
            if (!accessor.isRotating() && !accessor.isMoving() && onGround == Managers.POSITION.isOnGround()) {
                this.last = null;
                return true;
            }
            this.sendLast();
            return true;
        }
        if (accessor.isRotating()) {
            accessor.setYaw(r[0]);
            accessor.setPitch(r[1]);
            this.sendLast();
        } else if (accessor.isMoving()) {
            this.last = PacketUtil.positionRotation(x, y, z, r[0], r[1], onGround);
            this.sendLast();
        } else {
            this.last = PacketUtil.rotation(r[0], r[1], onGround);
            this.sendLast();
        }
        return true;
    }

    public void reset() {
        if (this.last != null && RotationCanceller.mc.player != null) {
            this.sendLast();
        }
    }

    public synchronized void drop() {
        this.last = null;
    }

    private synchronized void sendLast() {
        CPacketPlayer packet = this.last;
        if (packet != null && RotationCanceller.mc.player != null) {
            NetworkUtil.sendPacketNoEvent(packet);
            this.module.runPost();
        }
        this.last = null;
    }
}

