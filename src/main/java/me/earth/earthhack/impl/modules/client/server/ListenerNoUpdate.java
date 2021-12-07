/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.client.server;

import me.earth.earthhack.impl.event.events.network.NoMotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.client.server.ServerModule;
import me.earth.earthhack.impl.modules.client.server.protocol.ProtocolPlayUtil;
import me.earth.earthhack.impl.modules.client.server.util.ServerMode;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;

final class ListenerNoUpdate
extends ModuleListener<ServerModule, NoMotionUpdateEvent> {
    public ListenerNoUpdate(ServerModule module) {
        super(module, NoMotionUpdateEvent.class);
    }

    @Override
    public void invoke(NoMotionUpdateEvent event) {
        if (((ServerModule)this.module).currentMode == ServerMode.Client || !((ServerModule)this.module).sync.getValue().booleanValue()) {
            return;
        }
        ProtocolPlayUtil.sendVelocityAndPosition(((ServerModule)this.module).connectionManager, RotationUtil.getRotationPlayer());
    }
}

