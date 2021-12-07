package me.earth.earthhack.impl.modules.combat.holefiller;

import java.util.Collections;
import java.util.List;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.thread.holes.HoleObserver;
import me.earth.earthhack.impl.managers.thread.holes.IHoleManager;
import me.earth.earthhack.impl.modules.combat.holefiller.HoleFillerData;
import me.earth.earthhack.impl.modules.combat.holefiller.ListenerObby;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListenerModule;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class HoleFiller
extends ObbyListenerModule<ListenerObby>
implements HoleObserver,
IHoleManager {
    protected final Setting<Double> range = this.register(new NumberSetting<Double>("Range", 5.25, 0.0, 6.0));
    protected final Setting<Integer> disable = this.register(new NumberSetting<Integer>("Disable", 250, 0, 1000));
    protected final Setting<Boolean> longHoles = this.register(new BooleanSetting("2x1s", false));
    protected final Setting<Boolean> bigHoles = this.register(new BooleanSetting("2x2s", false));
    protected final Setting<Integer> calcDelay = this.register(new NumberSetting<Integer>("CalcDelay", 0, 0, 500));
    protected final Setting<Boolean> requireTarget = this.register(new BooleanSetting("RequireTarget", false));
    protected final Setting<Double> targetRange = this.register(new NumberSetting<Double>("Target-Range", 6.0, 0.0, 12.0));
    protected final Setting<Double> targetDistance = this.register(new NumberSetting<Double>("Target-Block", 3.0, 0.0, 12.0));
    protected final Setting<Boolean> safety = this.register(new BooleanSetting("Safety", false));
    protected final Setting<Double> minSelf = this.register(new NumberSetting<Double>("Min-Self", 2.0, 0.0, 6.0));
    protected final Setting<Boolean> waitForHoleLeave = this.register(new BooleanSetting("WaitForHoleLeave", false));
    protected List<BlockPos> safes = Collections.emptyList();
    protected List<BlockPos> unsafes = Collections.emptyList();
    protected List<BlockPos> longs = Collections.emptyList();
    protected List<BlockPos> bigs = Collections.emptyList();
    protected final StopWatch disableTimer = new StopWatch();
    protected final StopWatch calcTimer = new StopWatch();
    protected EntityPlayer target;
    protected boolean waiting;

    public HoleFiller() {
        super("HoleFiller", Category.Combat);
        this.listeners.clear();
        this.listeners.add(this.listener);
        this.setData(new HoleFillerData(this));
    }

    @Override
    public String getDisplayInfo() {
        if (this.target != null) {
            return (this.waiting ? "\u00a7c" : "") + this.target.getName();
        }
        return null;
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        this.disableTimer.reset();
        this.calcTimer.setTime(0L);
        this.target = null;
        this.waiting = false;
    }

    @Override
    public double getRange() {
        return this.range.getValue();
    }

    @Override
    public int getSafeHoles() {
        return 20;
    }

    @Override
    public int getUnsafeHoles() {
        return 20;
    }

    @Override
    public int get2x1Holes() {
        return this.longHoles.getValue() != false ? 4 : 0;
    }

    @Override
    public int get2x2Holes() {
        return this.bigHoles.getValue() != false ? 1 : 0;
    }

    @Override
    public void setSafe(List<BlockPos> safe) {
        this.safes = safe;
    }

    @Override
    public void setUnsafe(List<BlockPos> unsafe) {
        this.unsafes = unsafe;
    }

    @Override
    public void setLongHoles(List<BlockPos> longHoles) {
        this.longs = longHoles;
    }

    @Override
    public void setBigHoles(List<BlockPos> bigHoles) {
        this.bigs = bigHoles;
    }

    @Override
    public void setFinished() {
    }

    @Override
    protected ListenerObby createListener() {
        return new ListenerObby(this);
    }
}
