package me.earth.earthhack.impl.modules.combat.autocrystal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.earth.earthhack.impl.core.mixins.network.server.ISPacketEntity;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ReceiveListener;
import me.earth.earthhack.impl.event.listeners.SPacketEntityListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.RotationThread;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntity;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.util.math.BlockPos;

final class ListenerEntity
extends SPacketEntityListener {
    private final AutoCrystal module;

    public ListenerEntity(AutoCrystal module) {
        this.module = module;
        this.listeners.add(new ReceiveListener<SPacketEntityTeleport>(SPacketEntityTeleport.class, e -> {
            if (!this.shouldCalc()) {
                return;
            }
            EntityPlayer p = this.getEntity(((SPacketEntityTeleport)e.getPacket()).getEntityId());
            if (p != null) {
                double x = ((SPacketEntityTeleport)e.getPacket()).getX();
                double y = ((SPacketEntityTeleport)e.getPacket()).getY();
                double z = ((SPacketEntityTeleport)e.getPacket()).getZ();
                this.onEvent(p, x, y, z);
            }
        }));
    }

    @Override
    protected void onPacket(PacketEvent.Receive<SPacketEntity> event) {
    }

    @Override
    protected void onRotation(PacketEvent.Receive<SPacketEntity.S16PacketEntityLook> event) {
    }

    @Override
    protected void onPosition(PacketEvent.Receive<SPacketEntity.S15PacketEntityRelMove> event) {
        this.onEvent((SPacketEntity)event.getPacket());
    }

    @Override
    protected void onPositionRotation(PacketEvent.Receive<SPacketEntity.S17PacketEntityLookMove> event) {
        this.onEvent((SPacketEntity)event.getPacket());
    }

    protected void onEvent(SPacketEntity packet) {
        if (!this.shouldCalc()) {
            return;
        }
        EntityPlayer p = this.getEntity(((ISPacketEntity)((Object)packet)).getEntityId());
        if (p == null) {
            return;
        }
        double x = (double)(p.serverPosX + (long)packet.getX()) / 4096.0;
        double y = (double)(p.serverPosY + (long)packet.getY()) / 4096.0;
        double z = (double)(p.serverPosZ + (long)packet.getZ()) / 4096.0;
        this.onEvent(p, x, y, z);
    }

    protected void onEvent(EntityPlayer player, double x, double y, double z) {
        EntityPlayer entity = RotationUtil.getRotationPlayer();
        if (entity != null && entity.getDistanceSq(x, y, z) < (double)MathUtil.square(this.module.targetRange.getValue().floatValue()) && !Managers.FRIENDS.contains(player)) {
            boolean enemied = Managers.ENEMIES.contains(player);
            Scheduler.getInstance().scheduleAsynchronously(() -> {
                List<EntityPlayer> enemies;
                if (ListenerEntity.mc.world == null) {
                    return;
                }
                if (enemied) {
                    enemies = new ArrayList(1);
                    enemies.add(player);
                } else {
                    enemies = Collections.emptyList();
                }
                EntityPlayer target = this.module.targetMode.getValue().getTarget(ListenerEntity.mc.world.playerEntities, enemies, this.module.targetRange.getValue().floatValue());
                if (target == null || target.equals(player)) {
                    this.module.threadHelper.startThread(new BlockPos[0]);
                }
            });
        }
    }

    protected boolean shouldCalc() {
        return this.module.multiThread.getValue() != false && this.module.entityThread.getValue() != false && (this.module.rotate.getValue() == ACRotate.None || this.module.rotationThread.getValue() != RotationThread.Predict);
    }

    protected EntityPlayer getEntity(int id) {
        List<Entity> entities = Managers.ENTITIES.getEntities();
        if (entities == null) {
            return null;
        }
        Entity entity = null;
        for (Entity e : entities) {
            if (e == null || e.getEntityId() != id) continue;
            entity = e;
            break;
        }
        if (entity instanceof EntityPlayer) {
            return (EntityPlayer)entity;
        }
        return null;
    }
}
