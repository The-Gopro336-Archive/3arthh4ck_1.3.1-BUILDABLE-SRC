/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.impl.core.ducks.network.IPlayerControllerMP;
import me.earth.earthhack.impl.event.events.misc.ClickBlockEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.modules.player.speedmine.mode.MineMode;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import net.minecraft.entity.player.EntityPlayer;

final class ListenerClick
extends ModuleListener<Speedmine, ClickBlockEvent> {
    public ListenerClick(Speedmine module) {
        super(module, ClickBlockEvent.class);
    }

    @Override
    public void invoke(ClickBlockEvent event) {
        if (!PlayerUtil.isCreative((EntityPlayer)ListenerClick.mc.player) && (((Speedmine)this.module).noReset.getValue().booleanValue() || ((Speedmine)this.module).mode.getValue() == MineMode.Reset) && ((IPlayerControllerMP)ListenerClick.mc.playerController).getCurBlockDamageMP() > 0.1f) {
            ((IPlayerControllerMP)ListenerClick.mc.playerController).setIsHittingBlock(true);
        }
    }
}

