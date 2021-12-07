package me.earth.earthhack.impl.modules.movement.fastswim;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.movement.fastswim.FastSwim;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.block.material.Material;

final class ListenerMove
extends ModuleListener<FastSwim, MoveEvent> {
    public ListenerMove(FastSwim module) {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event) {
        if (((FastSwim)this.module).strafe.getValue().booleanValue()) {
            if (!((FastSwim)this.module).accelerate.getValue().booleanValue() && Managers.NCP.passed(250)) {
                if (!ListenerMove.mc.player.onGround) {
                    if (ListenerMove.mc.player.isInsideOfMaterial(Material.LAVA)) {
                        MovementUtil.strafe(event, (double)((FastSwim)this.module).hLava.getValue());
                        if (!((FastSwim)this.module).fall.getValue().booleanValue()) {
                            if (ListenerMove.mc.gameSettings.keyBindSneak.isKeyDown()) {
                                event.setY(-((FastSwim)this.module).downLava.getValue().doubleValue());
                            } else if (ListenerMove.mc.gameSettings.keyBindJump.isKeyDown()) {
                                event.setY(((FastSwim)this.module).hLava.getValue());
                            } else {
                                event.setY(0.0);
                            }
                        }
                    } else if (ListenerMove.mc.player.isInsideOfMaterial(Material.WATER)) {
                        MovementUtil.strafe(event, (double)((FastSwim)this.module).hLava.getValue());
                        if (!((FastSwim)this.module).fall.getValue().booleanValue()) {
                            if (ListenerMove.mc.gameSettings.keyBindSneak.isKeyDown()) {
                                event.setY(-((FastSwim)this.module).downLava.getValue().doubleValue());
                            } else if (ListenerMove.mc.gameSettings.keyBindJump.isKeyDown()) {
                                event.setY(((FastSwim)this.module).hLava.getValue());
                            } else {
                                event.setY(0.0);
                            }
                        }
                    }
                }
            } else if (((FastSwim)this.module).accelerate.getValue().booleanValue()) {
                if (!ListenerMove.mc.player.onGround) {
                    if (Managers.NCP.passed(250)) {
                        if (ListenerMove.mc.player.isInsideOfMaterial(Material.LAVA)) {
                            ((FastSwim)this.module).waterSpeed *= ((FastSwim)this.module).accelerateFactor.getValue().doubleValue();
                        } else if (ListenerMove.mc.player.isInsideOfMaterial(Material.WATER)) {
                            ((FastSwim)this.module).lavaSpeed *= ((FastSwim)this.module).accelerateFactor.getValue().doubleValue();
                        }
                    }
                    if (ListenerMove.mc.player.isInsideOfMaterial(Material.LAVA)) {
                        MovementUtil.strafe(event, ((FastSwim)this.module).lavaSpeed);
                        if (!((FastSwim)this.module).fall.getValue().booleanValue()) {
                            if (ListenerMove.mc.gameSettings.keyBindSneak.isKeyDown()) {
                                event.setY(-((FastSwim)this.module).downLava.getValue().doubleValue());
                            } else if (ListenerMove.mc.gameSettings.keyBindJump.isKeyDown()) {
                                event.setY(((FastSwim)this.module).hLava.getValue());
                            } else {
                                event.setY(0.0);
                            }
                        }
                    } else if (ListenerMove.mc.player.isInsideOfMaterial(Material.WATER)) {
                        MovementUtil.strafe(event, ((FastSwim)this.module).waterSpeed);
                        if (!((FastSwim)this.module).fall.getValue().booleanValue()) {
                            if (ListenerMove.mc.gameSettings.keyBindSneak.isKeyDown()) {
                                event.setY(-((FastSwim)this.module).downLava.getValue().doubleValue());
                            } else if (ListenerMove.mc.gameSettings.keyBindJump.isKeyDown()) {
                                event.setY(((FastSwim)this.module).hLava.getValue());
                            } else {
                                event.setY(0.0);
                            }
                        }
                    }
                } else {
                    ((FastSwim)this.module).waterSpeed = ((FastSwim)this.module).hWater.getValue();
                    ((FastSwim)this.module).lavaSpeed = ((FastSwim)this.module).hLava.getValue();
                }
            }
        } else if (Managers.NCP.passed(250) && !ListenerMove.mc.player.onGround) {
            if (ListenerMove.mc.player.isInsideOfMaterial(Material.LAVA)) {
                event.setX(event.getX() * ((FastSwim)this.module).hLava.getValue());
                event.setY(event.getY() * ((FastSwim)this.module).vLava.getValue());
                event.setZ(event.getZ() * ((FastSwim)this.module).hLava.getValue());
            } else if (ListenerMove.mc.player.isInsideOfMaterial(Material.WATER)) {
                event.setX(event.getX() * ((FastSwim)this.module).hWater.getValue());
                event.setY(event.getY() * ((FastSwim)this.module).vWater.getValue());
                event.setZ(event.getZ() * ((FastSwim)this.module).hWater.getValue());
            }
        }
    }
}
