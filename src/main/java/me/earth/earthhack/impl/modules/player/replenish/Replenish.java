package me.earth.earthhack.impl.modules.player.replenish;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.player.replenish.ListenerDeath;
import me.earth.earthhack.impl.modules.player.replenish.ListenerLogout;
import me.earth.earthhack.impl.modules.player.replenish.ListenerTick;
import me.earth.earthhack.impl.modules.player.replenish.ListenerWorldClient;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.helpers.addable.ListType;
import me.earth.earthhack.impl.util.helpers.addable.RemovingItemAddingModule;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.item.ItemStack;

public class Replenish
extends RemovingItemAddingModule {
    protected final Setting<Integer> threshold = this.register(new NumberSetting<Integer>("Threshold", 10, 0, 64));
    protected final Setting<Integer> minSize = this.register(new NumberSetting<Integer>("MinSize", 54, 0, 64));
    protected final Setting<Integer> delay = this.register(new NumberSetting<Integer>("Delay", 50, 0, 500));
    protected final Setting<Boolean> putBack = this.register(new BooleanSetting("PutBack", true));
    protected final Setting<Boolean> replenishInLoot = this.register(new BooleanSetting("ReplenishInLoot", true));
    protected final List<ItemStack> hotbar = new CopyOnWriteArrayList<ItemStack>();
    protected final StopWatch timer = new StopWatch();

    public Replenish() {
        super("Replenish", Category.Player, s -> "Black/Whitelists " + s.getName() + " from getting replenished.");
        this.listType.setValue(ListType.BlackList);
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerLogout(this));
        this.listeners.add(new ListenerDeath(this));
        this.listeners.add(new ListenerWorldClient(this));
        SimpleData data = new SimpleData(this, "Makes you never run out of items in your hotbar.");
        data.register(this.threshold, "If a stack in your hotbar reaches this threshold it will be replenished.");
        data.register(this.delay, "Delay between each time items are moved around in your Inventory. Low delays might cause Inventory desync.");
        data.register(this.putBack, "If this setting isn't enabled some items might end up in your dragslot.");
        this.setData(data);
        this.clear();
    }

    @Override
    protected void onEnable() {
        this.clear();
    }

    public void clear() {
        if (this.hotbar.isEmpty()) {
            for (int i = 0; i < 9; ++i) {
                this.hotbar.add(ItemStack.EMPTY);
            }
        } else {
            for (int i = 0; i < 9; ++i) {
                this.hotbar.set(i, ItemStack.EMPTY);
            }
        }
    }
}
