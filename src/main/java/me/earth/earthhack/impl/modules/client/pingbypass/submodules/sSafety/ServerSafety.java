package me.earth.earthhack.impl.modules.client.pingbypass.submodules.sSafety;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.gui.module.impl.SimpleSubModule;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypass;
import me.earth.earthhack.impl.modules.client.pingbypass.submodules.sSafety.ServerSafetyData;
import me.earth.earthhack.impl.modules.client.safety.util.Update;

public class ServerSafety
extends SimpleSubModule<PingBypass> {
    public ServerSafety(PingBypass pingBypass) {
        super(pingBypass, "S-Safety", Category.Client);
        this.register(new NumberSetting<Float>("MaxDamage", Float.valueOf(4.0f), Float.valueOf(0.0f), Float.valueOf(36.0f)));
        this.register(new BooleanSetting("BedCheck", false));
        this.register(new BooleanSetting("1.13+", false));
        this.register(new BooleanSetting("SafetyPlayer", false));
        this.register(new EnumSetting<Update>("Updates", Update.Tick));
        this.register(new NumberSetting<Integer>("Delay", 25, 0, 100));
        this.setData(new ServerSafetyData(this));
    }
}
