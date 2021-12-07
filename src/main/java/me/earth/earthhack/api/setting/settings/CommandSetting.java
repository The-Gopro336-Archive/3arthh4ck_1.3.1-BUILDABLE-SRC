/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 */
package me.earth.earthhack.api.setting.settings;

import com.google.gson.JsonElement;
import java.util.function.Consumer;
import me.earth.earthhack.api.setting.event.SettingResult;
import me.earth.earthhack.api.setting.settings.StringSetting;

public class CommandSetting
extends StringSetting {
    private final Consumer<String> inputReader;

    public CommandSetting(String nameIn, Consumer<String> inputReader) {
        super(nameIn, "<...>");
        this.inputReader = inputReader;
    }

    public void onInput(String input) {
        this.inputReader.accept(input);
    }

    @Override
    public void fromJson(JsonElement element) {
    }

    @Override
    public SettingResult fromString(String string) {
        this.onInput(string);
        return SettingResult.SUCCESSFUL;
    }

    @Override
    public void setValue(String value, boolean withEvent) {
        this.onInput(value);
    }
}

