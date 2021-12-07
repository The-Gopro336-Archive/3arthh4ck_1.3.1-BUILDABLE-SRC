/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.modules.player.automine;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.player.automine.ListenerBlockChange;
import me.earth.earthhack.impl.modules.player.automine.ListenerMultiBlockChange;
import me.earth.earthhack.impl.modules.player.automine.ListenerPlace;
import me.earth.earthhack.impl.modules.player.automine.ListenerUpdate;
import me.earth.earthhack.impl.modules.player.automine.ListenerWorldClient;
import me.earth.earthhack.impl.modules.player.automine.mode.AutoMineMode;
import me.earth.earthhack.impl.modules.player.automine.util.BigConstellation;
import me.earth.earthhack.impl.modules.player.automine.util.IAutomine;
import me.earth.earthhack.impl.modules.player.automine.util.IConstellation;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.helpers.addable.BlockAddingModule;
import me.earth.earthhack.impl.util.helpers.addable.ListType;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class AutoMine
extends BlockAddingModule
implements IAutomine {
    private static final ModuleCache<Speedmine> SPEED_MINE = Caches.getModule(Speedmine.class);
    protected final Setting<AutoMineMode> mode = this.register(new EnumSetting<AutoMineMode>("Mode", AutoMineMode.Combat));
    protected final Setting<Float> range = this.register(new NumberSetting<Float>("Range", Float.valueOf(6.0f), Float.valueOf(0.1f), Float.valueOf(100.0f)));
    protected final Setting<Boolean> head = this.register(new BooleanSetting("Head", false));
    protected final Setting<Boolean> rotate = this.register(new BooleanSetting("Rotate", false));
    protected final Setting<Boolean> self = this.register(new BooleanSetting("Self", true));
    protected final Setting<Boolean> prioSelf = this.register(new BooleanSetting("Prio-SelfUntrap", true));
    protected final Setting<Boolean> constellationCheck = this.register(new BooleanSetting("ConstCheck", true));
    protected final Setting<Boolean> improve = this.register(new BooleanSetting("Improve", false));
    protected final Setting<Integer> delay = this.register(new NumberSetting<Integer>("Delay", 100, 0, 10000));
    protected final Setting<Boolean> newV = this.register(new BooleanSetting("1.13+", false));
    protected final Setting<Boolean> newVEntities = this.register(new BooleanSetting("1.13-Entities", false));
    protected final Setting<Boolean> checkCurrent = this.register(new BooleanSetting("CheckCurrent", true));
    protected final Setting<Boolean> mineL = this.register(new BooleanSetting("Mine-L", false));
    protected final Setting<Integer> offset = this.register(new NumberSetting<Integer>("Reset-Offset", 0, 0, 1000));
    protected final Setting<Boolean> shouldBlackList = this.register(new BooleanSetting("BlackList", true));
    protected final Setting<Integer> blackListFor = this.register(new NumberSetting<Integer>("Blacklist-For", 120, 0, 3600));
    protected final Setting<Boolean> checkTrace = this.register(new BooleanSetting("Check-Range", false));
    protected final Setting<Float> placeRange = this.register(new NumberSetting<Float>("PlaceRange", Float.valueOf(6.0f), Float.valueOf(0.1f), Float.valueOf(100.0f)));
    protected final Setting<Float> placeTrace = this.register(new NumberSetting<Float>("PlaceTrace", Float.valueOf(6.0f), Float.valueOf(0.1f), Float.valueOf(100.0f)));
    protected final Setting<Float> breakTrace = this.register(new NumberSetting<Float>("BreakTrace", Float.valueOf(3.5f), Float.valueOf(0.1f), Float.valueOf(100.0f)));
    protected final Setting<Boolean> selfEchestMine = this.register(new BooleanSetting("Self-EchestBurrow-Mine", false));
    protected final Setting<Boolean> mineBurrow = this.register(new BooleanSetting("Mine-Burrow", false));
    protected final Setting<Boolean> checkPlayerState = this.register(new BooleanSetting("CheckPlayerState", true));
    protected final Setting<Boolean> resetIfNotValid = this.register(new BooleanSetting("Reset-Invalid", false));
    protected final Setting<Boolean> terrain = this.register(new BooleanSetting("Terrain", false));
    protected final Setting<Boolean> obbyPositions = this.register(new BooleanSetting("ObbyPositions", false));
    protected final Setting<Boolean> mineObby = this.register(new BooleanSetting("MineObby", false));
    protected final Setting<Boolean> closestPlayer = this.register(new BooleanSetting("ClosestPlayer", true));
    protected final Setting<Boolean> improveCalcs = this.register(new BooleanSetting("ImproveCalcs", false));
    protected final Setting<Float> minDmg = this.register(new NumberSetting<Float>("MinDamage", Float.valueOf(6.0f), Float.valueOf(0.1f), Float.valueOf(100.0f)));
    protected final Setting<Float> maxSelfDmg = this.register(new NumberSetting<Float>("MaxSelfDmg", Float.valueOf(10.0f), Float.valueOf(0.1f), Float.valueOf(100.0f)));
    protected final Setting<Integer> terrainDelay = this.register(new NumberSetting<Integer>("TerrainDelay", 500, 0, 10000));
    protected final Setting<Boolean> suicide = this.register(new BooleanSetting("Suicide", false));
    protected final Setting<Boolean> echest = this.register(new BooleanSetting("Echests", false));
    protected final Setting<Float> echestRange = this.register(new NumberSetting<Float>("Echest-Range", Float.valueOf(3.0f), Float.valueOf(0.1f), Float.valueOf(100.0f)));
    protected final Setting<Integer> maxTime = this.register(new NumberSetting<Integer>("MaxTime", 20000, 0, 60000));
    protected final Setting<Boolean> checkCrystalDownTime = this.register(new BooleanSetting("CheckCrystalDownTime", false));
    protected final Setting<Integer> downTime = this.register(new NumberSetting<Integer>("AutoCrystalDownTime", 500, 0, 5000));
    protected final Map<BlockPos, Long> blackList = new HashMap<BlockPos, Long>();
    protected final StopWatch constellationTimer = new StopWatch();
    protected final StopWatch terrainTimer = new StopWatch();
    protected final StopWatch downTimer = new StopWatch();
    protected final StopWatch timer = new StopWatch();
    protected IConstellation constellation;
    protected Future<?> future;
    protected boolean attacking;
    protected BlockPos current;
    protected BlockPos last;

    public AutoMine() {
        super("AutoMine", Category.Player, s -> "White/Blacklist the mining of " + s.getName() + " blocks.");
        this.listeners.add(new ListenerUpdate(this));
        this.listeners.add(new ListenerBlockChange(this));
        this.listeners.add(new ListenerMultiBlockChange(this));
        this.listeners.add(new ListenerWorldClient(this));
        this.listeners.add(new ListenerPlace(this));
        this.listType.setValue(ListType.BlackList);
        SimpleData data = new SimpleData(this, "Automatically mines Blocks.");
        data.register(this.mode, "-Combat will strategically mine enemies out. Uses Speedmine - Smart.\n-AntiTrap will mine you out of traps.");
        data.register(this.range, "Range in which blocks will be mined.");
        data.register(this.head, "Mines the Block above the Target.");
        data.register(this.rotate, "Rotates to mine the block.");
        data.register(this.self, "Touches Blocks in the own Surround so they can be mined quickly if an enemy jumps in.");
        data.register(this.prioSelf, "Prioritizes untrapping yourself.");
        data.register(this.constellationCheck, "Dev Setting, should be on.");
        data.register(this.delay, "Delay between touching blocks.");
        data.register(this.newV, "Takes 1.13+ crystal mechanics into account.");
        data.register(this.checkCurrent, "Dev Setting, should be on.");
        data.register(this.improve, "Will actively search for a better position.");
        data.register(this.mineL, "For Combat: Mines out L-Shape Holes");
        data.register(this.offset, "Time to wait after a block has been destroyed.");
        data.register(this.shouldBlackList, "Blacklists blocks that you reset by touching them again.");
        data.register(this.blackListFor, "Time in seconds a block should be blacklisted for. A value of 0 means it will never be blacklisted.");
        data.register(this.checkTrace, "Checks PlaceRange, PlaceTrace and BreakTrace for the crystal position.");
        data.register(this.placeRange, "PlaceRange of your CA.");
        data.register(this.placeTrace, "PlaceTrace of your CA.");
        data.register(this.breakTrace, "BreakTrace of your CA.");
        data.register(this.selfEchestMine, "Will mine an Echest you burrowed with.");
        data.register(this.resetIfNotValid, "Doesn't keep invalid positions mined.");
        data.register(this.mineBurrow, "Will mine players burrow blocks.");
        data.register(this.checkPlayerState, "Checks if a player burrowed in the meantime.");
        this.setData(data);
    }

    @Override
    public String getDisplayInfo() {
        return this.mode.getValue().name();
    }

    @Override
    public void onDisable() {
        this.reset(true);
        this.blackList.clear();
    }

    public AutoMineMode getMode() {
        return this.mode.getValue();
    }

    public void addToBlackList(BlockPos pos) {
        if (this.shouldBlackList.getValue().booleanValue()) {
            this.blackList.put(pos, System.currentTimeMillis());
        }
    }

    public void reset() {
        this.reset(false);
    }

    public void reset(boolean hard) {
        if (!hard && this.constellation instanceof BigConstellation) {
            return;
        }
        if (!this.attacking) {
            if (this.future != null) {
                this.future.cancel(true);
                this.future = null;
            }
            this.constellation = null;
            this.current = null;
            if (this.offset.getValue() != 0) {
                this.timer.setTime(System.currentTimeMillis() + (long)this.offset.getValue().intValue());
            }
        }
    }

    protected boolean checkCrystalPos(BlockPos pos) {
        if (this.checkTrace.getValue().booleanValue()) {
            return BlockUtil.isCrystalPosInRange(pos, this.placeRange.getValue().floatValue(), this.placeTrace.getValue().floatValue(), this.breakTrace.getValue().floatValue()) && BlockUtil.canPlaceCrystal(pos, true, this.newV.getValue(), AutoMine.mc.world.loadedEntityList, this.newVEntities.getValue(), 0L);
        }
        return BlockUtil.canPlaceCrystal(pos, true, this.newV.getValue(), AutoMine.mc.world.loadedEntityList, this.newVEntities.getValue(), 0L);
    }

    @Override
    public boolean isValid(IBlockState state) {
        return super.isValid(state.getBlock().getLocalizedName());
    }

    @Override
    public void offer(IConstellation constellation) {
        if (this.constellation != null && this.constellation.cantBeImproved() || AutoMine.mc.player == null || AutoMine.mc.world == null) {
            return;
        }
        if (this.future != null) {
            this.future.cancel(true);
            this.future = null;
        }
        this.constellation = constellation;
        this.constellationTimer.reset();
    }

    @Override
    public void attackPos(BlockPos pos) {
        EnumFacing facing = RayTraceUtil.getFacing((Entity)RotationUtil.getRotationPlayer(), pos, true);
        ((Speedmine)SPEED_MINE.get()).getTimer().setTime(0L);
        this.current = pos;
        this.attacking = true;
        assert (facing != null);
        AutoMine.mc.playerController.onPlayerDamageBlock(pos, facing);
        this.attacking = false;
        this.timer.reset();
    }

    @Override
    public void setCurrent(BlockPos pos) {
        this.current = pos;
    }

    @Override
    public BlockPos getCurrent() {
        return this.current;
    }

    @Override
    public void setFuture(Future<?> future) {
        this.future = future;
    }

    @Override
    public float getMinDmg() {
        return this.minDmg.getValue().floatValue();
    }

    @Override
    public float getMaxSelfDmg() {
        return this.maxSelfDmg.getValue().floatValue();
    }

    @Override
    public double getBreakTrace() {
        return this.breakTrace.getValue().floatValue();
    }

    @Override
    public boolean getNewVEntities() {
        return this.newVEntities.getValue();
    }

    @Override
    public boolean shouldMineObby() {
        return this.mineObby.getValue();
    }

    @Override
    public boolean isSuicide() {
        return this.suicide.getValue();
    }

    @Override
    public boolean canBigCalcsBeImproved() {
        return this.improveCalcs.getValue();
    }
}

