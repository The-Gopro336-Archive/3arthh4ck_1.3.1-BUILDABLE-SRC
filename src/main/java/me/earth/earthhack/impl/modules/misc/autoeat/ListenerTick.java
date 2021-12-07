package me.earth.earthhack.impl.modules.misc.autoeat;

import java.util.Iterator;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.impl.core.mixins.item.IITemFood;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.misc.autoeat.AutoEat;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

final class ListenerTick
extends ModuleListener<AutoEat, TickEvent> {
    public ListenerTick(AutoEat module) {
        super(module, TickEvent.class, 11);
    }

    @Override
    public void invoke(TickEvent event) {
        if (!event.isSafe()) {
            ((AutoEat)this.module).lastSlot = -1;
            ((AutoEat)this.module).isEating = false;
            ((AutoEat)this.module).force = false;
            ((AutoEat)this.module).server = false;
            return;
        }
        boolean bl = ((AutoEat)this.module).force = ((AutoEat)this.module).always.getValue() != false || ((AutoEat)this.module).server;
        if ((float)ListenerTick.mc.player.getFoodStats().getFoodLevel() > ((AutoEat)this.module).hunger.getValue().floatValue() && (((AutoEat)this.module).health.getValue() == false || !this.enemyCheck() || Managers.SAFETY.isSafe() && EntityUtil.getHealth(ListenerTick.mc.player, ((AutoEat)this.module).calcWithAbsorption.getValue()) > ((AutoEat)this.module).safeHealth.getValue().floatValue() || !Managers.SAFETY.isSafe() && EntityUtil.getHealth(ListenerTick.mc.player, ((AutoEat)this.module).calcWithAbsorption.getValue()) > ((AutoEat)this.module).unsafeHealth.getValue().floatValue()) && (!((AutoEat)this.module).absorption.getValue().booleanValue() || ListenerTick.mc.player.getAbsorptionAmount() > ((AutoEat)this.module).absorptionAmount.getValue().floatValue()) && !((AutoEat)this.module).force) {
            if (((AutoEat)this.module).isEating) {
                Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> InventoryUtil.switchTo(((AutoEat)this.module).lastSlot));
                ((AutoEat)this.module).reset();
            }
            return;
        }
        int slot = InventoryUtil.findInHotbar(s -> s.getItem() instanceof ItemFood && !this.hasBadEffect((IITemFood)((Object)s.getItem())) && (!(s.getItem() instanceof ItemFishFood) || ItemFishFood.FishType.byItemStack((ItemStack)s) != ItemFishFood.FishType.PUFFERFISH));
        if (slot == -1) {
            ModuleUtil.sendMessage((Module)this.module, "\u00a7cNo food found in your hotbar!");
            return;
        }
        if (((AutoEat)this.module).lastSlot == -1) {
            ((AutoEat)this.module).lastSlot = ListenerTick.mc.player.inventory.currentItem;
        }
        Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> InventoryUtil.switchTo(slot));
        KeyBinding.setKeyBindState((int)ListenerTick.mc.gameSettings.keyBindUseItem.getKeyCode(), (boolean)true);
        ((AutoEat)this.module).isEating = true;
    }

    private boolean enemyCheck() {
        if (((AutoEat)this.module).enemyRange.getValue().floatValue() != 0.0f) {
            EntityPlayer entity = EntityUtil.getClosestEnemy();
            return entity != null && entity.getDistanceSq(RotationUtil.getRotationPlayer()) < (double)MathUtil.square(((AutoEat)this.module).enemyRange.getValue().floatValue());
        }
        return false;
    }

    private boolean hasBadEffect(IITemFood itemFood) {
        PotionEffect effect = itemFood.getPotionId();
        if (effect != null) {
            Iterator iterator = Potion.REGISTRY.iterator();
            while (iterator.hasNext()) {
                Potion p = (Potion)iterator.next();
                if (!p.isBadEffect() || !p.equals(effect.getPotion())) continue;
                return true;
            }
        }
        return false;
    }
}
