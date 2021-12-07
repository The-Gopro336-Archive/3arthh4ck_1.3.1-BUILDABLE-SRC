/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemPickaxe
 *  net.minecraft.util.math.BlockPos
 *  org.lwjgl.input.Mouse
 */
package me.earth.earthhack.impl.modules.combat.autocrystal;

import java.awt.Color;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.gui.visibility.PageBuilder;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypass;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystalData;
import me.earth.earthhack.impl.modules.combat.autocrystal.HelperBreak;
import me.earth.earthhack.impl.modules.combat.autocrystal.HelperBreakMotion;
import me.earth.earthhack.impl.modules.combat.autocrystal.HelperLiquids;
import me.earth.earthhack.impl.modules.combat.autocrystal.HelperObby;
import me.earth.earthhack.impl.modules.combat.autocrystal.HelperPlace;
import me.earth.earthhack.impl.modules.combat.autocrystal.HelperRotation;
import me.earth.earthhack.impl.modules.combat.autocrystal.ListenerBlockChange;
import me.earth.earthhack.impl.modules.combat.autocrystal.ListenerBlockMulti;
import me.earth.earthhack.impl.modules.combat.autocrystal.ListenerCPlayers;
import me.earth.earthhack.impl.modules.combat.autocrystal.ListenerDestroyBlock;
import me.earth.earthhack.impl.modules.combat.autocrystal.ListenerDestroyEntities;
import me.earth.earthhack.impl.modules.combat.autocrystal.ListenerEntity;
import me.earth.earthhack.impl.modules.combat.autocrystal.ListenerExplosion;
import me.earth.earthhack.impl.modules.combat.autocrystal.ListenerGameLoop;
import me.earth.earthhack.impl.modules.combat.autocrystal.ListenerKeyboard;
import me.earth.earthhack.impl.modules.combat.autocrystal.ListenerMotion;
import me.earth.earthhack.impl.modules.combat.autocrystal.ListenerNoMotion;
import me.earth.earthhack.impl.modules.combat.autocrystal.ListenerPosLook;
import me.earth.earthhack.impl.modules.combat.autocrystal.ListenerPostPlace;
import me.earth.earthhack.impl.modules.combat.autocrystal.ListenerRender;
import me.earth.earthhack.impl.modules.combat.autocrystal.ListenerRenderEntities;
import me.earth.earthhack.impl.modules.combat.autocrystal.ListenerSound;
import me.earth.earthhack.impl.modules.combat.autocrystal.ListenerSpawnObject;
import me.earth.earthhack.impl.modules.combat.autocrystal.ListenerTick;
import me.earth.earthhack.impl.modules.combat.autocrystal.ListenerUseEntity;
import me.earth.earthhack.impl.modules.combat.autocrystal.ListenerWorldClient;
import me.earth.earthhack.impl.modules.combat.autocrystal.helpers.AntiTotemHelper;
import me.earth.earthhack.impl.modules.combat.autocrystal.helpers.DamageHelper;
import me.earth.earthhack.impl.modules.combat.autocrystal.helpers.DamageSyncHelper;
import me.earth.earthhack.impl.modules.combat.autocrystal.helpers.FakeCrystalRender;
import me.earth.earthhack.impl.modules.combat.autocrystal.helpers.ForceAntiTotemHelper;
import me.earth.earthhack.impl.modules.combat.autocrystal.helpers.IDHelper;
import me.earth.earthhack.impl.modules.combat.autocrystal.helpers.PositionHelper;
import me.earth.earthhack.impl.modules.combat.autocrystal.helpers.PositionHistoryHelper;
import me.earth.earthhack.impl.modules.combat.autocrystal.helpers.RotationCanceller;
import me.earth.earthhack.impl.modules.combat.autocrystal.helpers.ServerTimeHelper;
import me.earth.earthhack.impl.modules.combat.autocrystal.helpers.ThreadHelper;
import me.earth.earthhack.impl.modules.combat.autocrystal.helpers.WeaknessHelper;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACPages;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.AntiFriendPop;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.AntiWeakness;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.Attack;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.AutoSwitch;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.PreCalc;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.RenderDamage;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.RenderDamagePos;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.RotateMode;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.RotationThread;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.SwingTime;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.SwingType;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.Target;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.CrystalTimeStamp;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.RotationFunction;
import me.earth.earthhack.impl.util.helpers.blocks.modes.PlaceSwing;
import me.earth.earthhack.impl.util.helpers.blocks.modes.RayTraceMode;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.math.DiscreteTimer;
import me.earth.earthhack.impl.util.math.GuardTimer;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.CooldownBypass;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import me.earth.earthhack.impl.util.thread.ThreadUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Mouse;

