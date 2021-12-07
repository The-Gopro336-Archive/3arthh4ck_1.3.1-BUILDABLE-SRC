package me.earth.earthhack.impl.modules.combat.autocrystal;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.render.TextRenderer;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.RenderDamage;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.RenderDamagePos;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

final class ListenerRender
extends ModuleListener<AutoCrystal, Render3DEvent> {
    private final Map<BlockPos, Long> fadeList = new HashMap<BlockPos, Long>();
    private static final ResourceLocation CRYSTAL_LOCATION = new ResourceLocation("earthhack:textures/client/crystal.png");

    public ListenerRender(AutoCrystal module) {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
        BlockPos pos;
        RenderDamagePos mode = ((AutoCrystal)this.module).renderDamage.getValue();
        if (((AutoCrystal)this.module).render.getValue().booleanValue() && ((AutoCrystal)this.module).box.getValue().booleanValue() && ((AutoCrystal)this.module).fade.getValue().booleanValue() && !((AutoCrystal)this.module).isPingBypass()) {
            for (Map.Entry<BlockPos, Long> set : this.fadeList.entrySet()) {
                if (((AutoCrystal)this.module).getRenderPos() == set.getKey()) continue;
                Color boxColor = ((AutoCrystal)this.module).boxColor.getValue();
                Color outlineColor = ((AutoCrystal)this.module).outLine.getValue();
                float maxBoxAlpha = boxColor.getAlpha();
                float maxOutlineAlpha = outlineColor.getAlpha();
                float alphaBoxAmount = maxBoxAlpha / (float)((AutoCrystal)this.module).fadeTime.getValue().intValue();
                float alphaOutlineAmount = maxOutlineAlpha / (float)((AutoCrystal)this.module).fadeTime.getValue().intValue();
                int fadeBoxAlpha = MathHelper.clamp((int)((int)(alphaBoxAmount * (float)(set.getValue() + (long)((AutoCrystal)this.module).fadeTime.getValue().intValue() - System.currentTimeMillis()))), (int)0, (int)((int)maxBoxAlpha));
                int fadeOutlineAlpha = MathHelper.clamp((int)((int)(alphaOutlineAmount * (float)(set.getValue() + (long)((AutoCrystal)this.module).fadeTime.getValue().intValue() - System.currentTimeMillis()))), (int)0, (int)((int)maxOutlineAlpha));
                if (!((AutoCrystal)this.module).box.getValue().booleanValue()) continue;
                RenderUtil.renderBox(Interpolation.interpolatePos(set.getKey(), 1.0f), new Color(boxColor.getRed(), boxColor.getGreen(), boxColor.getBlue(), fadeBoxAlpha), new Color(outlineColor.getRed(), outlineColor.getGreen(), outlineColor.getBlue(), fadeOutlineAlpha), 1.5f);
            }
        }
        if (((AutoCrystal)this.module).render.getValue().booleanValue() && !((AutoCrystal)this.module).isPingBypass() && (pos = ((AutoCrystal)this.module).getRenderPos()) != null) {
            if (!((AutoCrystal)this.module).fade.getValue().booleanValue() && ((AutoCrystal)this.module).box.getValue().booleanValue()) {
                RenderUtil.renderBox(Interpolation.interpolatePos(pos, 1.0f), ((AutoCrystal)this.module).boxColor.getValue(), ((AutoCrystal)this.module).outLine.getValue(), 1.5f);
            }
            if (mode != RenderDamagePos.None) {
                this.renderDamage(pos);
            }
            if (((AutoCrystal)this.module).fade.getValue().booleanValue()) {
                this.fadeList.put(pos, System.currentTimeMillis());
            }
        }
        this.fadeList.entrySet().removeIf(e -> (Long)e.getValue() + (long)((AutoCrystal)this.module).fadeTime.getValue().intValue() < System.currentTimeMillis());
    }

    private void renderDamage(BlockPos pos) {
        String text = ((AutoCrystal)this.module).damage;
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset((float)1.0f, (float)-1500000.0f);
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        double x = (double)pos.getX() + 0.5;
        double y = (double)pos.getY() + (((AutoCrystal)this.module).renderDamage.getValue() == RenderDamagePos.OnTop ? 1.35 : 0.5);
        double z = (double)pos.getZ() + 0.5;
        float scale = 0.016666668f * (((AutoCrystal)this.module).renderMode.getValue() == RenderDamage.Indicator ? 0.95f : 1.3f);
        GlStateManager.translate((double)(x - Interpolation.getRenderPosX()), (double)(y - Interpolation.getRenderPosY()), (double)(z - Interpolation.getRenderPosZ()));
        GlStateManager.glNormal3f((float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.rotate((float)(-ListenerRender.mc.player.rotationYaw), (float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.rotate((float)ListenerRender.mc.player.rotationPitch, (float)(ListenerRender.mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f), (float)0.0f, (float)0.0f);
        GlStateManager.scale((float)(-scale), (float)(-scale), (float)scale);
        int distance = (int)ListenerRender.mc.player.getDistance(x, y, z);
        float scaleD = (float)distance / 2.0f / 3.0f;
        if (scaleD < 1.0f) {
            scaleD = 1.0f;
        }
        GlStateManager.scale((float)scaleD, (float)scaleD, (float)scaleD);
        TextRenderer m = Managers.TEXT;
        GlStateManager.translate((double)(-((double)m.getStringWidth(text) / 2.0)), (double)0.0, (double)0.0);
        if (((AutoCrystal)this.module).renderMode.getValue() == RenderDamage.Indicator) {
            Color clr = ((AutoCrystal)this.module).indicatorColor.getValue();
            Render2DUtil.drawUnfilledCircle((float)m.getStringWidth(text) / 2.0f, 0.0f, 22.0f, new Color(5, 5, 5, clr.getAlpha()).getRGB(), 5.0f);
            Render2DUtil.drawCircle((float)m.getStringWidth(text) / 2.0f, 0.0f, 22.0f, clr.getRGB());
            m.drawString(text, 0.0f, 6.0f, new Color(255, 255, 255).getRGB());
            Minecraft.getMinecraft().getTextureManager().bindTexture(CRYSTAL_LOCATION);
            Gui.drawScaledCustomSizeModalRect((int)((int)((float)m.getStringWidth(text) / 2.0f) - 10), (int)-17, (float)0.0f, (float)0.0f, (int)12, (int)12, (int)22, (int)22, (float)12.0f, (float)12.0f);
        } else {
            m.drawStringWithShadow(text, 0.0f, 0.0f, new Color(255, 255, 255).getRGB());
        }
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset((float)1.0f, (float)1500000.0f);
        GlStateManager.popMatrix();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }
}
