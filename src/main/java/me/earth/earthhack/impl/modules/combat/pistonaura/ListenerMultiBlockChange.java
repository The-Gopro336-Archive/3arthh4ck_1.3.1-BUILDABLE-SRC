/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.init.Blocks
 *  net.minecraft.network.play.server.SPacketMultiBlockChange
 *  net.minecraft.network.play.server.SPacketMultiBlockChange$BlockUpdateData
 */
package me.earth.earthhack.impl.modules.combat.pistonaura;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.pistonaura.PistonAura;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketMultiBlockChange;

final class ListenerMultiBlockChange
extends ModuleListener<PistonAura, PacketEvent.Receive<SPacketMultiBlockChange>> {
    public ListenerMultiBlockChange(PistonAura module) {
        super(module, PacketEvent.Receive.class, SPacketMultiBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketMultiBlockChange> event) {
        if (!((PistonAura)this.module).multiChange.getValue().booleanValue()) {
            return;
        }
        mc.addScheduledTask(() -> {
            if (((PistonAura)this.module).current != null) {
                SPacketMultiBlockChange packet = (SPacketMultiBlockChange)event.getPacket();
                for (SPacketMultiBlockChange.BlockUpdateData data : packet.getChangedBlocks()) {
                    if (!((PistonAura)this.module).checkUpdate(data.getPos(), data.getBlockState(), ((PistonAura)this.module).current.getRedstonePos(), Blocks.REDSTONE_BLOCK, Blocks.REDSTONE_TORCH) && !((PistonAura)this.module).checkUpdate(data.getPos(), data.getBlockState(), ((PistonAura)this.module).current.getPistonPos(), (Block)Blocks.PISTON, (Block)Blocks.STICKY_PISTON)) continue;
                    ((PistonAura)this.module).current.setValid(false);
                    return;
                }
            }
        });
    }
}

