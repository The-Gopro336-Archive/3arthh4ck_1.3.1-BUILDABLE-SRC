/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemFood
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItem
 */
package me.earth.earthhack.impl.modules.client.server;

import java.io.IOException;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.client.server.ServerModule;
import me.earth.earthhack.impl.modules.client.server.protocol.ProtocolUtil;
import me.earth.earthhack.impl.modules.client.server.util.ServerMode;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;

final class ListenerStartEating
extends ModuleListener<ServerModule, PacketEvent.Send<CPacketPlayerTryUseItem>> {
    public ListenerStartEating(ServerModule module) {
        super(module, PacketEvent.Send.class, Integer.MIN_VALUE, CPacketPlayerTryUseItem.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketPlayerTryUseItem> event) {
        if (event.isCancelled() || ((ServerModule)this.module).currentMode == ServerMode.Client || !((ServerModule)this.module).sync.getValue().booleanValue() || !(ListenerStartEating.mc.player.getHeldItem(((CPacketPlayerTryUseItem)event.getPacket()).getHand()).getItem() instanceof ItemFood)) {
            return;
        }
        ((ServerModule)this.module).isEating = true;
        byte[] packet = new byte[9];
        ProtocolUtil.addInt(11, packet);
        ProtocolUtil.addInt(1, packet, 4);
        packet[8] = -128;
        try {
            ((ServerModule)this.module).connectionManager.send(packet);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

