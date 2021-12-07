package me.earth.earthhack.impl.modules.client.server;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.client.server.ServerModule;
import me.earth.earthhack.impl.modules.client.server.util.ServerMode;

final class ListenerMove
extends ModuleListener<ServerModule, MoveEvent> {
    public ListenerMove(ServerModule module) {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event) {
        if (((ServerModule)this.module).currentMode == ServerMode.Client && ((ServerModule)this.module).sync.getValue().booleanValue()) {
            event.setX(((ServerModule)this.module).getLastX());
            event.setY(((ServerModule)this.module).getLastY());
            event.setZ(((ServerModule)this.module).getLastZ());
        }
    }
}
