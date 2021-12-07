package me.earth.earthhack.impl.modules.player.spectate;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.spectate.Spectate;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketDestroyEntities;

final class ListenerRemove
extends ModuleListener<Spectate, PacketEvent.Receive<SPacketDestroyEntities>> {
    public ListenerRemove(Spectate module) {
        super(module, PacketEvent.Receive.class, SPacketDestroyEntities.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketDestroyEntities> event) {
        EntityPlayer player;
        if (((Spectate)this.module).spectating && (player = ((Spectate)this.module).player) != null) {
            for (int id : ((SPacketDestroyEntities)event.getPacket()).getEntityIDs()) {
                if (id != player.getEntityId()) continue;
                mc.addScheduledTask(() -> {
                    ((Spectate)this.module).disable();
                    ModuleUtil.sendMessage((Module)this.module, "\u00a7cThe Player you spectated got removed.");
                });
                return;
            }
        }
    }
}
