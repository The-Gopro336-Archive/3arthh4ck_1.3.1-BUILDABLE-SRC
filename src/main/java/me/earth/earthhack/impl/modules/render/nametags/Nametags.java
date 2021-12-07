package me.earth.earthhack.impl.modules.render.nametags;

import java.util.ArrayList;
import java.util.List;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.render.nametags.ListenerRender;
import me.earth.earthhack.impl.modules.render.nametags.ListenerRender2D;
import me.earth.earthhack.impl.modules.render.nametags.Nametag;
import me.earth.earthhack.impl.modules.render.nametags.NametagsData;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.thread.SafeRunnable;
import net.minecraft.entity.player.EntityPlayer;

public class Nametags
extends Module {
    protected final Setting<Boolean> twoD = this.register(new BooleanSetting("2D", false));
    protected final Setting<Boolean> health = this.register(new BooleanSetting("Health", true));
    protected final Setting<Boolean> ping = this.register(new BooleanSetting("Ping", true));
    protected final Setting<Boolean> id = this.register(new BooleanSetting("Id", false));
    protected final Setting<Boolean> itemStack = this.register(new BooleanSetting("StackName", false));
    protected final Setting<Boolean> armor = this.register(new BooleanSetting("Armor", true));
    protected final Setting<Boolean> gameMode = this.register(new BooleanSetting("GameMode", false));
    protected final Setting<Boolean> durability = this.register(new BooleanSetting("Durability", true));
    protected final Setting<Boolean> invisibles = this.register(new BooleanSetting("Invisibles", false));
    protected final Setting<Boolean> pops = this.register(new BooleanSetting("Pops", true));
    protected final Setting<Boolean> burrow = this.register(new BooleanSetting("Burrow", true));
    protected final Setting<Boolean> fov = this.register(new BooleanSetting("Fov", true));
    protected final Setting<Boolean> sneak = this.register(new BooleanSetting("Sneak", true));
    protected final Setting<Float> scale = this.register(new NumberSetting<Float>("Scale", Float.valueOf(0.003f), Float.valueOf(0.001f), Float.valueOf(0.01f)));
    protected final Setting<Integer> delay = this.register(new NumberSetting<Integer>("Delay", 16, 0, 100));
    protected final Setting<Boolean> debug = this.register(new BooleanSetting("Debug", false));
    protected final Setting<Boolean> media = this.register(new BooleanSetting("Media", true));
    protected final Setting<Boolean> multiThread = this.register(new BooleanSetting("MultiThread", true));
    protected List<Nametag> nametags = new ArrayList<Nametag>();
    protected final StopWatch timer = new StopWatch();

    public Nametags() {
        super("Nametags", Category.Render);
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerRender2D(this));
        this.setData(new NametagsData(this));
    }

    protected void updateNametags() {
        if (this.timer.passed(this.delay.getValue().intValue())) {
            List<EntityPlayer> players = Managers.ENTITIES.getPlayers();
            if (players == null) {
                return;
            }
            SafeRunnable runnable = () -> {
                ArrayList<Nametag> nametags = new ArrayList<Nametag>(players.size());
                for (EntityPlayer player : players) {
                    if (player == null || player.isDead || player.equals(Nametags.mc.player)) continue;
                    nametags.add(new Nametag(this, player));
                }
                this.nametags = nametags;
            };
            if (this.multiThread.getValue().booleanValue()) {
                Managers.THREAD.submit(runnable);
            } else {
                runnable.run();
            }
            this.timer.reset();
        }
    }

    protected int getFontOffset(int enchHeight) {
        int armorOffset;
        int n = armorOffset = this.armor.getValue() != false ? -26 : -27;
        if (enchHeight > 4) {
            armorOffset -= (enchHeight - 4) * 8;
        }
        return armorOffset;
    }
}
