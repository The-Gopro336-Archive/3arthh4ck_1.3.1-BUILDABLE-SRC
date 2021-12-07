/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.settings.KeyBinding
 *  net.minecraft.init.MobEffects
 */
package me.earth.earthhack.impl.modules.movement.autosprint;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.modules.movement.autosprint.AutoSprintData;
import me.earth.earthhack.impl.modules.movement.autosprint.ListenerTick;
import me.earth.earthhack.impl.modules.movement.autosprint.mode.SprintMode;
import me.earth.earthhack.impl.util.minecraft.KeyBoardUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.MobEffects;

public class AutoSprint
extends Module {
    protected final Setting<SprintMode> mode = this.register(new EnumSetting<SprintMode>("Mode", SprintMode.Rage));

    public AutoSprint() {
        super("Sprint", Category.Movement);
        this.listeners.add(new ListenerTick(this));
        this.setData(new AutoSprintData(this));
    }

    @Override
    public String getDisplayInfo() {
        return this.mode.getValue().name();
    }

    @Override
    protected void onDisable() {
        KeyBinding.setKeyBindState((int)AutoSprint.mc.gameSettings.keyBindSprint.getKeyCode(), (boolean)KeyBoardUtil.isKeyDown(AutoSprint.mc.gameSettings.keyBindSprint));
    }

    public SprintMode getMode() {
        return this.mode.getValue();
    }

    public static boolean canSprint() {
        return AutoSprint.mc.player != null && !AutoSprint.mc.player.isSneaking() && !AutoSprint.mc.player.collidedHorizontally && MovementUtil.isMoving() && ((float)AutoSprint.mc.player.getFoodStats().getFoodLevel() > 6.0f || AutoSprint.mc.player.capabilities.allowFlying) && !AutoSprint.mc.player.isPotionActive(MobEffects.BLINDNESS);
    }

    public static boolean canSprintBetter() {
        return !(!AutoSprint.mc.gameSettings.keyBindForward.isKeyDown() && !AutoSprint.mc.gameSettings.keyBindBack.isKeyDown() && !AutoSprint.mc.gameSettings.keyBindLeft.isKeyDown() && !AutoSprint.mc.gameSettings.keyBindRight.isKeyDown() || AutoSprint.mc.player == null || AutoSprint.mc.player.isSneaking() || AutoSprint.mc.player.collidedHorizontally || (float)AutoSprint.mc.player.getFoodStats().getFoodLevel() <= 6.0f);
    }
}

