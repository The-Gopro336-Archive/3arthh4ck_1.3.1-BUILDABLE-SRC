/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.gui.chat.util;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.settings.ColorSetting;

public interface IColorIncrementor {
    public Runnable getCommand(ColorSetting var1, boolean var2, Module var3);
}

