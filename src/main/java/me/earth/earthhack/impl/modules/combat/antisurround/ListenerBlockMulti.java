/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.server.SPacketMultiBlockChange
 *  net.minecraft.network.play.server.SPacketMultiBlockChange$BlockUpdateData
 */
package me.earth.earthhack.impl.modules.combat.antisurround;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.antisurround.AntiSurround;
import net.minecraft.network.play.server.SPacketMultiBlockChange;

final class ListenerBlockMulti
extends ModuleListener<AntiSurround, PacketEvent.Post<SPacketMultiBlockChange>> {
    public ListenerBlockMulti(AntiSurround module) {
        super(module, PacketEvent.Post.class, SPacketMultiBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Post<SPacketMultiBlockChange> event) {
        SPacketMultiBlockChange.BlockUpdateData pos;
        if (!((AntiSurround)this.module).async.getValue().booleanValue() || ((AntiSurround)this.module).active.get() || ListenerBlockMulti.mc.player == null || ((AntiSurround)this.module).holdingCheck()) {
            return;
        }
        SPacketMultiBlockChange.BlockUpdateData[] arrblockUpdateData = ((SPacketMultiBlockChange)event.getPacket()).getChangedBlocks();
        int n = arrblockUpdateData.length;
        for (int i = 0; !(i >= n || (pos = arrblockUpdateData[i]).getBlockState().getMaterial().isReplaceable() && ((AntiSurround)this.module).onBlockBreak(pos.getPos(), Managers.ENTITIES.getPlayers(), Managers.ENTITIES.getEntities())); ++i) {
        }
    }
}

