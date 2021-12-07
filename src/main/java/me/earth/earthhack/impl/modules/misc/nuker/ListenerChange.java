package me.earth.earthhack.impl.modules.misc.nuker;

import java.util.Set;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.nuker.Nuker;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.mine.MineUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.block.Block;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.math.BlockPos;

final class ListenerChange
extends ModuleListener<Nuker, PacketEvent.Receive<SPacketBlockChange>> {
    public ListenerChange(Nuker module) {
        super(module, PacketEvent.Receive.class, 11, SPacketBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketBlockChange> event) {
        if (((Nuker)this.module).instant.getValue().booleanValue() && ((Nuker)this.module).rotate.getValue() != Rotate.Normal) {
            SPacketBlockChange packet = (SPacketBlockChange)event.getPacket();
            Set<Block> blocks = ((Nuker)this.module).getBlocks();
            if (blocks.isEmpty()) {
                return;
            }
            if (blocks.contains(packet.getBlockState().getBlock()) && BlockUtil.getDistanceSqDigging(packet.getBlockPosition()) <= (double)MathUtil.square(((Nuker)this.module).range.getValue().floatValue())) {
                mc.addScheduledTask(() -> {
                    if (ListenerChange.mc.player.getActiveItemStack().getItem() instanceof ItemFood) {
                        return;
                    }
                    BlockPos pos = packet.getBlockPosition();
                    int slot = MineUtil.findBestTool(pos);
                    if (slot != -1) {
                        Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> {
                            int lastSlot = ListenerChange.mc.player.inventory.currentItem;
                            ((Nuker)this.module).timer.reset();
                            InventoryUtil.switchTo(slot);
                            ((Nuker)this.module).attack(pos);
                            InventoryUtil.switchTo(lastSlot);
                        });
                    }
                });
            }
        }
    }
}
