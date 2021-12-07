/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 */
package me.earth.earthhack.impl.modules.player.fakeplayer;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.modules.player.fakeplayer.ListenerAttack;
import me.earth.earthhack.impl.modules.player.fakeplayer.ListenerExplosion;
import me.earth.earthhack.impl.modules.player.fakeplayer.ListenerMotion;
import me.earth.earthhack.impl.modules.player.fakeplayer.ListenerWorldClient;
import me.earth.earthhack.impl.modules.player.fakeplayer.util.EntityPlayerAttack;
import me.earth.earthhack.impl.modules.player.fakeplayer.util.EntityPlayerPop;
import me.earth.earthhack.impl.modules.player.fakeplayer.util.Position;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.helpers.disabling.DisablingModule;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.thread.LookUpUtil;

public class FakePlayer
extends DisablingModule {
    protected final Setting<Boolean> playRecording = this.register(new BooleanSetting("Play-Recording", false));
    protected final Setting<Boolean> record = this.register(new BooleanSetting("Record", false));
    protected final Setting<Boolean> loop = this.register(new BooleanSetting("Loop", false));
    protected final Setting<Boolean> gapple = this.register(new BooleanSetting("Gapple", true));
    protected final Setting<Integer> gappleDelay = this.register(new NumberSetting<Integer>("Gapple-Delay", 1600, 1500, 2000));
    protected final Setting<Boolean> damage = this.register(new BooleanSetting("Damage", true));
    protected final Setting<String> name = this.register(new StringSetting("Name", "FakePlayer"));
    protected final List<Position> positions = new ArrayList<Position>();
    protected final StopWatch timer = new StopWatch();
    protected EntityPlayerAttack fakePlayer;
    protected int index;

    public FakePlayer() {
        super("FakePlayer", Category.Player);
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerWorldClient(this));
        this.listeners.add(new ListenerAttack(this));
        this.listeners.add(new ListenerExplosion(this));
        this.setData(new SimpleData(this, "Spawns in a FakePlayer for testing purposes."));
    }

    @Override
    public String getDisplayInfo() {
        return this.record.getValue() != false ? "Recording" : (this.playRecording.getValue() != false ? "Playing" : null);
    }

    @Override
    protected void onEnable() {
        UUID uuid;
        GameProfile profile = new GameProfile(new UUID(1L, 1L), "FakePlayer");
        if (!this.name.getValue().equalsIgnoreCase("FakePlayer") && (uuid = LookUpUtil.getUUIDSimple(this.name.getValue())) != null) {
            profile = new GameProfile(uuid, this.name.getValue());
        }
        this.index = 0;
        this.fakePlayer = (EntityPlayerAttack)PlayerUtil.createFakePlayerAndAddToWorld(profile, EntityPlayerPop::new);
        this.fakePlayer.setRemoteSupplier(this.damage::getValue);
    }

    @Override
    protected void onDisable() {
        PlayerUtil.removeFakePlayer(this.fakePlayer);
        this.fakePlayer.isDead = true;
        this.playRecording.setValue(false);
        this.record.setValue(false);
        this.fakePlayer = null;
    }
}

