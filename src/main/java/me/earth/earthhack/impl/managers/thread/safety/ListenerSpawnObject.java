/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.server.SPacketSpawnObject
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.managers.thread.safety;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.thread.safety.SafetyManager;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.math.BlockPos;

final class ListenerSpawnObject
extends ModuleListener<SafetyManager, PacketEvent.Receive<SPacketSpawnObject>> {
    public ListenerSpawnObject(SafetyManager manager) {
        super(manager, PacketEvent.Receive.class, SPacketSpawnObject.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSpawnObject> event) {
        SPacketSpawnObject p = (SPacketSpawnObject)event.getPacket();
        if (p.getType() == 51 && ListenerSpawnObject.mc.player != null) {
            BlockPos blockPos = new BlockPos(p.getX(), p.getY(), p.getZ());
            if (DamageUtil.calculate(blockPos.down()) > ((SafetyManager)this.module).damage.getValue().floatValue()) {
                ((SafetyManager)this.module).setSafe(false);
            }
        }
    }
}

