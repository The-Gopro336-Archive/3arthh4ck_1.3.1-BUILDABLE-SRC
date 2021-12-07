/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayerDigging
 *  net.minecraft.network.play.client.CPacketPlayerDigging$Action
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.modules.player.blocktweaks;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.nuker.Nuker;
import me.earth.earthhack.impl.modules.player.blocktweaks.BlockTweaks;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.mine.MineUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.math.BlockPos;

final class ListenerPacket
extends ModuleListener<BlockTweaks, PacketEvent.Post<CPacketPlayerDigging>> {
    private static final ModuleCache<Nuker> NUKER = Caches.getModule(Nuker.class);
    private static final SettingCache<Boolean, BooleanSetting, Nuker> NUKE = Caches.getSetting(Nuker.class, BooleanSetting.class, "Nuke", false);

    public ListenerPacket(BlockTweaks module) {
        super(module, PacketEvent.Post.class, CPacketPlayerDigging.class);
    }

    @Override
    public void invoke(PacketEvent.Post<CPacketPlayerDigging> event) {
        BlockPos pos;
        CPacketPlayerDigging packet;
        if (!(!((BlockTweaks)this.module).noBreakAnim.getValue().booleanValue() || PlayerUtil.isCreative((EntityPlayer)ListenerPacket.mc.player) || NUKER.isEnabled() && NUKE.getValue().booleanValue() || (packet = (CPacketPlayerDigging)event.getPacket()).getAction() != CPacketPlayerDigging.Action.START_DESTROY_BLOCK || !MineUtil.canBreak(pos = packet.getPosition()))) {
            ListenerPacket.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, packet.getPosition(), packet.getFacing()));
        }
    }
}

