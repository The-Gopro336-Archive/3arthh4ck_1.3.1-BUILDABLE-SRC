/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketCloseWindow
 */
package me.earth.earthhack.impl.modules.player.xcarry;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.modules.player.xcarry.ListenerCloseWindow;
import me.earth.earthhack.impl.util.client.SimpleData;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketCloseWindow;

public class XCarry
extends Module {
    public XCarry() {
        super("XCarry", Category.Player);
        this.listeners.add(new ListenerCloseWindow(this));
        this.setData(new SimpleData(this, "Allows you to store items in your crafting inventory and drag slot."));
    }

    @Override
    protected void onDisable() {
        if (XCarry.mc.player != null) {
            XCarry.mc.player.connection.sendPacket((Packet)new CPacketCloseWindow(XCarry.mc.player.inventoryContainer.windowId));
        }
    }
}

