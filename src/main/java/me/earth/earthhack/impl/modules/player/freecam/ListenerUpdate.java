/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.player.freecam;

import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.freecam.Freecam;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;

final class ListenerUpdate
extends ModuleListener<Freecam, UpdateEvent> {
    public ListenerUpdate(Freecam module) {
        super(module, UpdateEvent.class);
    }

    @Override
    public void invoke(UpdateEvent event) {
        ListenerUpdate.mc.player.noClip = true;
        ListenerUpdate.mc.player.setVelocity(0.0, 0.0, 0.0);
        ListenerUpdate.mc.player.jumpMovementFactor = ((Freecam)this.module).speed.getValue().floatValue();
        double[] dir = MovementUtil.strafe(((Freecam)this.module).speed.getValue().floatValue());
        if (ListenerUpdate.mc.player.movementInput.moveStrafe != 0.0f || ListenerUpdate.mc.player.movementInput.moveForward != 0.0f) {
            ListenerUpdate.mc.player.motionX = dir[0];
            ListenerUpdate.mc.player.motionZ = dir[1];
        } else {
            ListenerUpdate.mc.player.motionX = 0.0;
            ListenerUpdate.mc.player.motionZ = 0.0;
        }
        ListenerUpdate.mc.player.setSprinting(false);
        if (ListenerUpdate.mc.gameSettings.keyBindJump.isKeyDown()) {
            ListenerUpdate.mc.player.motionY += (double)((Freecam)this.module).speed.getValue().floatValue();
        }
        if (ListenerUpdate.mc.gameSettings.keyBindSneak.isKeyDown()) {
            ListenerUpdate.mc.player.motionY -= (double)((Freecam)this.module).speed.getValue().floatValue();
        }
    }
}

