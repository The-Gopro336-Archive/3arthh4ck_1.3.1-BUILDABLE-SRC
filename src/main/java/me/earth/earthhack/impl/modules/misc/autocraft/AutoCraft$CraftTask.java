package me.earth.earthhack.impl.modules.misc.autocraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.misc.autocraft.AutoCraft;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.ResourceLocation;

public class AutoCraft$CraftTask {
    private final IRecipe recipe;
    private List<AutoCraft.SlotEntry> entryList;
    private final boolean inTable;
    protected int runs;
    protected int step = 0;

    public AutoCraft$CraftTask(String recipeName, int runs) {
        this(Objects.requireNonNull(CraftingManager.getRecipe((ResourceLocation)new ResourceLocation(recipeName))), runs);
    }

    public AutoCraft$CraftTask(IRecipe recipe, int runs) {
        this.recipe = recipe;
        this.entryList = new ArrayList<AutoCraft.SlotEntry>();
        this.inTable = !recipe.canFit(2, 2);
        this.runs = runs;
        int i = 1;
        for (Ingredient stack : recipe.getIngredients()) {
            if (recipe instanceof ShapedRecipes) {
                if (stack != Ingredient.EMPTY) {
                    int inventorySlot = InventoryUtil.findInInventory(itemStack -> Arrays.stream(stack.getMatchingStacks()).anyMatch(itemStack1 -> itemStack1.getItem() == itemStack.getItem()), false);
                    this.entryList.add(new AutoCraft.SlotEntry(i, inventorySlot));
                }
                ++i;
                continue;
            }
            if (!(recipe instanceof ShapelessRecipes)) continue;
        }
    }

    public void updateSlots() {
        int i = 1;
        ArrayList<AutoCraft.SlotEntry> entries = new ArrayList<AutoCraft.SlotEntry>();
        for (Ingredient stack : this.recipe.getIngredients()) {
            if (this.recipe instanceof ShapedRecipes) {
                int inventorySlot;
                if (stack != Ingredient.EMPTY && (inventorySlot = Globals.mc.currentScreen instanceof GuiCrafting ? InventoryUtil.findInCraftingTable(((GuiContainer)Globals.mc.currentScreen).inventorySlots, itemStack -> Arrays.stream(stack.getMatchingStacks()).anyMatch(itemStack1 -> itemStack1.getItem() == itemStack.getItem())) : InventoryUtil.findInInventory(itemStack -> Arrays.stream(stack.getMatchingStacks()).anyMatch(itemStack1 -> itemStack1.getItem() == itemStack.getItem()), false)) != -1) {
                    entries.add(new AutoCraft.SlotEntry(i, inventorySlot));
                }
                ++i;
                continue;
            }
            if (!(this.recipe instanceof ShapelessRecipes)) continue;
        }
        this.entryList = entries;
    }

    public IRecipe getRecipe() {
        return this.recipe;
    }

    public List<AutoCraft.SlotEntry> getSlotToSlotMap() {
        return this.entryList;
    }

    public boolean isInTable() {
        return this.inTable;
    }

    public int getStep() {
        return this.step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getRuns() {
        return this.runs;
    }

    public void setRuns(int runs) {
        this.runs = runs;
    }
}
