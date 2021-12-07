package me.earth.earthhack.impl.modules.player.suicide;

import com.google.common.collect.Sets;
import java.util.Set;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.commands.gui.YesNoNonPausing;
import me.earth.earthhack.impl.modules.player.suicide.ListenerMotion;
import me.earth.earthhack.impl.modules.player.suicide.ListenerSpawnObject;
import me.earth.earthhack.impl.modules.player.suicide.SuicideMode;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.helpers.disabling.DisablingModule;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

public class Suicide
extends DisablingModule {
    protected final Setting<SuicideMode> mode = this.register(new EnumSetting<SuicideMode>("Mode", SuicideMode.Command));
    protected final Setting<Boolean> armor = this.register(new BooleanSetting("Armor", true));
    protected final Setting<Boolean> offhand = this.register(new BooleanSetting("Offhand", true));
    protected final Setting<Boolean> throwAwayTotem = this.register(new BooleanSetting("ThrowAwayTotem", true));
    protected final Setting<Integer> throwDelay = this.register(new NumberSetting<Integer>("Throw-Delay", 500, 0, 1000));
    protected final Setting<Boolean> ask = this.register(new BooleanSetting("Ask", true));
    protected final Setting<Boolean> newVer = this.register(new BooleanSetting("1.13+", false));
    protected final Setting<Boolean> newVerEntities = this.register(new BooleanSetting("1.13-Entities", false));
    protected final Setting<Float> breakRange = this.register(new NumberSetting<Float>("BreakRange", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(6.0f)));
    protected final Setting<Float> placeRange = this.register(new NumberSetting<Float>("PlaceRange", Float.valueOf(5.25f), Float.valueOf(0.0f), Float.valueOf(6.0f)));
    protected final Setting<Integer> placeDelay = this.register(new NumberSetting<Integer>("PlaceDelay", 50, 0, 500));
    protected final Setting<Float> trace = this.register(new NumberSetting<Float>("RayTrace", Float.valueOf(3.0f), Float.valueOf(0.0f), Float.valueOf(6.0f)));
    protected final Setting<Integer> breakDelay = this.register(new NumberSetting<Integer>("BreakDelay", 50, 0, 500));
    protected final Setting<Boolean> instant = this.register(new BooleanSetting("Instant", true));
    protected final Setting<Float> minInstant = this.register(new NumberSetting<Float>("Min-Instant", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(36.0f)));
    protected final Setting<Boolean> instantCalc = this.register(new BooleanSetting("Instant-Calc", false));
    protected final Setting<Boolean> rotate = this.register(new BooleanSetting("Rotate", false));
    protected final Setting<Boolean> silent = this.register(new BooleanSetting("Silent", false));
    protected final Set<BlockPos> placed = Sets.newConcurrentHashSet();
    protected final StopWatch placeTimer = new StopWatch();
    protected final StopWatch breakTimer = new StopWatch();
    protected final StopWatch timer = new StopWatch();
    protected boolean displaying;
    protected Entity crystal;
    protected RayTraceResult result;
    protected BlockPos pos;

    public Suicide() {
        super("Suicide", Category.Player);
        SimpleData data = new SimpleData(this, "Kills you.");
        data.register(this.mode, "-Command sends a /kill command\n-AutoCrystal makes the AutoCrystal target you.");
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerSpawnObject(this));
        this.setData(data);
    }

    @Override
    protected void onEnable() {
        this.pos = null;
        this.placed.clear();
        if (this.ask.getValue().booleanValue()) {
            this.displaying = true;
            GuiScreen current = Suicide.mc.currentScreen;
            mc.displayGuiScreen(new YesNoNonPausing((r, id) -> {
                mc.displayGuiScreen(current);
                if (r) {
                    this.displaying = false;
                } else {
                    this.disable();
                }
            }, "\u00a7cDo you want to kill yourself? (recommended)", "If you don't want to get asked again, turn off the \"Ask\" Setting.", 1337));
            return;
        }
        this.displaying = false;
        if (this.mode.getValue() == SuicideMode.Command) {
            NetworkUtil.sendPacketNoEvent(new CPacketChatMessage("/kill"));
            this.disable();
        }
    }

    public boolean shouldTakeOffArmor() {
        return this.isEnabled() && !this.displaying && this.mode.getValue() != SuicideMode.Command && this.armor.getValue() != false;
    }

    public boolean deactivateOffhand() {
        return this.isEnabled() && !this.displaying && this.mode.getValue() != SuicideMode.Command && this.offhand.getValue() != false;
    }
}
