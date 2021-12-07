package me.earth.earthhack.impl.modules.misc.autocraft;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.autocraft.AutoCraft;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;

final class ListenerTick
extends ModuleListener<AutoCraft, TickEvent> {
    public ListenerTick(AutoCraft module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        if (((AutoCraft)this.module).currentTask != null) {
            System.out.println("current task:" + ((AutoCraft)this.module).currentTask.getRecipe().getRecipeOutput().getItem().getRegistryName());
        }
        if (((AutoCraft)this.module).lastTask != null) {
            System.out.println("last task:" + ((AutoCraft)this.module).lastTask.getRecipe().getRecipeOutput().getItem().getRegistryName());
        }
        if (((AutoCraft)this.module).delayTimer.passed(((AutoCraft)this.module).delay.getValue().intValue()) && ((AutoCraft)this.module).currentTask == null) {
            ((AutoCraft)this.module).currentTask = ((AutoCraft)this.module).dequeue();
        }
        if (((AutoCraft)this.module).currentTask != null && ((AutoCraft)this.module).currentTask.isInTable() && !(ListenerTick.mc.currentScreen instanceof GuiCrafting)) {
            if (((AutoCraft)this.module).getCraftingTable() == null && InventoryUtil.findBlock(Blocks.CRAFTING_TABLE, false) == -1 && ((AutoCraft)this.module).craftTable.getValue().booleanValue()) {
                ((AutoCraft)this.module).lastTask = ((AutoCraft)this.module).currentTask;
                ((AutoCraft)this.module).currentTask = new AutoCraft.CraftTask("crafting_table", 1);
            }
            ((AutoCraft)this.module).shouldTable = true;
            return;
        }
        if (((AutoCraft)this.module).lastTask != null && InventoryUtil.findBlock(Blocks.CRAFTING_TABLE, false) != -1) {
            ((AutoCraft)this.module).currentTask = ((AutoCraft)this.module).lastTask;
            ((AutoCraft)this.module).lastTask = null;
        }
        if (((AutoCraft)this.module).clickDelay.getValue() != 0 && ((AutoCraft)this.module).clickDelayTimer.passed(((AutoCraft)this.module).clickDelay.getValue().intValue()) && ((AutoCraft)this.module).currentTask != null && (!((AutoCraft)this.module).currentTask.isInTable() || ListenerTick.mc.currentScreen instanceof GuiCrafting)) {
            ((AutoCraft)this.module).currentTask.updateSlots();
            int windowId = 0;
            if (((AutoCraft)this.module).currentTask.isInTable()) {
                assert (ListenerTick.mc.currentScreen != null);
                windowId = ((GuiContainer)ListenerTick.mc.currentScreen).inventorySlots.windowId;
            }
            if (((AutoCraft)this.module).currentTask.step < ((AutoCraft)this.module).currentTask.getSlotToSlotMap().size()) {
                AutoCraft.SlotEntry entry = ((AutoCraft)this.module).currentTask.getSlotToSlotMap().get(((AutoCraft)this.module).currentTask.getStep());
                System.out.println("inventory slot:" + entry.getInventorySlot());
                System.out.println("gui slot:" + entry.getGuiSlot());
                ListenerTick.mc.playerController.windowClick(windowId, entry.getInventorySlot(), 0, ClickType.PICKUP, ListenerTick.mc.player);
                for (int i = 0; i < ((AutoCraft)this.module).currentTask.runs; ++i) {
                    ListenerTick.mc.playerController.windowClick(windowId, entry.getGuiSlot(), 1, ClickType.PICKUP, ListenerTick.mc.player);
                }
                ListenerTick.mc.playerController.windowClick(windowId, entry.getInventorySlot(), 0, ClickType.PICKUP, ListenerTick.mc.player);
                ++((AutoCraft)this.module).currentTask.step;
            } else if (((AutoCraft)this.module).currentTask.step == ((AutoCraft)this.module).currentTask.getSlotToSlotMap().size()) {
                ListenerTick.mc.playerController.windowClick(windowId, 0, 0, ClickType.QUICK_MOVE, ListenerTick.mc.player);
                ((AutoCraft)this.module).currentTask = null;
                ((AutoCraft)this.module).delayTimer.reset();
                if (ListenerTick.mc.currentScreen instanceof GuiCrafting) {
                    mc.displayGuiScreen(null);
                }
            }
        }
    }
}
