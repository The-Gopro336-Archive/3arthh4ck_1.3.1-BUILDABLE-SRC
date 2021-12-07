/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ServerData
 *  net.minecraft.util.math.Vec3i
 */
package me.earth.earthhack.impl.modules.misc.chat;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.chat.Chat;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.math.Vec3i;

final class ListenerTick
extends ModuleListener<Chat, TickEvent> {
    public ListenerTick(Chat module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        ServerData data;
        if (event.isSafe() && ((Chat)this.module).autoQMain.getValue().booleanValue() && ((Chat)this.module).timer.passed(((Chat)this.module).qDelay.getValue().intValue()) && (data = mc.getCurrentServerData()) != null && "2b2t.org".equalsIgnoreCase(data.serverIP) && ListenerTick.mc.player.dimension == 1 && ListenerTick.mc.player.getPosition().equals((Object)new Vec3i(0, 240, 0))) {
            ChatUtil.sendMessage("<" + ((Chat)this.module).getDisplayName() + "> Sending " + "\u00a7y" + ((Chat)this.module).message.getValue() + "\u00a7r" + "...");
            ListenerTick.mc.player.sendChatMessage(((Chat)this.module).message.getValue());
            ((Chat)this.module).timer.reset();
        }
    }
}

