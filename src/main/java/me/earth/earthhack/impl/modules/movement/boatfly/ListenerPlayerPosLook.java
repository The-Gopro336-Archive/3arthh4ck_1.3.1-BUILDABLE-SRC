package me.earth.earthhack.impl.modules.movement.boatfly;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.boatfly.BoatFly;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

final class ListenerPlayerPosLook
extends ModuleListener<BoatFly, PacketEvent.Receive<SPacketPlayerPosLook>> {
    public ListenerPlayerPosLook(BoatFly module) {
        super(module, PacketEvent.Receive.class, SPacketPlayerPosLook.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketPlayerPosLook> event) {
        if (!(!((BoatFly)this.module).noForceRotate.getValue().booleanValue() || ListenerPlayerPosLook.mc.player.getRidingEntity() == null || ListenerPlayerPosLook.mc.currentScreen instanceof GuiMainMenu || ListenerPlayerPosLook.mc.currentScreen instanceof GuiDisconnected || ListenerPlayerPosLook.mc.currentScreen instanceof GuiDownloadTerrain || ListenerPlayerPosLook.mc.currentScreen instanceof GuiConnecting || ListenerPlayerPosLook.mc.currentScreen instanceof GuiMultiplayer)) {
            event.setCancelled(true);
        }
    }
}
