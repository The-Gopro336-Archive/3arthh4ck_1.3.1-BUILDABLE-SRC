package me.earth.earthhack.impl.modules.player.ncptweaks;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.player.ncptweaks.ListenerInput;
import me.earth.earthhack.impl.modules.player.ncptweaks.ListenerWindowClick;

public class NCPTweaks
extends Module {
    protected final Setting<Boolean> eating = this.register(new BooleanSetting("Eating", true));
    protected final Setting<Boolean> moving = this.register(new BooleanSetting("Moving", true));
    protected final Setting<Boolean> packet = this.register(new BooleanSetting("Packet", true));
    protected final Setting<Integer> delay = this.register(new NumberSetting<Integer>("Delay", 50, 0, 500));
    protected final Setting<Boolean> resetNCP = this.register(new BooleanSetting("Reset-NCP", false));
    protected final Setting<Boolean> sneakEat = this.register(new BooleanSetting("Sneak-Eat", false));
    protected final Setting<Boolean> stopSpeed = this.register(new BooleanSetting("Stop-Speed", false));
    protected boolean speedStopped;

    public NCPTweaks() {
        super("NCPTweaks", Category.Player);
        this.listeners.add(new ListenerWindowClick(this));
        this.listeners.add(new ListenerInput(this));
    }

    @Override
    protected void onDisable() {
        this.speedStopped = false;
    }

    public boolean isSpeedStopped() {
        return this.stopSpeed.getValue() != false && this.speedStopped;
    }
}
