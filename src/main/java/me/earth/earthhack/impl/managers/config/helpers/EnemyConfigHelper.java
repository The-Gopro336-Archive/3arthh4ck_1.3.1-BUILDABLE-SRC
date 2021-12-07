package me.earth.earthhack.impl.managers.config.helpers;

import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.config.helpers.PlayerManagerConfigHelper;

public class EnemyConfigHelper
extends PlayerManagerConfigHelper {
    public EnemyConfigHelper() {
        super("enemy", "enemies", Managers.ENEMIES);
    }
}
