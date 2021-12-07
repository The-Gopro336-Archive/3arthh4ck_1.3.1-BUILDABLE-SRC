/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.play.server.SPacketEntityStatus
 */
package me.earth.earthhack.impl.modules.combat.offhand;

import java.util.HashSet;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.core.mixins.network.server.ISPacketEntityStatus;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.offhand.Offhand;
import me.earth.earthhack.impl.modules.player.suicide.Suicide;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityStatus;

final class ListenerTotem
extends ModuleListener<Offhand, PacketEvent.Receive<SPacketEntityStatus>> {
    private static final ModuleCache<Suicide> SUICIDE = Caches.getModule(Suicide.class);

    public ListenerTotem(Offhand module) {
        super(module, PacketEvent.Receive.class, SPacketEntityStatus.class);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void invoke(PacketEvent.Receive<SPacketEntityStatus> event) {
        int currentItem;
        EntityPlayerSP player = ListenerTotem.mc.player;
        if (player == null || !InventoryUtil.validScreen() || !((Offhand)this.module).async.getValue().booleanValue() || ((SPacketEntityStatus)event.getPacket()).getOpCode() != 35 || !((Offhand)this.module).timer.passed(((Offhand)this.module).delay.getValue().intValue()) || player.getEntityId() != ((ISPacketEntityStatus)event.getPacket()).getEntityId() || SUICIDE.returnIfPresent(Suicide::deactivateOffhand, false).booleanValue()) {
            return;
        }
        try {
            Locks.PLACE_SWITCH_LOCK.lock();
            currentItem = ListenerTotem.mc.player.inventory.currentItem;
        }
        finally {
            Locks.PLACE_SWITCH_LOCK.unlock();
        }
        int slot = InventoryUtil.hotbarToInventory(currentItem);
        ItemStack stack = ListenerTotem.mc.player.inventory.getStackInSlot(currentItem);
        if (stack.getItem() != Items.TOTEM_OF_UNDYING) {
            slot = 45;
            stack = ListenerTotem.mc.player.getHeldItemOffhand();
            if (stack.getItem() != Items.TOTEM_OF_UNDYING) {
                return;
            }
        }
        if (stack.getCount() - 1 > 0) {
            return;
        }
        HashSet<Integer> ignore = new HashSet<Integer>();
        ignore.add(slot);
        int t = InventoryUtil.findItem(Items.TOTEM_OF_UNDYING, true, ignore);
        if (t == -1) {
            return;
        }
        int finalSlot = slot;
        Locks.acquire(Locks.WINDOW_CLICK_LOCK, () -> {
            if (InventoryUtil.get(t).getItem() == Items.TOTEM_OF_UNDYING) {
                InventoryUtil.put(finalSlot, ItemStack.EMPTY);
                if (t != -2) {
                    Managers.NCP.startMultiClick();
                    InventoryUtil.click(t);
                }
                InventoryUtil.click(finalSlot);
                if (t != 2) {
                    Managers.NCP.releaseMultiClick();
                }
            }
        });
        ((Offhand)this.module).asyncSlot = slot;
        ((Offhand)this.module).asyncTimer.reset();
        ((Offhand)this.module).postWindowClick();
    }
}

