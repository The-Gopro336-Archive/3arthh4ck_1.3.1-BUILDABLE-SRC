/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 */
package me.earth.earthhack.api.setting.settings;

import com.google.gson.JsonElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.event.SettingResult;
import me.earth.earthhack.api.util.EnumHelper;

public class EnumSetting<E extends Enum<E>>
extends Setting<E> {
    private final String concatenated = this.concatenateInputs();

    public EnumSetting(String nameIn, E initialValue) {
        super(nameIn, initialValue);
    }

    @Override
    public void fromJson(JsonElement element) {
        this.fromString(element.getAsString());
    }

    @Override
    public SettingResult fromString(String string) {
        Enum<?> entry = EnumHelper.fromString((Enum)this.value, string);
        this.setValue(entry);
        return SettingResult.SUCCESSFUL;
    }

    @Override
    public String getInputs(String string) {
        if (string == null || string.isEmpty()) {
            return this.concatenated;
        }
        Enum<?> entry = EnumHelper.getEnumStartingWith(string, ((Enum)this.initial).getDeclaringClass());
        return entry == null ? "" : entry.toString();
    }

    private String concatenateInputs() {
        StringBuilder builder = new StringBuilder("<");
        Class clazz = ((Enum)this.initial).getDeclaringClass();
        for (Enum entry : (Enum[])clazz.getEnumConstants()) {
            builder.append(entry.name()).append(", ");
        }
        builder.replace(builder.length() - 2, builder.length(), ">");
        return builder.toString();
    }
}

