/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.client.CPacketCloseWindow
 */
package me.earth.earthhack.impl.modules.combat.autothirtytwok;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.autothirtytwok.Auto32k;
import net.minecraft.network.play.client.CPacketCloseWindow;

final class ListenerCPacketCloseWindow
extends ModuleListener<Auto32k, PacketEvent.Send<CPacketCloseWindow>> {
    public ListenerCPacketCloseWindow(Auto32k module) {
        super(module, PacketEvent.Send.class, CPacketCloseWindow.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketCloseWindow> event) {
        ((Auto32k)this.module).onCPacketCloseWindow(event);
    }
}

