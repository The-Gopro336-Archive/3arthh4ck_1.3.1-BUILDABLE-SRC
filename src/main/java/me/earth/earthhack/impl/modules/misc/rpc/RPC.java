package me.earth.earthhack.impl.modules.misc.rpc;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.util.discord.DiscordPresence;

public class RPC
extends Module {
    public final Setting<String> state = this.register(new StringSetting("State", "3arfH4ck :3"));
    public final Setting<String> details = this.register(new StringSetting("Details", "3arfH4ck :3"));
    public final Setting<String> largeImageKey = this.register(new StringSetting("LargeImageKey", "earthhack"));
    public final Setting<String> smallImageKey = this.register(new StringSetting("SmallImageKey", "Da greatest"));
    public final Setting<Boolean> customDetails = this.register(new BooleanSetting("CustomDetails", false));
    public final Setting<Boolean> showIP = this.register(new BooleanSetting("ShowIP", false));
    public final Setting<Boolean> froggers = this.register(new BooleanSetting("Froggers", false));

    public RPC() {
        super("RPC", Category.Misc);
    }

    @Override
    protected void onEnable() {
        DiscordPresence.start();
    }

    @Override
    protected void onDisable() {
        DiscordPresence.stop();
    }
}
