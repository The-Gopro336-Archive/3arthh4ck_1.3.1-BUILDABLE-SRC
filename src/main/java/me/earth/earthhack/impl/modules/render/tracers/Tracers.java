/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 */
package me.earth.earthhack.impl.modules.render.tracers;

import java.util.ArrayList;
import java.util.List;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.render.tracers.ListenerRender;
import me.earth.earthhack.impl.modules.render.tracers.ListenerTick;
import me.earth.earthhack.impl.modules.render.tracers.TracersData;
import me.earth.earthhack.impl.modules.render.tracers.mode.BodyPart;
import me.earth.earthhack.impl.modules.render.tracers.mode.TracerMode;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.minecraft.entity.module.EntityTypeModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;

public class Tracers
extends EntityTypeModule {
    protected final Setting<Boolean> items = this.register(new BooleanSetting("Items", false));
    protected final Setting<Boolean> invisibles = this.register(new BooleanSetting("Invisibles", false));
    protected final Setting<Boolean> friends = this.register(new BooleanSetting("Friends", true));
    protected final Setting<TracerMode> mode = this.register(new EnumSetting<TracerMode>("Mode", TracerMode.Outline));
    protected final Setting<BodyPart> target = this.register(new EnumSetting<BodyPart>("Target", BodyPart.Body));
    protected final Setting<Boolean> lines = this.register(new BooleanSetting("Lines", true));
    protected final Setting<Float> lineWidth = this.register(new NumberSetting<Float>("LineWidth", Float.valueOf(1.5f), Float.valueOf(0.1f), Float.valueOf(5.0f)));
    protected final Setting<Integer> tracers = this.register(new NumberSetting<Integer>("Amount", 100, 1, 250));
    protected final Setting<Float> minRange = this.register(new NumberSetting<Float>("MinRange", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(1000.0f)));
    protected final Setting<Float> maxRange = this.register(new NumberSetting<Float>("MaxRange", Float.valueOf(1000.0f), Float.valueOf(0.0f), Float.valueOf(1000.0f)));
    protected List<Entity> sorted = new ArrayList<Entity>();

    public Tracers() {
        super("Tracers", Category.Render);
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerTick(this));
        this.setData(new TracersData(this));
    }

    @Override
    public boolean isValid(Entity entity) {
        if (!(entity == null || EntityUtil.isDead(entity) || entity == mc.getRenderViewEntity() || mc.getRenderViewEntity() != null && entity.equals((Object)mc.getRenderViewEntity().getRidingEntity()))) {
            if (entity.getDistanceSq((Entity)Tracers.mc.player) < (double)MathUtil.square(this.minRange.getValue().floatValue()) || entity.getDistanceSq((Entity)Tracers.mc.player) > (double)MathUtil.square(this.maxRange.getValue().floatValue())) {
                return false;
            }
            if (this.items.getValue().booleanValue() && entity instanceof EntityItem) {
                return true;
            }
            if (((Boolean)this.players.getValue()).booleanValue() && entity instanceof EntityPlayer && (this.invisibles.getValue().booleanValue() || !entity.isInvisible()) && (this.friends.getValue().booleanValue() || !Managers.FRIENDS.contains(entity.getName()))) {
                return true;
            }
            return super.isValid(entity);
        }
        return false;
    }
}

