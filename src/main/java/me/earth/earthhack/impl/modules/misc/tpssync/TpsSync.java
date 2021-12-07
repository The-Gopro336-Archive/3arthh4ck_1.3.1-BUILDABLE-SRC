package me.earth.earthhack.impl.modules.misc.tpssync;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.modules.misc.tpssync.TpsSyncData;

public class TpsSync
extends Module {
    public TpsSync() {
        super("TpsSync", Category.Player);
        this.register(new BooleanSetting("Attack", false));
        this.register(new BooleanSetting("Mine", false));
        this.setData(new TpsSyncData(this));
    }
}
