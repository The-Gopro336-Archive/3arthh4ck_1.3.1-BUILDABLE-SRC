/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.gui.chat.components.values;

import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.util.EnumHelper;
import me.earth.earthhack.impl.gui.chat.components.SettingComponent;

public class EnumHoverComponent<E extends Enum<E>>
extends SettingComponent<E, EnumSetting<E>> {
    public EnumHoverComponent(EnumSetting<E> setting) {
        super(setting);
    }

    @Override
    public String getText() {
        return "\u00a7b" + ((Enum)((EnumSetting)this.setting).getValue()).name() + "\u00a77" + " -> " + "\u00a7f" + EnumHelper.next((Enum)((EnumSetting)this.setting).getValue()).name();
    }
}

