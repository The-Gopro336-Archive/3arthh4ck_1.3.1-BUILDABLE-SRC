package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.HelperUtil;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.math.BlockPos;

final class ListenerBlockChange
extends ModuleListener<AutoCrystal, PacketEvent.Receive<SPacketBlockChange>> {
    public ListenerBlockChange(AutoCrystal module) {
        super(module, PacketEvent.Receive.class, Integer.MIN_VALUE, SPacketBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketBlockChange> event) {
        SPacketBlockChange packet;
        if (((AutoCrystal)this.module).multiThread.getValue().booleanValue() && ((AutoCrystal)this.module).blockChangeThread.getValue().booleanValue() && (packet = (SPacketBlockChange)event.getPacket()).getBlockState().getBlock() == Blocks.AIR && ListenerBlockChange.mc.player.getDistanceSq(packet.getBlockPosition()) < 40.0) {
            event.addPostEvent(() -> {
                if (ListenerBlockChange.mc.world != null && HelperUtil.validChange(packet.getBlockPosition(), ListenerBlockChange.mc.world.playerEntities)) {
                    ((AutoCrystal)this.module).threadHelper.startThread(new BlockPos[0]);
                }
            });
        }
    }
}
