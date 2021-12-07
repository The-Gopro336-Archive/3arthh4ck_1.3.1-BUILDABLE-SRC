package me.earth.earthhack.impl.modules.render.search;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.search.Search;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.play.server.SPacketMultiBlockChange;

final class ListenerMultiBlockChange
extends ModuleListener<Search, PacketEvent.Receive<SPacketMultiBlockChange>> {
    public ListenerMultiBlockChange(Search module) {
        super(module, PacketEvent.Receive.class, SPacketMultiBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketMultiBlockChange> event) {
        if (((Search)this.module).remove.getValue().booleanValue()) {
            for (SPacketMultiBlockChange.BlockUpdateData data : ((SPacketMultiBlockChange)event.getPacket()).getChangedBlocks()) {
                IBlockState state = data.getBlockState();
                if (state.func_185904_a() != Material.AIR && ((Search)this.module).isValid(state.getBlock().getLocalizedName())) continue;
                ((Search)this.module).toRender.remove(data.getPos());
            }
        }
    }
}
