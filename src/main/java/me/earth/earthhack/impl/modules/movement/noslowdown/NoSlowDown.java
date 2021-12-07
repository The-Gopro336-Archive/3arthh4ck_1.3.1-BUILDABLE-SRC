/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiIngameMenu
 *  net.minecraft.client.gui.GuiOptions
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.gui.GuiScreenOptionsSounds
 *  net.minecraft.client.gui.GuiVideoSettings
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.client.settings.KeyBinding
 *  net.minecraft.network.play.client.CPacketPlayer
 */
package me.earth.earthhack.impl.modules.movement.noslowdown;

import java.util.ArrayList;
import java.util.List;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.gui.click.Click;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.movement.noslowdown.ListenerInput;
import me.earth.earthhack.impl.modules.movement.noslowdown.ListenerMove;
import me.earth.earthhack.impl.modules.movement.noslowdown.ListenerPostKeys;
import me.earth.earthhack.impl.modules.movement.noslowdown.ListenerRightClickItem;
import me.earth.earthhack.impl.modules.movement.noslowdown.ListenerSprint;
import me.earth.earthhack.impl.modules.movement.noslowdown.ListenerTick;
import me.earth.earthhack.impl.modules.movement.noslowdown.ListenerTryUseItem;
import me.earth.earthhack.impl.modules.movement.noslowdown.ListenerTryUseItemOnBlock;
import me.earth.earthhack.impl.modules.movement.noslowdown.NoSlowDownData;
import me.earth.earthhack.impl.util.minecraft.KeyBoardUtil;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenOptionsSounds;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.CPacketPlayer;

public class NoSlowDown
extends Module {
    protected final Setting<Boolean> guiMove = this.register(new BooleanSetting("GuiMove", true));
    protected final Setting<Boolean> items = this.register(new BooleanSetting("Items", true));
    protected final Setting<Boolean> legit = this.register(new BooleanSetting("Legit", false));
    protected final Setting<Boolean> sprint = this.register(new BooleanSetting("Sprint", true));
    protected final Setting<Boolean> input = this.register(new BooleanSetting("Input", true));
    protected final Setting<Boolean> sneakPacket = this.register(new BooleanSetting("SneakPacket", false));
    protected final Setting<Double> websY = this.register(new NumberSetting<Double>("WebsVertical", 2.0, 1.0, 100.0));
    protected final Setting<Double> websXZ = this.register(new NumberSetting<Double>("WebsHorizontal", 1.1, 1.0, 100.0));
    protected final Setting<Boolean> sneak = this.register(new BooleanSetting("WebsSneak", false));
    protected final Setting<Boolean> useTimerWeb = this.register(new BooleanSetting("UseTimerInWeb", false));
    protected final Setting<Double> timerSpeed = this.register(new NumberSetting<Double>("Timer", 8.0, 0.1, 20.0));
    protected final Setting<Boolean> onGroundSpoof = this.register(new BooleanSetting("OnGroundSpoof", false));
    protected final Setting<Boolean> superStrict = this.register(new BooleanSetting("SuperStrict", false));
    protected final Setting<Boolean> phobosGui = this.register(new BooleanSetting("PhobosGui", false));
    protected final List<Class<? extends GuiScreen>> screens = new ArrayList<Class<? extends GuiScreen>>();
    protected final KeyBinding[] keys;
    protected boolean spoof = true;

    public NoSlowDown() {
        super("NoSlowDown", Category.Movement);
        this.register(new BooleanSetting("SoulSand", true));
        this.keys = new KeyBinding[]{NoSlowDown.mc.gameSettings.keyBindForward, NoSlowDown.mc.gameSettings.keyBindBack, NoSlowDown.mc.gameSettings.keyBindLeft, NoSlowDown.mc.gameSettings.keyBindRight, NoSlowDown.mc.gameSettings.keyBindJump, NoSlowDown.mc.gameSettings.keyBindSprint};
        this.screens.add(GuiOptions.class);
        this.screens.add(GuiVideoSettings.class);
        this.screens.add(GuiScreenOptionsSounds.class);
        this.screens.add(GuiContainer.class);
        this.screens.add(GuiIngameMenu.class);
        this.listeners.add(new ListenerSprint(this));
        this.listeners.add(new ListenerInput(this));
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerMove(this));
        this.listeners.add(new ListenerPostKeys(this));
        this.listeners.add(new ListenerRightClickItem(this));
        this.listeners.add(new ListenerTryUseItem(this));
        this.listeners.add(new ListenerTryUseItemOnBlock(this));
        this.setData(new NoSlowDownData(this));
    }

    @Override
    protected void onDisable() {
        Managers.NCP.setStrict(false);
    }

    protected void updateKeyBinds() {
        block2: {
            block3: {
                if (!this.guiMove.getValue().booleanValue()) break block2;
                if (!this.screens.stream().anyMatch(screen -> screen.isInstance((Object)NoSlowDown.mc.currentScreen)) && (!this.phobosGui.getValue().booleanValue() || !(NoSlowDown.mc.currentScreen instanceof Click))) break block3;
                for (KeyBinding key : this.keys) {
                    KeyBinding.setKeyBindState((int)key.getKeyCode(), (boolean)KeyBoardUtil.isKeyDown(key));
                }
                break block2;
            }
            if (NoSlowDown.mc.currentScreen != null) break block2;
            for (KeyBinding key : this.keys) {
                if (KeyBoardUtil.isKeyDown(key)) continue;
                KeyBinding.setKeyBindState((int)key.getKeyCode(), (boolean)false);
            }
        }
    }

    protected void onPacket(CPacketPlayer packet) {
    }
}

