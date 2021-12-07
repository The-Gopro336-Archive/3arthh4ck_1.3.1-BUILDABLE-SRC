package me.earth.earthhack.impl.modules.misc.extratab;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.media.Media;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;

public class ExtraTab
extends Module {
    private static final ModuleCache<Media> MEDIA = Caches.getModule(Media.class);
    protected final Setting<Integer> size = this.register(new NumberSetting<Integer>("Size", 250, 0, 500));

    public ExtraTab() {
        super("ExtraTab", Category.Misc);
        this.register(new BooleanSetting("Download-Threads", false));
    }

    public int getSize(int defaultSize) {
        return this.isEnabled() ? this.size.getValue() : defaultSize;
    }

    public String getName(NetworkPlayerInfo info) {
        String name;
        String finalName = name = info.getDisplayName() != null ? info.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName((Team)info.getPlayerTeam(), (String)info.getGameProfile().getName());
        name = MEDIA.returnIfPresent(m -> m.convert(finalName), name);
        if (this.isEnabled()) {
            if (Managers.FRIENDS.contains(finalName)) {
                return "\u00a7b" + name;
            }
            if (Managers.ENEMIES.contains(finalName)) {
                return "\u00a7c" + name;
            }
        }
        return name;
    }
}
