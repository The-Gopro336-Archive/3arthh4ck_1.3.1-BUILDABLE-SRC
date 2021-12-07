package me.earth.earthhack.impl.modules.misc.autofish;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.autoeat.AutoEat;
import me.earth.earthhack.impl.modules.misc.autofish.AutoFish;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

final class ListenerTick
extends ModuleListener<AutoFish, TickEvent> {
    private static final ModuleCache<AutoEat> AUTOEAT = Caches.getModule(AutoEat.class);

    public ListenerTick(AutoFish module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        if (!event.isSafe() || AUTOEAT.returnIfPresent(AutoEat::isEating, false).booleanValue()) {
            return;
        }
        int slot = InventoryUtil.findHotbarItem(Items.FISHING_ROD, new Item[0]);
        if (slot == -1) {
            ModuleUtil.disableRed((Module)this.module, "No fishing rod found in your hotbar.");
        }
        if (ListenerTick.mc.player.inventory.currentItem != slot) {
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> InventoryUtil.switchTo(slot));
        } else if (((AutoFish)this.module).delayCounter > 0) {
            --((AutoFish)this.module).delayCounter;
        } else {
            EntityFishHook fish = ListenerTick.mc.player.fishEntity;
            if (fish == null) {
                ((AutoFish)this.module).click();
                return;
            }
            if (++((AutoFish)this.module).timeout > 720) {
                ((AutoFish)this.module).click();
            }
            if (ListenerTick.mc.player.fishEntity.caughtEntity != null) {
                ((AutoFish)this.module).click();
            }
            if (((AutoFish)this.module).splash) {
                if (++((AutoFish)this.module).splashTicks >= 4) {
                    ((AutoFish)this.module).click();
                    ((AutoFish)this.module).splash = false;
                }
            } else if (((AutoFish)this.module).splashTicks != 0) {
                --((AutoFish)this.module).splashTicks;
            }
        }
    }
}
