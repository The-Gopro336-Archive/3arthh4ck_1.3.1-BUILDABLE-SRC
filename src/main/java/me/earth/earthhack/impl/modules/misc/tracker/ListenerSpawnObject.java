package me.earth.earthhack.impl.modules.misc.tracker;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.tracker.Tracker;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.math.BlockPos;

final class ListenerSpawnObject
extends ModuleListener<Tracker, PacketEvent.Receive<SPacketSpawnObject>> {
    public ListenerSpawnObject(Tracker module) {
        super(module, PacketEvent.Receive.class, SPacketSpawnObject.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSpawnObject> event) {
        SPacketSpawnObject p = (SPacketSpawnObject)event.getPacket();
        if (ListenerSpawnObject.mc.world == null || ListenerSpawnObject.mc.player == null) {
            return;
        }
        if (p.getType() == 51) {
            BlockPos pos = new BlockPos(p.getX(), p.getY(), p.getZ());
            if (!((Tracker)this.module).placed.remove(pos)) {
                ((Tracker)this.module).crystals.incrementAndGet();
            }
        } else if (p.getType() == 75) {
            if (((Tracker)this.module).awaitingExp.get() > 0) {
                if (ListenerSpawnObject.mc.player.getDistanceSq(p.getX(), p.getY(), p.getZ()) < 16.0) {
                    ((Tracker)this.module).awaitingExp.decrementAndGet();
                } else {
                    ((Tracker)this.module).exp.incrementAndGet();
                }
            } else {
                ((Tracker)this.module).exp.incrementAndGet();
            }
        }
    }
}
