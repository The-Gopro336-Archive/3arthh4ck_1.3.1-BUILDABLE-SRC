package me.earth.earthhack.impl.modules.movement.flight;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.movement.flight.FlightData;
import me.earth.earthhack.impl.modules.movement.flight.ListenerMotion;
import me.earth.earthhack.impl.modules.movement.flight.ListenerMove;
import me.earth.earthhack.impl.modules.movement.flight.ListenerOnground;
import me.earth.earthhack.impl.modules.movement.flight.ListenerPlayerPacket;
import me.earth.earthhack.impl.modules.movement.flight.ListenerTick;
import me.earth.earthhack.impl.modules.movement.flight.mode.FlightMode;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.potion.PotionEffect;

public class Flight
extends Module {
    protected final Setting<FlightMode> mode = this.register(new EnumSetting<FlightMode>("Mode", FlightMode.Normal));
    protected final Setting<Double> speed = this.register(new NumberSetting<Double>("Speed", 2.5, 0.0, 50.0));
    protected final Setting<Boolean> animation = this.register(new BooleanSetting("Animation", true));
    protected final Setting<Boolean> damage = this.register(new BooleanSetting("Damage", false));
    protected final Setting<Boolean> antiKick = this.register(new BooleanSetting("AntiKick", true));
    protected final Setting<Boolean> glide = this.register(new BooleanSetting("Glide", true));
    protected final Setting<Double> glideSpeed = this.register(new NumberSetting<Double>("Glide-Speed", 0.03126, -2.0, 2.0));
    protected final Setting<Double> aacY = this.register(new NumberSetting<Double>("AAC-Y", 0.83, 0.0, 10.0));
    protected int counter;
    protected int antiCounter;
    protected int constantiamStage;
    protected int constantiamTicks;
    protected double moveSpeed;
    protected int stage;
    protected int ticks;
    protected double y;
    protected int constNewStage;
    protected int constNewTicks;
    protected double constNewOffset;
    protected double constY;
    protected double constMovementSpeed;
    protected double lastDist;
    protected boolean clipped;
    protected int oHareCounter;
    protected int oHareLevel;
    protected double oHareMoveSpeed;
    protected double oHareLastDist;

    public Flight() {
        super("Flight", Category.Movement);
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerOnground(this));
        this.listeners.add(new ListenerMove(this));
        this.listeners.add(new ListenerMotion(this));
        this.listeners.addAll(new ListenerPlayerPacket(this).getListeners());
        this.setData(new FlightData(this));
    }

    @Override
    public String getDisplayInfo() {
        return this.mode.getValue().toString();
    }

    @Override
    protected void onEnable() {
        this.constantiamStage = 0;
        this.constantiamTicks = 0;
        this.moveSpeed = 0.0;
        this.constNewStage = 0;
        this.constNewTicks = 0;
        this.constY = 0.0;
        this.constMovementSpeed = 0.0;
        this.oHareLevel = 1;
        this.oHareMoveSpeed = 0.1;
        this.oHareLastDist = 0.0;
        if (this.damage.getValue().booleanValue()) {
            Flight.damage();
        }
        if (this.mode.getValue() == FlightMode.Constantiam) {
            PacketUtil.doY(Flight.mc.player.posY + 0.22534, false);
            PacketUtil.doY(Flight.mc.player.posY + 0.04534, false);
        }
        if (this.mode.getValue() == FlightMode.ConstoHareFast && Flight.mc.player != null && Flight.mc.player.onGround) {
            Flight.mc.player.motionY = 0.40245f;
        }
    }

    @Override
    protected void onDisable() {
        if (this.mode.getValue() == FlightMode.Jump && Flight.mc.player != null) {
            this.counter = 0;
            Flight.mc.player.jumpMovementFactor = 0.02f;
        }
        if (this.mode.getValue() == FlightMode.ConstantiamNew) {
            Flight.mc.player.setPosition(Flight.mc.player.posX, Flight.mc.player.posY + this.constY, Flight.mc.player.posZ);
        }
        if (this.mode.getValue() == FlightMode.ConstoHare && Flight.mc.player != null) {
            Flight.mc.player.motionX = 0.0;
            Flight.mc.player.motionZ = 0.0;
            this.oHareLevel = 0;
            this.oHareCounter = 0;
            this.oHareMoveSpeed = 0.1;
            this.oHareLastDist = 0.0;
        }
    }

    public static void damage() {
        double offset = 0.0625;
        if (Flight.mc.player != null && Flight.mc.player.onGround) {
            int i = 0;
            while ((double)i <= 4.0 / offset) {
                Flight.mc.player.connection.sendPacket(new CPacketPlayer.Position(Flight.mc.player.posX, Flight.mc.player.posY + offset, Flight.mc.player.posZ, false));
                Flight.mc.player.connection.sendPacket(new CPacketPlayer.Position(Flight.mc.player.posX, Flight.mc.player.posY, Flight.mc.player.posZ, (double)i == 4.0 / offset));
                ++i;
            }
        }
    }

    public static float getMaxFallDist() {
        PotionEffect potioneffect = Flight.mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST);
        int f = potioneffect != null ? potioneffect.getAmplifier() + 1 : 0;
        return Flight.mc.player.getMaxFallHeight() + f;
    }
}
