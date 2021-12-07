/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItem
 */
package me.earth.earthhack.impl.modules.render.norender;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.render.norender.NoRender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;

final class ListenerTick
extends ModuleListener<NoRender, TickEvent> {
    private boolean previous;

    public ListenerTick(NoRender module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        boolean shouldUpdate = ((NoRender)this.module).items.getValue();
        if (event.isSafe()) {
            if (this.previous != shouldUpdate) {
                if (shouldUpdate) {
                    for (Entity entity : ListenerTick.mc.world.loadedEntityList) {
                        if (!(entity instanceof EntityItem) || entity.isDead) continue;
                        Managers.SET_DEAD.setDeadCustom(entity, Long.MAX_VALUE);
                        ((NoRender)this.module).ids.add(entity.getEntityId());
                    }
                } else {
                    ((NoRender)this.module).ids.forEach(Managers.SET_DEAD::revive);
                    ((NoRender)this.module).ids.clear();
                }
            }
        } else {
            ((NoRender)this.module).ids.forEach(Managers.SET_DEAD::confirmKill);
            ((NoRender)this.module).ids.clear();
        }
        this.previous = shouldUpdate;
    }
}

