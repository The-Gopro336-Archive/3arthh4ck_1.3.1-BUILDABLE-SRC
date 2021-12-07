/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.network.play.client.CPacketPlayer$PositionRotation
 *  net.minecraft.network.play.client.CPacketVehicleMove
 */
package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.event.listeners.SendListener;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketVehicleMove;

public class EntityDesyncCommand
extends Command
implements Globals {
    private Entity dismounted;

    public EntityDesyncCommand() {
        super(new String[][]{{"entitydesync"}, {"dismount", "remount", "delete"}});
        CommandDescriptions.register(this, "EntityDesync for exploit purposes.");
        Bus.EVENT_BUS.register(new EventListener<WorldClientEvent.Load>(WorldClientEvent.Load.class){

            @Override
            public void invoke(WorldClientEvent.Load event) {
                EntityDesyncCommand.this.dismounted = null;
            }
        });
        Bus.EVENT_BUS.register(new EventListener<TickEvent>(TickEvent.class){

            @Override
            public void invoke(TickEvent event) {
                if (Globals.mc.player != null && EntityDesyncCommand.this.dismounted != null) {
                    if (Globals.mc.player.isRiding()) {
                        EntityDesyncCommand.this.dismounted = null;
                        return;
                    }
                    EntityDesyncCommand.this.dismounted.setPosition(Globals.mc.player.posX, Globals.mc.player.posY, Globals.mc.player.posZ);
                    Globals.mc.player.connection.sendPacket((Packet)new CPacketVehicleMove(EntityDesyncCommand.this.dismounted));
                }
            }
        });
        Bus.EVENT_BUS.register(new SendListener<CPacketPlayer.Position>(CPacketPlayer.Position.class, event -> {
            if (this.dismounted != null) {
                event.setCancelled(true);
            }
        }));
        Bus.EVENT_BUS.register(new SendListener<CPacketPlayer.PositionRotation>(CPacketPlayer.PositionRotation.class, event -> {
            if (this.dismounted != null) {
                event.setCancelled(true);
            }
        }));
    }

    @Override
    public void execute(String[] args) {
        if (EntityDesyncCommand.mc.player == null) {
            ChatUtil.sendMessage("\u00a7cYou need to be ingame to use this command.");
            return;
        }
        if (args.length == 1) {
            if (this.dismounted == null) {
                ChatUtil.sendMessage("You are currently not desynced from any entity.");
            } else {
                ChatUtil.sendMessage("You are currently dismounted.");
            }
            return;
        }
        if (args[1].equalsIgnoreCase("dismount")) {
            Entity entity = EntityDesyncCommand.mc.player.getRidingEntity();
            if (entity == null) {
                ChatUtil.sendMessage("\u00a7cThere's no entity to dismount!");
                return;
            }
            this.dismounted = entity;
            EntityDesyncCommand.mc.player.dismountRidingEntity();
            EntityDesyncCommand.mc.world.removeEntity(entity);
            ChatUtil.sendMessage("\u00a7aDismounted successfully.");
        } else if (args[1].equalsIgnoreCase("remount")) {
            if (this.dismounted == null) {
                ChatUtil.sendMessage("\u00a7cThere's no entity to remount!");
                return;
            }
            this.dismounted.isDead = false;
            EntityDesyncCommand.mc.world.spawnEntity(this.dismounted);
            EntityDesyncCommand.mc.player.startRiding(this.dismounted, true);
            ChatUtil.sendMessage("\u00a7aRemounted successfully.");
        } else if (args[1].equalsIgnoreCase("delete")) {
            if (this.dismounted == null) {
                ChatUtil.sendMessage("\u00a7cThere's no entity to delete!");
                return;
            }
            this.dismounted = null;
            ChatUtil.sendMessage("\u00a7aDeleted dismounted entity.");
        } else {
            ChatUtil.sendMessage("\u00a7cUnrecognized option, try dis/remount/delete.");
        }
    }
}

