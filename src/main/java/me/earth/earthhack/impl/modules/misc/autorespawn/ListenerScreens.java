/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGameOver
 */
package me.earth.earthhack.impl.modules.misc.autorespawn;

import me.earth.earthhack.impl.event.events.render.GuiScreenEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.autorespawn.AutoRespawn;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.client.gui.GuiGameOver;

final class ListenerScreens
extends ModuleListener<AutoRespawn, GuiScreenEvent<GuiGameOver>> {
    public ListenerScreens(AutoRespawn module) {
        super(module, GuiScreenEvent.class, GuiGameOver.class);
    }

    @Override
    public void invoke(GuiScreenEvent<GuiGameOver> event) {
        if (ListenerScreens.mc.player != null) {
            if (((AutoRespawn)this.module).coords.getValue().booleanValue()) {
                ChatUtil.sendMessage("\u00a7cYou died at \u00a7f" + MathUtil.round(ListenerScreens.mc.player.posX, 2) + "\u00a7c" + "x, " + "\u00a7f" + MathUtil.round(ListenerScreens.mc.player.posY, 2) + "\u00a7c" + "y, " + "\u00a7f" + MathUtil.round(ListenerScreens.mc.player.posZ, 2) + "\u00a7c" + "z.");
            }
            ListenerScreens.mc.player.respawnPlayer();
            event.setCancelled(true);
        }
    }
}

