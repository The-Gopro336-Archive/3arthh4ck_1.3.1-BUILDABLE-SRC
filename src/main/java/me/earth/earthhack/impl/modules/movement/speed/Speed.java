package me.earth.earthhack.impl.modules.movement.speed;

import java.util.List;
import java.util.Objects;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.longjump.LongJump;
import me.earth.earthhack.impl.modules.movement.speed.ListenerBlockPush;
import me.earth.earthhack.impl.modules.movement.speed.ListenerExplosion;
import me.earth.earthhack.impl.modules.movement.speed.ListenerMotion;
import me.earth.earthhack.impl.modules.movement.speed.ListenerMove;
import me.earth.earthhack.impl.modules.movement.speed.ListenerPosLook;
import me.earth.earthhack.impl.modules.movement.speed.ListenerVelocity;
import me.earth.earthhack.impl.modules.movement.speed.SpeedData;
import me.earth.earthhack.impl.modules.movement.speed.SpeedMode;
import me.earth.earthhack.impl.modules.movement.step.Step;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.init.MobEffects;

public class Speed
extends Module {
    private static final ModuleCache<Step> STEP = Caches.getModule(Step.class);
    protected final ModuleCache<LongJump> LONG_JUMP = Caches.getModule(LongJump.class);
    protected final Setting<SpeedMode> mode = this.register(new EnumSetting<SpeedMode>("Mode", SpeedMode.Instant));
    protected final Setting<Boolean> inWater = this.register(new BooleanSetting("InWater", false));
    protected final Setting<Double> strafeSpeed = this.register(new NumberSetting<Double>("StrafeSpeed", 0.2873, 0.1, 1.0));
    protected final Setting<Double> speedSet = this.register(new NumberSetting<Double>("Speed", 4.0, 0.1, 10.0));
    protected final Setting<Integer> constTicks = this.register(new NumberSetting<Integer>("ConstTicks", 10, 1, 40));
    protected final Setting<Integer> constOff = this.register(new NumberSetting<Integer>("ConstOff", 3, 1, 10));
    protected final Setting<Double> constFactor = this.register(new NumberSetting<Double>("ConstFactor", 2.149, 1.0, 5.0));
    protected final Setting<Boolean> useTimer = this.register(new BooleanSetting("UseTimer", false));
    protected final Setting<Boolean> explosions = this.register(new BooleanSetting("Explosions", false));
    protected final Setting<Boolean> velocity = this.register(new BooleanSetting("Velocity", false));
    protected final Setting<Float> multiplier = this.register(new NumberSetting<Float>("H-Factor", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(5.0f)));
    protected final Setting<Float> vertical = this.register(new NumberSetting<Float>("V-Factor", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(5.0f)));
    protected final Setting<Integer> coolDown = this.register(new NumberSetting<Integer>("CoolDown", 1000, 0, 5000));
    protected final Setting<Boolean> directional = this.register(new BooleanSetting("Directional", false));
    protected final Setting<Boolean> lagOut = this.register(new BooleanSetting("LagOutBlocks", false));
    protected final Setting<Integer> lagTime = this.register(new NumberSetting<Integer>("LagTime", 500, 0, 1000));
    protected final Setting<Double> cap = this.register(new NumberSetting<Double>("Cap", 10.0, 0.0, 10.0));
    protected final Setting<Boolean> scaleCap = this.register(new BooleanSetting("ScaleCap", false));
    protected final Setting<Boolean> slow = this.register(new BooleanSetting("Slowness", false));
    protected final Setting<Boolean> noWaterInstant = this.register(new BooleanSetting("NoLiquidInstant", false));
    protected final StopWatch expTimer = new StopWatch();
    protected boolean stop;
    protected int vanillaStage;
    protected int onGroundStage;
    protected int oldGroundStage;
    protected double speed;
    protected double distance;
    protected int gayStage;
    protected int stage;
    protected int ncpStage;
    protected int bhopStage;
    protected int vStage;
    protected int lowStage;
    protected int constStage;
    protected double lastExp;
    protected double lastDist;
    protected boolean boost;

    public Speed() {
        super("Speed", Category.Movement);
        this.listeners.add(new ListenerMove(this));
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerPosLook(this));
        this.listeners.add(new ListenerExplosion(this));
        this.listeners.add(new ListenerBlockPush(this));
        this.listeners.add(new ListenerVelocity(this));
        this.setData(new SpeedData(this));
    }

    @Override
    public String getDisplayInfo() {
        return this.mode.getValue().toString();
    }

    @Override
    protected void onEnable() {
        if (Speed.mc.player != null) {
            this.speed = MovementUtil.getSpeed();
            this.distance = MovementUtil.getDistance2D();
        }
        this.vanillaStage = 0;
        this.onGroundStage = 2;
        this.oldGroundStage = 4;
        this.ncpStage = 0;
        this.gayStage = 1;
        this.vStage = 1;
        this.bhopStage = 4;
        this.stage = 4;
        this.lowStage = 4;
        this.lastDist = 0.0;
        this.constStage = 0;
    }

    @Override
    protected void onDisable() {
        Managers.TIMER.reset();
    }

    protected boolean notColliding() {
        boolean stepping = false;
        List collisions = Speed.mc.world.getCollisionBoxes(Speed.mc.player, Speed.mc.player.getEntityBoundingBox().grow(0.1, 0.0, 0.1));
        if (STEP.isEnabled() && !collisions.isEmpty()) {
            stepping = true;
        }
        return Speed.mc.player.onGround && !stepping && !PositionUtil.inLiquid() && !PositionUtil.inLiquid(true);
    }

    public double getCap() {
        int amplifier;
        double ret = this.cap.getValue();
        if (!this.scaleCap.getValue().booleanValue()) {
            return ret;
        }
        if (Speed.mc.player.isPotionActive(MobEffects.SPEED)) {
            amplifier = Objects.requireNonNull(Speed.mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            ret *= 1.0 + 0.2 * (double)(amplifier + 1);
        }
        if (this.slow.getValue().booleanValue() && Speed.mc.player.isPotionActive(MobEffects.SLOWNESS)) {
            amplifier = Objects.requireNonNull(Speed.mc.player.getActivePotionEffect(MobEffects.SLOWNESS)).getAmplifier();
            ret /= 1.0 + 0.2 * (double)(amplifier + 1);
        }
        return ret;
    }

    public SpeedMode getMode() {
        return this.mode.getValue();
    }
}
