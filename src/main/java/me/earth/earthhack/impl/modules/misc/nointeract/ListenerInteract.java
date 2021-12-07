/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 */
package me.earth.earthhack.impl.modules.misc.nointeract;

import me.earth.earthhack.impl.event.events.misc.ClickBlockEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.misc.nointeract.NoInteract;
import net.minecraft.block.state.IBlockState;

final class ListenerInteract
extends ModuleListener<NoInteract, ClickBlockEvent.Right> {
    public ListenerInteract(NoInteract module) {
        super(module, ClickBlockEvent.Right.class);
    }

    @Override
    public void invoke(ClickBlockEvent.Right event) {
        if (((NoInteract)this.module).sneak.getValue().booleanValue() && Managers.ACTION.isSneaking()) {
            return;
        }
        IBlockState state = ListenerInteract.mc.world.getBlockState(event.getPos());
        if (((NoInteract)this.module).isValid(state.getBlock().getLocalizedName())) {
            event.setCancelled(true);
        }
    }
}

