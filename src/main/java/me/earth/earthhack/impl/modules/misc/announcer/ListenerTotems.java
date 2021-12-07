package me.earth.earthhack.impl.modules.misc.announcer;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.misc.announcer.Announcer;
import me.earth.earthhack.impl.modules.misc.announcer.util.Announcement;
import me.earth.earthhack.impl.modules.misc.announcer.util.AnnouncementType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;

final class ListenerTotems
extends ModuleListener<Announcer, PacketEvent.Receive<SPacketEntityStatus>> {
    public ListenerTotems(Announcer module) {
        super(module, PacketEvent.Receive.class, SPacketEntityStatus.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntityStatus> event) {
        Entity entity;
        SPacketEntityStatus packet;
        if (((Announcer)this.module).totems.getValue().booleanValue() && (packet = (SPacketEntityStatus)event.getPacket()).getOpCode() == 35 && (entity = packet.getEntity(ListenerTotems.mc.world)) instanceof EntityPlayer && (!((Announcer)this.module).friends.getValue().booleanValue() || !Managers.FRIENDS.contains((EntityPlayer)entity))) {
            Announcement announcement = ((Announcer)this.module).addWordAndIncrement(AnnouncementType.Totems, entity.getName());
            announcement.setAmount(Managers.COMBAT.getPops(entity) + 1);
        }
    }
}
