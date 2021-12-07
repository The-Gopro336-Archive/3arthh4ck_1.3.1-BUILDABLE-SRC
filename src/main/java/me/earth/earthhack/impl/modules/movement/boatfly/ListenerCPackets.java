package me.earth.earthhack.impl.modules.movement.boatfly;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import me.earth.earthhack.impl.modules.movement.boatfly.BoatFly;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayer;

final class ListenerCPackets
extends CPacketPlayerListener {
    private final BoatFly module;
    private static final Minecraft mc = Minecraft.getMinecraft();

    public ListenerCPackets(BoatFly module) {
        this.module = module;
    }

    @Override
    protected void onPacket(PacketEvent.Send<CPacketPlayer> event) {
        if (this.module.noPosUpdate.getValue().booleanValue() && ListenerCPackets.mc.player.getRidingEntity() != null) {
            event.setCancelled(true);
        }
    }

    @Override
    protected void onPosition(PacketEvent.Send<CPacketPlayer.Position> event) {
        if (this.module.noPosUpdate.getValue().booleanValue() && ListenerCPackets.mc.player.getRidingEntity() != null) {
            event.setCancelled(true);
        }
    }

    @Override
    protected void onRotation(PacketEvent.Send<CPacketPlayer.Rotation> event) {
        if (this.module.noPosUpdate.getValue().booleanValue() && ListenerCPackets.mc.player.getRidingEntity() != null) {
            event.setCancelled(true);
        }
    }

    @Override
    protected void onPositionRotation(PacketEvent.Send<CPacketPlayer.PositionRotation> event) {
        if (this.module.noPosUpdate.getValue().booleanValue() && ListenerCPackets.mc.player.getRidingEntity() != null) {
            event.setCancelled(true);
        }
    }
}
