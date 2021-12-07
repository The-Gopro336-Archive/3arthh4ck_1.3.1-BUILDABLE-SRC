package me.earth.earthhack.impl.modules.render.nametags;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.render.ColorHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class StackRenderer
implements Globals {
    private final ItemStack stack;
    private final List<String> enchantTexts;
    private final boolean damageable;
    private final int color;
    private final float percent;

    public StackRenderer(ItemStack stack, boolean damageable) {
        this.damageable = damageable;
        if (stack.isItemStackDamageable()) {
            this.percent = DamageUtil.getPercent(stack) / 100.0f;
            this.color = ColorHelper.toColor(this.percent * 120.0f, 100.0f, 50.0f, 1.0f).getRGB();
        } else {
            this.percent = 100.0f;
            this.color = -1;
        }
        this.stack = stack;
        Set e = EnchantmentHelper.getEnchantments((ItemStack)stack).keySet();
        this.enchantTexts = new ArrayList<String>(e.size());
        for (Enchantment enchantment : e) {
            this.enchantTexts.add(this.getEnchantText(enchantment, EnchantmentHelper.getEnchantmentLevel((Enchantment)enchantment, (ItemStack)stack)));
        }
    }

    public boolean isDamageable() {
        return this.damageable;
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public void renderStack(int x, int y, int enchHeight) {
        GlStateManager.pushMatrix();
        GlStateManager.depthMask((boolean)true);
        GlStateManager.clear((int)256);
        RenderHelper.enableStandardItemLighting();
        StackRenderer.mc.getRenderItem().zLevel = -150.0f;
        GlStateManager.disableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.disableCull();
        int height = enchHeight > 4 ? (enchHeight - 4) * 8 / 2 : 0;
        mc.getRenderItem().renderItemAndEffectIntoGUI(this.stack, x, y + height);
        mc.getRenderItem().renderItemOverlays(StackRenderer.mc.fontRenderer, this.stack, x, y + height);
        StackRenderer.mc.getRenderItem().zLevel = 0.0f;
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.scale((float)0.5f, (float)0.5f, (float)0.5f);
        GlStateManager.disableDepth();
        this.renderEnchants(this.stack, x, y - 24);
        GlStateManager.enableDepth();
        GlStateManager.scale((float)2.0f, (float)2.0f, (float)2.0f);
        GlStateManager.popMatrix();
    }

    public void renderStack2D(int x, int y, int enchHeight) {
        GlStateManager.pushMatrix();
        GlStateManager.depthMask((boolean)true);
        GlStateManager.clear((int)256);
        RenderHelper.enableStandardItemLighting();
        StackRenderer.mc.getRenderItem().zLevel = -150.0f;
        GlStateManager.disableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.disableCull();
        int height = enchHeight > 4 ? (enchHeight - 4) * 8 / 2 : 0;
        mc.getRenderItem().renderItemAndEffectIntoGUI(this.stack, x, y + height);
        mc.getRenderItem().renderItemOverlays(StackRenderer.mc.fontRenderer, this.stack, x, y + height);
        StackRenderer.mc.getRenderItem().zLevel = 0.0f;
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.scale((float)0.5f, (float)0.5f, (float)0.5f);
        GlStateManager.disableDepth();
        this.renderEnchants(this.stack, x, y * 2 + 5);
        GlStateManager.enableDepth();
        GlStateManager.scale((float)2.0f, (float)2.0f, (float)2.0f);
        GlStateManager.popMatrix();
    }

    public void renderDurability(float x, float y) {
        GlStateManager.scale((float)0.5f, (float)0.5f, (float)0.5f);
        GlStateManager.disableDepth();
        Managers.TEXT.drawStringWithShadow((int)(this.percent * 100.0f) + "%", x * 2.0f, y, this.color);
        GlStateManager.enableDepth();
        GlStateManager.scale((float)2.0f, (float)2.0f, (float)2.0f);
    }

    public void renderText(int y) {
        GlStateManager.scale((float)0.5f, (float)0.5f, (float)0.5f);
        GlStateManager.disableDepth();
        String name = this.stack.getDisplayName();
        Managers.TEXT.drawStringWithShadow(name, -Managers.TEXT.getStringWidth(name) / 2, y, -1);
        GlStateManager.enableDepth();
        GlStateManager.scale((float)2.0f, (float)2.0f, (float)2.0f);
    }

    public void renderText(float x, float y) {
        GlStateManager.scale((float)0.5f, (float)0.5f, (float)0.5f);
        GlStateManager.disableDepth();
        String name = this.stack.getDisplayName();
        Managers.TEXT.drawStringWithShadow(name, x + (float)(-Managers.TEXT.getStringWidth(name) / 2), y, -1);
        GlStateManager.enableDepth();
        GlStateManager.scale((float)2.0f, (float)2.0f, (float)2.0f);
    }

    private void renderEnchants(ItemStack stack, int xOffset, int yOffset) {
        for (String enchantment : this.enchantTexts) {
            if (enchantment == null) continue;
            Managers.TEXT.drawStringWithShadow(enchantment, (float)xOffset * 2.0f, yOffset, -1);
            yOffset += 8;
        }
        if (stack.getItem().equals(Items.GOLDEN_APPLE) && stack.hasEffect()) {
            Managers.TEXT.drawStringWithShadow("\u00a7cGod", (float)xOffset * 2.0f, yOffset, -3977919);
        }
    }

    private String getEnchantText(Enchantment ench, int lvl) {
        int lvlOffset;
        ResourceLocation resource = (ResourceLocation)Enchantment.REGISTRY.getNameForObject(ench);
        String name = resource == null ? ench.getName() : resource.toString();
        int n = lvlOffset = lvl > 1 ? 12 : 13;
        if (name.length() > lvlOffset) {
            name = name.substring(10, lvlOffset);
        }
        if (lvl > 1) {
            name = name + lvl;
        }
        return name.length() < 2 ? name : TextUtil.capitalize(name);
    }
}
