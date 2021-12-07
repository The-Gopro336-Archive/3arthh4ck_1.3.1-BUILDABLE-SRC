/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.network.play.client.CPacketUseEntity
 *  net.minecraft.network.play.client.CPacketUseEntity$Action
 *  net.minecraft.util.math.Vec3d
 */
package me.earth.earthhack.impl.modules.combat.criticals;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.core.ducks.network.ICPacketUseEntity;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.criticals.Criticals;
import me.earth.earthhack.impl.modules.combat.killaura.KillAura;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.math.Vec3d;

final class ListenerUseEntity
extends ModuleListener<Criticals, PacketEvent.Send<CPacketUseEntity>> {
    private static final ModuleCache<KillAura> KILL_AURA = Caches.getModule(KillAura.class);

    public ListenerUseEntity(Criticals module) {
        super(module, PacketEvent.Send.class, CPacketUseEntity.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketUseEntity> event) {
        if (((CPacketUseEntity)event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK && ListenerUseEntity.mc.player.onGround && !ListenerUseEntity.mc.gameSettings.keyBindJump.isKeyDown() && !ListenerUseEntity.mc.player.isInWater() && !ListenerUseEntity.mc.player.isInLava() && ((Criticals)this.module).timer.passed(((Criticals)this.module).delay.getValue().intValue())) {
            CPacketUseEntity packet = (CPacketUseEntity)event.getPacket();
            Entity entity = ((ICPacketUseEntity)packet).getAttackedEntity();
            if (!((Criticals)this.module).noDesync.getValue().booleanValue() || entity instanceof EntityLivingBase) {
                Vec3d vec = RotationUtil.getRotationPlayer().getPositionVector();
                Vec3d pos = KILL_AURA.returnIfPresent(k -> k.criticalCallback(vec), vec);
                switch (((Criticals)this.module).mode.getValue()) {
                    case Packet: {
                        ListenerUseEntity.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(pos.x, pos.y + 0.0625101, pos.z, false));
                        ListenerUseEntity.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(pos.x, pos.y, pos.z, false));
                        ListenerUseEntity.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(pos.x, pos.y + 1.1E-5, pos.z, false));
                        ListenerUseEntity.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(pos.x, pos.y, pos.z, false));
                        break;
                    }
                    case Bypass: {
                        ListenerUseEntity.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(pos.x, pos.y + 0.062600301692775, pos.z, false));
                        ListenerUseEntity.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(pos.x, pos.y + 0.07260029960661, pos.z, false));
                        ListenerUseEntity.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(pos.x, pos.y, pos.z, false));
                        ListenerUseEntity.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(pos.x, pos.y, pos.z, false));
                        break;
                    }
                    case Jump: {
                        ListenerUseEntity.mc.player.jump();
                        break;
                    }
                    case MiniJump: {
                        ListenerUseEntity.mc.player.jump();
                        ListenerUseEntity.mc.player.motionY /= 2.0;
                    }
                }
                ((Criticals)this.module).timer.reset();
            }
        }
    }
}

