package me.earth.earthhack.impl.managers.thread.safety;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.safety.ListenerGameLoop;
import me.earth.earthhack.impl.managers.thread.safety.ListenerMotionUpdate;
import me.earth.earthhack.impl.managers.thread.safety.ListenerSpawnObject;
import me.earth.earthhack.impl.managers.thread.safety.ListenerTick;
import me.earth.earthhack.impl.managers.thread.safety.SafetyRunnable;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.safety.Safety;
import me.earth.earthhack.impl.modules.client.safety.util.Update;
import net.minecraft.entity.Entity;

public class SafetyManager
extends SubscriberImpl
implements Globals {
    private final AtomicBoolean safe = new AtomicBoolean(false);
    protected final SettingCache<Boolean, BooleanSetting, Safety> newV = Caches.getSetting(Safety.class, BooleanSetting.class, "1.13+", false);
    protected final SettingCache<Boolean, BooleanSetting, Safety> newVEntities = Caches.getSetting(Safety.class, BooleanSetting.class, "1.13-Entities", false);
    protected final SettingCache<Boolean, BooleanSetting, Safety> beds = Caches.getSetting(Safety.class, BooleanSetting.class, "BedCheck", false);
    protected final SettingCache<Float, NumberSetting<Float>, Safety> damage = Caches.getSetting(Safety.class, Setting.class, "MaxDamage", Float.valueOf(4.0f));
    protected final SettingCache<Integer, NumberSetting<Integer>, Safety> d = Caches.getSetting(Safety.class, Setting.class, "Delay", 25);
    protected final SettingCache<Update, EnumSetting<Update>, Safety> mode = Caches.getSetting(Safety.class, Setting.class, "Updates", Update.Tick);
    protected final SettingCache<Boolean, BooleanSetting, Safety> longs = Caches.getSetting(Safety.class, BooleanSetting.class, "2x1s", false);
    protected final SettingCache<Boolean, BooleanSetting, Safety> big = Caches.getSetting(Safety.class, BooleanSetting.class, "2x2s", false);
    protected final SettingCache<Boolean, BooleanSetting, Safety> post = Caches.getSetting(Safety.class, BooleanSetting.class, "Post-Calc", false);
    protected final SettingCache<Boolean, BooleanSetting, Safety> anvils = Caches.getSetting(Safety.class, BooleanSetting.class, "Anvils", false);
    protected final SettingCache<Boolean, BooleanSetting, Safety> terrain = Caches.getSetting(Safety.class, BooleanSetting.class, "Terrain", false);

    public SafetyManager() {
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerGameLoop(this));
        this.listeners.add(new ListenerSpawnObject(this));
        this.listeners.add(new ListenerMotionUpdate(this));
    }

    public boolean isSafe() {
        return this.safe.get();
    }

    public void setSafe(boolean safeIn) {
        this.safe.set(safeIn);
    }

    protected void runThread() {
        if (SafetyManager.mc.player != null && SafetyManager.mc.world != null) {
            ArrayList<Entity> crystals = new ArrayList<Entity>(SafetyManager.mc.world.loadedEntityList);
            SafetyRunnable runnable = new SafetyRunnable(this, crystals, this.newVEntities.getValue(), this.newV.getValue(), this.beds.getValue(), this.damage.getValue().floatValue(), this.longs.getValue(), this.big.getValue(), this.anvils.getValue(), this.terrain.getValue());
            Managers.THREAD.submit(runnable);
        }
    }
}
