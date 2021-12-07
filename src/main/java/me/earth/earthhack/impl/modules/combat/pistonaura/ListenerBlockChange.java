package me.earth.earthhack.impl.modules.combat.pistonaura;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.pistonaura.PistonAura;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockChange;

final class ListenerBlockChange
extends ModuleListener<PistonAura, PacketEvent.Receive<SPacketBlockChange>> {
    public ListenerBlockChange(PistonAura module) {
        super(module, PacketEvent.Receive.class, SPacketBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketBlockChange> event) {
        if (!((PistonAura)this.module).change.getValue().booleanValue()) {
            return;
        }
        mc.addScheduledTask(() -> {
            SPacketBlockChange packet;
            if (((PistonAura)this.module).current != null && (((PistonAura)this.module).checkUpdate((packet = (SPacketBlockChange)event.getPacket()).getBlockPosition(), packet.getBlockState(), ((PistonAura)this.module).current.getRedstonePos(), Blocks.REDSTONE_BLOCK, Blocks.REDSTONE_TORCH) || ((PistonAura)this.module).checkUpdate(packet.getBlockPosition(), packet.getBlockState(), ((PistonAura)this.module).current.getPistonPos(), Blocks.PISTON, Blocks.STICKY_PISTON))) {
                ((PistonAura)this.module).current.setValid(false);
            }
        });
    }
}
