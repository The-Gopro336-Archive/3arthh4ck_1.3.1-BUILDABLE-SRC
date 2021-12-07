package me.earth.earthhack.impl.modules.movement.highjump;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.movement.highjump.HighJumpData;
import me.earth.earthhack.impl.modules.movement.highjump.ListenerExplosion;
import me.earth.earthhack.impl.modules.movement.highjump.ListenerInput;
import me.earth.earthhack.impl.modules.movement.highjump.ListenerMove;
import me.earth.earthhack.impl.modules.movement.highjump.ListenerObby;
import me.earth.earthhack.impl.modules.movement.highjump.ListenerVelocity;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListenerModule;
import me.earth.earthhack.impl.util.helpers.blocks.attack.InstantAttackListener;
import me.earth.earthhack.impl.util.helpers.blocks.attack.InstantAttackingModule;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.Passable;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class HighJump
extends ObbyListenerModule<ListenerObby>
implements InstantAttackingModule {
    protected final Setting<Boolean> scaffold;
    protected final Setting<Double> range;
    protected final Setting<Double> height = this.register(new NumberSetting<Double>("Height", 0.42, 0.0, 1.0));
    protected final Setting<Boolean> onGround = this.register(new BooleanSetting("OnGround", true));
    protected final Setting<Boolean> onlySpecial = this.register(new BooleanSetting("OnlySpecial", false));
    protected final Setting<Boolean> explosions = this.register(new BooleanSetting("Explosions", false));
    protected final Setting<Boolean> velocity = this.register(new BooleanSetting("Velocity", false));
    protected final Setting<Integer> delay = this.register(new NumberSetting<Integer>("Jump-Delay", 250, 0, 1000));
    protected final Setting<Boolean> constant = this.register(new BooleanSetting("Constant", false));
    protected final Setting<Double> factor = this.register(new NumberSetting<Double>("Factor", 1.0, 0.0, 10.0));
    protected final Setting<Double> minY = this.register(new NumberSetting<Double>("MinY", 0.0, 0.0, 5.0));
    protected final Setting<Boolean> cancelJump = this.register(new BooleanSetting("Cancel-Jump", false));
    protected final Setting<Integer> lagTime = this.register(new NumberSetting<Integer>("LagTime", 1000, 0, 2500));
    protected final Setting<Boolean> resetAlways = this.register(new BooleanSetting("Reset-Always", false));
    protected final Setting<Double> alwaysY = this.register(new NumberSetting<Double>("Always-Y", 0.0, 0.0, 5.0));
    protected final Setting<Boolean> addY = this.register(new BooleanSetting("Add-Y", false));
    protected final Setting<Double> addFactor = this.register(new NumberSetting<Double>("Add-Factor", 0.5, 0.1, 2.0));
    protected final Setting<Boolean> addJump = this.register(new BooleanSetting("Add-Jump", false));
    protected final Setting<Double> jumpFactor = this.register(new NumberSetting<Double>("Jump-Factor", 0.5, 0.1, 2.0));
    protected final Setting<Double> scaffoldY = this.register(new NumberSetting<Double>("Scaffold-Y", 0.0, 0.0, 2.0));
    protected final Setting<Double> scaffoldMaxY = this.register(new NumberSetting<Double>("Scaffold-Max-Y", 10.0, 0.0, 10.0));
    protected final Setting<Integer> scaffoldOffset = this.register(new NumberSetting<Integer>("Scaffold-Offset", 2, 0, 3));
    protected final Setting<Boolean> instant = this.register(new BooleanSetting("Instant-Attack", true));
    protected final StopWatch timer = new StopWatch();
    protected double motionY;

    public HighJump() {
        super("HighJump", Category.Movement);
        this.listeners.clear();
        this.listeners.add(this.listener);
        this.listeners.add(new ListenerMove(this));
        this.listeners.add(new ListenerExplosion(this));
        this.listeners.add(new ListenerVelocity(this));
        this.listeners.add(new ListenerInput(this));
        this.listeners.add(new InstantAttackListener<HighJump>(this));
        this.scaffold = this.registerBefore(new BooleanSetting("Scaffold", false), this.blocks);
        this.range = this.registerAfter(new NumberSetting<Double>("Range", 5.25, 0.1, 6.0), this.scaffold);
        this.setData(new HighJumpData(this));
    }

    @Override
    protected boolean checkNull() {
        this.packets.clear();
        this.blocksPlaced = 0;
        return HighJump.mc.player != null && HighJump.mc.world != null;
    }

    @Override
    public String getDisplayInfo() {
        if (this.velocity.getValue().booleanValue() || this.explosions.getValue().booleanValue()) {
            if (!Managers.NCP.passed(this.lagTime.getValue())) {
                return "\u00a7cLag";
            }
            if (this.timer.passed(this.delay.getValue().intValue())) {
                return "0.00";
            }
            double y = MathUtil.round(this.motionY, 2);
            if (this.motionY < this.minY.getValue()) {
                return "\u00a7c" + y;
            }
            return "\u00a7a" + y;
        }
        return null;
    }

    @Override
    protected void onEnable() {
        this.motionY = 0.0;
    }

    @Override
    protected ListenerObby createListener() {
        return new ListenerObby(this, 9);
    }

    public void addVelocity(double y) {
        if (this.timer.passed(this.delay.getValue().intValue())) {
            this.motionY = y;
            this.timer.reset();
        } else if (this.addY.getValue().booleanValue()) {
            this.motionY += y * this.addFactor.getValue();
        }
        if (this.resetAlways.getValue().booleanValue() && y >= this.alwaysY.getValue()) {
            this.timer.reset();
        }
        if (this.addJump.getValue().booleanValue() && !HighJump.mc.player.onGround && HighJump.mc.gameSettings.keyBindJump.isKeyDown()) {
            HighJump.mc.player.motionY += y * this.jumpFactor.getValue();
        }
    }

    @Override
    public boolean shouldAttack(EntityEnderCrystal entity) {
        if (!((Boolean)this.attack.getValue()).booleanValue() || !this.instant.getValue().booleanValue()) {
            return false;
        }
        BlockPos pos = PositionUtil.getPosition();
        int i = 0;
        while ((double)i <= this.range.getValue()) {
            if (entity.getEntityBoundingBox().intersects(new AxisAlignedBB(pos.down(i)))) {
                return true;
            }
            ++i;
        }
        return false;
    }

    @Override
    public Passable getTimer() {
        return this.attackTimer;
    }

    @Override
    public int getBreakDelay() {
        return (Integer)this.breakDelay.getValue();
    }

    @Override
    public int getCooldown() {
        return (Integer)this.cooldown.getValue();
    }
}
