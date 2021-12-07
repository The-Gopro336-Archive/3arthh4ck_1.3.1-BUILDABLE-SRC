package me.earth.earthhack.impl.modules.player.exptweaks;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.exptweaks.ExpTweaks;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;

final class ListenerUseItem
extends ModuleListener<ExpTweaks, PacketEvent.Send<CPacketPlayerTryUseItem>> {
    private boolean sending = false;

    public ListenerUseItem(ExpTweaks module) {
        super(module, PacketEvent.Send.class, CPacketPlayerTryUseItem.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketPlayerTryUseItem> event) {
        int packets;
        CPacketPlayerTryUseItem p = (CPacketPlayerTryUseItem)event.getPacket();
        if (this.sending || event.isCancelled() || ListenerUseItem.mc.player.getHeldItem(p.getHand()).getItem() != Items.EXPERIENCE_BOTTLE) {
            return;
        }
        if (((ExpTweaks)this.module).wasteStop.getValue().booleanValue() && ((ExpTweaks)this.module).isWasting()) {
            event.setCancelled(true);
            ((ExpTweaks)this.module).justCancelled = true;
            return;
        }
        int n = packets = ((ExpTweaks)this.module).isMiddleClick ? ((ExpTweaks)this.module).mcePackets.getValue().intValue() : ((ExpTweaks)this.module).expPackets.getValue().intValue();
        if (packets != 0 && (((ExpTweaks)this.module).packetsInLoot.getValue().booleanValue() || ListenerUseItem.mc.world.getEntitiesWithinAABB(EntityItem.class, RotationUtil.getRotationPlayer().getEntityBoundingBox()).isEmpty())) {
            for (int i = 0; i < packets; ++i) {
                this.sending = true;
                NetworkUtil.send(new CPacketPlayerTryUseItem(p.getHand()));
                this.sending = false;
            }
        }
    }
}
