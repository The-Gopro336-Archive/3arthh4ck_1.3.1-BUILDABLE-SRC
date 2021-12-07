/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemFood
 *  org.lwjgl.input.Mouse
 */
package me.earth.earthhack.impl.modules.misc.antiaim;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.misc.antiaim.AntiAimData;
import me.earth.earthhack.impl.modules.misc.antiaim.AntiAimMode;
import me.earth.earthhack.impl.modules.misc.antiaim.ListenerMotion;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.item.ItemFood;
import org.lwjgl.input.Mouse;

public class AntiAim
extends Module {
    protected final Setting<AntiAimMode> mode = this.register(new EnumSetting<AntiAimMode>("Mode", AntiAimMode.Spin));
    protected final Setting<Float> hSpeed = this.register(new NumberSetting<Float>("H-Speed", Float.valueOf(10.0f), Float.valueOf(0.1f), Float.valueOf(180.0f)));
    protected final Setting<Float> vSpeed = this.register(new NumberSetting<Float>("V-Speed", Float.valueOf(10.0f), Float.valueOf(0.1f), Float.valueOf(180.0f)));
    protected final Setting<Boolean> strict = this.register(new BooleanSetting("Strict", true));
    protected final Setting<Boolean> sneak = this.register(new BooleanSetting("Sneak", false));
    protected final Setting<Integer> sneakDelay = this.register(new NumberSetting<Integer>("Sneak-Delay", 500, 0, 5000));
    protected final Setting<Float> yaw = this.register(new NumberSetting<Float>("Yaw", Float.valueOf(0.0f), Float.valueOf(-360.0f), Float.valueOf(360.0f)));
    protected final Setting<Float> pitch = this.register(new NumberSetting<Float>("Pitch", Float.valueOf(0.0f), Float.valueOf(-90.0f), Float.valueOf(90.0f)));
    protected final Setting<Integer> skip = this.register(new NumberSetting<Integer>("Skip", 1, 1, 20));
    protected final StopWatch timer = new StopWatch();
    protected float lastYaw;
    protected float lastPitch;

    public AntiAim() {
        super("AntiAim", Category.Misc);
        this.listeners.add(new ListenerMotion(this));
        this.setData(new AntiAimData(this));
    }

    @Override
    protected void onEnable() {
        if (AntiAim.mc.player != null) {
            this.lastYaw = AntiAim.mc.player.rotationYaw;
            this.lastPitch = AntiAim.mc.player.rotationPitch;
        }
    }

    public boolean dontRotate() {
        return !(this.strict.getValue() == false || (AntiAim.mc.player.getActiveItemStack().getItem() instanceof ItemFood && !AntiAim.mc.gameSettings.keyBindAttack.isKeyDown() || !AntiAim.mc.gameSettings.keyBindAttack.isKeyDown() && !AntiAim.mc.gameSettings.keyBindUseItem.isKeyDown()) && !Mouse.isButtonDown((int)2));
    }
}

