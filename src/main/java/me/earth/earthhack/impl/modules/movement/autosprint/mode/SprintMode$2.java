package me.earth.earthhack.impl.modules.movement.autosprint.mode;

import me.earth.earthhack.impl.modules.movement.autosprint.mode.SprintMode;
import net.minecraft.client.settings.KeyBinding;

final class SprintMode$2
extends SprintMode {
    @Override
    public void sprint() {
        KeyBinding.setKeyBindState((int)SprintMode$2.mc.gameSettings.keyBindSprint.getKeyCode(), (boolean)true);
    }
}
