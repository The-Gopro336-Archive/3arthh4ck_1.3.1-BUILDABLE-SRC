package me.earth.earthhack.impl.modules.player.ncptweaks;

import me.earth.earthhack.impl.core.ducks.entity.IEntityPlayerSP;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.player.ncptweaks.NCPTweaks;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;

final class ListenerWindowClick
extends ModuleListener<NCPTweaks, PacketEvent.Send<CPacketClickWindow>> {
    private final StopWatch timer = new StopWatch();

    public ListenerWindowClick(NCPTweaks module) {
        super(module, PacketEvent.Send.class, -1001, CPacketClickWindow.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketClickWindow> event) {
        if (((NCPTweaks)this.module).eating.getValue().booleanValue() && this.isEating()) {
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> ListenerWindowClick.mc.playerController.onStoppedUsingItem(ListenerWindowClick.mc.player));
        }
        if (((NCPTweaks)this.module).moving.getValue().booleanValue()) {
            if (((NCPTweaks)this.module).packet.getValue().booleanValue() && this.timer.passed(((NCPTweaks)this.module).delay.getValue().intValue())) {
                float yaw = ((IEntityPlayerSP)((Object)ListenerWindowClick.mc.player)).getLastReportedYaw();
                PacketUtil.doRotation((float)((double)yaw + 4.0E-4), ((IEntityPlayerSP)((Object)ListenerWindowClick.mc.player)).getLastReportedPitch(), ListenerWindowClick.mc.player.onGround);
                this.timer.reset();
            }
            ListenerWindowClick.mc.player.setVelocity(0.0, 0.0, 0.0);
        }
        if (((NCPTweaks)this.module).resetNCP.getValue().booleanValue()) {
            Managers.NCP.reset();
        }
    }

    private boolean isEating() {
        ItemStack stack = ListenerWindowClick.mc.player.getActiveItemStack();
        if (ListenerWindowClick.mc.player.isHandActive() && !stack.isEmpty()) {
            Item item = stack.getItem();
            if (item.getItemUseAction(stack) != EnumAction.EAT) {
                return false;
            }
            return item.getMaxItemUseDuration(stack) - ListenerWindowClick.mc.player.getItemInUseCount() >= 5;
        }
        return false;
    }
}
