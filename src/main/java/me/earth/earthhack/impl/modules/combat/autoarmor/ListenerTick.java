/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemStack
 */
package me.earth.earthhack.impl.modules.combat.autoarmor;

import java.util.ArrayList;
import java.util.Map;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.autoarmor.AutoArmor;
import me.earth.earthhack.impl.modules.combat.autoarmor.util.DamageStack;
import me.earth.earthhack.impl.modules.combat.autoarmor.util.DesyncClick;
import me.earth.earthhack.impl.modules.player.xcarry.XCarry;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

final class ListenerTick
extends ModuleListener<AutoArmor, TickEvent> {
    private static final ModuleCache<XCarry> XCARRY = Caches.getModule(XCarry.class);

    public ListenerTick(AutoArmor module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        if (!event.isSafe() || this.checkDesync()) {
            ((AutoArmor)this.module).putBackClick = null;
            return;
        }
        ((AutoArmor)this.module).stackSet = false;
        ((AutoArmor)this.module).queuedSlots.clear();
        ((AutoArmor)this.module).windowClicks.clear();
        ((AutoArmor)this.module).desyncMap.entrySet().removeIf(e -> System.currentTimeMillis() - ((DesyncClick)e.getValue()).getTimeStamp() > (long)((AutoArmor)this.module).removeTime.getValue().intValue());
        if (InventoryUtil.validScreen()) {
            if (((AutoArmor)this.module).canAutoMend()) {
                ((AutoArmor)this.module).queuedSlots.add(-2);
                ArrayList<DamageStack> stacks = new ArrayList<DamageStack>(4);
                for (int i = 5; i < 9; ++i) {
                    float percent;
                    ItemStack stack = ListenerTick.mc.player.inventoryContainer.getSlot(i).getStack();
                    if (stack.isEmpty() || !((percent = DamageUtil.getPercent(stack)) > (float)((Integer)((AutoArmor)this.module).damages[i - 5].getValue()).intValue())) continue;
                    stacks.add(new DamageStack(stack, percent, i));
                }
                stacks.sort(DamageStack::compareTo);
                ItemStack setStack = ((AutoArmor)this.module).setStack();
                if (setStack == null) {
                    return;
                }
                ItemStack drag = setStack;
                for (DamageStack stack : stacks) {
                    ItemStack sStack = stack.getStack();
                    int slot = AutoArmor.findItem(Items.AIR, XCARRY.isEnabled(), ((AutoArmor)this.module).queuedSlots);
                    if (slot == -1) {
                        if (((AutoArmor)this.module).dragTakeOff.getValue().booleanValue() && (((AutoArmor)this.module).stackSet || ListenerTick.mc.player.inventory.getItemStack().isEmpty())) {
                            ((AutoArmor)this.module).queueClick(stack.getSlot(), sStack, drag, -1);
                        }
                        return;
                    }
                    if (slot == -2) continue;
                    ((AutoArmor)this.module).queueClick(stack.getSlot(), sStack, drag, slot).setDoubleClick(((AutoArmor)this.module).doubleClicks.getValue());
                    drag = sStack;
                    ItemStack inSlot = InventoryUtil.get(slot);
                    ((AutoArmor)this.module).queueClick(slot, inSlot, drag);
                    ((AutoArmor)this.module).queuedSlots.add(slot);
                    drag = inSlot;
                }
            } else {
                int slot;
                Map<EntityEquipmentSlot, Integer> map = ((AutoArmor)this.module).mode.getValue().setup(XCARRY.isEnabled(), ((AutoArmor)this.module).curse.getValue() == false, ((AutoArmor)this.module).prioLow.getValue(), ((AutoArmor)this.module).prioThreshold.getValue().floatValue());
                int last = -1;
                ItemStack drag = ListenerTick.mc.player.inventory.getItemStack();
                for (Map.Entry<EntityEquipmentSlot, Integer> entry : map.entrySet()) {
                    if (entry.getValue() != 8) continue;
                    slot = AutoArmor.fromEquipment(entry.getKey());
                    if (slot != -1 && slot != 45) {
                        ItemStack inSlot = InventoryUtil.get(slot);
                        ((AutoArmor)this.module).queueClick(slot, inSlot, drag);
                        drag = inSlot;
                        last = slot;
                    }
                    map.remove((Object)entry.getKey());
                    break;
                }
                for (Map.Entry<EntityEquipmentSlot, Integer> entry : map.entrySet()) {
                    slot = AutoArmor.fromEquipment(entry.getKey());
                    if (slot == -1 || slot == 45 || entry.getValue() == null) continue;
                    int i = entry.getValue();
                    ItemStack inSlot = InventoryUtil.get(i);
                    ((AutoArmor)this.module).queueClick(i, inSlot, drag).setDoubleClick(((AutoArmor)this.module).doubleClicks.getValue());
                    if (!drag.isEmpty()) {
                        ((AutoArmor)this.module).queuedSlots.add(i);
                    }
                    drag = inSlot;
                    inSlot = InventoryUtil.get(slot);
                    ((AutoArmor)this.module).queueClick(slot, inSlot, drag);
                    drag = inSlot;
                    last = slot;
                }
                if (((AutoArmor)this.module).putBack.getValue().booleanValue()) {
                    if (last != -1) {
                        ItemStack stack = InventoryUtil.get(last);
                        if (!stack.isEmpty()) {
                            ((AutoArmor)this.module).queuedSlots.add(-2);
                            int air = AutoArmor.findItem(Items.AIR, XCARRY.isEnabled(), ((AutoArmor)this.module).queuedSlots);
                            if (air != -1) {
                                ItemStack inSlot = InventoryUtil.get(air);
                                ((AutoArmor)this.module).putBackClick = ((AutoArmor)this.module).queueClick(air, inSlot, drag);
                                ((AutoArmor)this.module).putBackClick.addPost(() -> {
                                    ((AutoArmor)this.module).putBackClick = null;
                                });
                            }
                        }
                    } else if (((AutoArmor)this.module).putBackClick != null && ((AutoArmor)this.module).putBackClick.isValid()) {
                        ((AutoArmor)this.module).queueClick(((AutoArmor)this.module).putBackClick);
                    } else {
                        ((AutoArmor)this.module).putBackClick = null;
                    }
                }
            }
        }
        ((AutoArmor)this.module).runClick();
    }

    private boolean checkDesync() {
        if (((AutoArmor)this.module).noDesync.getValue().booleanValue() && InventoryUtil.validScreen() && ((AutoArmor)this.module).timer.passed(((AutoArmor)this.module).checkDelay.getValue().intValue()) && ((AutoArmor)this.module).desyncTimer.passed(((AutoArmor)this.module).desyncDelay.getValue().intValue()) && ((AutoArmor)this.module).propertyTimer.passed(((AutoArmor)this.module).propertyDelay.getValue().intValue())) {
            int bestSlot = -1;
            int clientValue = 0;
            boolean foundType = false;
            int armorValue = ListenerTick.mc.player.getTotalArmorValue();
            for (int i = 5; i < 9; ++i) {
                ItemStack stack = ListenerTick.mc.player.inventoryContainer.getSlot(i).getStack();
                if (stack.isEmpty() && !foundType) {
                    bestSlot = i;
                    if (((AutoArmor)this.module).lastType != AutoArmor.fromSlot(i)) continue;
                    foundType = true;
                    continue;
                }
                if (!(stack.getItem() instanceof ItemArmor)) continue;
                ItemArmor itemArmor = (ItemArmor)stack.getItem();
                clientValue += itemArmor.damageReduceAmount;
            }
            if (clientValue != armorValue && ((AutoArmor)this.module).timer.passed(((AutoArmor)this.module).delay.getValue().intValue())) {
                if (((AutoArmor)this.module).illegalSync.getValue().booleanValue()) {
                    ModuleUtil.sendMessage((Module)this.module, "\u00a7cDesync!");
                    InventoryUtil.illegalSync();
                } else if (bestSlot != -1 && AutoArmor.getSlot(ListenerTick.mc.player.inventory.getItemStack()) == AutoArmor.fromSlot(bestSlot)) {
                    ModuleUtil.sendMessage((Module)this.module, "\u00a7cDesync! (Code: " + bestSlot + ")");
                    Item i = InventoryUtil.get(bestSlot).getItem();
                    InventoryUtil.clickLocked(bestSlot, bestSlot, i, i);
                } else {
                    ModuleUtil.sendMessage((Module)this.module, "\u00a7cDesync!");
                    Item i = InventoryUtil.get(20).getItem();
                    InventoryUtil.clickLocked(20, 20, i, i);
                }
                ((AutoArmor)this.module).resetTimer();
                ((AutoArmor)this.module).desyncTimer.reset();
                return true;
            }
        }
        return false;
    }
}

