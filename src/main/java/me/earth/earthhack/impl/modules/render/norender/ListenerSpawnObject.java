/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityTracker
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.network.play.server.SPacketSpawnObject
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.modules.render.norender;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.render.norender.NoRender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.world.World;

final class ListenerSpawnObject
extends ModuleListener<NoRender, PacketEvent.Receive<SPacketSpawnObject>> {
    public ListenerSpawnObject(NoRender module) {
        super(module, PacketEvent.Receive.class, -10, SPacketSpawnObject.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSpawnObject> event) {
        if (event.isCancelled() || !((NoRender)this.module).items.getValue().booleanValue() || ((SPacketSpawnObject)event.getPacket()).getType() != 2) {
            return;
        }
        SPacketSpawnObject p = (SPacketSpawnObject)event.getPacket();
        EntityItem e = new EntityItem((World)ListenerSpawnObject.mc.world, p.getX(), p.getY(), p.getZ());
        EntityTracker.updateServerPosition((Entity)e, (double)p.getX(), (double)p.getY(), (double)p.getZ());
        e.rotationPitch = (float)(p.getPitch() * 360) / 256.0f;
        e.rotationYaw = (float)(p.getYaw() * 360) / 256.0f;
        Entity[] parts = e.getParts();
        if (parts != null) {
            int id = p.getEntityID() - e.getEntityId();
            for (Entity part : parts) {
                part.setEntityId(part.getEntityId() + id);
            }
        }
        e.setEntityId(p.getEntityID());
        e.setUniqueId(p.getUniqueId());
        if (p.getData() > 0) {
            e.setVelocity((double)p.getSpeedX() / 8000.0, (double)p.getSpeedY() / 8000.0, (double)p.getSpeedZ() / 8000.0);
        }
        event.setCancelled(true);
        mc.addScheduledTask(() -> this.lambda$invoke$0((Entity)e, p));
    }

    private /* synthetic */ void lambda$invoke$0(Entity e, SPacketSpawnObject p) {
        Managers.SET_DEAD.setDeadCustom(e, Long.MAX_VALUE);
        ((NoRender)this.module).ids.add(p.getEntityID());
    }
}

