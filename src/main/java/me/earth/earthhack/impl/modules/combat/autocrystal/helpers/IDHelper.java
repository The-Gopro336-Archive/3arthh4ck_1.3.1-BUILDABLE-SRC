/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemPickaxe
 *  net.minecraft.item.ItemSpade
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketUseEntity
 *  net.minecraft.network.play.server.SPacketSpawnExperienceOrb
 *  net.minecraft.network.play.server.SPacketSpawnGlobalEntity
 *  net.minecraft.network.play.server.SPacketSpawnMob
 *  net.minecraft.network.play.server.SPacketSpawnObject
 *  net.minecraft.network.play.server.SPacketSpawnPainting
 *  net.minecraft.network.play.server.SPacketSpawnPlayer
 *  net.minecraft.util.EnumHand
 */
package me.earth.earthhack.impl.modules.combat.autocrystal.helpers;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.listeners.ReceiveListener;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.SwingTime;
import me.earth.earthhack.impl.util.helpers.blocks.modes.PlaceSwing;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.thread.ThreadUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSpawnExperienceOrb;
import net.minecraft.network.play.server.SPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.SPacketSpawnMob;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.network.play.server.SPacketSpawnPainting;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.util.EnumHand;

public class IDHelper
extends SubscriberImpl
implements Globals {
    private static final ScheduledExecutorService THREAD = ThreadUtil.newDaemonScheduledExecutor("ID-Helper");
    private volatile int highestID;
    private boolean updated;

    public IDHelper() {
        this.listeners.add(new ReceiveListener<SPacketSpawnObject>(SPacketSpawnObject.class, event -> this.checkID(((SPacketSpawnObject)event.getPacket()).getEntityID())));
        this.listeners.add(new ReceiveListener<SPacketSpawnExperienceOrb>(SPacketSpawnExperienceOrb.class, event -> this.checkID(((SPacketSpawnExperienceOrb)event.getPacket()).getEntityID())));
        this.listeners.add(new ReceiveListener<SPacketSpawnPlayer>(SPacketSpawnPlayer.class, event -> this.checkID(((SPacketSpawnPlayer)event.getPacket()).getEntityID())));
        this.listeners.add(new ReceiveListener<SPacketSpawnGlobalEntity>(SPacketSpawnGlobalEntity.class, event -> this.checkID(((SPacketSpawnGlobalEntity)event.getPacket()).getEntityId())));
        this.listeners.add(new ReceiveListener<SPacketSpawnPainting>(SPacketSpawnPainting.class, event -> this.checkID(((SPacketSpawnPainting)event.getPacket()).getEntityID())));
        this.listeners.add(new ReceiveListener<SPacketSpawnMob>(SPacketSpawnMob.class, event -> this.checkID(((SPacketSpawnMob)event.getPacket()).getEntityID())));
    }

    public int getHighestID() {
        return this.highestID;
    }

    public void setHighestID(int id) {
        this.highestID = id;
    }

    public boolean isUpdated() {
        return this.updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public void update() {
        int highest = this.getHighestID();
        for (Entity entity : IDHelper.mc.world.loadedEntityList) {
            if (entity.getEntityId() <= highest) continue;
            highest = entity.getEntityId();
        }
        if (highest > this.highestID) {
            this.highestID = highest;
        }
    }

    public boolean isSafe(List<EntityPlayer> players, boolean holdingCheck, boolean toolCheck) {
        if (!holdingCheck) {
            return true;
        }
        for (EntityPlayer player : players) {
            if (!this.isDangerous(player, true, toolCheck)) continue;
            return false;
        }
        return true;
    }

    public boolean isDangerous(EntityPlayer player, boolean holdingCheck, boolean toolCheck) {
        if (!holdingCheck) {
            return false;
        }
        return InventoryUtil.isHolding((EntityLivingBase)player, (Item)Items.BOW) || InventoryUtil.isHolding((EntityLivingBase)player, Items.EXPERIENCE_BOTTLE) || toolCheck && (player.getHeldItemMainhand().getItem() instanceof ItemPickaxe || player.getHeldItemMainhand().getItem() instanceof ItemSpade);
    }

    public void attack(SwingTime breakSwing, PlaceSwing godSwing, int idOffset, int packets, int sleep) {
        if (sleep <= 0) {
            this.attackPackets(breakSwing, godSwing, idOffset, packets);
        } else {
            THREAD.schedule(() -> {
                this.update();
                this.attackPackets(breakSwing, godSwing, idOffset, packets);
            }, (long)sleep, TimeUnit.MILLISECONDS);
        }
    }

    private void attackPackets(SwingTime breakSwing, PlaceSwing godSwing, int idOffset, int packets) {
        for (int i = 0; i < packets; ++i) {
            int id = this.highestID + idOffset + i;
            Entity entity = IDHelper.mc.world.getEntityByID(id);
            if (entity != null && !(entity instanceof EntityEnderCrystal)) continue;
            if (godSwing == PlaceSwing.Always && breakSwing == SwingTime.Pre) {
                Swing.Packet.swing(EnumHand.MAIN_HAND);
            }
            CPacketUseEntity packet = PacketUtil.attackPacket(id);
            IDHelper.mc.player.connection.sendPacket((Packet)packet);
            if (godSwing != PlaceSwing.Always || breakSwing != SwingTime.Post) continue;
            Swing.Packet.swing(EnumHand.MAIN_HAND);
        }
        if (godSwing == PlaceSwing.Once) {
            Swing.Packet.swing(EnumHand.MAIN_HAND);
        }
    }

    private void checkID(int id) {
        if (id > this.highestID) {
            this.highestID = id;
        }
    }
}

