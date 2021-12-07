/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.gui.chat.components.setting;

import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.gui.chat.components.setting.DefaultComponent;

public class StringComponent
extends DefaultComponent<String, StringSetting> {
    public StringComponent(StringSetting setting) {
        super(setting);
    }

    @Override
    public String getText() {
        return ((StringSetting)this.setting).getName() + "\u00a77" + " : " + "\u00a76";
    }
}

