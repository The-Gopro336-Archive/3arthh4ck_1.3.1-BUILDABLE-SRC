package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.modules.player.speedmine.mode.MineMode;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.EnumFacing;

final class ListenerBlockChange
extends ModuleListener<Speedmine, PacketEvent.Receive<SPacketBlockChange>> {
    public ListenerBlockChange(Speedmine module) {
        super(module, PacketEvent.Receive.class, SPacketBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketBlockChange> event) {
        SPacketBlockChange packet = (SPacketBlockChange)event.getPacket();
        if (packet.getBlockPosition().equals(((Speedmine)this.module).pos) && packet.getBlockState().getBlock() == Blocks.AIR && (((Speedmine)this.module).mode.getValue() != MineMode.Smart || ((Speedmine)this.module).sentPacket) && ((Speedmine)this.module).mode.getValue() != MineMode.Instant && ((Speedmine)this.module).mode.getValue() != MineMode.Civ) {
            mc.addScheduledTask(((Speedmine)this.module)::reset);
        } else if (packet.getBlockPosition().equals(((Speedmine)this.module).pos) && packet.getBlockState() == ListenerBlockChange.mc.world.getBlockState(((Speedmine)this.module).pos) && ((Speedmine)this.module).shouldAbort && ((Speedmine)this.module).mode.getValue() == MineMode.Instant) {
            ListenerBlockChange.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, ((Speedmine)this.module).pos, EnumFacing.DOWN));
            ((Speedmine)this.module).shouldAbort = false;
        }
    }
}
