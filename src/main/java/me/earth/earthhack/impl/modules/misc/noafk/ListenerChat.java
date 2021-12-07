/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.server.SPacketChat
 */
package me.earth.earthhack.impl.modules.misc.noafk;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.noafk.NoAFK;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.network.play.server.SPacketChat;

final class ListenerChat
extends ModuleListener<NoAFK, PacketEvent.Receive<SPacketChat>> {
    public ListenerChat(NoAFK module) {
        super(module, PacketEvent.Receive.class, SPacketChat.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketChat> event) {
        String m;
        if (((NoAFK)this.module).autoReply.getValue().booleanValue() && ((m = ((SPacketChat)event.getPacket()).getChatComponent().getFormattedText()).contains(((NoAFK)this.module).color.getValue().getColor()) || ((NoAFK)this.module).color.getValue() == TextColor.Reset) && m.contains(((NoAFK)this.module).indicator.getValue())) {
            ListenerChat.mc.player.sendChatMessage(((NoAFK)this.module).reply.getValue() + ((NoAFK)this.module).message.getValue());
        }
    }
}

