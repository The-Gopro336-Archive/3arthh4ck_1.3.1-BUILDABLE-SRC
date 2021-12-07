/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 *  net.minecraft.network.play.server.SPacketBlockChange
 */
package me.earth.earthhack.impl.modules.combat.legswitch;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.legswitch.LegSwitch;
import me.earth.earthhack.impl.modules.combat.legswitch.modes.LegAutoSwitch;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.init.Items;
import net.minecraft.network.play.server.SPacketBlockChange;

final class ListenerBlockChange
extends ModuleListener<LegSwitch, PacketEvent.Receive<SPacketBlockChange>> {
    public ListenerBlockChange(LegSwitch module) {
        super(module, PacketEvent.Receive.class, SPacketBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketBlockChange> event) {
        SPacketBlockChange p = (SPacketBlockChange)event.getPacket();
        if (((LegSwitch)this.module).breakBlock.getValue().booleanValue() && (InventoryUtil.isHolding(Items.END_CRYSTAL) || ((LegSwitch)this.module).autoSwitch.getValue() != LegAutoSwitch.None) && (((LegSwitch)this.module).rotate.getValue() == ACRotate.None || ((LegSwitch)this.module).rotate.getValue() == ACRotate.Break) && ((LegSwitch)this.module).isValid(p.getBlockPosition(), p.getBlockState(), Managers.ENTITIES.getPlayers())) {
            event.addPostEvent(((LegSwitch)this.module)::startCalculation);
        }
    }
}

