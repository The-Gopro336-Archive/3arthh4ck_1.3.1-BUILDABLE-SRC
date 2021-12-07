package me.earth.earthhack.impl.modules.combat.legswitch;

import java.util.List;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.legswitch.LegSwitch;
import me.earth.earthhack.impl.modules.combat.legswitch.modes.LegAutoSwitch;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.play.server.SPacketMultiBlockChange;

final class ListenerBlockMulti
extends ModuleListener<LegSwitch, PacketEvent.Receive<SPacketMultiBlockChange>> {
    public ListenerBlockMulti(LegSwitch module) {
        super(module, PacketEvent.Receive.class, SPacketMultiBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketMultiBlockChange> event) {
        SPacketMultiBlockChange p = (SPacketMultiBlockChange)event.getPacket();
        if (((LegSwitch)this.module).breakBlock.getValue().booleanValue() && (InventoryUtil.isHolding(Items.END_CRYSTAL) || ((LegSwitch)this.module).autoSwitch.getValue() != LegAutoSwitch.None) && (((LegSwitch)this.module).rotate.getValue() == ACRotate.None || ((LegSwitch)this.module).rotate.getValue() == ACRotate.Break)) {
            List<EntityPlayer> players = Managers.ENTITIES.getPlayers();
            for (SPacketMultiBlockChange.BlockUpdateData d : p.getChangedBlocks()) {
                if (!((LegSwitch)this.module).isValid(d.getPos(), d.getBlockState(), players)) continue;
                event.addPostEvent(((LegSwitch)this.module)::startCalculation);
                return;
            }
        }
    }
}
