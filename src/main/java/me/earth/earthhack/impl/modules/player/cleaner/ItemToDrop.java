package me.earth.earthhack.impl.modules.player.cleaner;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.modules.player.cleaner.SlotCount;

public class ItemToDrop
extends SlotCount {
    private final Setting<Integer> setting;
    private int stacks;

    public ItemToDrop(Setting<Integer> setting) {
        super(Integer.MAX_VALUE, 0);
        this.setting = setting;
    }

    public void addSlot(int slot, int count) {
        ++this.stacks;
        if (count < this.getCount()) {
            this.setSlot(slot);
            this.setCount(count);
        }
    }

    public boolean shouldDrop() {
        return this.setting == null || this.setting.getValue() < this.stacks;
    }
}
