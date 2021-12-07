/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  org.lwjgl.input.Keyboard
 */
package me.earth.earthhack.api.setting.settings;

import com.google.gson.JsonElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.event.SettingResult;
import me.earth.earthhack.api.util.bind.Bind;
import org.lwjgl.input.Keyboard;

public class BindSetting
extends Setting<Bind> {
    public BindSetting(String name) {
        this(name, Bind.none());
    }

    public BindSetting(String name, Bind initialValue) {
        super(name, initialValue);
    }

    @Override
    public void fromJson(JsonElement element) {
        this.fromString(element.getAsString());
    }

    @Override
    public SettingResult fromString(String string) {
        if ("none".equalsIgnoreCase(string)) {
            this.value = Bind.none();
        } else {
            this.setValue(Bind.fromString(string));
        }
        return SettingResult.SUCCESSFUL;
    }

    @Override
    public String getInputs(String string) {
        if (string == null || string.isEmpty()) {
            return "<key>";
        }
        if ("none".startsWith(string.toLowerCase())) {
            return "NONE";
        }
        for (int i = 0; i < 256; ++i) {
            String keyName = Keyboard.getKeyName((int)i);
            if (keyName == null || !keyName.toLowerCase().startsWith(string.toLowerCase())) continue;
            return keyName;
        }
        return "";
    }

    public void setKey(int key) {
        this.value = Bind.fromKey(key);
    }
}

