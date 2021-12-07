/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.settings.KeyBinding
 *  org.lwjgl.input.Keyboard
 *  org.lwjgl.input.Mouse
 */
package me.earth.earthhack.impl.util.minecraft;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.util.bind.Bind;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class KeyBoardUtil {
    public static boolean isKeyDown(KeyBinding binding) {
        return KeyBoardUtil.isKeyDown(binding.getKeyCode());
    }

    public static boolean isKeyDown(Setting<Bind> setting) {
        return KeyBoardUtil.isKeyDown(setting.getValue());
    }

    public static boolean isKeyDown(Bind bind) {
        return KeyBoardUtil.isKeyDown(bind.getKey());
    }

    public static boolean isKeyDown(int key) {
        return key != 0 && key != -1 && (key < 0 ? Mouse.isButtonDown((int)(key + 100)) : Keyboard.isKeyDown((int)key));
    }
}

