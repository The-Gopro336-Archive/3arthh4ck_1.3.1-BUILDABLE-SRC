/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.modules.combat.autocrystal.helpers;

import me.earth.earthhack.api.event.bus.api.EventBus;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.autocrystal.helpers.Confirmer;
import me.earth.earthhack.impl.util.math.DiscreteTimer;
import me.earth.earthhack.impl.util.math.GuardTimer;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.network.ServerUtil;
import net.minecraft.util.math.BlockPos;

public class DamageSyncHelper {
    private final DiscreteTimer discreteTimer = new GuardTimer(1000L, 5L);
    private final StopWatch timer = new StopWatch();
    private final Setting<Integer> syncDelay;
    private final Setting<Boolean> discrete;
    private final Setting<Boolean> danger;
    private final Confirmer confirmer;
    private float lastDamage;

    public DamageSyncHelper(EventBus eventBus, Setting<Boolean> discrete, Setting<Integer> syncDelay, Setting<Boolean> danger) {
        this.danger = danger;
        this.confirmer = Confirmer.createAndSubscribe(eventBus);
        this.syncDelay = syncDelay;
        this.discrete = discrete;
        this.discreteTimer.reset(syncDelay.getValue().intValue());
    }

    public void setSync(BlockPos pos, float damage, boolean newVer) {
        int placeTime = (int)((double)ServerUtil.getPingNoPingSpoof() / 2.0 + 1.0);
        this.confirmer.setPos(pos.toImmutable(), newVer, placeTime);
        this.lastDamage = damage;
        if (this.discrete.getValue().booleanValue() && this.discreteTimer.passed(this.syncDelay.getValue().intValue())) {
            this.discreteTimer.reset(this.syncDelay.getValue().intValue());
        } else if (!this.discrete.getValue().booleanValue() && this.timer.passed(this.syncDelay.getValue().intValue())) {
            this.timer.reset();
        }
    }

    public boolean isSyncing(float damage, boolean damageSync) {
        return damageSync && (this.danger.getValue() == false || Managers.SAFETY.isSafe()) && this.confirmer.isValid() && damage <= this.lastDamage && (this.discrete.getValue() != false && !this.discreteTimer.passed(this.syncDelay.getValue().intValue()) || this.discrete.getValue() == false && !this.timer.passed(this.syncDelay.getValue().intValue()));
    }

    public Confirmer getConfirmer() {
        return this.confirmer;
    }
}

