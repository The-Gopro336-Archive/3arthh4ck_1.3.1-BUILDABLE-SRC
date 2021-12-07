package me.earth.earthhack.impl.modules.misc.autoeat;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.misc.autoeat.ListenerTick;
import me.earth.earthhack.impl.util.minecraft.KeyBoardUtil;
import net.minecraft.client.settings.KeyBinding;

public class AutoEat
extends Module {
    protected final Setting<Float> hunger = this.register(new NumberSetting<Float>("Hunger", Float.valueOf(19.0f), Float.valueOf(0.1f), Float.valueOf(19.0f)));
    protected final Setting<Boolean> health = this.register(new BooleanSetting("Health", false));
    protected final Setting<Float> enemyRange = this.register(new NumberSetting<Float>("Enemy-Range", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(24.0f)));
    protected final Setting<Float> safeHealth = this.register(new NumberSetting<Float>("Safe-Health", Float.valueOf(19.0f), Float.valueOf(0.1f), Float.valueOf(36.0f)));
    protected final Setting<Float> unsafeHealth = this.register(new NumberSetting<Float>("Unsafe-Health", Float.valueOf(19.0f), Float.valueOf(0.1f), Float.valueOf(36.0f)));
    protected final Setting<Boolean> calcWithAbsorption = this.register(new BooleanSetting("CalcWithAbsorption", true));
    protected final Setting<Boolean> absorption = this.register(new BooleanSetting("Absorption", false));
    protected final Setting<Float> absorptionAmount = this.register(new NumberSetting<Float>("AbsorptionAmount", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(16.0f)));
    protected final Setting<Boolean> always = this.register(new BooleanSetting("Always", false));
    protected boolean isEating;
    protected boolean server;
    protected boolean force;
    protected int lastSlot;

    public AutoEat() {
        super("AutoEat", Category.Misc);
        this.listeners.add(new ListenerTick(this));
    }

    @Override
    protected void onEnable() {
        this.force = false;
        this.server = false;
        this.lastSlot = -1;
        this.isEating = false;
    }

    @Override
    protected void onDisable() {
        this.reset();
    }

    public void reset() {
        this.force = false;
        this.server = false;
        this.lastSlot = -1;
        this.isEating = false;
        KeyBinding.setKeyBindState((int)AutoEat.mc.gameSettings.keyBindUseItem.getKeyCode(), (boolean)KeyBoardUtil.isKeyDown(AutoEat.mc.gameSettings.keyBindUseItem));
    }

    public boolean isEating() {
        return this.isEnabled() && this.isEating;
    }

    public void setServer(boolean server) {
        this.server = server;
    }
}
