package me.earth.earthhack.impl.modules.render.popchams;

import java.awt.Color;
import java.util.HashMap;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.render.popchams.ListenerRender;
import me.earth.earthhack.impl.modules.render.popchams.ListenerTotemPop;
import me.earth.earthhack.impl.util.helpers.render.BlockESPModule;
import net.minecraft.entity.player.EntityPlayer;

public class PopChams
extends BlockESPModule {
    protected final Setting<Integer> fadeTime = this.register(new NumberSetting<Integer>("Fade-Time", 1500, 0, 5000));
    protected final Setting<Boolean> selfPop = this.register(new BooleanSetting("Self-Pop", false));
    public final ColorSetting selfColor = this.register(new ColorSetting("Self-Color", new Color(80, 80, 255, 80)));
    public final ColorSetting selfOutline = this.register(new ColorSetting("Self-Outline", new Color(80, 80, 255, 255)));
    protected final Setting<Boolean> friendPop = this.register(new BooleanSetting("Friend-Pop", false));
    public final ColorSetting friendColor = this.register(new ColorSetting("Friend-Color", new Color(45, 255, 45, 80)));
    public final ColorSetting friendOutline = this.register(new ColorSetting("Friend-Outline", new Color(45, 255, 45, 255)));
    private final HashMap<String, PopData> popDataHashMap = new HashMap();

    public PopChams() {
        super("PopChams", Category.Render);
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerTotemPop(this));
        this.color.setValue(new Color(255, 45, 45, 80));
        this.outline.setValue(new Color(255, 45, 45, 255));
        this.unregister(this.height);
    }

    @Override
    protected void onEnable() {
        this.popDataHashMap.clear();
    }

    public HashMap<String, PopData> getPopDataHashMap() {
        return this.popDataHashMap;
    }

    protected Color getColor(EntityPlayer entity) {
        if (entity == PopChams.mc.player) {
            return (Color)this.selfColor.getValue();
        }
        if (Managers.FRIENDS.contains(entity)) {
            return (Color)this.friendColor.getValue();
        }
        return (Color)this.color.getValue();
    }

    protected Color getOutlineColor(EntityPlayer entity) {
        if (entity == PopChams.mc.player) {
            return (Color)this.selfOutline.getValue();
        }
        if (Managers.FRIENDS.contains(entity)) {
            return (Color)this.friendOutline.getValue();
        }
        return (Color)this.outline.getValue();
    }

    protected boolean isValidEntity(EntityPlayer entity) {
        return !(entity == PopChams.mc.player && this.selfPop.getValue() == false || Managers.FRIENDS.contains(entity) && entity != PopChams.mc.player && this.friendPop.getValue() == false);
    }

    public static class PopData {
        private final EntityPlayer player;
        private final long time;
        private final float yaw;
        private final float pitch;
        private final double x;
        private final double y;
        private final double z;

        public PopData(EntityPlayer player, long time, float yaw, float pitch, double x, double y, double z) {
            this.player = player;
            this.time = time;
            this.yaw = yaw;
            this.pitch = pitch;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public EntityPlayer getPlayer() {
            return this.player;
        }

        public long getTime() {
            return this.time;
        }

        public float getYaw() {
            return this.yaw;
        }

        public float getPitch() {
            return this.pitch;
        }

        public double getX() {
            return this.x;
        }

        public double getY() {
            return this.y;
        }

        public double getZ() {
            return this.z;
        }
    }
}
