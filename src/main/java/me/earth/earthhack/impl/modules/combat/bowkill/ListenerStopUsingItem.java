/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.init.Items
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketConfirmTeleport
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 *  net.minecraft.network.play.client.CPacketPlayerDigging
 *  net.minecraft.network.play.client.CPacketPlayerDigging$Action
 */
package me.earth.earthhack.impl.modules.combat.bowkill;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.bowkill.BowKiller;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerDigging;

final class ListenerStopUsingItem
extends ModuleListener<BowKiller, PacketEvent.Send<CPacketPlayerDigging>> {
    public ListenerStopUsingItem(BowKiller module) {
        super(module, PacketEvent.Send.class, CPacketPlayerDigging.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketPlayerDigging> event) {
        if (!ListenerStopUsingItem.mc.player.collidedVertically) {
            return;
        }
        if (((CPacketPlayerDigging)event.getPacket()).getAction() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM && ListenerStopUsingItem.mc.player.getActiveItemStack().getItem() == Items.BOW && ((BowKiller)this.module).blockUnder) {
            ((BowKiller)this.module).cancelling = false;
            if (((BowKiller)this.module).packetsSent >= ((BowKiller)this.module).runs.getValue() * 2 || ((BowKiller)this.module).always.getValue().booleanValue()) {
                PacketUtil.sendAction(CPacketEntityAction.Action.START_SPRINTING);
                if (((BowKiller)this.module).cancelRotate.getValue().booleanValue() && (ListenerStopUsingItem.mc.player.rotationYaw != Managers.ROTATION.getServerYaw() || ListenerStopUsingItem.mc.player.rotationPitch != Managers.ROTATION.getServerPitch())) {
                    PacketUtil.doRotation(ListenerStopUsingItem.mc.player.rotationYaw, ListenerStopUsingItem.mc.player.rotationPitch, true);
                }
                for (int i = 0; i < ((BowKiller)this.module).runs.getValue() + ((BowKiller)this.module).buffer.getValue(); ++i) {
                    if (i != 0 && i % ((BowKiller)this.module).interval.getValue() == 0) {
                        int id = Managers.POSITION.getTeleportID();
                        for (int j = 0; j < ((BowKiller)this.module).teleports.getValue(); ++j) {
                            ListenerStopUsingItem.mc.player.connection.sendPacket((Packet)new CPacketConfirmTeleport(++id));
                        }
                    }
                    double[] dir = MovementUtil.strafe(0.001);
                    if (((BowKiller)this.module).rotate.getValue().booleanValue()) {
                        ((BowKiller)this.module).target = ((BowKiller)this.module).findTarget();
                        if (((BowKiller)this.module).target != null) {
                            float[] rotations = ((BowKiller)this.module).rotationSmoother.getRotations((Entity)RotationUtil.getRotationPlayer(), ((BowKiller)this.module).target, ((BowKiller)this.module).height.getValue(), ((BowKiller)this.module).soft.getValue().floatValue());
                            if (rotations == null) continue;
                            PacketUtil.doPosRotNoEvent(ListenerStopUsingItem.mc.player.posX + (((BowKiller)this.module).move.getValue() != false ? dir[0] : 0.0), ListenerStopUsingItem.mc.player.posY + 1.3E-13, ListenerStopUsingItem.mc.player.posZ + (((BowKiller)this.module).move.getValue() != false ? dir[1] : 0.0), rotations[0], rotations[1], true);
                            PacketUtil.doPosRotNoEvent(ListenerStopUsingItem.mc.player.posX + (((BowKiller)this.module).move.getValue() != false ? dir[0] * 2.0 : 0.0), ListenerStopUsingItem.mc.player.posY + 2.7E-13, ListenerStopUsingItem.mc.player.posZ + (((BowKiller)this.module).move.getValue() != false ? dir[1] * 2.0 : 0.0), rotations[0], rotations[1], false);
                            continue;
                        }
                        PacketUtil.doPosRotNoEvent(ListenerStopUsingItem.mc.player.posX + (((BowKiller)this.module).move.getValue() != false ? dir[0] : 0.0), ListenerStopUsingItem.mc.player.posY + 1.3E-13, ListenerStopUsingItem.mc.player.posZ + (((BowKiller)this.module).move.getValue() != false ? dir[1] : 0.0), ListenerStopUsingItem.mc.player.rotationYaw, ListenerStopUsingItem.mc.player.rotationPitch, true);
                        PacketUtil.doPosRotNoEvent(ListenerStopUsingItem.mc.player.posX + (((BowKiller)this.module).move.getValue() != false ? dir[0] * 2.0 : 0.0), ListenerStopUsingItem.mc.player.posY + 2.7E-13, ListenerStopUsingItem.mc.player.posZ + (((BowKiller)this.module).move.getValue() != false ? dir[1] * 2.0 : 0.0), ListenerStopUsingItem.mc.player.rotationYaw, ListenerStopUsingItem.mc.player.rotationPitch, false);
                        continue;
                    }
                    PacketUtil.doPosRotNoEvent(ListenerStopUsingItem.mc.player.posX + (((BowKiller)this.module).move.getValue() != false ? dir[0] : 0.0), ListenerStopUsingItem.mc.player.posY + 1.3E-13, ListenerStopUsingItem.mc.player.posZ + (((BowKiller)this.module).move.getValue() != false ? dir[1] : 0.0), ListenerStopUsingItem.mc.player.rotationYaw, ListenerStopUsingItem.mc.player.rotationPitch, true);
                    PacketUtil.doPosRotNoEvent(ListenerStopUsingItem.mc.player.posX + (((BowKiller)this.module).move.getValue() != false ? dir[0] * 2.0 : 0.0), ListenerStopUsingItem.mc.player.posY + 2.7E-13, ListenerStopUsingItem.mc.player.posZ + (((BowKiller)this.module).move.getValue() != false ? dir[1] * 2.0 : 0.0), ListenerStopUsingItem.mc.player.rotationYaw, ListenerStopUsingItem.mc.player.rotationPitch, false);
                }
            }
            ((BowKiller)this.module).packetsSent = 0;
        }
    }
}

