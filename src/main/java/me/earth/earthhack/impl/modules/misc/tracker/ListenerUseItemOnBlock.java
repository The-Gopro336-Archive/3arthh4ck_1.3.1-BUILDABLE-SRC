package me.earth.earthhack.impl.modules.misc.tracker;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.tracker.Tracker;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.math.BlockPos;

final class ListenerUseItemOnBlock
extends ModuleListener<Tracker, PacketEvent.Post<CPacketPlayerTryUseItemOnBlock>> {
    public ListenerUseItemOnBlock(Tracker module) {
        super(module, PacketEvent.Post.class, CPacketPlayerTryUseItemOnBlock.class);
    }

    @Override
    public void invoke(PacketEvent.Post<CPacketPlayerTryUseItemOnBlock> event) {
        CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock)event.getPacket();
        if (ListenerUseItemOnBlock.mc.player.getHeldItem(packet.getHand()).getItem() == Items.END_CRYSTAL) {
            BlockPos pos = packet.getPos();
            ((Tracker)this.module).placed.add(new BlockPos((float)pos.getX() + 0.5f, (double)(pos.getY() + 1), (float)pos.getZ() + 0.5f));
        }
    }
}
