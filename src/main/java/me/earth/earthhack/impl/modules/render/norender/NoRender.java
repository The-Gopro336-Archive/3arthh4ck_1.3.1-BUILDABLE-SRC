/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.EntityEquipmentSlot
 */
package me.earth.earthhack.impl.modules.render.norender;

import java.util.HashSet;
import java.util.Set;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.modules.render.norender.ListenerAnimation;
import me.earth.earthhack.impl.modules.render.norender.ListenerRenderEntities;
import me.earth.earthhack.impl.modules.render.norender.ListenerSpawnObject;
import me.earth.earthhack.impl.modules.render.norender.ListenerSuffocation;
import me.earth.earthhack.impl.modules.render.norender.ListenerTick;
import me.earth.earthhack.impl.modules.render.norender.NoRenderData;
import net.minecraft.inventory.EntityEquipmentSlot;

public class NoRender
extends Module {
    protected final Setting<Boolean> fire = this.register(new BooleanSetting("Fire", true));
    protected final Setting<Boolean> portal = this.register(new BooleanSetting("Portal", true));
    protected final Setting<Boolean> pumpkin = this.register(new BooleanSetting("Pumpkin", true));
    protected final Setting<Boolean> totemPops = this.register(new BooleanSetting("TotemPop", true));
    protected final Setting<Boolean> nausea = this.register(new BooleanSetting("Nausea", true));
    protected final Setting<Boolean> hurtCam = this.register(new BooleanSetting("HurtCam", true));
    protected final Setting<Boolean> noWeather = this.register(new BooleanSetting("Weather", true));
    protected final Setting<Boolean> barriers = this.register(new BooleanSetting("Barriers", false));
    protected final Setting<Boolean> skyLight = this.register(new BooleanSetting("SkyLight", true));
    protected final Setting<Boolean> noFog = this.register(new BooleanSetting("NoFog", true));
    protected final Setting<Boolean> blocks = this.register(new BooleanSetting("Blocks", true));
    protected final Setting<Boolean> advancements = this.register(new BooleanSetting("Advancements", false));
    protected final Setting<Boolean> critParticles = this.register(new BooleanSetting("CritParticles", false));
    protected final Setting<Boolean> dynamicFov = this.register(new BooleanSetting("DynamicFov", true));
    public final Setting<Boolean> boss = this.register(new BooleanSetting("BossHealth", true));
    public final Setting<Boolean> explosions = this.register(new BooleanSetting("Explosions", true));
    public final Setting<Boolean> defaultBackGround = this.register(new BooleanSetting("DefaultGuiBackGround", false));
    protected final Setting<Boolean> items = this.register(new BooleanSetting("Items", false));
    protected final Setting<Boolean> helmet = this.register(new BooleanSetting("Helmet", false));
    protected final Setting<Boolean> chestplate = this.register(new BooleanSetting("Breastplate", false));
    protected final Setting<Boolean> leggings = this.register(new BooleanSetting("Leggings", false));
    protected final Setting<Boolean> boots = this.register(new BooleanSetting("Boots", false));
    protected final Setting<Boolean> entities = this.register(new BooleanSetting("Entities", false));
    protected final Set<Integer> ids = new HashSet<Integer>();

    public NoRender() {
        super("NoRender", Category.Render);
        this.listeners.add(new ListenerSuffocation(this));
        this.listeners.add(new ListenerAnimation(this));
        this.listeners.add(new ListenerSpawnObject(this));
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerRenderEntities(this));
        this.setData(new NoRenderData(this));
    }

    public boolean noFire() {
        return this.isEnabled() && this.fire.getValue() != false;
    }

    public boolean noTotems() {
        return this.isEnabled() && this.totemPops.getValue() != false;
    }

    public boolean noHurtCam() {
        return this.isEnabled() && this.hurtCam.getValue() != false;
    }

    public boolean noPortal() {
        return this.isEnabled() && this.portal.getValue() != false;
    }

    public boolean noPumpkin() {
        return this.isEnabled() && this.pumpkin.getValue() != false;
    }

    public boolean noNausea() {
        return this.isEnabled() && this.nausea.getValue() != false;
    }

    public boolean noFog() {
        return this.isEnabled() && this.noFog.getValue() != false;
    }

    public boolean noSkyLight() {
        return this.isEnabled() && this.skyLight.getValue() != false;
    }

    public boolean noAdvancements() {
        return this.isEnabled() && this.advancements.getValue() != false;
    }

    public boolean noWeather() {
        return this.isEnabled() && this.noWeather.getValue() != false;
    }

    public boolean showBarriers() {
        return this.isEnabled() && this.barriers.getValue() != false;
    }

    public boolean dynamicFov() {
        return this.isEnabled() && this.dynamicFov.getValue() != false;
    }

    public boolean isValidArmorPiece(EntityEquipmentSlot slot) {
        if (!this.isEnabled()) {
            return true;
        }
        if (slot == EntityEquipmentSlot.HEAD && this.helmet.getValue().booleanValue()) {
            return false;
        }
        if (slot == EntityEquipmentSlot.CHEST && this.chestplate.getValue().booleanValue()) {
            return false;
        }
        if (slot == EntityEquipmentSlot.LEGS && this.leggings.getValue().booleanValue()) {
            return false;
        }
        return slot != EntityEquipmentSlot.FEET || this.boots.getValue() == false;
    }
}

