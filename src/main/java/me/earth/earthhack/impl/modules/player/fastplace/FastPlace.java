package me.earth.earthhack.impl.modules.player.fastplace;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.core.ducks.IMinecraft;
import me.earth.earthhack.impl.modules.player.fastplace.ListenerTick;
import me.earth.earthhack.impl.modules.player.fastplace.ListenerUseItem;
import me.earth.earthhack.impl.modules.player.fastplace.ListenerUseOnBlock;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.helpers.addable.RemovingItemAddingModule;

public class FastPlace
extends RemovingItemAddingModule {
    protected final Setting<Boolean> all = this.register(new BooleanSetting("All", true));
    protected final Setting<Boolean> bypass = this.register(new BooleanSetting("Bypass", false));
    protected final Setting<Boolean> foodBypass = this.register(new BooleanSetting("Food-Bypass", false));
    protected final Setting<Boolean> bypassContainers = this.register(new BooleanSetting("Bypass-Containers", false));
    protected final Setting<Integer> delay = this.register(new NumberSetting<Integer>("Delay", 0, 0, 4));
    protected final Setting<Boolean> doubleEat = this.register(new BooleanSetting("Double-Eat", false));

    public FastPlace() {
        super("FastPlace", Category.Player, s -> "Makes you place " + s.getName() + " faster.");
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerUseOnBlock(this));
        this.listeners.add(new ListenerUseItem(this));
        SimpleData data = new SimpleData(this, "Removes the place delay when holding down right click.");
        data.register(this.all, "Ignores the White/Blacklisted items and removes the delay for all items.");
        data.register(this.delay, "The delay in ticks between using an item.");
        this.setData(data);
    }

    protected void onTick() {
        if (FastPlace.mc.gameSettings.keyBindUseItem.isKeyDown() && this.delay.getValue() < ((IMinecraft)((Object)mc)).getRightClickDelay() && (this.all.getValue().booleanValue() || this.isStackValid(FastPlace.mc.player.getHeldItemMainhand()) || this.isStackValid(FastPlace.mc.player.getHeldItemOffhand()))) {
            ((IMinecraft)((Object)mc)).setRightClickDelay(this.delay.getValue());
        }
    }
}
