package me.earth.earthhack.impl.modules.misc.autofish;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.core.ducks.IMinecraft;
import me.earth.earthhack.impl.modules.misc.autofish.ListenerSound;
import me.earth.earthhack.impl.modules.misc.autofish.ListenerTick;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.item.ItemFishingRod;

public class AutoFish
extends Module {
    protected final Setting<Boolean> openInv = this.register(new BooleanSetting("OpenInventory", true));
    protected final Setting<Float> delay = this.register(new NumberSetting<Float>("Delay", Float.valueOf(15.0f), Float.valueOf(10.0f), Float.valueOf(25.0f)));
    protected final Setting<Double> range = this.register(new NumberSetting<Double>("SoundRange", 2.0, 0.1, 5.0));
    protected boolean splash;
    protected int delayCounter;
    protected int splashTicks;
    protected int timeout;

    public AutoFish() {
        super("AutoFish", Category.Misc);
        this.listeners.add(new ListenerSound(this));
        this.listeners.add(new ListenerTick(this));
    }

    @Override
    protected void onEnable() {
        this.splash = false;
        this.splashTicks = 0;
        this.delayCounter = 0;
        this.timeout = 0;
    }

    protected void click() {
        if ((!AutoFish.mc.player.inventory.getCurrentItem().isEmpty() || AutoFish.mc.player.inventory.getCurrentItem().getItem() instanceof ItemFishingRod) && (this.openInv.getValue().booleanValue() || AutoFish.mc.currentScreen instanceof GuiChat || AutoFish.mc.currentScreen == null)) {
            ((IMinecraft)((Object)mc)).click(IMinecraft.Click.RIGHT);
            this.delayCounter = this.delay.getValue().intValue();
            this.timeout = 0;
        }
    }
}
