package me.earth.earthhack.impl.modules.player.cleaner;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.combat.autoarmor.util.WindowClick;
import me.earth.earthhack.impl.modules.player.cleaner.ListenerMotion;
import me.earth.earthhack.impl.modules.player.cleaner.RemovingInteger;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.helpers.addable.ItemAddingModule;
import me.earth.earthhack.impl.util.helpers.addable.ListType;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.thread.Locks;

public class Cleaner
extends ItemAddingModule<Integer, RemovingInteger> {
    protected final Setting<Integer> delay = this.register(new NumberSetting<Integer>("Delay", 500, 0, 10000));
    protected final Setting<Boolean> rotate = this.register(new BooleanSetting("Rotate", false));
    protected final Setting<Boolean> prioHotbar = this.register(new BooleanSetting("PrioHotbar", true));
    protected final Setting<Boolean> stack = this.register(new BooleanSetting("Stack", false));
    protected final Setting<Boolean> inInventory = this.register(new BooleanSetting("InInventory", false));
    protected final Setting<Boolean> stackDrag = this.register(new BooleanSetting("StackDrag", true));
    protected final Setting<Boolean> smartStack = this.register(new BooleanSetting("SmartStack", false));
    protected final Setting<Boolean> xCarry = this.register(new BooleanSetting("XCarry", false));
    protected final Setting<Boolean> dragCarry = this.register(new BooleanSetting("DragXCarry", true));
    protected final Setting<Boolean> cleanInLoot = this.register(new BooleanSetting("CleanInLoot", true));
    protected final Setting<Boolean> cleanWithFull = this.register(new BooleanSetting("LootFullInvClean", true));
    protected final Setting<Boolean> sizeCheck = this.register(new BooleanSetting("SizeCheck", true));
    protected final Setting<Integer> minXcarry = this.register(new NumberSetting<Integer>("Min-XCarry", 5, 0, 36));
    protected final Setting<Integer> xCarryStacks = this.register(new NumberSetting<Integer>("XCarry-Stacks", 31, 0, 36));
    protected final Setting<Integer> globalDelay = this.register(new NumberSetting<Integer>("Global-Delay", 500, 0, 10000));
    protected final StopWatch timer = new StopWatch();
    protected WindowClick action;

    public Cleaner() {
        super("Cleaner", Category.Player, (String s) -> new RemovingInteger((String)s, 0, 0, 36), (Setting<?> s) -> "How many stacks of " + s.getName() + " to allow. When List-Type is Whitelist the value doesn't matter, all other items will be dropped.");
        this.listType.setValue(ListType.BlackList);
        this.listeners.add(new ListenerMotion(this));
        SimpleData data = new SimpleData(this, "Cleans up your Inventory.");
        data.register(this.delay, "Delay between 2 actions.");
        data.register(this.rotate, "Rotates away when throwing away an item.");
        data.register(this.prioHotbar, "Prioritizes the Hotbar.");
        data.register(this.stack, "Stacks Stacks.");
        data.register(this.inInventory, "Manages your Inventory while you are in it.");
        data.register(this.stackDrag, "Stacks the DragSlot.");
        data.register(this.smartStack, "");
        data.register(this.xCarry, "Puts Items in your XCarry");
        data.register(this.minXcarry, "Minimum amount of stacks you need to have of an item for it to be put in your XCarry");
        data.register(this.xCarryStacks, "Minimum amount of Stacks that you need to have in your Inventory for XCarry to be active.");
        this.setData(data);
    }

    public void runAction() {
        if (this.action != null) {
            Locks.acquire(Locks.WINDOW_CLICK_LOCK, () -> {
                if (this.action.isValid()) {
                    this.action.runClick(Cleaner.mc.playerController);
                }
            });
            this.timer.reset();
            this.action = null;
        }
    }

    public StopWatch getTimer() {
        return this.timer;
    }

    public int getDelay() {
        return this.delay.getValue();
    }
}
