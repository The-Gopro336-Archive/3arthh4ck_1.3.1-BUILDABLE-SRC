package me.earth.earthhack.impl.modules.client.server;

import java.io.IOException;
import me.earth.earthhack.impl.event.events.misc.AbortEatingEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.client.server.ServerModule;
import me.earth.earthhack.impl.modules.client.server.protocol.ProtocolUtil;
import me.earth.earthhack.impl.modules.client.server.util.ServerMode;
import net.minecraft.item.ItemFood;

final class ListenerStopEating
extends ModuleListener<ServerModule, AbortEatingEvent> {
    public ListenerStopEating(ServerModule module) {
        super(module, AbortEatingEvent.class, Integer.MIN_VALUE);
    }

    @Override
    public void invoke(AbortEatingEvent event) {
        if (!(((ServerModule)this.module).currentMode != ServerMode.Client && ((ServerModule)this.module).sync.getValue().booleanValue() && ((ServerModule)this.module).isEating && ListenerStopEating.mc.player.getActiveItemStack().getItem() instanceof ItemFood)) {
            return;
        }
        ((ServerModule)this.module).isEating = false;
        byte[] packet = new byte[9];
        ProtocolUtil.addInt(11, packet);
        ProtocolUtil.addInt(1, packet, 4);
        packet[8] = 0;
        try {
            ((ServerModule)this.module).connectionManager.send(packet);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
