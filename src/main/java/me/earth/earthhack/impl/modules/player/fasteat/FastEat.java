package me.earth.earthhack.impl.modules.player.fasteat;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.player.fasteat.ListenerDigging;
import me.earth.earthhack.impl.modules.player.fasteat.ListenerTryUseItem;
import me.earth.earthhack.impl.modules.player.fasteat.ListenerUpdate;
import me.earth.earthhack.impl.modules.player.fasteat.mode.FastEatMode;
import me.earth.earthhack.impl.util.client.SimpleData;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;

public class FastEat
extends Module {
    protected final Setting<FastEatMode> mode = this.register(new EnumSetting<FastEatMode>("Mode", FastEatMode.Packet));
    protected final Setting<Float> speed = this.register(new NumberSetting<Float>("Speed", Float.valueOf(15.0f), Float.valueOf(1.0f), Float.valueOf(25.0f)));
    protected final Setting<Boolean> cancel = this.register(new BooleanSetting("Cancel-Digging", false));

    public FastEat() {
        super("FastEat", Category.Player);
        this.listeners.add(new ListenerUpdate(this));
        this.listeners.add(new ListenerTryUseItem(this));
        this.listeners.add(new ListenerDigging(this));
        SimpleData data = new SimpleData(this, "Exploits that make you fat.");
        data.register(this.mode, "Different Modes. NoDelay won't lagback, the others might.");
        data.register(this.speed, "Speed for mode Packet.");
        data.register(this.cancel, "Makes it so that you just need to click once and the item will be eaten.");
        this.setData(data);
    }

    @Override
    public String getDisplayInfo() {
        return this.mode.getValue().name();
    }

    public FastEatMode getMode() {
        return this.mode.getValue();
    }

    protected boolean isValid(ItemStack stack) {
        return stack != null && FastEat.mc.player.isHandActive() && (stack.getItem() instanceof ItemFood || stack.getItem() instanceof ItemPotion || stack.getItem() instanceof ItemBucketMilk);
    }
}
