/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.Vec3i
 */
package me.earth.earthhack.impl.modules.combat.antitrap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.combat.antitrap.AntiTrapData;
import me.earth.earthhack.impl.modules.combat.antitrap.ListenerMotion;
import me.earth.earthhack.impl.modules.combat.antitrap.util.AntiTrapMode;
import me.earth.earthhack.impl.modules.combat.offhand.modes.OffhandMode;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyModule;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3i;

public class AntiTrap
extends ObbyModule {
    protected final Setting<AntiTrapMode> mode;
    protected final Setting<Boolean> offhand;
    protected final Setting<Integer> timeOut;
    protected final Setting<Boolean> empty;
    protected final Setting<Boolean> swing;
    protected final Setting<Boolean> highFill;
    protected final Setting<Integer> confirm;
    protected final Setting<Boolean> autoOff;
    protected final Map<BlockPos, Long> placed;
    protected final Set<BlockPos> confirmed;
    protected final StopWatch interval;
    protected RayTraceResult result;
    protected OffhandMode previous;
    protected BlockPos startPos;
    protected BlockPos pos;

    public AntiTrap() {
        super("AntiTrap", Category.Combat);
        this.mode = this.registerBefore(new EnumSetting<AntiTrapMode>("Mode", AntiTrapMode.Crystal), this.blocks);
        this.offhand = this.register(new BooleanSetting("Offhand", false));
        this.timeOut = this.register(new NumberSetting<Integer>("TimeOut", 400, 0, 1000));
        this.empty = this.register(new BooleanSetting("Empty", true));
        this.swing = this.register(new BooleanSetting("Swing", false));
        this.highFill = this.register(new BooleanSetting("HighFill", false));
        this.confirm = this.register(new NumberSetting<Integer>("Confirm", 250, 0, 1000));
        this.autoOff = this.register(new BooleanSetting("Auto-Off", true));
        this.placed = new HashMap<BlockPos, Long>();
        this.confirmed = new HashSet<BlockPos>();
        this.interval = new StopWatch();
        this.listeners.add(new ListenerMotion(this));
        this.setData(new AntiTrapData(this));
    }

    @Override
    public String getDisplayInfo() {
        return this.mode.getValue().name();
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        this.previous = null;
        this.placed.clear();
        this.confirmed.clear();
        if (super.checkNull() && this.interval.passed(this.timeOut.getValue().intValue())) {
            this.interval.reset();
            this.result = null;
            this.pos = null;
            this.startPos = PositionUtil.getPosition();
        } else {
            this.disable();
        }
    }

    @Override
    protected void onDisable() {
        if (this.offhand.getValue().booleanValue() && this.previous != null) {
            ListenerMotion.OFFHAND.computeIfPresent(o -> o.setMode(this.previous));
        }
    }

    @Override
    public boolean placeBlock(BlockPos pos) {
        boolean hasPlaced = super.placeBlock(pos);
        if (hasPlaced) {
            this.placed.put(pos, System.currentTimeMillis());
        }
        return hasPlaced;
    }

    protected List<BlockPos> getCrystalPositions() {
        ArrayList<BlockPos> result = new ArrayList<BlockPos>();
        BlockPos playerPos = PositionUtil.getPosition();
        if (!AntiTrap.mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(playerPos, playerPos.up().add(1, 2, 1))).isEmpty()) {
            this.disable();
            return result;
        }
        for (Vec3i vec : AntiTrapMode.Crystal.getOffsets()) {
            BlockPos pos = playerPos.add(vec);
            if (!BlockUtil.canPlaceCrystal(pos, false, false)) continue;
            result.add(pos);
        }
        return result;
    }
}

