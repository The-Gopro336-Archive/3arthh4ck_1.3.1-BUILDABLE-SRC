/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.text.ITextComponent
 */
package me.earth.earthhack.impl.gui.chat.components.setting;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.gui.chat.components.SettingComponent;
import me.earth.earthhack.impl.gui.chat.components.values.ValueComponent;
import net.minecraft.util.text.ITextComponent;

public class DefaultComponent<T, S extends Setting<T>>
extends SettingComponent<T, S> {
    public DefaultComponent(S setting) {
        super(setting);
        ValueComponent value = new ValueComponent((Setting<?>)setting);
        value.getStyle().setClickEvent(this.getStyle().getClickEvent());
        value.getStyle().setHoverEvent(this.getStyle().getHoverEvent());
        this.appendSibling((ITextComponent)value);
    }
}

