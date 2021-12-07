/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.client.gui.inventory.GuiCrafting
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.crafting.CraftingManager
 *  net.minecraft.item.crafting.IRecipe
 *  net.minecraft.item.crafting.Ingredient
 *  net.minecraft.item.crafting.ShapedRecipes
 *  net.minecraft.item.crafting.ShapelessRecipes
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 */
package me.earth.earthhack.impl.modules.misc.autocraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.misc.autocraft.ListenerMotion;
import me.earth.earthhack.impl.modules.misc.autocraft.ListenerTick;
import me.earth.earthhack.impl.util.helpers.blocks.BlockPlacingModule;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.init.Blocks;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class AutoCraft
extends BlockPlacingModule {
    protected final Setting<Integer> delay = this.register(new NumberSetting<Integer>("Delay", 50, 0, 500));
    protected final Setting<Integer> clickDelay = this.register(new NumberSetting<Integer>("ClickDelay", 50, 0, 500));
    protected final Setting<Boolean> placeTable = this.register(new BooleanSetting("PlaceTable", false));
    protected final Setting<Boolean> craftTable = this.register(new BooleanSetting("CraftTable", false));
    protected final Setting<Boolean> moveTable = this.register(new BooleanSetting("MoveTable", false));
    protected final Setting<Float> tableRange = this.register(new NumberSetting<Float>("TableRange", Float.valueOf(6.0f), Float.valueOf(1.0f), Float.valueOf(8.0f)));
    private final Queue<CraftTask> taskQueue = new ConcurrentLinkedDeque<CraftTask>();
    protected CraftTask currentTask;
    protected CraftTask lastTask;
    protected final StopWatch delayTimer = new StopWatch();
    protected final StopWatch clickDelayTimer = new StopWatch();
    protected boolean shouldTable = false;

    public AutoCraft() {
        super("AutoCraft", Category.Misc);
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerMotion(this));
    }

    @Override
    protected void onEnable() {
        this.delayTimer.reset();
        this.clickDelayTimer.reset();
        this.submit(new CraftTask("furnace", 1));
    }

    public BlockPos getCraftingTable() {
        AtomicReference craftingTable = new AtomicReference();
        BlockUtil.sphere(this.tableRange.getValue().floatValue(), pos -> {
            if (AutoCraft.mc.world.getBlockState(pos).getBlock() == Blocks.CRAFTING_TABLE) {
                craftingTable.set(pos);
            }
            return false;
        });
        return (BlockPos)craftingTable.get();
    }

    public BlockPos getCraftingTableBlock() {
        HashSet positions = new HashSet();
        BlockUtil.sphere(this.tableRange.getValue().floatValue(), pos -> {
            if (AutoCraft.mc.world.getBlockState(pos).getBlock().isReplaceable((IBlockAccess)AutoCraft.mc.world, pos) && this.entityCheck((BlockPos)pos)) {
                positions.add(pos);
            }
            return false;
        });
        return (BlockPos)positions.stream().sorted(Comparator.comparingInt(pos -> this.safety((BlockPos)pos) * -1)).collect(Collectors.toList()).get(0);
    }

    public void submit(CraftTask task) {
        this.taskQueue.add(task);
    }

    public CraftTask dequeue() {
        return this.taskQueue.poll();
    }

    private int safetyFactor(BlockPos pos) {
        return this.safety(pos) + this.safety(pos.up());
    }

    private int safety(BlockPos pos) {
        int safety = 0;
        for (EnumFacing facing : EnumFacing.values()) {
            if (AutoCraft.mc.world.getBlockState(pos.offset(facing)).getMaterial().isReplaceable()) continue;
            ++safety;
        }
        return safety;
    }

    public static class CraftTask {
        private final IRecipe recipe;
        private List<SlotEntry> entryList;
        private final boolean inTable;
        protected int runs;
        protected int step = 0;

        public CraftTask(String recipeName, int runs) {
            this(Objects.requireNonNull(CraftingManager.getRecipe((ResourceLocation)new ResourceLocation(recipeName))), runs);
        }

        public CraftTask(IRecipe recipe, int runs) {
            this.recipe = recipe;
            this.entryList = new ArrayList<SlotEntry>();
            this.inTable = !recipe.canFit(2, 2);
            this.runs = runs;
            int i = 1;
            for (Ingredient stack : recipe.getIngredients()) {
                if (recipe instanceof ShapedRecipes) {
                    if (stack != Ingredient.EMPTY) {
                        int inventorySlot = InventoryUtil.findInInventory(itemStack -> Arrays.stream(stack.getMatchingStacks()).anyMatch(itemStack1 -> itemStack1.getItem() == itemStack.getItem()), false);
                        this.entryList.add(new SlotEntry(i, inventorySlot));
                    }
                    ++i;
                    continue;
                }
                if (!(recipe instanceof ShapelessRecipes)) continue;
            }
        }

        public void updateSlots() {
            int i = 1;
            ArrayList<SlotEntry> entries = new ArrayList<SlotEntry>();
            for (Ingredient stack : this.recipe.getIngredients()) {
                if (this.recipe instanceof ShapedRecipes) {
                    int inventorySlot;
                    if (stack != Ingredient.EMPTY && (inventorySlot = Globals.mc.currentScreen instanceof GuiCrafting ? InventoryUtil.findInCraftingTable(((GuiContainer)Globals.mc.currentScreen).inventorySlots, itemStack -> Arrays.stream(stack.getMatchingStacks()).anyMatch(itemStack1 -> itemStack1.getItem() == itemStack.getItem())) : InventoryUtil.findInInventory(itemStack -> Arrays.stream(stack.getMatchingStacks()).anyMatch(itemStack1 -> itemStack1.getItem() == itemStack.getItem()), false)) != -1) {
                        entries.add(new SlotEntry(i, inventorySlot));
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

        public List<SlotEntry> getSlotToSlotMap() {
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

    public static class SlotEntry {
        private final int guiSlot;
        private final int inventorySlot;

        public SlotEntry(int guiSlot, int inventorySlot) {
            this.guiSlot = guiSlot;
            this.inventorySlot = inventorySlot;
        }

        public int getGuiSlot() {
            return this.guiSlot;
        }

        public int getInventorySlot() {
            return this.inventorySlot;
        }
    }
}

