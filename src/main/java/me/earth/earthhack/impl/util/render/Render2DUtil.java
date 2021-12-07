package me.earth.earthhack.impl.util.render;

import java.awt.Color;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.render.IShaderGroup;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.render.GLUProjection;
import me.earth.earthhack.impl.util.render.Interpolation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class Render2DUtil
implements Globals {
    protected static ShaderGroup blurShader;
    protected static float zLevel;
    protected static Framebuffer buffer;
    protected static int lastScale;
    protected static int lastScaleWidth;
    protected static int lastScaleHeight;
    protected static final ResourceLocation shader;
    protected static final StopWatch timer;

    public static void initFboAndShader() {
        try {
            blurShader = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), shader);
            blurShader.createBindFramebuffers(Render2DUtil.mc.displayWidth, Render2DUtil.mc.displayHeight);
            buffer = ((IShaderGroup)((Object)blurShader)).getListFramebuffers().get(0);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setShaderConfigs(float intensity, float blurWidth, float blurHeight) {
        ((IShaderGroup)((Object)blurShader)).getListShaders().get(0).getShaderManager().getShaderUniform("Radius").set(intensity);
        ((IShaderGroup)((Object)blurShader)).getListShaders().get(1).getShaderManager().getShaderUniform("Radius").set(intensity);
        ((IShaderGroup)((Object)blurShader)).getListShaders().get(0).getShaderManager().getShaderUniform("BlurDir").set(blurWidth, blurHeight);
        ((IShaderGroup)((Object)blurShader)).getListShaders().get(1).getShaderManager().getShaderUniform("BlurDir").set(blurHeight, blurWidth);
    }

    public static void drawBlurryRect(float x, float y, float x1, float y1, int intensity, int size) {
        Render2DUtil.drawRect((int)x, (int)y, (int)x1, (int)y1, new Color(50, 50, 50, 50).getRGB());
        Render2DUtil.blurArea((int)x, (int)y, (int)x1 - (int)x, (int)y1 - (int)y, intensity, size, size);
    }

    public static void drawRect(float startX, float startY, float endX, float endY, int color) {
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(startX, endY, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(endX, endY, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(endX, startY, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(startX, startY, 0.0).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawUnfilledCircle(float x, float y, float radius, int color, float width) {
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glLineWidth(width);
        GL11.glBegin(2);
        for (int i = 0; i <= 360; ++i) {
            GL11.glVertex2d((double)x + Math.sin((double)i * 3.141526 / 180.0) * (double)radius, (double)y + Math.cos((double)i * 3.141526 / 180.0) * (double)radius);
        }
        GL11.glEnd();
        GlStateManager.resetColor();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawCircle(float x, float y, float radius, int color) {
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glBegin(9);
        for (int i = 0; i <= 360; ++i) {
            GL11.glVertex2d((double)x + Math.sin((double)i * 3.141526 / 180.0) * (double)radius, (double)y + Math.cos((double)i * 3.141526 / 180.0) * (double)radius);
        }
        GL11.glEnd();
        GlStateManager.resetColor();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawBorderedRect(float x, float y, float x2, float y2, float lineSize, int color, int borderColor) {
        Render2DUtil.drawRect(x, y, x2, y2, color);
        Render2DUtil.drawRect(x, y, x + lineSize, y2, borderColor);
        Render2DUtil.drawRect(x2 - lineSize, y, x2, y2, borderColor);
        Render2DUtil.drawRect(x, y2 - lineSize, x2, y2, borderColor);
        Render2DUtil.drawRect(x, y, x2, y + lineSize, borderColor);
    }

    public static void drawTexturedRect(int x, int y, int textureX, int textureY, int width, int height, int zLevel) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder BufferBuilder2 = tessellator.getBuffer();
        BufferBuilder2.begin(7, DefaultVertexFormats.POSITION_TEX);
        BufferBuilder2.pos(x, y + height, zLevel).tex((float)textureX * 0.00390625f, (float)(textureY + height) * 0.00390625f).endVertex();
        BufferBuilder2.pos(x + width, y + height, zLevel).tex((float)(textureX + width) * 0.00390625f, (float)(textureY + height) * 0.00390625f).endVertex();
        BufferBuilder2.pos(x + width, y, zLevel).tex((float)(textureX + width) * 0.00390625f, (float)textureY * 0.00390625f).endVertex();
        BufferBuilder2.pos(x, y, zLevel).tex((float)textureX * 0.00390625f, (float)textureY * 0.00390625f).endVertex();
        tessellator.draw();
    }

    public static void drawCompleteImage(float posX, float posY, float width, float height) {
        GL11.glPushMatrix();
        GL11.glTranslatef(posX, posY, 0.0f);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(0.0f, height, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(width, height, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(width, 0.0f, 0.0f);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public static void drawCheckMark(float x, float y, int width, int color) {
        float f = (float)(color >> 24 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 16 & 0xFF) / 255.0f;
        float f2 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(color & 0xFF) / 255.0f;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(3.0f);
        GL11.glBegin(3);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        GL11.glVertex2d((double)(x + (float)width) - 6.25, y + 2.75f);
        GL11.glVertex2d((double)(x + (float)width) - 11.5, y + 10.25f);
        GL11.glVertex2d(x + (float)width - 13.75f, y + 7.75f);
        GL11.glEnd();
        GL11.glLineWidth(1.5f);
        GL11.glBegin(3);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glVertex2d((double)(x + (float)width) - 6.5, y + 3.0f);
        GL11.glVertex2d((double)(x + (float)width) - 11.5, y + 10.0f);
        GL11.glVertex2d((double)(x + (float)width) - 13.5, y + 8.0f);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void drawLine(float x, float y, float x1, float y1, float lineWidth, int color) {
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth((float)lineWidth);
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glBegin(1);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x1, y1);
        GL11.glEnd();
        GlStateManager.resetColor();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawCheckeredBackground(float x, float y, float x2, float y2) {
        Render2DUtil.drawRect(x, y, x2, y2, -1);
        boolean offset = false;
        while (y < y2) {
            offset = !offset;
            for (float x1 = x + (float)(offset ? true : false); x1 < x2; x1 += 2.0f) {
                if (x1 > x2 - 1.0f) continue;
                Render2DUtil.drawRect(x1, y, x1 + 1.0f, y + 1.0f, -8355712);
            }
            y += 1.0f;
        }
    }

    public static void drawGradientRect(float left, float top, float right, float bottom, boolean sideways, int startColor, int endColor) {
        float f = (float)(startColor >> 24 & 0xFF) / 255.0f;
        float f1 = (float)(startColor >> 16 & 0xFF) / 255.0f;
        float f2 = (float)(startColor >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(startColor & 0xFF) / 255.0f;
        float f4 = (float)(endColor >> 24 & 0xFF) / 255.0f;
        float f5 = (float)(endColor >> 16 & 0xFF) / 255.0f;
        float f6 = (float)(endColor >> 8 & 0xFF) / 255.0f;
        float f7 = (float)(endColor & 0xFF) / 255.0f;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel((int)7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        if (sideways) {
            bufferbuilder.pos(left, top, zLevel).color(f1, f2, f3, f).endVertex();
            bufferbuilder.pos(left, bottom, zLevel).color(f1, f2, f3, f).endVertex();
            bufferbuilder.pos(right, bottom, zLevel).color(f5, f6, f7, f4).endVertex();
            bufferbuilder.pos(right, top, zLevel).color(f5, f6, f7, f4).endVertex();
        } else {
            bufferbuilder.pos(right, top, zLevel).color(f1, f2, f3, f).endVertex();
            bufferbuilder.pos(left, top, zLevel).color(f1, f2, f3, f).endVertex();
            bufferbuilder.pos(left, bottom, zLevel).color(f5, f6, f7, f4).endVertex();
            bufferbuilder.pos(right, bottom, zLevel).color(f5, f6, f7, f4).endVertex();
        }
        tessellator.draw();
        GlStateManager.shadeModel((int)7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void blurArea(int x, int y, int width, int height, float intensity, float blurWidth, float blurHeight) {
        ScaledResolution scale = new ScaledResolution(Minecraft.getMinecraft());
        int factor = scale.getScaleFactor();
        int factor2 = scale.getScaledWidth();
        int factor3 = scale.getScaledHeight();
        if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3 || buffer == null || blurShader == null) {
            Render2DUtil.initFboAndShader();
        }
        lastScale = factor;
        lastScaleWidth = factor2;
        lastScaleHeight = factor3;
        if (OpenGlHelper.isFramebufferEnabled()) {
            if (timer.passed(1000L)) {
                buffer.framebufferClear();
                timer.reset();
            }
            GL11.glScissor(x * factor, Render2DUtil.mc.displayHeight - y * factor - height * factor, width * factor, height * factor);
            GL11.glEnable(3089);
            Render2DUtil.setShaderConfigs(intensity, blurWidth, blurHeight);
            buffer.bindFramebuffer(true);
            blurShader.render(mc.getRenderPartialTicks());
            mc.getFramebuffer().bindFramebuffer(true);
            GL11.glDisable(3089);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
            buffer.framebufferRenderExt(Render2DUtil.mc.displayWidth, Render2DUtil.mc.displayHeight, false);
            GlStateManager.disableBlend();
            GL11.glScalef(factor, factor, 0.0f);
        }
    }

    public static float[] getOnScreen2DHitBox(Entity e, int width, int height) {
        Vec3d[] corners;
        float x = -1.0f;
        float y = -1.0f;
        float w = width + 1;
        float h = height + 1;
        Vec3d pos = Interpolation.interpolateEntity(e);
        AxisAlignedBB bb = e.getEntityBoundingBox();
        if (e instanceof EntityEnderCrystal) {
            bb = new AxisAlignedBB(bb.minX + (double)0.3f, bb.minY + (double)0.2f, bb.minZ + (double)0.3f, bb.maxX - (double)0.3f, bb.maxY, bb.maxZ - (double)0.3f);
        }
        if (e instanceof EntityItem) {
            bb = new AxisAlignedBB(bb.minX, bb.minY + (double)0.7f, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
        }
        bb = bb.expand(0.15f, 0.1f, 0.15f);
        for (Vec3d vec : corners = new Vec3d[]{new Vec3d(bb.minX - bb.maxX + (double)(e.width / 2.0f), 0.0, bb.minZ - bb.maxZ + (double)(e.width / 2.0f)), new Vec3d(bb.maxX - bb.minX - (double)(e.width / 2.0f), 0.0, bb.minZ - bb.maxZ + (double)(e.width / 2.0f)), new Vec3d(bb.minX - bb.maxX + (double)(e.width / 2.0f), 0.0, bb.maxZ - bb.minZ - (double)(e.width / 2.0f)), new Vec3d(bb.maxX - bb.minX - (double)(e.width / 2.0f), 0.0, bb.maxZ - bb.minZ - (double)(e.width / 2.0f)), new Vec3d(bb.minX - bb.maxX + (double)(e.width / 2.0f), bb.maxY - bb.minY, bb.minZ - bb.maxZ + (double)(e.width / 2.0f)), new Vec3d(bb.maxX - bb.minX - (double)(e.width / 2.0f), bb.maxY - bb.minY, bb.minZ - bb.maxZ + (double)(e.width / 2.0f)), new Vec3d(bb.minX - bb.maxX + (double)(e.width / 2.0f), bb.maxY - bb.minY, bb.maxZ - bb.minZ - (double)(e.width / 2.0f)), new Vec3d(bb.maxX - bb.minX - (double)(e.width / 2.0f), bb.maxY - bb.minY, bb.maxZ - bb.minZ - (double)(e.width / 2.0f))}) {
            GLUProjection.Projection projection = GLUProjection.getInstance().project(pos.x + vec.x, pos.y + vec.y, pos.z + vec.z, GLUProjection.ClampMode.NONE, false);
            x = Math.max(x, (float)projection.getX());
            y = Math.max(y, (float)projection.getY());
            w = Math.min(w, (float)projection.getX());
            h = Math.min(h, (float)projection.getY());
        }
        if (x != -1.0f && y != -1.0f && w != (float)(width + 1) && h != (float)(height + 1)) {
            return new float[]{x, y, w, h};
        }
        return null;
    }

    public static void drawOutlineRect(float x, float y, float w, float h, float lineWidth, int c) {
        Render2DUtil.drawRect(x, y, x - lineWidth, h, c);
        Render2DUtil.drawRect(w + lineWidth, y, w, h, c);
        Render2DUtil.drawRect(x, y, w, y - lineWidth, c);
        Render2DUtil.drawRect(x, h + lineWidth, w, h, c);
    }

    static {
        shader = new ResourceLocation("earthhack:shaders/blur.json");
        timer = new StopWatch();
    }
}
