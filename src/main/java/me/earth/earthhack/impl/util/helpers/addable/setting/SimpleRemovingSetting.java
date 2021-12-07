/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.util.helpers.addable.setting;

import me.earth.earthhack.api.setting.event.SettingEvent;
import me.earth.earthhack.impl.gui.chat.factory.ComponentFactory;
import me.earth.earthhack.impl.util.helpers.addable.setting.RemovingSetting;
import me.earth.earthhack.impl.util.helpers.addable.setting.component.SimpleRemovingComponent;

public class SimpleRemovingSetting
extends RemovingSetting<Boolean> {
    public SimpleRemovingSetting(String name) {
        super(name, true);
    }

    @Override
    public void setValue(Boolean value, boolean withEvent) {
        SettingEvent<Boolean> event = this.onChange(new SettingEvent<Boolean>(this, value));
        if (!event.isCancelled() && !value.booleanValue()) {
            super.remove();
        }
    }

    static {
        ComponentFactory.register(SimpleRemovingSetting.class, SimpleRemovingComponent::new);
    }
}

