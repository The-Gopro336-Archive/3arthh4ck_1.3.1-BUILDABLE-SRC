/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package me.earth.earthhack.impl.modules.client.server.protocol.handlers;

import java.nio.ByteBuffer;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.client.server.api.IConnection;
import me.earth.earthhack.impl.modules.client.server.api.ILogger;
import me.earth.earthhack.impl.modules.client.server.api.IPacketHandler;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.entity.player.EntityPlayer;

public class PositionHandler
implements IPacketHandler,
Globals {
    private final ILogger logger;

    public PositionHandler(ILogger logger) {
        this.logger = logger;
    }

    @Override
    public void handle(IConnection connection, byte[] bytes) {
        EntityPlayer player = RotationUtil.getRotationPlayer();
        if (player == null) {
            this.logger.log("Received Position without being ingame!");
            return;
        }
        ByteBuffer buf = ByteBuffer.wrap(bytes);
        double x = buf.getDouble();
        double y = buf.getDouble();
        double z = buf.getDouble();
        mc.addScheduledTask(() -> player.setPosition(x, y, z));
    }
}

