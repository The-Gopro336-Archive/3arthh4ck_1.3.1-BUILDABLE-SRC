package me.earth.earthhack.impl.modules.render.logoutspots;

import me.earth.earthhack.impl.event.events.network.ConnectionEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.render.logoutspots.LogoutSpots;
import me.earth.earthhack.impl.modules.render.logoutspots.mode.MessageMode;
import me.earth.earthhack.impl.modules.render.logoutspots.util.LogoutSpot;
import me.earth.earthhack.impl.util.math.MathUtil;
import net.minecraft.entity.player.EntityPlayer;

final class ListenerLeave
extends ModuleListener<LogoutSpots, ConnectionEvent.Leave> {
    public ListenerLeave(LogoutSpots module) {
        super(module, ConnectionEvent.Leave.class);
    }

    @Override
    public void invoke(ConnectionEvent.Leave event) {
        EntityPlayer player = event.getPlayer();
        if (((LogoutSpots)this.module).message.getValue() != MessageMode.None) {
            String text = null;
            if (player != null) {
                text = String.format("\u00a7e" + player.getName() + "\u00a7c" + " just logged out, at: %sx, %sy, %sz.", MathUtil.round(player.posX, 1), MathUtil.round(player.posY, 1), MathUtil.round(player.posZ, 1));
            } else if (((LogoutSpots)this.module).message.getValue() != MessageMode.Render) {
                text = "\u00a7e" + event.getName() + "\u00a7c" + " just logged out.";
            }
            if (text != null) {
                Managers.CHAT.sendDeleteMessageScheduled(text, event.getUuid().toString(), 2000);
            }
        }
        if (player != null && (((LogoutSpots)this.module).friends.getValue().booleanValue() || !Managers.FRIENDS.contains(player))) {
            LogoutSpot spot = new LogoutSpot(player);
            ((LogoutSpots)this.module).spots.put(player.getUniqueID(), spot);
        }
    }
}
