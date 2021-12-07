/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.block.material.Material
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.init.MobEffects
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  org.lwjgl.opengl.GL11
 */
package me.earth.earthhack.impl.modules.client.hud;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.render.TextRenderer;
import me.earth.earthhack.impl.modules.client.hud.ListenerPostKey;
import me.earth.earthhack.impl.modules.client.hud.ListenerRender;
import me.earth.earthhack.impl.modules.client.hud.arraylist.ArrayEntry;
import me.earth.earthhack.impl.modules.client.hud.modes.HudRainbow;
import me.earth.earthhack.impl.modules.client.hud.modes.Modules;
import me.earth.earthhack.impl.modules.client.hud.modes.PotionColor;
import me.earth.earthhack.impl.modules.client.hud.modes.Potions;
import me.earth.earthhack.impl.modules.client.hud.modes.RenderMode;
import me.earth.earthhack.impl.modules.client.hud.util.HUDData;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.network.ServerUtil;
import me.earth.earthhack.impl.util.render.ColorHelper;
import me.earth.earthhack.impl.util.render.ColorUtil;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.lwjgl.opengl.GL11;

public class HUD
extends Module {
    public static final TextRenderer RENDERER = Managers.TEXT;
    protected final Setting<RenderMode> renderMode = this.register(new EnumSetting<RenderMode>("RenderMode", RenderMode.Normal));
    protected final Setting<HudRainbow> colorMode = this.register(new EnumSetting<HudRainbow>("Rainbow", HudRainbow.None));
    protected final Setting<Color> color = this.register(new ColorSetting("Color", Color.WHITE));
    protected final Setting<Boolean> logo = this.register(new BooleanSetting("Logo", true));
    protected final Setting<Boolean> coordinates = this.register(new BooleanSetting("Coordinates", true));
    protected final Setting<Boolean> armor = this.register(new BooleanSetting("Armor", true));
    protected final Setting<Modules> renderModules = this.register(new EnumSetting<Modules>("Modules", Modules.Length));
    protected final Setting<Potions> potions = this.register(new EnumSetting<Potions>("Potions", Potions.Move));
    protected final Setting<PotionColor> potionColor = this.register(new EnumSetting<PotionColor>("PotionColor", PotionColor.Normal));
    protected final Setting<Boolean> shadow = this.register(new BooleanSetting("Shadow", true));
    protected final Setting<Boolean> ping = this.register(new BooleanSetting("Ping", false));
    protected final Setting<Boolean> speed = this.register(new BooleanSetting("Speed", false));
    protected final Setting<Boolean> fps = this.register(new BooleanSetting("FPS", false));
    protected final Setting<Boolean> tps = this.register(new BooleanSetting("TPS", false));
    protected final Setting<Boolean> animations = this.register(new BooleanSetting("Animations", true));
    protected final List<Map.Entry<String, Module>> modules = new ArrayList<Map.Entry<String, Module>>();
    protected final Map<Module, ArrayEntry> arrayEntries = new HashMap<Module, ArrayEntry>();
    protected final Map<Module, ArrayEntry> removeEntries = new HashMap<Module, ArrayEntry>();
    protected ScaledResolution resolution = new ScaledResolution(mc);
    protected int width;
    protected int height;
    protected float animationY = 0.0f;
    private final Map<Potion, Color> potionColorMap = new HashMap<Potion, Color>();

    public HUD() {
        super("HUD", Category.Client);
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerPostKey(this));
        this.setData(new HUDData(this));
        this.potionColorMap.put(MobEffects.SPEED, new Color(124, 175, 198));
        this.potionColorMap.put(MobEffects.SLOWNESS, new Color(90, 108, 129));
        this.potionColorMap.put(MobEffects.HASTE, new Color(217, 192, 67));
        this.potionColorMap.put(MobEffects.MINING_FATIGUE, new Color(74, 66, 23));
        this.potionColorMap.put(MobEffects.STRENGTH, new Color(147, 36, 35));
        this.potionColorMap.put(MobEffects.INSTANT_HEALTH, new Color(67, 10, 9));
        this.potionColorMap.put(MobEffects.INSTANT_DAMAGE, new Color(67, 10, 9));
        this.potionColorMap.put(MobEffects.JUMP_BOOST, new Color(34, 255, 76));
        this.potionColorMap.put(MobEffects.NAUSEA, new Color(85, 29, 74));
        this.potionColorMap.put(MobEffects.REGENERATION, new Color(205, 92, 171));
        this.potionColorMap.put(MobEffects.RESISTANCE, new Color(153, 69, 58));
        this.potionColorMap.put(MobEffects.FIRE_RESISTANCE, new Color(228, 154, 58));
        this.potionColorMap.put(MobEffects.WATER_BREATHING, new Color(46, 82, 153));
        this.potionColorMap.put(MobEffects.INVISIBILITY, new Color(127, 131, 146));
        this.potionColorMap.put(MobEffects.BLINDNESS, new Color(31, 31, 35));
        this.potionColorMap.put(MobEffects.NIGHT_VISION, new Color(31, 31, 161));
        this.potionColorMap.put(MobEffects.HUNGER, new Color(88, 118, 83));
        this.potionColorMap.put(MobEffects.WEAKNESS, new Color(72, 77, 72));
        this.potionColorMap.put(MobEffects.POISON, new Color(78, 147, 49));
        this.potionColorMap.put(MobEffects.WITHER, new Color(53, 42, 39));
        this.potionColorMap.put(MobEffects.HEALTH_BOOST, new Color(248, 125, 35));
        this.potionColorMap.put(MobEffects.ABSORPTION, new Color(37, 82, 165));
        this.potionColorMap.put(MobEffects.SATURATION, new Color(248, 36, 35));
        this.potionColorMap.put(MobEffects.GLOWING, new Color(148, 160, 97));
        this.potionColorMap.put(MobEffects.LEVITATION, new Color(206, 255, 255));
        this.potionColorMap.put(MobEffects.LUCK, new Color(51, 153, 0));
        this.potionColorMap.put(MobEffects.UNLUCK, new Color(192, 164, 77));
    }

    protected void renderLogo() {
        if (this.logo.getValue().booleanValue()) {
            this.renderText("3arthh4ck - 1.3.1", 2.0f, 2.0f);
        }
    }

    protected void renderModules() {
        int offset = 0;
        if (this.potions.getValue() == Potions.Text) {
            ArrayList<Potion> sorted = new ArrayList<Potion>();
            for (Potion potion2 : Potion.REGISTRY) {
                if (potion2 == null || !HUD.mc.player.isPotionActive(potion2)) continue;
                sorted.add(potion2);
            }
            sorted.sort(Comparator.comparingDouble(potion -> -RENDERER.getStringWidth(I18n.format((String)potion.getName(), (Object[])new Object[0]) + (HUD.mc.player.getActivePotionEffect(potion).getAmplifier() > 0 ? " " + (HUD.mc.player.getActivePotionEffect(potion).getAmplifier() + 1) : "") + (Object)ChatFormatting.GRAY + " " + Potion.getPotionDurationString((PotionEffect)Objects.requireNonNull(HUD.mc.player.getActivePotionEffect(potion)), (float)1.0f))));
            for (Potion potion2 : sorted) {
                PotionEffect effect = HUD.mc.player.getActivePotionEffect(potion2);
                if (effect == null) continue;
                String string = I18n.format((String)potion2.getName(), (Object[])new Object[0]) + (effect.getAmplifier() > 0 ? " " + (effect.getAmplifier() + 1) : "") + (Object)ChatFormatting.GRAY + " " + Potion.getPotionDurationString((PotionEffect)effect, (float)1.0f);
                GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                int x = this.width - 2 - RENDERER.getStringWidth(string);
                this.renderPotionText(string, x, (float)(this.height - 2) - RENDERER.getStringHeight() - (float)offset - this.animationY, effect.getPotion());
                offset = (int)((float)offset + (RENDERER.getStringHeight() + 3.0f));
            }
        }
        if (this.speed.getValue().booleanValue()) {
            String text = "Speed \u00a77" + MathUtil.round(Managers.SPEED.getSpeed(), 2) + " km/h";
            this.renderText(text, this.width - 2 - RENDERER.getStringWidth(text), (float)(this.height - 2) - RENDERER.getStringHeight() - (float)offset - this.animationY);
            offset = (int)((float)offset + (RENDERER.getStringHeight() + 3.0f));
        }
        if (this.tps.getValue().booleanValue()) {
            String tps = "TPS \u00a77" + MathUtil.round(Managers.TPS.getTps(), 2);
            this.renderText(tps, this.width - 2 - RENDERER.getStringWidth(tps), (float)(this.height - 2) - RENDERER.getStringHeight() - (float)offset - this.animationY);
            offset = (int)((float)offset + (RENDERER.getStringHeight() + 3.0f));
        }
        if (this.fps.getValue().booleanValue()) {
            String fps = "FPS \u00a77" + Minecraft.getDebugFPS();
            this.renderText(fps, this.width - 2 - RENDERER.getStringWidth(fps), (float)(this.height - 2) - RENDERER.getStringHeight() - (float)offset - this.animationY);
            offset = (int)((float)offset + (RENDERER.getStringHeight() + 3.0f));
        }
        if (this.ping.getValue().booleanValue()) {
            String ping = "Ping \u00a77" + ServerUtil.getPing();
            this.renderText(ping, this.width - 2 - RENDERER.getStringWidth(ping), (float)(this.height - 2) - RENDERER.getStringHeight() - (float)offset - this.animationY);
        }
        if (this.coordinates.getValue().booleanValue()) {
            long x = Math.round(HUD.mc.player.posX);
            long y = Math.round(HUD.mc.player.posY);
            long l = Math.round(HUD.mc.player.posZ);
            String coords = HUD.mc.player.dimension == -1 ? String.format("\u00a77%s \u00a7f[%s]\u00a78, \u00a77%s\u00a78, \u00a77%s \u00a7f[%s]", x, x * 8L, y, l, l * 8L) : (HUD.mc.player.dimension == 0 ? String.format("\u00a7f%s \u00a77[%s]\u00a78, \u00a7f%s\u00a78, \u00a7f%s \u00a77[%s]", x, x / 8L, y, l, l / 8L) : String.format("\u00a7f%s\u00a78, \u00a7f%s\u00a78, \u00a7f%s", x, y, l));
            this.renderText(coords, 2.0f, (float)(this.height - 2) - RENDERER.getStringHeight() - this.animationY);
            String dir = RotationUtil.getDirection4D(false);
            this.renderText(dir, 2.0f, (float)(this.height - 3) - RENDERER.getStringHeight() * 2.0f - this.animationY);
        }
        this.renderArmor();
        if (this.renderModules.getValue() != Modules.None) {
            int o;
            boolean move = this.potions.getValue() == Potions.Move && !HUD.mc.player.getActivePotionEffects().isEmpty();
            int j = move ? 2 : 0;
            int n = o = move ? 5 : 2;
            if (this.animations.getValue().booleanValue()) {
                for (Map.Entry entry2 : this.modules) {
                    if (this.isArrayMember((Module)entry2.getValue())) continue;
                    this.getArrayEntries().put((Module)entry2.getValue(), new ArrayEntry((Module)entry2.getValue()));
                }
                Map arrayEntriesSorted = this.renderModules.getValue() == Modules.Length ? (Map)this.getArrayEntries().entrySet().stream().sorted(Comparator.comparingDouble(entry -> Managers.TEXT.getStringWidth(ModuleUtil.getHudName((Module)entry.getKey())) * -1)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new)) : (Map)this.getArrayEntries().entrySet().stream().sorted(Comparator.comparing(entry -> ModuleUtil.getHudName((Module)entry.getKey()))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
                for (ArrayEntry arrayEntry : arrayEntriesSorted.values()) {
                    arrayEntry.drawArrayEntry(this.width - 2, o + j * 10);
                    ++j;
                }
                this.getRemoveEntries().forEach((key, value) -> this.getArrayEntries().remove(key));
                this.getRemoveEntries().clear();
            } else {
                for (Map.Entry<String, Module> entry3 : this.modules) {
                    this.renderText(entry3.getKey(), this.width - 2 - RENDERER.getStringWidth(entry3.getKey()), o + j * 10);
                    ++j;
                }
            }
        }
    }

    private void renderArmor() {
        if (this.armor.getValue().booleanValue()) {
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            int x = 15;
            RenderHelper.enableGUIStandardItemLighting();
            for (int i = 3; i >= 0; --i) {
                int y;
                ItemStack stack = (ItemStack)HUD.mc.player.inventory.armorInventory.get(i);
                if (stack.isEmpty()) continue;
                if (HUD.mc.player.isInsideOfMaterial(Material.WATER) && HUD.mc.player.getAir() > 0 && !HUD.mc.player.capabilities.isCreativeMode) {
                    y = 65;
                } else if (HUD.mc.player.getRidingEntity() != null && !HUD.mc.player.capabilities.isCreativeMode) {
                    if (HUD.mc.player.getRidingEntity() instanceof EntityLivingBase) {
                        EntityLivingBase entity = (EntityLivingBase)HUD.mc.player.getRidingEntity();
                        y = (int)(45.0 + Math.ceil((entity.getMaxHealth() - 1.0f) / 20.0f) * 10.0);
                    } else {
                        y = 45;
                    }
                } else {
                    y = HUD.mc.player.capabilities.isCreativeMode ? (HUD.mc.player.isRidingHorse() ? 45 : 38) : 55;
                }
                float percent = DamageUtil.getPercent(stack) / 100.0f;
                GlStateManager.pushMatrix();
                GlStateManager.scale((float)0.625f, (float)0.625f, (float)0.625f);
                GlStateManager.disableDepth();
                Managers.TEXT.drawStringWithShadow((int)(percent * 100.0f) + "%", (float)((this.width >> 1) + x + 1) * 1.6f, (float)(this.height - y - 3) * 1.6f, ColorHelper.toColor(percent * 120.0f, 100.0f, 50.0f, 1.0f).getRGB());
                GlStateManager.enableDepth();
                GlStateManager.scale((float)1.0f, (float)1.0f, (float)1.0f);
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                mc.getRenderItem().renderItemIntoGUI(stack, this.width / 2 + x, this.height - y);
                mc.getRenderItem().renderItemOverlays(HUD.mc.fontRenderer, stack, this.width / 2 + x, this.height - y);
                GlStateManager.popMatrix();
                x += 18;
            }
            RenderHelper.disableStandardItemLighting();
        }
    }

    public void renderText(String text, float x, float y) {
        String colorCode = this.colorMode.getValue().getColor();
        RENDERER.drawStringWithShadow(colorCode + text, x, y, this.colorMode.getValue() == HudRainbow.None ? this.color.getValue().getRGB() : (this.colorMode.getValue() == HudRainbow.Static ? ColorUtil.staticRainbow((y + 1.0f) * 0.89f, this.color.getValue()) : -1));
    }

    public void renderPotionText(String text, float x, float y, Potion potion) {
        String colorCode = this.potionColor.getValue() == PotionColor.Normal ? "" : this.colorMode.getValue().getColor();
        RENDERER.drawStringWithShadow(colorCode + text, x, y, this.potionColor.getValue() == PotionColor.Normal ? this.potionColorMap.get((Object)potion).getRGB() : (this.colorMode.getValue() == HudRainbow.None ? this.color.getValue().getRGB() : (this.colorMode.getValue() == HudRainbow.Static ? ColorUtil.staticRainbow((y + 1.0f) * 0.89f, this.color.getValue()) : -1)));
    }

    public Map<Module, ArrayEntry> getArrayEntries() {
        return this.arrayEntries;
    }

    public Map<Module, ArrayEntry> getRemoveEntries() {
        return this.removeEntries;
    }

    protected boolean isArrayMember(Module module) {
        return this.getArrayEntries().containsKey(module);
    }
}

