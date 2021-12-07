/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 */
package me.earth.earthhack.impl.modules.client.management;

import com.mojang.authlib.GameProfile;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.management.ListenerAspectRatio;
import me.earth.earthhack.impl.modules.client.management.ListenerGameLoop;
import me.earth.earthhack.impl.modules.client.management.ListenerLogout;
import me.earth.earthhack.impl.modules.client.management.ListenerTick;
import me.earth.earthhack.impl.modules.client.management.ManagementData;
import me.earth.earthhack.impl.util.text.ChatUtil;

public class Management
extends Module {
    protected final Setting<Boolean> clear = this.register(new BooleanSetting("ClearPops", false));
    protected final Setting<Boolean> logout = this.register(new BooleanSetting("LogoutPops", false));
    protected final Setting<Boolean> friend = this.register(new BooleanSetting("SelfFriend", true));
    protected final Setting<Boolean> soundRemove = this.register(new BooleanSetting("SoundRemove", true));
    protected final Setting<Integer> deathTime = this.register(new NumberSetting<Integer>("DeathTime", 250, 0, 1000));
    protected final Setting<Integer> time = this.register(new NumberSetting<Integer>("Time", 0, 0, 24000));
    protected final Setting<Boolean> aspectRatio = this.register(new BooleanSetting("ChangeAspectRatio", false));
    protected final Setting<Integer> aspectRatioWidth;
    protected final Setting<Integer> aspectRatioHeight;
    protected final Setting<Boolean> pooledScreenShots;
    protected final Setting<Boolean> pauseOnLeftFocus;
    protected GameProfile lastProfile;

    public Management() {
        super("Management", Category.Client);
        this.aspectRatioWidth = this.register(new NumberSetting<Integer>("AspectRatioWidth", Management.mc.displayWidth, 0, Management.mc.displayWidth));
        this.aspectRatioHeight = this.register(new NumberSetting<Integer>("AspectRatioHeight", Management.mc.displayHeight, 0, Management.mc.displayHeight));
        this.pooledScreenShots = this.register(new BooleanSetting("Pooled-Screenshots", false));
        this.pauseOnLeftFocus = this.register(new BooleanSetting("PauseOnLeftFocus", Management.mc.gameSettings.pauseOnLostFocus));
        Bus.EVENT_BUS.register(new ListenerLogout(this));
        Bus.EVENT_BUS.register(new ListenerGameLoop(this));
        Bus.EVENT_BUS.register(new ListenerAspectRatio(this));
        Bus.EVENT_BUS.register(new ListenerTick(this));
        this.setData(new ManagementData(this));
        this.clear.addObserver(event -> {
            event.setValue(false);
            ChatUtil.sendMessage("Clearing TotemPops...");
            Managers.COMBAT.reset();
        });
        this.pauseOnLeftFocus.addObserver(e -> {
            Management.mc.gameSettings.pauseOnLostFocus = (Boolean)e.getValue();
        });
        this.register(new BooleanSetting("IgnoreForgeRegistries", false));
    }

    @Override
    protected void onLoad() {
        if (this.friend.getValue().booleanValue()) {
            this.lastProfile = mc.getSession().getProfile();
            Managers.FRIENDS.add(this.lastProfile.getName(), this.lastProfile.getId());
        }
    }
}

