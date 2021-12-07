/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.network.play.server.SPacketBlockChange
 */
package me.earth.earthhack.impl.modules.render.search;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.search.Search;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.play.server.SPacketBlockChange;

final class ListenerBlockChange
extends ModuleListener<Search, PacketEvent.Receive<SPacketBlockChange>> {
    public ListenerBlockChange(Search module) {
        super(module, PacketEvent.Receive.class, SPacketBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketBlockChange> event) {
        IBlockState state;
        if (((Search)this.module).remove.getValue().booleanValue() && ((state = ((SPacketBlockChange)event.getPacket()).getBlockState()).getMaterial() == Material.AIR || !((Search)this.module).isValid(state.getBlock().getLocalizedName()))) {
            ((Search)this.module).toRender.remove((Object)((SPacketBlockChange)event.getPacket()).getBlockPosition());
        }
    }
}

