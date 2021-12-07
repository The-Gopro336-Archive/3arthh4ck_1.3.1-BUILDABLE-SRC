/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.server.SPacketExplosion
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.modules.combat.pistonaura;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.pistonaura.PistonAura;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.util.math.BlockPos;

final class ListenerExplosion
extends ModuleListener<PistonAura, PacketEvent.Receive<SPacketExplosion>> {
    public ListenerExplosion(PistonAura module) {
        super(module, PacketEvent.Receive.class, SPacketExplosion.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketExplosion> event) {
        if (!((PistonAura)this.module).explosions.getValue().booleanValue()) {
            return;
        }
        mc.addScheduledTask(() -> {
            if (((PistonAura)this.module).current != null) {
                SPacketExplosion packet = (SPacketExplosion)event.getPacket();
                BlockPos pos = new BlockPos(packet.getX(), packet.getY(), packet.getZ());
                if (pos.equals((Object)((PistonAura)this.module).current.getStartPos().up()) || pos.equals((Object)((PistonAura)this.module).current.getCrystalPos().up())) {
                    ((PistonAura)this.module).current.setValid(false);
                    return;
                }
                for (BlockPos affected : packet.getAffectedBlockPositions()) {
                    if (!affected.equals((Object)((PistonAura)this.module).current.getPistonPos()) && !affected.equals((Object)((PistonAura)this.module).current.getRedstonePos())) continue;
                    ((PistonAura)this.module).current.setValid(false);
                    break;
                }
            }
        });
    }
}

