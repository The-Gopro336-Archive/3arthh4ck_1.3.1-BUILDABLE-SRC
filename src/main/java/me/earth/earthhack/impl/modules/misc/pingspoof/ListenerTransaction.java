package me.earth.earthhack.impl.modules.misc.pingspoof;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypass;
import me.earth.earthhack.impl.modules.misc.pingspoof.PingSpoof;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTransaction;

final class ListenerTransaction
extends ModuleListener<PingSpoof, PacketEvent.Send<CPacketConfirmTransaction>> {
    private static final ModuleCache<PingBypass> PINGBYPASS = Caches.getModule(PingBypass.class);

    public ListenerTransaction(PingSpoof module) {
        super(module, PacketEvent.Send.class, CPacketConfirmTransaction.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketConfirmTransaction> event) {
        if (!PINGBYPASS.isEnabled() && ((PingSpoof)this.module).transactions.getValue().booleanValue()) {
            if (((PingSpoof)this.module).transactionIDs.remove(((CPacketConfirmTransaction)event.getPacket()).getUid())) {
                return;
            }
            ((PingSpoof)this.module).onPacket((Packet<?>)event.getPacket());
            event.setCancelled(true);
        }
    }
}
