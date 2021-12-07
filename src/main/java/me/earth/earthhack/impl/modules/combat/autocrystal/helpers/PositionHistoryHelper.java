/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  net.minecraft.entity.Entity
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.network.play.client.CPacketPlayer$PositionRotation
 *  net.minecraft.network.play.client.CPacketPlayer$Rotation
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.modules.combat.autocrystal.helpers;

import com.google.common.base.Predicate;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerPostListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.raytrace.RayTracer;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class PositionHistoryHelper
extends SubscriberImpl
implements Globals {
    private static final int REMOVE_TIME = 1000;
    private final Deque<RotationHistory> packets = new ConcurrentLinkedDeque<RotationHistory>();

    public PositionHistoryHelper() {
        this.listeners.addAll(new CPacketPlayerPostListener(){

            @Override
            protected void onPacket(PacketEvent.Post<CPacketPlayer> event) {
                PositionHistoryHelper.this.onPlayerPacket((CPacketPlayer)event.getPacket());
            }

            @Override
            protected void onPosition(PacketEvent.Post<CPacketPlayer.Position> event) {
                PositionHistoryHelper.this.onPlayerPacket((CPacketPlayer)event.getPacket());
            }

            @Override
            protected void onRotation(PacketEvent.Post<CPacketPlayer.Rotation> event) {
                PositionHistoryHelper.this.onPlayerPacket((CPacketPlayer)event.getPacket());
            }

            @Override
            protected void onPositionRotation(PacketEvent.Post<CPacketPlayer.PositionRotation> event) {
                PositionHistoryHelper.this.onPlayerPacket((CPacketPlayer)event.getPacket());
            }
        }.getListeners());
        this.listeners.add(new EventListener<WorldClientEvent.Load>(WorldClientEvent.Load.class){

            @Override
            public void invoke(WorldClientEvent.Load event) {
                PositionHistoryHelper.this.packets.clear();
            }
        });
    }

    private void onPlayerPacket(CPacketPlayer packet) {
        this.packets.removeIf(h -> h == null || System.currentTimeMillis() - h.time > 1000L);
        this.packets.addFirst(new RotationHistory(packet));
    }

    public boolean arePreviousRotationsLegit(Entity entity, int time, boolean skipFirst) {
        if (time == 0) {
            return true;
        }
        Iterator<RotationHistory> itr = this.packets.iterator();
        while (itr.hasNext()) {
            RotationHistory next = itr.next();
            if (skipFirst || next == null) continue;
            if (System.currentTimeMillis() - next.time > 1000L) {
                itr.remove();
                continue;
            }
            if (System.currentTimeMillis() - next.time > (long)time) break;
            if (this.isLegit(next, entity)) continue;
            return false;
        }
        return true;
    }

    private boolean isLegit(RotationHistory history, Entity entity) {
        RayTraceResult result = RayTracer.rayTraceEntities((World)PositionHistoryHelper.mc.world, (Entity)RotationUtil.getRotationPlayer(), 7.0, history.x, history.y, history.z, history.yaw, history.pitch, history.bb, (Predicate<Entity>)((Predicate)e -> e != null && e.equals((Object)entity)), entity, entity);
        return result != null && entity.equals((Object)result.entityHit);
    }

    private static final class RotationHistory {
        public final double x;
        public final double y;
        public final double z;
        public final float yaw;
        public final float pitch;
        public final long time;
        public final AxisAlignedBB bb;

        public RotationHistory(CPacketPlayer packet) {
            this(packet.getX(Managers.POSITION.getX()), packet.getY(Managers.POSITION.getY()), packet.getZ(Managers.POSITION.getZ()), packet.getYaw(Managers.ROTATION.getServerYaw()), packet.getPitch(Managers.ROTATION.getServerPitch()));
        }

        public RotationHistory(double x, double y, double z, float yaw, float pitch) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
            this.time = System.currentTimeMillis();
            float w = Globals.mc.player.width / 2.0f;
            float h = Globals.mc.player.height;
            this.bb = new AxisAlignedBB(x - (double)w, y, z - (double)w, x + (double)w, y + (double)h, z + (double)w);
        }
    }
}

