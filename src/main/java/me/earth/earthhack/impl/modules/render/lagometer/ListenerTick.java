/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.ScaledResolution
 */
package me.earth.earthhack.impl.modules.render.lagometer;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.render.lagometer.LagOMeter;
import net.minecraft.client.gui.ScaledResolution;

final class ListenerTick
extends ModuleListener<LagOMeter, TickEvent> {
    public ListenerTick(LagOMeter module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        if (((LagOMeter)this.module).lagTime.getValue().booleanValue() || ((LagOMeter)this.module).response.getValue().booleanValue()) {
            ((LagOMeter)this.module).resolution = new ScaledResolution(mc);
        }
        if (!((LagOMeter)this.module).chat.getValue().booleanValue()) {
            if (((LagOMeter)this.module).sent) {
                Managers.CHAT.deleteMessage("Lag-O-Meter-Res", 2000);
                Managers.CHAT.deleteMessage("Lag-O-Meter-Lag", 2000);
                ((LagOMeter)this.module).sent = false;
            }
            return;
        }
        boolean sent = ((LagOMeter)this.module).sent;
        ((LagOMeter)this.module).sent = false;
        if (((LagOMeter)this.module).respondingMessage != null) {
            ((LagOMeter)this.module).sent = true;
            Managers.CHAT.sendDeleteMessage("<" + ((LagOMeter)this.module).getDisplayName() + "> " + ((LagOMeter)this.module).respondingMessage + ".", "Lag-O-Meter-Res", 2000);
        }
        if (((LagOMeter)this.module).lagMessage != null) {
            ((LagOMeter)this.module).sent = true;
            Managers.CHAT.sendDeleteMessage("<" + ((LagOMeter)this.module).getDisplayName() + "> " + ((LagOMeter)this.module).lagMessage + ".", "Lag-O-Meter-Lag", 2000);
        }
        if (sent && !((LagOMeter)this.module).sent) {
            Managers.CHAT.deleteMessage("Lag-O-Meter-Res", 2000);
            Managers.CHAT.deleteMessage("Lag-O-Meter-Lag", 2000);
        }
    }
}

