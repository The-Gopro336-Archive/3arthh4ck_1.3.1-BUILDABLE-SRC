/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.math.AxisAlignedBB
 */
package me.earth.earthhack.impl.modules.player.spectate;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.spectate.Spectate;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;

final class ListenerMove
extends ModuleListener<Spectate, MoveEvent> {
    public ListenerMove(Spectate module) {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event) {
        if (((Spectate)this.module).stopMove.getValue().booleanValue()) {
            double x = event.getX();
            double y = event.getY();
            double z = event.getZ();
            if (y != 0.0) {
                for (AxisAlignedBB a : ListenerMove.mc.world.getCollisionBoxes((Entity)ListenerMove.mc.player, ListenerMove.mc.player.getEntityBoundingBox().expand(x, y, z))) {
                    y = a.calculateYOffset(ListenerMove.mc.player.getEntityBoundingBox(), y);
                }
            }
            event.setX(0.0);
            event.setY(y == 0.0 ? -0.0784000015258789 : 0.0);
            event.setZ(0.0);
        }
    }
}

