/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Blocks
 */
package me.earth.earthhack.impl.modules.movement.icespeed;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.icespeed.IceSpeed;
import net.minecraft.init.Blocks;

final class ListenerTick
extends ModuleListener<IceSpeed, TickEvent> {
    public ListenerTick(IceSpeed module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        Blocks.ICE.slipperiness = ((IceSpeed)this.module).speed.getValue().floatValue();
        Blocks.PACKED_ICE.slipperiness = ((IceSpeed)this.module).speed.getValue().floatValue();
        Blocks.FROSTED_ICE.slipperiness = ((IceSpeed)this.module).speed.getValue().floatValue();
    }
}

