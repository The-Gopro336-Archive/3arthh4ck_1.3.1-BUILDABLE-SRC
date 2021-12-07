/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.network.play.server.SPacketSpawnObject
 */
package me.earth.earthhack.impl.modules.misc.announcer;

import java.util.Comparator;
import java.util.stream.Collectors;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.misc.announcer.Announcer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketSpawnObject;

public class ListenerSpawn
extends ModuleListener<Announcer, PacketEvent.Receive<SPacketSpawnObject>> {
    public ListenerSpawn(Announcer module) {
        super(module, PacketEvent.Receive.class, SPacketSpawnObject.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSpawnObject> event) {
        EntityPlayer closestPlayer;
        if ((((SPacketSpawnObject)event.getPacket()).getType() == 60 || ((SPacketSpawnObject)event.getPacket()).getType() == 91) && ((double)Math.abs(((SPacketSpawnObject)event.getPacket()).getSpeedX() / 8000) > 0.001 || (double)Math.abs(((SPacketSpawnObject)event.getPacket()).getSpeedY() / 8000) > 0.001 || (double)Math.abs(((SPacketSpawnObject)event.getPacket()).getSpeedZ() / 8000) > 0.001) && ((Announcer)this.module).miss.getValue().booleanValue() && (closestPlayer = (EntityPlayer)Managers.ENTITIES.getPlayers().stream().filter(player -> player != ListenerSpawn.mc.player && !Managers.FRIENDS.contains((EntityPlayer)player)).sorted(Comparator.comparing(player -> player.getDistanceSq(((SPacketSpawnObject)event.getPacket()).getX(), ((SPacketSpawnObject)event.getPacket()).getY(), ((SPacketSpawnObject)event.getPacket()).getZ()))).collect(Collectors.toList()).get(0)) != null) {
            ((Announcer)this.module).arrowMap.put(((SPacketSpawnObject)event.getPacket()).getEntityID(), closestPlayer);
        }
    }
}

