/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.movement.step;

import java.awt.Color;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.movement.step.ListenerBreak;
import me.earth.earthhack.impl.modules.movement.step.ListenerDestroy;
import me.earth.earthhack.impl.modules.movement.step.ListenerRender;
import me.earth.earthhack.impl.modules.movement.step.ListenerStep;
import me.earth.earthhack.impl.modules.movement.step.StepData;
import me.earth.earthhack.impl.modules.movement.step.StepESP;
import me.earth.earthhack.impl.util.helpers.disabling.DisablingModule;
import me.earth.earthhack.impl.util.helpers.render.BlockESPModule;
import me.earth.earthhack.impl.util.math.StopWatch;

public class Step
extends BlockESPModule {
    protected final Setting<StepESP> esp;
    protected final Setting<Float> height;
    protected final Setting<Boolean> useTimer;
    protected final Setting<Boolean> vanilla;
    protected final Setting<Boolean> entityStep;
    protected final Setting<Boolean> autoOff;
    protected final Setting<Integer> lagTime;
    protected final Setting<Boolean> gapple;
    protected final StopWatch breakTimer;
    protected boolean stepping;
    protected double y;

    public Step() {
        super("Step", Category.Movement);
        this.esp = this.registerBefore(new EnumSetting<StepESP>("ESP", StepESP.None), this.color);
        this.height = this.register(new NumberSetting<Float>("Height", Float.valueOf(2.0f), Float.valueOf(0.6f), Float.valueOf(10.0f)));
        this.useTimer = this.register(new BooleanSetting("UseTimer", false));
        this.vanilla = this.register(new BooleanSetting("Vanilla", false));
        this.entityStep = this.register(new BooleanSetting("EntityStep", true));
        this.autoOff = this.register(new BooleanSetting("AutoOff", false));
        this.lagTime = this.register(new NumberSetting<Integer>("LagTime", 0, 0, 250));
        this.gapple = this.register(new BooleanSetting("Mine-Gapple", false));
        this.breakTimer = new StopWatch();
        this.listeners.add(new ListenerStep(this));
        this.listeners.add(new ListenerDestroy(this));
        this.listeners.add(new ListenerBreak(this));
        this.listeners.add(new ListenerRender(this));
        DisablingModule.makeDisablingModule(this);
        this.color.setValue(new Color(0, 255, 255, 76));
        this.outline.setValue(new Color(0, 255, 255));
        this.setData(new StepData(this));
    }

    @Override
    protected void onDisable() {
        if (Step.mc.player != null) {
            if (Step.mc.player.getRidingEntity() != null) {
                Step.mc.player.getRidingEntity().stepHeight = 1.0f;
            }
            Step.mc.player.stepHeight = 0.6f;
        }
        Managers.TIMER.reset();
    }

    public void onBreak() {
        this.breakTimer.reset();
    }

    protected boolean canStep() {
        return !Step.mc.player.isInWater() && Step.mc.player.onGround && !Step.mc.player.isOnLadder() && !Step.mc.player.movementInput.jump && Step.mc.player.collidedVertically && (double)Step.mc.player.fallDistance < 0.1;
    }
}

