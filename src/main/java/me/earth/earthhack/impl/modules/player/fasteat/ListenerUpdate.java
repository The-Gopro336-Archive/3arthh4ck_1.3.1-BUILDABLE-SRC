package me.earth.earthhack.impl.modules.player.fasteat;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.fasteat.FastEat;
import me.earth.earthhack.impl.modules.player.fasteat.mode.FastEatMode;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

final class ListenerUpdate
extends ModuleListener<FastEat, MotionUpdateEvent> {
    public ListenerUpdate(FastEat module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (event.getStage() == Stage.PRE && ((FastEat)this.module).mode.getValue() == FastEatMode.Update && ((FastEat)this.module).isValid(ListenerUpdate.mc.player.getActiveItemStack())) {
            EnumHand hand = ListenerUpdate.mc.player.getActiveHand();
            if (hand == null) {
                hand = ListenerUpdate.mc.player.getHeldItemOffhand().equals(ListenerUpdate.mc.player.getActiveItemStack()) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
            }
            ListenerUpdate.mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(hand));
        } else if (event.getStage() == Stage.POST && ((FastEat)this.module).mode.getValue() == FastEatMode.Packet && ((FastEat)this.module).isValid(ListenerUpdate.mc.player.getActiveItemStack()) && (float)ListenerUpdate.mc.player.getItemInUseMaxCount() > ((FastEat)this.module).speed.getValue().floatValue() - 1.0f && ((FastEat)this.module).speed.getValue().floatValue() < 25.0f) {
            for (int i = 0; i < 32; ++i) {
                ListenerUpdate.mc.player.connection.sendPacket(new CPacketPlayer(ListenerUpdate.mc.player.onGround));
            }
            ListenerUpdate.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            ListenerUpdate.mc.player.stopActiveHand();
        }
    }
}