public class AutoCrystal
extends Module {
    private static final ScheduledExecutorService EXECUTOR = ThreadUtil.newDaemonScheduledExecutor("AutoCrystal");
    private static final ModuleCache<PingBypass> PINGBYPASS = Caches.getModule(PingBypass.class);
    private static final AtomicBoolean ATOMIC_STARTED = new AtomicBoolean();
    private static boolean started;
    protected final Setting<ACPages> pages = this.register(new EnumSetting<ACPages>("Page", ACPages.Place));
    protected final Setting<Boolean> place = this.register(new BooleanSetting("Place", true));
    protected final Setting<Target> targetMode = this.register(new EnumSetting<Target>("Target", Target.Closest));
    protected final Setting<Float> placeRange = this.register(new NumberSetting<Float>("PlaceRange", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(6.0f)));
    protected final Setting<Float> placeTrace = this.register(new NumberSetting<Float>("PlaceTrace", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(6.0f)));
    protected final Setting<Float> minDamage = this.register(new NumberSetting<Float>("MinDamage", Float.valueOf(6.0f), Float.valueOf(0.1f), Float.valueOf(20.0f)));
    protected final Setting<Integer> placeDelay = this.register(new NumberSetting<Integer>("PlaceDelay", 25, 0, 500));
    protected final Setting<Float> maxSelfPlace = this.register(new NumberSetting<Float>("MaxSelfPlace", Float.valueOf(9.0f), Float.valueOf(0.0f), Float.valueOf(20.0f)));
    protected final Setting<Integer> multiPlace = this.register(new NumberSetting<Integer>("MultiPlace", 1, 1, 5));
    protected final Setting<Float> slowPlaceDmg = this.register(new NumberSetting<Float>("SlowPlace", Float.valueOf(4.0f), Float.valueOf(0.1f), Float.valueOf(20.0f)));
    protected final Setting<Integer> slowPlaceDelay = this.register(new NumberSetting<Integer>("SlowPlaceDelay", 500, 0, 500));
    protected final Setting<Boolean> override = this.register(new BooleanSetting("OverridePlace", false));
    protected final Setting<Boolean> newVer = this.register(new BooleanSetting("1.13+", false));
    protected final Setting<Boolean> newVerEntities = this.register(new BooleanSetting("1.13-Entities", false));
    protected final Setting<SwingTime> placeSwing = this.register(new EnumSetting<SwingTime>("PlaceSwing", SwingTime.Post));
    protected final Setting<Boolean> smartTrace = this.register(new BooleanSetting("Smart-Trace", false));
    protected final Setting<Double> traceWidth = this.register(new NumberSetting<Double>("TraceWidth", -1.0, -1.0, 1.0));
    protected final Setting<Boolean> fallbackTrace = this.register(new BooleanSetting("Fallback-Trace", true));
    protected final Setting<Integer> simulatePlace = this.register(new NumberSetting<Integer>("Simulate-Place", 0, 0, 10));
    protected final Setting<Attack> attackMode = this.register(new EnumSetting<Attack>("Attack", Attack.Crystal));
    protected final Setting<Boolean> attack = this.register(new BooleanSetting("Break", true));
    protected final Setting<Float> breakRange = this.register(new NumberSetting<Float>("BreakRange", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(6.0f)));
    protected final Setting<Integer> breakDelay = this.register(new NumberSetting<Integer>("BreakDelay", 25, 0, 500));
    protected final Setting<Float> breakTrace = this.register(new NumberSetting<Float>("BreakTrace", Float.valueOf(3.0f), Float.valueOf(0.0f), Float.valueOf(6.0f)));
    protected final Setting<Float> minBreakDamage = this.register(new NumberSetting<Float>("MinBreakDmg", Float.valueOf(2.0f), Float.valueOf(0.0f), Float.valueOf(20.0f)));
    protected final Setting<Float> maxSelfBreak = this.register(new NumberSetting<Float>("MaxSelfBreak", Float.valueOf(10.0f), Float.valueOf(0.0f), Float.valueOf(20.0f)));
    protected final Setting<Float> slowBreakDamage = this.register(new NumberSetting<Float>("SlowBreak", Float.valueOf(3.0f), Float.valueOf(0.1f), Float.valueOf(20.0f)));
    protected final Setting<Integer> slowBreakDelay = this.register(new NumberSetting<Integer>("SlowBreakDelay", 500, 0, 500));
    protected final Setting<Boolean> instant = this.register(new BooleanSetting("Instant", false));
    protected final Setting<Boolean> asyncCalc = this.register(new BooleanSetting("Async-Calc", false));
    protected final Setting<Boolean> alwaysCalc = this.register(new BooleanSetting("Always-Calc", false));
    protected final Setting<Integer> packets = this.register(new NumberSetting<Integer>("Packets", 1, 1, 5));
    protected final Setting<Boolean> overrideBreak = this.register(new BooleanSetting("OverrideBreak", false));
    protected final Setting<AntiWeakness> antiWeakness = this.register(new EnumSetting<AntiWeakness>("AntiWeakness", AntiWeakness.None));
    protected final Setting<Boolean> instantAntiWeak = this.register(new BooleanSetting("AW-Instant", true));
    protected final Setting<Boolean> efficient = this.register(new BooleanSetting("Efficient", true));
    protected final Setting<Boolean> manually = this.register(new BooleanSetting("Manually", true));
    protected final Setting<Integer> manualDelay = this.register(new NumberSetting<Integer>("ManualDelay", 500, 0, 500));
    protected final Setting<SwingTime> breakSwing = this.register(new EnumSetting<SwingTime>("BreakSwing", SwingTime.Post));
    protected final Setting<ACRotate> rotate = this.register(new EnumSetting<ACRotate>("Rotate", ACRotate.None));
    protected final Setting<RotateMode> rotateMode = this.register(new EnumSetting<RotateMode>("Rotate-Mode", RotateMode.Normal));
    protected final Setting<Float> smoothSpeed = this.register(new NumberSetting<Float>("Smooth-Speed", Float.valueOf(0.5f), Float.valueOf(0.1f), Float.valueOf(2.0f)));
    protected final Setting<Integer> endRotations = this.register(new NumberSetting<Integer>("End-Rotations", 250, 0, 1000));
    protected final Setting<Float> angle = this.register(new NumberSetting<Float>("Break-Angle", Float.valueOf(180.0f), Float.valueOf(0.1f), Float.valueOf(180.0f)));
    protected final Setting<Float> placeAngle = this.register(new NumberSetting<Float>("Place-Angle", Float.valueOf(180.0f), Float.valueOf(0.1f), Float.valueOf(180.0f)));
    protected final Setting<Float> height = this.register(new NumberSetting<Float>("Height", Float.valueOf(0.05f), Float.valueOf(0.0f), Float.valueOf(1.0f)));
    protected final Setting<Double> placeHeight = this.register(new NumberSetting<Double>("Place-Height", 1.0, 0.0, 1.0));
    protected final Setting<Integer> rotationTicks = this.register(new NumberSetting<Integer>("Rotations-Existed", 0, 0, 500));
    protected final Setting<Boolean> focusRotations = this.register(new BooleanSetting("Focus-Rotations", false));
    protected final Setting<Boolean> focusAngleCalc = this.register(new BooleanSetting("FocusRotationCompare", false));
    protected final Setting<Double> focusExponent = this.register(new NumberSetting<Double>("FocusExponent", 0.0, 0.0, 10.0));
    protected final Setting<Double> focusDiff = this.register(new NumberSetting<Double>("FocusDiff", 0.0, 0.0, 180.0));
    protected final Setting<Double> rotationExponent = this.register(new NumberSetting<Double>("RotationExponent", 0.0, 0.0, 10.0));
    protected final Setting<Double> minRotDiff = this.register(new NumberSetting<Double>("MinRotationDiff", 0.0, 0.0, 180.0));
    protected final Setting<Integer> existed = this.register(new NumberSetting<Integer>("Existed", 0, 0, 500));
    protected final Setting<Boolean> pingExisted = this.register(new BooleanSetting("Ping-Existed", false));
    protected final Setting<Float> targetRange = this.register(new NumberSetting<Float>("TargetRange", Float.valueOf(12.0f), Float.valueOf(0.1f), Float.valueOf(20.0f)));
    protected final Setting<Float> pbTrace = this.register(new NumberSetting<Float>("CombinedTrace", Float.valueOf(3.0f), Float.valueOf(0.0f), Float.valueOf(6.0f)));
    protected final Setting<Float> range = this.register(new NumberSetting<Float>("Range", Float.valueOf(12.0f), Float.valueOf(0.1f), Float.valueOf(20.0f)));
    protected final Setting<Boolean> suicide = this.register(new BooleanSetting("Suicide", false));
    protected final Setting<Boolean> multiTask = this.register(new BooleanSetting("MultiTask", true));
    protected final Setting<Boolean> multiPlaceCalc = this.register(new BooleanSetting("MultiPlace-Calc", true));
    protected final Setting<Boolean> multiPlaceMinDmg = this.register(new BooleanSetting("MultiPlace-MinDmg", true));
    protected final Setting<Boolean> yCalc = this.register(new BooleanSetting("Y-Calc", false));
    protected final Setting<Boolean> dangerSpeed = this.register(new BooleanSetting("Danger-Speed", false));
    protected final Setting<Float> dangerHealth = this.register(new NumberSetting<Float>("Danger-Health", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(36.0f)));
    protected final Setting<Integer> cooldown = this.register(new NumberSetting<Integer>("CoolDown", 500, 0, 500));
    protected final Setting<Integer> placeCoolDown = this.register(new NumberSetting<Integer>("PlaceCooldown", 0, 0, 500));
    protected final Setting<AntiFriendPop> antiFriendPop = this.register(new EnumSetting<AntiFriendPop>("AntiFriendPop", AntiFriendPop.None));
    protected final Setting<Boolean> antiFeetPlace = this.register(new BooleanSetting("AntiFeetPlace", false));
    protected final Setting<Integer> feetBuffer = this.register(new NumberSetting<Integer>("FeetBuffer", 5, 0, 50));
    protected final Setting<Boolean> motionCalc = this.register(new BooleanSetting("Motion-Calc", false));
    protected final Setting<Boolean> holdFacePlace = this.register(new BooleanSetting("HoldFacePlace", false));
    protected final Setting<Float> facePlace = this.register(new NumberSetting<Float>("FacePlace", Float.valueOf(10.0f), Float.valueOf(0.0f), Float.valueOf(36.0f)));
    protected final Setting<Float> minFaceDmg = this.register(new NumberSetting<Float>("Min-FP", Float.valueOf(2.0f), Float.valueOf(0.0f), Float.valueOf(5.0f)));
    protected final Setting<Float> armorPlace = this.register(new NumberSetting<Float>("ArmorPlace", Float.valueOf(5.0f), Float.valueOf(0.0f), Float.valueOf(100.0f)));
    protected final Setting<Boolean> pickAxeHold = this.register(new BooleanSetting("PickAxe-Hold", false));
    protected final Setting<Boolean> antiNaked = this.register(new BooleanSetting("AntiNaked", false));
    protected final Setting<Boolean> fallBack = this.register(new BooleanSetting("FallBack", true));
    protected final Setting<Float> fallBackDiff = this.register(new NumberSetting<Float>("Fallback-Difference", Float.valueOf(10.0f), Float.valueOf(0.0f), Float.valueOf(16.0f)));
    protected final Setting<Float> fallBackDmg = this.register(new NumberSetting<Float>("FallBackDmg", Float.valueOf(3.0f), Float.valueOf(0.0f), Float.valueOf(6.0f)));
    protected final Setting<AutoSwitch> autoSwitch = this.register(new EnumSetting<AutoSwitch>("AutoSwitch", AutoSwitch.Bind));
    protected final Setting<Boolean> mainHand = this.register(new BooleanSetting("MainHand", false));
    protected final Setting<Bind> switchBind = this.register(new BindSetting("SwitchBind", Bind.none()));
    protected final Setting<Boolean> switchBack = this.register(new BooleanSetting("SwitchBack", true));
    protected final Setting<Boolean> useAsOffhand = this.register(new BooleanSetting("UseAsOffHandBind", false));
    protected final Setting<Boolean> instantOffhand = this.register(new BooleanSetting("Instant-Offhand", true));
    protected final Setting<Boolean> pingBypass = this.register(new BooleanSetting("PingBypass", true));
    protected final Setting<SwingType> swing = this.register(new EnumSetting<SwingType>("BreakHand", SwingType.MainHand));
    protected final Setting<SwingType> placeHand = this.register(new EnumSetting<SwingType>("PlaceHand", SwingType.MainHand));
    protected final Setting<CooldownBypass> cooldownBypass = this.register(new EnumSetting<CooldownBypass>("CooldownBypass", CooldownBypass.None));
    protected final Setting<CooldownBypass> obsidianBypass = this.register(new EnumSetting<CooldownBypass>("ObsidianBypass", CooldownBypass.None));
    protected final Setting<CooldownBypass> antiWeaknessBypass = this.register(new EnumSetting<CooldownBypass>("AntiWeaknessBypass", CooldownBypass.None));
    protected final Setting<CooldownBypass> mineBypass = this.register(new EnumSetting<CooldownBypass>("MineBypass", CooldownBypass.None));
    protected final Setting<SwingType> obbyHand = this.register(new EnumSetting<SwingType>("ObbyHand", SwingType.MainHand));
    protected final Setting<Boolean> render = this.register(new BooleanSetting("Render", true));
    protected final Setting<Integer> renderTime = this.register(new NumberSetting<Integer>("Render-Time", 600, 0, 5000));
    protected final Setting<Boolean> box = this.register(new BooleanSetting("Draw-Box", true));
    protected final Setting<Color> boxColor = this.register(new ColorSetting("Box", new Color(255, 255, 255, 120)));
    protected final Setting<Color> outLine = this.register(new ColorSetting("Outline", new Color(255, 255, 255, 240)));
    protected final Setting<Color> indicatorColor = this.register(new ColorSetting("IndicatorColor", new Color(190, 5, 5, 255)));
    protected final Setting<Boolean> fade = this.register(new BooleanSetting("Fade", true));
    protected final Setting<Integer> fadeTime = this.register(new NumberSetting<Integer>("Fade-Time", 1000, 0, 5000));
    protected final Setting<Boolean> realtime = this.register(new BooleanSetting("Realtime", false));
    protected final Setting<RenderDamagePos> renderDamage = this.register(new EnumSetting<RenderDamagePos>("DamageRender", RenderDamagePos.None));
    protected final Setting<RenderDamage> renderMode = this.register(new EnumSetting<RenderDamage>("DamageMode", RenderDamage.Normal));
    protected final Setting<Boolean> setDead = this.register(new BooleanSetting("SetDead", false));
    protected final Setting<Boolean> instantSetDead = this.register(new BooleanSetting("Instant-Dead", false));
    protected final Setting<Boolean> pseudoSetDead = this.register(new BooleanSetting("Pseudo-Dead", false));
    protected final Setting<Boolean> simulateExplosion = this.register(new BooleanSetting("SimulateExplosion", false));
    protected final Setting<Boolean> soundRemove = this.register(new BooleanSetting("SoundRemove", true));
    protected final Setting<Integer> deathTime = this.register(new NumberSetting<Integer>("Death-Time", 0, 0, 500));
    protected final Setting<Boolean> obsidian = this.register(new BooleanSetting("Obsidian", false));
    protected final Setting<Boolean> obbySwitch = this.register(new BooleanSetting("Obby-Switch", false));
    protected final Setting<Integer> obbyDelay = this.register(new NumberSetting<Integer>("ObbyDelay", 500, 0, 5000));
    protected final Setting<Integer> obbyCalc = this.register(new NumberSetting<Integer>("ObbyCalc", 500, 0, 5000));
    protected final Setting<Integer> helpingBlocks = this.register(new NumberSetting<Integer>("HelpingBlocks", 1, 0, 5));
    protected final Setting<Float> obbyMinDmg = this.register(new NumberSetting<Float>("Obby-MinDamage", Float.valueOf(7.0f), Float.valueOf(0.1f), Float.valueOf(36.0f)));
    protected final Setting<Boolean> terrainCalc = this.register(new BooleanSetting("TerrainCalc", true));
    protected final Setting<Boolean> obbySafety = this.register(new BooleanSetting("ObbySafety", false));
    protected final Setting<RayTraceMode> obbyTrace = this.register(new EnumSetting<RayTraceMode>("Obby-Raytrace", RayTraceMode.Fast));
    protected final Setting<Boolean> obbyTerrain = this.register(new BooleanSetting("Obby-Terrain", true));
    protected final Setting<Boolean> obbyPreSelf = this.register(new BooleanSetting("Obby-PreSelf", true));
    protected final Setting<Integer> fastObby = this.register(new NumberSetting<Integer>("Fast-Obby", 0, 0, 3));
    protected final Setting<Integer> maxDiff = this.register(new NumberSetting<Integer>("Max-Difference", 1, 0, 5));
    protected final Setting<Double> maxDmgDiff = this.register(new NumberSetting<Double>("Max-DamageDiff", 0.0, 0.0, 10.0));
    protected final Setting<Boolean> setState = this.register(new BooleanSetting("Client-Blocks", false));
    protected final Setting<PlaceSwing> obbySwing = this.register(new EnumSetting<PlaceSwing>("Obby-Swing", PlaceSwing.Once));
    protected final Setting<Boolean> obbyFallback = this.register(new BooleanSetting("Obby-Fallback", false));
    protected final Setting<Rotate> obbyRotate = this.register(new EnumSetting<Rotate>("Obby-Rotate", Rotate.None));
    protected final Setting<Boolean> interact = this.register(new BooleanSetting("Interact", false));
    protected final Setting<Boolean> inside = this.register(new BooleanSetting("Inside", false));
    protected final Setting<Boolean> lava = this.register(new BooleanSetting("Lava", false));
    protected final Setting<Boolean> water = this.register(new BooleanSetting("Water", false));
    protected final Setting<Boolean> liquidObby = this.register(new BooleanSetting("LiquidObby", false));
    protected final Setting<Boolean> liquidRayTrace = this.register(new BooleanSetting("LiquidRayTrace", false));
    protected final Setting<Integer> liqDelay = this.register(new NumberSetting<Integer>("LiquidDelay", 500, 0, 1000));
    protected final Setting<Rotate> liqRotate = this.register(new EnumSetting<Rotate>("LiquidRotate", Rotate.None));
    protected final Setting<Boolean> pickaxeOnly = this.register(new BooleanSetting("PickaxeOnly", false));
    protected final Setting<Boolean> interruptSpeedmine = this.register(new BooleanSetting("InterruptSpeedmine", false));
    protected final Setting<Boolean> setAir = this.register(new BooleanSetting("SetAir", true));
    protected final Setting<Boolean> absorb = this.register(new BooleanSetting("Absorb", false));
    protected final Setting<Boolean> requireOnGround = this.register(new BooleanSetting("RequireOnGround", true));
    protected final Setting<Boolean> ignoreLavaItems = this.register(new BooleanSetting("IgnoreLavaItems", false));
    protected final Setting<Boolean> sponges = this.register(new BooleanSetting("Sponges", false));
    protected final Setting<Boolean> antiTotem = this.register(new BooleanSetting("AntiTotem", false));
    protected final Setting<Float> totemHealth = this.register(new NumberSetting<Float>("Totem-Health", Float.valueOf(1.5f), Float.valueOf(0.0f), Float.valueOf(10.0f)));
    protected final Setting<Float> minTotemOffset = this.register(new NumberSetting<Float>("Min-Offset", Float.valueOf(0.5f), Float.valueOf(0.0f), Float.valueOf(5.0f)));
    protected final Setting<Float> maxTotemOffset = this.register(new NumberSetting<Float>("Max-Offset", Float.valueOf(2.0f), Float.valueOf(0.0f), Float.valueOf(5.0f)));
    protected final Setting<Float> popDamage = this.register(new NumberSetting<Float>("Pop-Damage", Float.valueOf(12.0f), Float.valueOf(10.0f), Float.valueOf(20.0f)));
    protected final Setting<Boolean> totemSync = this.register(new BooleanSetting("TotemSync", true));
    protected final Setting<Boolean> forceAntiTotem = this.register(new BooleanSetting("Force-AntiTotem", false));
    protected final Setting<Boolean> forceSlow = this.register(new BooleanSetting("Force-Slow", false));
    protected final Setting<Boolean> syncForce = this.register(new BooleanSetting("Sync-Force", true));
    protected final Setting<Boolean> dangerForce = this.register(new BooleanSetting("Danger-Force", false));
    protected final Setting<Integer> forcePlaceConfirm = this.register(new NumberSetting<Integer>("Force-Place", 100, 0, 500));
    protected final Setting<Integer> forceBreakConfirm = this.register(new NumberSetting<Integer>("Force-Break", 100, 0, 500));
    protected final Setting<Integer> attempts = this.register(new NumberSetting<Integer>("Attempts", 500, 0, 10000));
    protected final Setting<Boolean> damageSync = this.register(new BooleanSetting("DamageSync", false));
    protected final Setting<Boolean> preSynCheck = this.register(new BooleanSetting("Pre-SyncCheck", false));
    protected final Setting<Boolean> discreteSync = this.register(new BooleanSetting("Discrete-Sync", false));
    protected final Setting<Boolean> dangerSync = this.register(new BooleanSetting("Danger-Sync", false));
    protected final Setting<Integer> placeConfirm = this.register(new NumberSetting<Integer>("Place-Confirm", 250, 0, 500));
    protected final Setting<Integer> breakConfirm = this.register(new NumberSetting<Integer>("Break-Confirm", 250, 0, 500));
    protected final Setting<Integer> syncDelay = this.register(new NumberSetting<Integer>("SyncDelay", 500, 0, 500));
    protected final Setting<Boolean> surroundSync = this.register(new BooleanSetting("SurroundSync", true));
    protected final Setting<Integer> bExtrapol = this.register(new NumberSetting<Integer>("BreakExtrapolation", 0, 0, 50));
    protected final Setting<Integer> placeExtrapolation = this.register(new NumberSetting<Integer>("PlaceExtrapolation", 0, 0, 50));
    protected final Setting<Boolean> selfExtrapolation = this.register(new BooleanSetting("SelfExtrapolation", false));
    protected final Setting<Boolean> fullExtrapol = this.register(new BooleanSetting("Full-Extrapolation", false));
    protected final Setting<Boolean> idPredict = this.register(new BooleanSetting("ID-Predict", false));
    protected final Setting<Integer> idOffset = this.register(new NumberSetting<Integer>("ID-Offset", 1, 1, 10));
    protected final Setting<Integer> idDelay = this.register(new NumberSetting<Integer>("ID-Delay", 0, 0, 500));
    protected final Setting<Integer> idPackets = this.register(new NumberSetting<Integer>("ID-Packets", 1, 1, 10));
    protected final Setting<Boolean> godAntiTotem = this.register(new BooleanSetting("God-AntiTotem", false));
    protected final Setting<Boolean> holdingCheck = this.register(new BooleanSetting("Holding-Check", true));
    protected final Setting<Boolean> toolCheck = this.register(new BooleanSetting("Tool-Check", true));
    protected final Setting<PlaceSwing> godSwing = this.register(new EnumSetting<PlaceSwing>("God-Swing", PlaceSwing.Once));
    protected final Setting<PreCalc> preCalc = this.register(new EnumSetting<PreCalc>("Pre-Calc", PreCalc.None));
    protected final Setting<Float> preCalcDamage = this.register(new NumberSetting<Float>("Pre-CalcDamage", Float.valueOf(15.0f), Float.valueOf(0.0f), Float.valueOf(36.0f)));
    protected final Setting<Boolean> multiThread = this.register(new BooleanSetting("MultiThread", false));
    protected final Setting<Boolean> smartPost = this.register(new BooleanSetting("Smart-Post", true));
    protected final Setting<RotationThread> rotationThread = this.register(new EnumSetting<RotationThread>("RotationThread", RotationThread.Predict));
    protected final Setting<Float> partial = this.register(new NumberSetting<Float>("Partial", Float.valueOf(0.8f), Float.valueOf(0.0f), Float.valueOf(1.0f)));
    protected final Setting<Integer> maxCancel = this.register(new NumberSetting<Integer>("MaxCancel", 10, 1, 50));
    protected final Setting<Integer> timeOut = this.register(new NumberSetting<Integer>("Wait", 2, 1, 10));
    protected final Setting<Boolean> blockDestroyThread = this.register(new BooleanSetting("BlockDestroyThread", false));
    protected final Setting<Integer> threadDelay = this.register(new NumberSetting<Integer>("ThreadDelay", 25, 0, 100));
    protected final Setting<Integer> tickThreshold = this.register(new NumberSetting<Integer>("TickThreshold", 5, 1, 20));
    protected final Setting<Integer> preSpawn = this.register(new NumberSetting<Integer>("PreSpawn", 3, 1, 20));
    protected final Setting<Integer> maxEarlyThread = this.register(new NumberSetting<Integer>("MaxEarlyThread", 8, 1, 20));
    protected final Setting<Boolean> explosionThread = this.register(new BooleanSetting("ExplosionThread", false));
    protected final Setting<Boolean> soundThread = this.register(new BooleanSetting("SoundThread", false));
    protected final Setting<Boolean> entityThread = this.register(new BooleanSetting("EntityThread", false));
    protected final Setting<Boolean> spawnThread = this.register(new BooleanSetting("SpawnThread", false));
    protected final Setting<Boolean> destroyThread = this.register(new BooleanSetting("DestroyThread", false));
    protected final Setting<Boolean> serverThread = this.register(new BooleanSetting("ServerThread", false));
    protected final Setting<Boolean> asyncServerThread = this.register(new BooleanSetting("AsyncServerThread", false));
    protected final Setting<Boolean> earlyFeetThread = this.register(new BooleanSetting("EarlyFeetThread", false));
    protected final Setting<Boolean> lateBreakThread = this.register(new BooleanSetting("LateBreakThread", false));
    protected final Setting<Boolean> motionThread = this.register(new BooleanSetting("MotionThread", true));
    protected final Setting<Boolean> blockChangeThread = this.register(new BooleanSetting("BlockChangeThread", false));
    protected final Setting<Integer> priority = this.register(new NumberSetting<Integer>("Priority", 1500, Integer.MIN_VALUE, Integer.MAX_VALUE));
    protected final Setting<Boolean> spectator = this.register(new BooleanSetting("Spectator", false));
    protected final Setting<Boolean> clearPost = this.register(new BooleanSetting("ClearPost", true));
    protected final Setting<Integer> removeTime = this.register(new NumberSetting<Integer>("Remove-Time", 1000, 0, 2500));
    protected final Map<BlockPos, CrystalTimeStamp> placed = new ConcurrentHashMap<BlockPos, CrystalTimeStamp>();
    protected final ListenerSound soundObserver = new ListenerSound(this);
    protected final AtomicInteger motionID = new AtomicInteger();
    protected final DiscreteTimer placeTimer = new GuardTimer(1000L, 5L).reset(this.placeDelay.getValue().intValue());
    protected final DiscreteTimer breakTimer = new GuardTimer(1000L, 5L).reset(this.breakDelay.getValue().intValue());
    protected final StopWatch renderTimer = new StopWatch();
    protected final StopWatch obbyTimer = new StopWatch();
    protected final StopWatch obbyCalcTimer = new StopWatch();
    protected final StopWatch targetTimer = new StopWatch();
    protected final StopWatch cTargetTimer = new StopWatch();
    protected final StopWatch forceTimer = new StopWatch();
    protected final StopWatch liquidTimer = new StopWatch();
    protected final Queue<Runnable> post = new ConcurrentLinkedQueue<Runnable>();
    protected volatile RotationFunction rotation;
    protected EntityPlayer target;
    protected Entity crystal;
    protected Entity focus;
    protected BlockPos renderPos;
    protected boolean switching;
    protected boolean isSpoofing;
    protected boolean noGod;
    protected String damage;
    protected final PositionHelper positionHelper = new PositionHelper(this);
    protected final IDHelper idHelper = new IDHelper();
    protected final HelperLiquids liquidHelper = new HelperLiquids();
    protected final PositionHistoryHelper positionHistoryHelper = new PositionHistoryHelper();
    protected final HelperPlace placeHelper = new HelperPlace(this);
    protected final HelperBreak breakHelper = new HelperBreak(this);
    protected final HelperObby obbyHelper = new HelperObby(this);
    protected final HelperBreakMotion breakHelperMotion = new HelperBreakMotion(this);
    protected final AntiTotemHelper antiTotemHelper = new AntiTotemHelper(this.totemHealth);
    protected final WeaknessHelper weaknessHelper = new WeaknessHelper(this.antiWeakness, this.cooldown);
    protected final RotationCanceller rotationCanceller = new RotationCanceller(this, this.maxCancel);
    protected final ThreadHelper threadHelper = new ThreadHelper(this, this.multiThread, this.threadDelay, this.rotationThread, this.rotate);
    protected final DamageHelper damageHelper = new DamageHelper(this.positionHelper, this.terrainCalc, this.placeExtrapolation, this.bExtrapol, this.selfExtrapolation, this.obbyTerrain);
    protected final DamageSyncHelper damageSyncHelper = new DamageSyncHelper(Bus.EVENT_BUS, this.discreteSync, this.syncDelay, this.dangerSync);
    protected final ForceAntiTotemHelper forceHelper = new ForceAntiTotemHelper(Bus.EVENT_BUS, this.discreteSync, this.syncDelay, this.forcePlaceConfirm, this.forceBreakConfirm, this.dangerForce);
    protected final FakeCrystalRender crystalRender = new FakeCrystalRender(this.simulatePlace);
    protected final HelperRotation rotationHelper = new HelperRotation(this);
    protected final ServerTimeHelper serverTimeHelper = new ServerTimeHelper(this, this.rotate, this.placeSwing, this.antiFeetPlace, this.newVer, this.feetBuffer);

    public AutoCrystal() {
        super("AutoCrystal", Category.Combat);
        Bus.EVENT_BUS.subscribe(this.positionHistoryHelper);
        Bus.EVENT_BUS.subscribe(this.idHelper);
        this.listeners.add(new ListenerBlockChange(this));
        this.listeners.add(new ListenerBlockMulti(this));
        this.listeners.add(new ListenerDestroyEntities(this));
        this.listeners.add(new ListenerExplosion(this));
        this.listeners.add(new ListenerGameLoop(this));
        this.listeners.add(new ListenerKeyboard(this));
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerNoMotion(this));
        this.listeners.add(new ListenerPosLook(this));
        this.listeners.add(new ListenerPostPlace(this));
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerRenderEntities(this));
        this.listeners.add(new ListenerSpawnObject(this));
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerWorldClient(this));
        this.listeners.add(new ListenerDestroyBlock(this));
        this.listeners.add(new ListenerUseEntity(this));
        this.listeners.addAll(new ListenerCPlayers(this).getListeners());
        this.listeners.addAll(new ListenerEntity(this).getListeners());
        new PageBuilder<ACPages>(this, this.pages).addPage(p -> p == ACPages.Place, (Setting<?>)this.place, (Setting<?>)this.simulatePlace).addPage(p -> p == ACPages.Break, (Setting<?>)this.attackMode, (Setting<?>)this.breakSwing).addPage(p -> p == ACPages.Rotate, (Setting<?>)this.rotate, (Setting<?>)this.pingExisted).addPage(p -> p == ACPages.Misc, (Setting<?>)this.targetRange, (Setting<?>)this.motionCalc).addPage(p -> p == ACPages.FacePlace, (Setting<?>)this.holdFacePlace, (Setting<?>)this.fallBackDmg).addPage(p -> p == ACPages.Switch, (Setting<?>)this.autoSwitch, (Setting<?>)this.obbyHand).addPage(p -> p == ACPages.Render, (Setting<?>)this.render, (Setting<?>)this.renderMode).addPage(p -> p == ACPages.SetDead, (Setting<?>)this.setDead, (Setting<?>)this.deathTime).addPage(p -> p == ACPages.Obsidian, (Setting<?>)this.obsidian, (Setting<?>)this.obbyRotate).addPage(p -> p == ACPages.Liquids, (Setting<?>)this.interact, (Setting<?>)this.sponges).addPage(p -> p == ACPages.AntiTotem, (Setting<?>)this.antiTotem, (Setting<?>)this.attempts).addPage(p -> p == ACPages.DamageSync, (Setting<?>)this.damageSync, (Setting<?>)this.surroundSync).addPage(p -> p == ACPages.Extrapolation, (Setting<?>)this.bExtrapol, (Setting<?>)this.fullExtrapol).addPage(p -> p == ACPages.GodModule, (Setting<?>)this.idPredict, (Setting<?>)this.godSwing).addPage(p -> p == ACPages.MultiThread, (Setting<?>)this.preCalc, (Setting<?>)this.blockChangeThread).addPage(p -> p == ACPages.Development, (Setting<?>)this.priority, (Setting<?>)this.removeTime).register(Visibilities.VISIBILITY_MANAGER);
        this.priority.addObserver(e -> {
            if (Bus.EVENT_BUS.isSubscribed(this)) {
                Bus.EVENT_BUS.unsubscribe(this);
                Bus.EVENT_BUS.subscribe(this);
            }
        });
        this.setData(new AutoCrystalData(this));
    }

    @Override
    protected void onEnable() {
        this.reset();
        Managers.SET_DEAD.addObserver(this.soundObserver);
    }

    @Override
    protected void onDisable() {
        Managers.SET_DEAD.removeObserver(this.soundObserver);
        this.reset();
    }

    @Override
    public String getDisplayInfo() {
        if (this.switching) {
            return "\u00a7aSwitching";
        }
        EntityPlayer t = this.getTarget();
        return t == null ? null : t.getName();
    }

    public void setRenderPos(BlockPos pos, float damage) {
        this.setRenderPos(pos, MathUtil.round(damage, 1) + "");
    }

    public void setRenderPos(BlockPos pos, String text) {
        this.renderTimer.reset();
        this.renderPos = pos;
        this.damage = text;
    }

    public BlockPos getRenderPos() {
        if (this.renderTimer.passed(this.renderTime.getValue().intValue())) {
            this.renderPos = null;
        }
        return this.renderPos;
    }

    public void setTarget(EntityPlayer target) {
        this.targetTimer.reset();
        this.target = target;
    }

    public EntityPlayer getTarget() {
        if (this.targetTimer.passed(600L)) {
            this.target = null;
        }
        return this.target;
    }

    public void setCrystal(Entity crystal) {
        if (this.focusRotations.getValue().booleanValue() && !this.rotate.getValue().noRotate(ACRotate.Break)) {
            this.focus = crystal;
        }
        this.cTargetTimer.reset();
        this.crystal = crystal;
    }

    public Entity getCrystal() {
        if (this.cTargetTimer.passed(600L)) {
            this.crystal = null;
        }
        return this.crystal;
    }

    public boolean isPingBypass() {
        return this.pingBypass.getValue() != false && PINGBYPASS.isEnabled();
    }

    public float getMinDamage() {
        return this.holdFacePlace.getValue() != false && AutoCrystal.mc.currentScreen == null && Mouse.isButtonDown((int)0) && (!(AutoCrystal.mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe) || this.pickAxeHold.getValue() != false) ? this.minFaceDmg.getValue().floatValue() : this.minDamage.getValue().floatValue();
    }

    public void runPost() {
        CollectionUtil.emptyQueue(this.post);
    }

    protected void reset() {
        this.target = null;
        this.crystal = null;
        this.renderPos = null;
        this.rotation = null;
        this.switching = false;
        this.post.clear();
        mc.addScheduledTask(this.crystalRender::clear);
        try {
            this.placed.clear();
            this.threadHelper.reset();
            this.rotationCanceller.reset();
            this.antiTotemHelper.setTarget(null);
            this.antiTotemHelper.setTargetPos(null);
            this.idHelper.setUpdated(false);
            this.idHelper.setHighestID(0);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    protected boolean shouldDanger() {
        return this.dangerSpeed.getValue() != false && (!Managers.SAFETY.isSafe() || EntityUtil.getHealth((EntityLivingBase)AutoCrystal.mc.player) < this.dangerHealth.getValue().floatValue());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected void checkExecutor() {
        if (started || !this.asyncServerThread.getValue().booleanValue() || !this.serverThread.getValue().booleanValue() || !this.multiThread.getValue().booleanValue() || this.rotate.getValue() != ACRotate.None) return;
        Class<AutoCrystal> class_ = AutoCrystal.class;
        synchronized (AutoCrystal.class) {
            if (ATOMIC_STARTED.get()) return;
            this.startExecutor();
            ATOMIC_STARTED.set(true);
            started = true;
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return;
        }
    }

    private void startExecutor() {
        EXECUTOR.scheduleAtFixedRate(this::doExecutorTick, 0L, 1L, TimeUnit.MILLISECONDS);
    }

    private void doExecutorTick() {
        if (this.isEnabled() && AutoCrystal.mc.player != null && AutoCrystal.mc.world != null && this.asyncServerThread.getValue().booleanValue() && this.rotate.getValue() == ACRotate.None && this.serverThread.getValue().booleanValue() && this.multiThread.getValue().booleanValue()) {
            if (Managers.TICK.valid(Managers.TICK.getTickTimeAdjusted(), Managers.TICK.normalize(Managers.TICK.getSpawnTime() - this.tickThreshold.getValue()), Managers.TICK.normalize(Managers.TICK.getSpawnTime() - this.preSpawn.getValue()))) {
                if (!this.earlyFeetThread.getValue().booleanValue()) {
                    this.threadHelper.startThread(new BlockPos[0]);
                } else if (this.lateBreakThread.getValue().booleanValue()) {
                    this.threadHelper.startThread(true, false, new BlockPos[0]);
                }
            } else {
                EntityPlayer closest = EntityUtil.getClosestEnemy();
                if (closest != null && BlockUtil.isSemiSafe(closest, true, this.newVer.getValue()) && BlockUtil.canBeFeetPlaced(closest, true, this.newVer.getValue()) && this.earlyFeetThread.getValue().booleanValue() && Managers.TICK.valid(Managers.TICK.getTickTimeAdjusted(), 0, this.maxEarlyThread.getValue())) {
                    this.threadHelper.startThread(false, true, new BlockPos[0]);
                }
            }
        }
    }
}

