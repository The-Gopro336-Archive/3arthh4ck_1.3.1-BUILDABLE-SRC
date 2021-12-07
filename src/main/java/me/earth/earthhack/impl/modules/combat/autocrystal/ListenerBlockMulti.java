package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.HelperUtil;
import net.minecraft.block.material.Material;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.util.math.BlockPos;

final class ListenerBlockMulti
extends ModuleListener<AutoCrystal, PacketEvent.Receive<SPacketMultiBlockChange>> {
    public ListenerBlockMulti(AutoCrystal module) {
        super(module, PacketEvent.Receive.class, Integer.MIN_VALUE, SPacketMultiBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketMultiBlockChange> event) {
        if (((AutoCrystal)this.module).multiThread.getValue().booleanValue() && ((AutoCrystal)this.module).blockChangeThread.getValue().booleanValue()) {
            SPacketMultiBlockChange packet = (SPacketMultiBlockChange)event.getPacket();
            event.addPostEvent(() -> {
                for (SPacketMultiBlockChange.BlockUpdateData data : packet.getChangedBlocks()) {
                    if (data.getBlockState().func_185904_a() != Material.AIR || !HelperUtil.validChange(data.getPos(), ListenerBlockMulti.mc.world.playerEntities)) continue;
                    ((AutoCrystal)this.module).threadHelper.startThread(new BlockPos[0]);
                    break;
                }
            });
        }
    }
}
