package me.earth.earthhack.impl.modules.client.safety;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.safety.SafetyData;
import me.earth.earthhack.impl.modules.client.safety.util.Update;
import me.earth.earthhack.impl.util.minecraft.ICachedDamage;

public class Safety
extends Module {
    public Safety() {
        super("Safety", Category.Client);
        this.register(new NumberSetting<Float>("MaxDamage", Float.valueOf(4.0f), Float.valueOf(0.0f), Float.valueOf(36.0f)));
        this.register(new BooleanSetting("BedCheck", false));
        this.register(new BooleanSetting("1.13+", false));
        this.register(new BooleanSetting("1.13-Entities", false));
        this.register(new EnumSetting<Update>("Updates", Update.Tick));
        this.register(new NumberSetting<Integer>("Delay", 25, 0, 100));
        this.register(new BooleanSetting("2x1s", true));
        this.register(new BooleanSetting("2x2s", true));
        this.register(new BooleanSetting("Post-Calc", false));
        this.register(new BooleanSetting("Terrain", false));
        this.register(new BooleanSetting("Anvils", false));
        this.register(ICachedDamage.SHOULD_CACHE);
        this.setData(new SafetyData(this));
    }

    @Override
    public String getDisplayInfo() {
        return Managers.SAFETY.isSafe() ? "\u00a7aSafe" : "\u00a7cUnsafe";
    }
}
