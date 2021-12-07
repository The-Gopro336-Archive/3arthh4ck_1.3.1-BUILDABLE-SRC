package me.earth.earthhack.impl.modules.render.logoutspots;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.render.logoutspots.ListenerDisconnect;
import me.earth.earthhack.impl.modules.render.logoutspots.ListenerJoin;
import me.earth.earthhack.impl.modules.render.logoutspots.ListenerLeave;
import me.earth.earthhack.impl.modules.render.logoutspots.ListenerRender;
import me.earth.earthhack.impl.modules.render.logoutspots.mode.MessageMode;
import me.earth.earthhack.impl.modules.render.logoutspots.util.LogoutSpot;

public class LogoutSpots
extends Module {
    protected final Setting<MessageMode> message = this.register(new EnumSetting<MessageMode>("Message", MessageMode.Render));
    protected final Setting<Boolean> render = this.register(new BooleanSetting("Render", true));
    protected final Setting<Boolean> friends = this.register(new BooleanSetting("Friends", true));
    protected final Setting<Float> scale = this.register(new NumberSetting<Float>("Scale", Float.valueOf(0.003f), Float.valueOf(0.001f), Float.valueOf(0.01f)));
    protected final Map<UUID, LogoutSpot> spots = new ConcurrentHashMap<UUID, LogoutSpot>();

    public LogoutSpots() {
        super("LogoutSpots", Category.Render);
        this.listeners.add(new ListenerDisconnect(this));
        this.listeners.add(new ListenerJoin(this));
        this.listeners.add(new ListenerLeave(this));
        this.listeners.add(new ListenerRender(this));
    }

    @Override
    protected void onDisable() {
        this.spots.clear();
    }
}
