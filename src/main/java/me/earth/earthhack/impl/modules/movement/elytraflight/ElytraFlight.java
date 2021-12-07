package me.earth.earthhack.impl.modules.movement.elytraflight;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.movement.elytraflight.ListenerMotion;
import me.earth.earthhack.impl.modules.movement.elytraflight.ListenerMove;
import me.earth.earthhack.impl.modules.movement.elytraflight.ListenerPosLook;
import me.earth.earthhack.impl.modules.movement.elytraflight.mode.ElytraMode;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.network.play.client.CPacketEntityAction;

public class ElytraFlight
extends Module {
    protected final Setting<ElytraMode> mode = this.register(new EnumSetting<ElytraMode>("Mode", ElytraMode.Wasp));
    protected final Setting<Double> hSpeed = this.register(new NumberSetting<Double>("H-Speed", 1.0, 0.0, 100.0));
    protected final Setting<Double> vSpeed = this.register(new NumberSetting<Double>("V-Speed", 1.0, 0.0, 100.0));
    protected final Setting<Boolean> autoStart = this.register(new BooleanSetting("AutoStart", false));
    protected final Setting<Boolean> infDura = this.register(new BooleanSetting("InfiniteDurability", false));
    protected final Setting<Boolean> noWater = this.register(new BooleanSetting("StopInWater", false));
    protected final Setting<Boolean> noGround = this.register(new BooleanSetting("StopOnGround", false));
    protected final Setting<Boolean> antiKick = this.register(new BooleanSetting("AntiKick", false));
    protected final Setting<Float> glide = this.register(new NumberSetting<Float>("Glide", Float.valueOf(1.0E-4f), Float.valueOf(0.0f), Float.valueOf(0.2f)));
    protected final Setting<Boolean> ncp = this.register(new BooleanSetting("NCP", false));
    protected final Setting<Boolean> vertical = this.register(new BooleanSetting("Vertical", true));
    protected final Setting<Boolean> accel = this.register(new BooleanSetting("Accelerate", true));
    protected final Setting<Boolean> instant = this.register(new BooleanSetting("Instant", true));
    protected final StopWatch timer = new StopWatch();
    protected boolean lag;
    protected double speed;
    protected int kick;

    public ElytraFlight() {
        super("ElytraFlight", Category.Movement);
        this.listeners.add(new ListenerMove(this));
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerPosLook(this));
    }

    @Override
    public String getDisplayInfo() {
        return this.mode.getValue().toString();
    }

    @Override
    protected void onEnable() {
        this.lag = true;
        this.timer.reset();
        this.kick = 0;
    }

    public ElytraMode getMode() {
        return this.mode.getValue();
    }

    public void sendFallPacket() {
        ElytraFlight.mc.player.connection.sendPacket(new CPacketEntityAction(ElytraFlight.mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
    }
}
