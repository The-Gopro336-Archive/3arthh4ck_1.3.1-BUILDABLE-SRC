package me.earth.earthhack.impl.modules.client.pingbypass.submodules.sautototem;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.core.ducks.util.IContainer;
import me.earth.earthhack.impl.core.mixins.network.server.ISPacketSetSlot;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypass;
import me.earth.earthhack.impl.modules.client.pingbypass.submodules.sautototem.ServerAutoTotem;
import me.earth.earthhack.impl.modules.combat.offhand.Offhand;
import me.earth.earthhack.impl.modules.combat.offhand.modes.OffhandMode;
import net.minecraft.inventory.Slot;
import net.minecraft.network.play.server.SPacketSetSlot;

final class ListenerSetSlot
extends ModuleListener<ServerAutoTotem, PacketEvent.Receive<SPacketSetSlot>> {
    private static final ModuleCache<Offhand> OFFHAND = Caches.getModule(Offhand.class);

    public ListenerSetSlot(ServerAutoTotem module) {
        super(module, PacketEvent.Receive.class, SPacketSetSlot.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSetSlot> event) {
        if (((PingBypass)((ServerAutoTotem)this.module).getParent()).isEnabled() && ListenerSetSlot.mc.player != null) {
            SPacketSetSlot packet = (SPacketSetSlot)event.getPacket();
            if (packet.getSlot() == -1337) {
                ((IContainer)((Object)ListenerSetSlot.mc.player.openContainer)).setTransactionID((short)packet.getWindowId());
                ((ISPacketSetSlot)((Object)packet)).setWindowId(-1);
            } else if (packet.getWindowId() == -128) {
                event.setCancelled(true);
                mc.addScheduledTask(() -> {
                    OffhandMode recovery = OFFHAND.returnIfPresent(Offhand::getMode, null);
                    OFFHAND.computeIfPresent(offhand -> {
                        offhand.setMode(OffhandMode.TOTEM);
                        offhand.postWindowClick();
                    });
                    OFFHAND.computeIfPresent(offhand -> offhand.setRecovery(recovery));
                    Slot slot = (Slot)ListenerSetSlot.mc.player.inventoryContainer.inventorySlots.get(packet.getSlot());
                    slot.putStack(packet.getStack());
                });
            }
        }
    }
}
