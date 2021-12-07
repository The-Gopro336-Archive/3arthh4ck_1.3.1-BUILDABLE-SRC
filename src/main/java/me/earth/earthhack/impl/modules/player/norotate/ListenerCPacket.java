package me.earth.earthhack.impl.modules.player.norotate;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.mixins.network.client.ICPacketPlayer;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.player.norotate.NoRotate;
import me.earth.earthhack.impl.util.minecraft.ItemUtil;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.CPacketPlayer;

final class ListenerCPacket
extends CPacketPlayerListener
implements Globals {
    private final NoRotate module;

    public ListenerCPacket(NoRotate module) {
        this.module = module;
    }

    @Override
    protected void onPacket(PacketEvent.Send<CPacketPlayer> event) {
        this.onPacket((CPacketPlayer)event.getPacket());
    }

    @Override
    protected void onPosition(PacketEvent.Send<CPacketPlayer.Position> event) {
        this.onPacket((CPacketPlayer)event.getPacket());
    }

    @Override
    protected void onRotation(PacketEvent.Send<CPacketPlayer.Rotation> event) {
        this.onPacket((CPacketPlayer)event.getPacket());
    }

    @Override
    protected void onPositionRotation(PacketEvent.Send<CPacketPlayer.PositionRotation> event) {
        this.onPacket((CPacketPlayer)event.getPacket());
    }

    private void onPacket(CPacketPlayer packet) {
        if (this.module.noSpoof.getValue().booleanValue() && !Managers.ROTATION.isBlocking() && (ItemUtil.isThrowable(ListenerCPacket.mc.player.getActiveItemStack().getItem()) || ListenerCPacket.mc.player.getActiveItemStack().getItem() instanceof ItemBow) && packet.getYaw(ListenerCPacket.mc.player.rotationYaw) != ListenerCPacket.mc.player.rotationYaw) {
            ((ICPacketPlayer)((Object)packet)).setYaw(ListenerCPacket.mc.player.rotationYaw);
            ((ICPacketPlayer)((Object)packet)).setPitch(ListenerCPacket.mc.player.rotationPitch);
        }
    }
}
