package me.earth.earthhack.impl.modules.misc.autocraft;

public class AutoCraft$SlotEntry {
    private final int guiSlot;
    private final int inventorySlot;

    public AutoCraft$SlotEntry(int guiSlot, int inventorySlot) {
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
