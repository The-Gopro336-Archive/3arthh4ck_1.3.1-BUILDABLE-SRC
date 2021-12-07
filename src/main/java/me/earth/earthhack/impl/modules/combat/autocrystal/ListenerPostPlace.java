/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 */
package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;

final class ListenerPostPlace
extends ModuleListener<AutoCrystal, PacketEvent.Post<CPacketPlayerTryUseItemOnBlock>> {
    public ListenerPostPlace(AutoCrystal module) {
        super(module, PacketEvent.Post.class, CPacketPlayerTryUseItemOnBlock.class);
    }

    @Override
    public void invoke(PacketEvent.Post<CPacketPlayerTryUseItemOnBlock> event) {
        if (((AutoCrystal)this.module).idPredict.getValue().booleanValue() && !((AutoCrystal)this.module).noGod && ((AutoCrystal)this.module).breakTimer.passed(((AutoCrystal)this.module).breakDelay.getValue().intValue()) && ListenerPostPlace.mc.player.getHeldItem(((CPacketPlayerTryUseItemOnBlock)event.getPacket()).getHand()).getItem() == Items.END_CRYSTAL && ((AutoCrystal)this.module).idHelper.isSafe(Managers.ENTITIES.getPlayersAsync(), ((AutoCrystal)this.module).holdingCheck.getValue(), ((AutoCrystal)this.module).toolCheck.getValue())) {
            ((AutoCrystal)this.module).idHelper.attack(((AutoCrystal)this.module).breakSwing.getValue(), ((AutoCrystal)this.module).godSwing.getValue(), ((AutoCrystal)this.module).idOffset.getValue(), ((AutoCrystal)this.module).idPackets.getValue(), ((AutoCrystal)this.module).idDelay.getValue());
            ((AutoCrystal)this.module).breakTimer.reset(((AutoCrystal)this.module).breakDelay.getValue().intValue());
        }
    }
}

