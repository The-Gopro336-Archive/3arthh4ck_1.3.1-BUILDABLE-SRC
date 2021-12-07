/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiDownloadTerrain
 *  net.minecraft.entity.Entity
 *  net.minecraft.network.play.server.SPacketPlayerPosLook
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.modules.movement.packetfly;

import me.earth.earthhack.impl.core.mixins.network.server.ISPacketPlayerPosLook;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.packetfly.PacketFly;
import me.earth.earthhack.impl.modules.movement.packetfly.util.Mode;
import me.earth.earthhack.impl.modules.movement.packetfly.util.TimeVec;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;

final class ListenerPosLook
extends ModuleListener<PacketFly, PacketEvent.Receive<SPacketPlayerPosLook>> {
    public ListenerPosLook(PacketFly module) {
        super(module, PacketEvent.Receive.class, SPacketPlayerPosLook.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketPlayerPosLook> event) {
        TimeVec vec;
        ISPacketPlayerPosLook packet = (ISPacketPlayerPosLook)event.getPacket();
        if (ListenerPosLook.mc.player.isEntityAlive() && ((PacketFly)this.module).mode.getValue() != Mode.Setback && ((PacketFly)this.module).mode.getValue() != Mode.Slow && !(ListenerPosLook.mc.currentScreen instanceof GuiDownloadTerrain) && ListenerPosLook.mc.world.isBlockLoaded(new BlockPos((Entity)ListenerPosLook.mc.player), false) && (vec = ((PacketFly)this.module).posLooks.remove(packet.getTeleportId())) != null && vec.x == packet.getX() && vec.y == packet.getY() && vec.z == packet.getZ()) {
            event.setCancelled(true);
            return;
        }
        ((PacketFly)this.module).teleportID.set(packet.getTeleportId());
        if (((PacketFly)this.module).answer.getValue().booleanValue()) {
            event.setCancelled(true);
            mc.addScheduledTask(() -> PacketUtil.handlePosLook((SPacketPlayerPosLook)event.getPacket(), (Entity)ListenerPosLook.mc.player, true, false));
            return;
        }
        packet.setYaw(ListenerPosLook.mc.player.rotationYaw);
        packet.setPitch(ListenerPosLook.mc.player.rotationPitch);
    }
}

