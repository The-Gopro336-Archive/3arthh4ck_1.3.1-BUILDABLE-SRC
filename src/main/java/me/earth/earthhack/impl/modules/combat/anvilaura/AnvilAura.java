/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.modules.combat.anvilaura;

import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.combat.anvilaura.ListenerAnvilAura;
import me.earth.earthhack.impl.modules.combat.anvilaura.ListenerRender;
import me.earth.earthhack.impl.modules.combat.anvilaura.modes.AnvilMode;
import me.earth.earthhack.impl.modules.combat.anvilaura.modes.AnvilStage;
import me.earth.earthhack.impl.modules.combat.anvilaura.util.AnvilResult;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListenerModule;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class AnvilAura
extends ObbyListenerModule<ListenerAnvilAura> {
    protected final Setting<AnvilMode> mode = this.register(new EnumSetting<AnvilMode>("Mode", AnvilMode.Mine));
    protected final Setting<Integer> fastDelay = this.register(new NumberSetting<Integer>("Fast-Delay", 0, 0, 1000));
    protected final Setting<Double> range = this.register(new NumberSetting<Double>("Range", 5.25, 0.1, 6.0));
    protected final Setting<Boolean> holdingAnvil = this.register(new BooleanSetting("HoldingAnvil", false));
    protected final Setting<Integer> yHeight = this.register(new NumberSetting<Integer>("Y-Offset", 3, 0, 256));
    protected final Setting<Boolean> trap = this.register(new BooleanSetting("Trap", true));
    protected final Setting<Boolean> mineESP = this.register(new BooleanSetting("Mine-ESP", true));
    protected final Setting<Boolean> renderBest = this.register(new BooleanSetting("RenderBest", false));
    protected final Setting<Boolean> checkFalling = this.register(new BooleanSetting("CheckFalling", true));
    protected final Setting<Boolean> pressureFalling = this.register(new BooleanSetting("PressureFalling", false));
    protected final Setting<Integer> helpingBlocks = this.register(new NumberSetting<Integer>("HelpingBlocks", 6, 0, 12));
    protected final Setting<Integer> trapHelping = this.register(new NumberSetting<Integer>("Trap-Helping", 2, 0, 3));
    protected final Setting<Double> mineRange = this.register(new NumberSetting<Double>("Mine-Range", 6.0, 0.1, 10.0));
    public final ColorSetting box = this.register(new ColorSetting("Box", new Color(100, 100, 100, 155)));
    public final ColorSetting outline = this.register(new ColorSetting("Outline", new Color(0, 0, 0, 0)));
    public final Setting<Float> lineWidth = this.register(new NumberSetting<Float>("LineWidth", Float.valueOf(1.5f), Float.valueOf(0.0f), Float.valueOf(10.0f)));
    protected final Setting<Integer> mineTime = this.register(new NumberSetting<Integer>("Mine-Time", 250, 0, 1000));
    protected final Setting<Boolean> confirmMine = this.register(new BooleanSetting("ConfirmMine", true));
    protected final Setting<Boolean> pressurePass = this.register(new BooleanSetting("PressurePass", false));
    protected final Setting<Boolean> crystal = this.register(new BooleanSetting("Crystal", false));
    protected final Setting<Integer> crystalDelay = this.register(new NumberSetting<Integer>("CrystalDelay", 500, 0, 1000));
    protected List<AxisAlignedBB> renderBBs = Collections.emptyList();
    protected final AtomicBoolean awaiting = new AtomicBoolean();
    protected final StopWatch renderTimer = new StopWatch();
    protected final StopWatch mineTimer = new StopWatch();
    protected final StopWatch awaitTimer = new StopWatch();
    protected final StopWatch crystalTimer = new StopWatch();
    protected AnvilStage stage = AnvilStage.ANVIL;
    protected AnvilResult currentResult;
    protected EntityPlayer target;
    protected AxisAlignedBB mineBB;
    protected Runnable action;
    protected int pressureSlot;
    protected int crystalSlot;
    protected int pickSlot;
    protected int obbySlot;
    protected BlockPos awaitPos;
    protected BlockPos minePos;
    protected EnumFacing mineFacing;

    public AnvilAura() {
        super("AnvilAura", Category.Combat);
        this.listeners.clear();
        this.listeners.add(this.listener);
        this.listeners.add(new ListenerRender(this));
        this.delay.setValue(500);
    }

    @Override
    protected boolean checkNull() {
        this.renderBBs = Collections.emptyList();
        this.mineBB = null;
        this.packets.clear();
        this.blocksPlaced = 0;
        if (AnvilAura.mc.player == null || AnvilAura.mc.world == null) {
            if (!this.holdingAnvil.getValue().booleanValue() && this.mode.getValue() != AnvilMode.Render) {
                this.disable();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onDisable() {
        super.onDisable();
    }

    @Override
    public String getDisplayInfo() {
        if (this.renderTimer.passed(600L)) {
            this.currentResult = null;
            this.target = null;
        }
        return this.target != null ? this.target.getName() : null;
    }

    @Override
    public boolean execute() {
        switch (this.stage) {
            case OBSIDIAN: {
                if (this.obbySlot == -1) {
                    return false;
                }
                this.slot = this.obbySlot;
                break;
            }
            case PRESSURE: {
                if (this.pressureSlot == -1) {
                    return false;
                }
                this.slot = this.pressureSlot;
                break;
            }
            case CRYSTAL: {
                if (this.crystalSlot == -1) {
                    return false;
                }
                this.slot = this.crystalSlot;
            }
        }
        if (this.action != null) {
            this.action.run();
            return true;
        }
        return super.execute();
    }

    @Override
    protected ListenerAnvilAura createListener() {
        return new ListenerAnvilAura(this, 500);
    }

    @Override
    protected boolean entityCheckSimple(BlockPos pos) {
        if (this.stage == AnvilStage.PRESSURE) {
            return true;
        }
        return super.entityCheckSimple(pos);
    }

    @Override
    public boolean entityCheck(BlockPos pos) {
        if (this.stage == AnvilStage.PRESSURE) {
            return true;
        }
        return super.entityCheck(pos);
    }

    @Override
    protected boolean quickEntityCheck(BlockPos pos) {
        if (this.stage == AnvilStage.PRESSURE) {
            return false;
        }
        return super.quickEntityCheck(pos);
    }

    @Override
    public int getDelay() {
        AnvilResult r = this.currentResult;
        if (r != null && r.hasSpecialPressure()) {
            return this.fastDelay.getValue();
        }
        return super.getDelay();
    }

    public void setCurrentResult(AnvilResult result) {
        this.renderTimer.reset();
        this.currentResult = result;
        this.target = result.getPlayer();
    }

    public boolean isMining() {
        return this.mode.getValue() == AnvilMode.Mine && (this.holdingAnvil.getValue() == false || InventoryUtil.isHolding(Blocks.ANVIL));
    }
}

