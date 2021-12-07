/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.movement.phase;

import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.phase.Phase;
import me.earth.earthhack.impl.modules.movement.phase.mode.PhaseMode;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;

final class ListenerUpdate
extends ModuleListener<Phase, UpdateEvent> {
    public ListenerUpdate(Phase module) {
        super(module, UpdateEvent.class);
    }

    @Override
    public void invoke(UpdateEvent event) {
        double z;
        double x;
        double mz;
        double mx;
        double multiplier;
        if (((Phase)this.module).mode.getValue() == PhaseMode.NoClip) {
            ListenerUpdate.mc.player.noClip = true;
            ListenerUpdate.mc.player.onGround = false;
            ListenerUpdate.mc.player.fallDistance = 0.0f;
        }
        if (((Phase)this.module).mode.getValue() == PhaseMode.Constantiam && MovementUtil.isMoving() && ((Phase)this.module).constTeleport.getValue().booleanValue() && ((Phase)this.module).isPhasing()) {
            multiplier = ((Phase)this.module).constSpeed.getValue();
            mx = -Math.sin(Math.toRadians(ListenerUpdate.mc.player.rotationYaw));
            mz = Math.cos(Math.toRadians(ListenerUpdate.mc.player.rotationYaw));
            x = (double)ListenerUpdate.mc.player.movementInput.moveForward * multiplier * mx + (double)ListenerUpdate.mc.player.movementInput.moveStrafe * multiplier * mz;
            z = (double)ListenerUpdate.mc.player.movementInput.moveForward * multiplier * mz - (double)ListenerUpdate.mc.player.movementInput.moveStrafe * multiplier * mx;
            ListenerUpdate.mc.player.setPosition(ListenerUpdate.mc.player.posX + x, ListenerUpdate.mc.player.posY, ListenerUpdate.mc.player.posZ + z);
        }
        if (((Phase)this.module).mode.getValue() == PhaseMode.ConstantiamNew) {
            multiplier = 0.3;
            mx = -Math.sin(Math.toRadians(ListenerUpdate.mc.player.rotationYaw));
            mz = Math.cos(Math.toRadians(ListenerUpdate.mc.player.rotationYaw));
            x = (double)ListenerUpdate.mc.player.movementInput.moveForward * multiplier * mx + (double)ListenerUpdate.mc.player.movementInput.moveStrafe * multiplier * mz;
            z = (double)ListenerUpdate.mc.player.movementInput.moveForward * multiplier * mz - (double)ListenerUpdate.mc.player.movementInput.moveStrafe * multiplier * mx;
            if (ListenerUpdate.mc.player.collidedHorizontally) {
                if (!ListenerUpdate.mc.player.isOnLadder()) {
                    PacketUtil.doPosition(ListenerUpdate.mc.player.posX + x, ListenerUpdate.mc.player.posY, ListenerUpdate.mc.player.posZ + z, false);
                    for (int i = 1; i < 10; ++i) {
                        PacketUtil.doPosition(ListenerUpdate.mc.player.posX, 8.988465674311579E307, ListenerUpdate.mc.player.posZ, false);
                    }
                    ListenerUpdate.mc.player.setPosition(ListenerUpdate.mc.player.posX + x, ListenerUpdate.mc.player.posY, ListenerUpdate.mc.player.posZ + z);
                }
            }
        }
    }
}

