/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 */
package me.earth.earthhack.impl.modules.misc.tooltips;

import me.earth.earthhack.impl.event.events.render.Render2DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.misc.tooltips.ToolTips;
import me.earth.earthhack.impl.modules.misc.tooltips.util.TimeStack;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

final class ListenerRender2D
extends ModuleListener<ToolTips, Render2DEvent> {
    public ListenerRender2D(ToolTips module) {
        super(module, Render2DEvent.class);
    }

    @Override
    public void invoke(Render2DEvent event) {
        int x = 1;
        int y = (int)(Managers.TEXT.getStringHeight() + 4.0f);
        for (EntityPlayer player : ListenerRender2D.mc.world.playerEntities) {
            TimeStack stack;
            if (player == null || EntityUtil.isDead((Entity)player) || (stack = ((ToolTips)this.module).spiedPlayers.get(player.getName().toLowerCase())) == null || !player.getHeldItemMainhand().equals((Object)stack.getStack()) && System.nanoTime() - stack.getTime() >= 2000000000L) continue;
            if (!((ToolTips)this.module).drawShulkerToolTip(stack.getStack(), x, y, player.getName())) {
                ((ToolTips)this.module).spiedPlayers.remove(player.getName().toLowerCase());
                continue;
            }
            y += 79;
        }
    }
}

