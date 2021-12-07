package me.earth.earthhack.impl.managers.minecraft.movement;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketEntityAction;

class NCPManager$3
extends EventListener<PacketEvent.Send<CPacketClickWindow>> {
    NCPManager$3(Class target, int priority, Class type) {
        super(target, priority, type);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketClickWindow> event) {
        if (!NCPManager.this.isStrict() || event.isCancelled()) {
            return;
        }
        if (Globals.mc.player.isActiveItemStackBlocking()) {
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> Globals.mc.playerController.onStoppedUsingItem(Globals.mc.player));
        }
        if (Managers.ACTION.isSneaking()) {
            NCPManager.this.endedSneak = true;
            Globals.mc.player.connection.sendPacket(new CPacketEntityAction(Globals.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }
        if (Managers.ACTION.isSprinting()) {
            NCPManager.this.endedSprint = true;
            Globals.mc.player.connection.sendPacket(new CPacketEntityAction(Globals.mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
        }
    }
}
