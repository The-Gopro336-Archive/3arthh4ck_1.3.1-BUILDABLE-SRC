/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.server.SPacketChat
 */
package me.earth.earthhack.impl.modules.misc.tracker;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.modules.misc.tracker.Tracker;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import net.minecraft.network.play.server.SPacketChat;

final class ListenerChat
extends ModuleListener<Tracker, PacketEvent.Receive<SPacketChat>> {
    public ListenerChat(Tracker module) {
        super(module, PacketEvent.Receive.class, SPacketChat.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketChat> event) {
        String s;
        if (((Tracker)this.module).autoEnable.getValue().booleanValue() && !((Tracker)this.module).awaiting && !((Tracker)this.module).isEnabled() && !(s = ((SPacketChat)event.getPacket()).getChatComponent().getFormattedText()).contains("<") && (s.contains("has accepted your duel request") || s.contains("Accepted the duel request from"))) {
            Scheduler.getInstance().scheduleAsynchronously(() -> {
                ModuleUtil.sendMessage((Module)this.module, "\u00a7dDuel accepted. Tracker will enable in \u00a7f5.0\u00a7d seconds!");
                ((Tracker)this.module).timer.reset();
                ((Tracker)this.module).awaiting = true;
            });
        }
    }
}

