/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.network.Packet
 */
package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.nuker.Nuker;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;

final class ListenerMotion
extends ModuleListener<Speedmine, MotionUpdateEvent> {
    private static final ModuleCache<Nuker> NUKER = Caches.getModule(Nuker.class);
    private static final SettingCache<Boolean, BooleanSetting, Nuker> NUKE = Caches.getSetting(Nuker.class, BooleanSetting.class, "Nuke", false);

    public ListenerMotion(Speedmine module) {
        super(module, MotionUpdateEvent.class, 999);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (!((Speedmine)this.module).rotate.getValue().booleanValue()) {
            return;
        }
        Packet<?> packet = ((Speedmine)this.module).limitRotationPacket;
        if (!(event.getStage() != Stage.PRE || ((Speedmine)this.module).pos == null || PlayerUtil.isCreative((EntityPlayer)ListenerMotion.mc.player) || NUKER.isEnabled() && NUKE.getValue().booleanValue() || InventoryUtil.isHolding(Items.EXPERIENCE_BOTTLE) && !ListenerMotion.mc.gameSettings.keyBindUseItem.isKeyDown() || ((Speedmine)this.module).limitRotations.getValue().booleanValue() && packet == null)) {
            ((Speedmine)this.module).rotations = RotationUtil.getRotations(((Speedmine)this.module).pos, ((Speedmine)this.module).facing);
            event.setYaw(((Speedmine)this.module).rotations[0]);
            event.setPitch(((Speedmine)this.module).rotations[1]);
        } else if (event.getStage() == Stage.POST && ((Speedmine)this.module).limitRotations.getValue().booleanValue() && packet != null) {
            boolean toAir = ((Speedmine)this.module).toAir.getValue();
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> {
                int last = ListenerMotion.mc.player.inventory.currentItem;
                InventoryUtil.switchTo(((Speedmine)this.module).limitRotationSlot);
                if (((Speedmine)this.module).event.getValue().booleanValue()) {
                    ListenerMotion.mc.player.connection.sendPacket(packet);
                } else {
                    NetworkUtil.sendPacketNoEvent(packet, false);
                }
                InventoryUtil.switchTo(last);
            });
            ((Speedmine)this.module).onSendPacket();
            ((Speedmine)this.module).limitRotationPacket = null;
            ((Speedmine)this.module).limitRotationSlot = -1;
            ((Speedmine)this.module).postSend(toAir);
        }
    }
}

