/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec2f
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.opengl.GL11
 */
package me.earth.earthhack.impl.util.render;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.Vec2d;
import me.earth.earthhack.impl.util.render.ColorUtil;
import me.earth.earthhack.impl.util.render.GLUProjection;
import me.earth.earthhack.impl.util.render.GlShader;
import me.earth.earthhack.impl.util.render.Interpolation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class RenderUtil
implements Globals {
    private static ScaledResolution res;
    private static final GlShader IMAGE_SHADER;
    private static final FloatBuffer screenCoords;
    private static final IntBuffer viewport;
    private static final FloatBuffer modelView;
    private static final FloatBuffer projection;

    public static void updateMatrices() {
        GL11.glGetFloat((int)2982, (FloatBuffer)modelView);
        GL11.glGetFloat((int)2983, (FloatBuffer)projection);
        GL11.glGetInteger((int)2978, (IntBuffer)viewport);
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        GLUProjection.getInstance().updateMatrices(viewport, modelView, projection, (float)res.getScaledWidth() / (float)Minecraft.getMinecraft().displayWidth, (float)res.getScaledHeight() / (float)Minecraft.getMinecraft().displayHeight);
    }

    public static Entity getEntity() {
        return mc.getRenderViewEntity() == null ? RenderUtil.mc.player : mc.getRenderViewEntity();
    }

    public static void renderBox(BlockPos pos, Color color, float height) {
        GL11.glPushMatrix();
        GL11.glPushAttrib((int)1048575);
        AxisAlignedBB bb = Interpolation.interpolatePos(pos, height);
        RenderUtil.startRender();
        RenderUtil.drawOutline(bb, 1.5f, color);
        RenderUtil.endRender();
        Color boxColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 76);
        RenderUtil.startRender();
        RenderUtil.drawBox(bb, boxColor);
        RenderUtil.endRender();
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    public static void renderBox(BlockPos pos, Color color, float height, int boxAlpha) {
        GL11.glPushMatrix();
        GL11.glPushAttrib((int)1048575);
        AxisAlignedBB bb = Interpolation.interpolatePos(pos, height);
        RenderUtil.startRender();
        RenderUtil.drawOutline(bb, 1.5f, color);
        RenderUtil.endRender();
        Color boxColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), boxAlpha);
        RenderUtil.startRender();
        RenderUtil.drawBox(bb, boxColor);
        RenderUtil.endRender();
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    public static void renderBox(AxisAlignedBB bb, Color color, Color outLineColor, float lineWidth) {
        GL11.glPushMatrix();
        GL11.glPushAttrib((int)1048575);
        RenderUtil.startRender();
        RenderUtil.drawOutline(bb, lineWidth, outLineColor);
        RenderUtil.endRender();
        RenderUtil.startRender();
        RenderUtil.drawBox(bb, color);
        RenderUtil.endRender();
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    public static void drawBox(AxisAlignedBB bb) {
        GL11.glPushMatrix();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDisable((int)2896);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        RenderUtil.fillBox(bb);
        GL11.glDisable((int)2848);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2896);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
    }

    public static void drawBox(AxisAlignedBB bb, Color color) {
        GL11.glPushMatrix();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDisable((int)2896);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        RenderUtil.color(color);
        RenderUtil.fillBox(bb);
        GL11.glDisable((int)2848);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2896);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
    }

    public static void drawOutline(AxisAlignedBB bb, float lineWidth) {
        GL11.glPushMatrix();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDisable((int)2896);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        GL11.glLineWidth((float)lineWidth);
        RenderUtil.fillOutline(bb);
        GL11.glLineWidth((float)1.0f);
        GL11.glDisable((int)2848);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2896);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
    }

    public static void drawOutline(AxisAlignedBB bb, float lineWidth, Color color) {
        GL11.glPushMatrix();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDisable((int)2896);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        GL11.glLineWidth((float)lineWidth);
        RenderUtil.color(color);
        RenderUtil.fillOutline(bb);
        GL11.glLineWidth((float)1.0f);
        GL11.glDisable((int)2848);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2896);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
    }

    public static void fillOutline(AxisAlignedBB bb) {
        if (bb != null) {
            GL11.glBegin((int)1);
            GL11.glVertex3d((double)bb.minX, (double)bb.minY, (double)bb.minZ);
            GL11.glVertex3d((double)bb.maxX, (double)bb.minY, (double)bb.minZ);
            GL11.glVertex3d((double)bb.maxX, (double)bb.minY, (double)bb.minZ);
            GL11.glVertex3d((double)bb.maxX, (double)bb.minY, (double)bb.maxZ);
            GL11.glVertex3d((double)bb.maxX, (double)bb.minY, (double)bb.maxZ);
            GL11.glVertex3d((double)bb.minX, (double)bb.minY, (double)bb.maxZ);
            GL11.glVertex3d((double)bb.minX, (double)bb.minY, (double)bb.maxZ);
            GL11.glVertex3d((double)bb.minX, (double)bb.minY, (double)bb.minZ);
            GL11.glVertex3d((double)bb.minX, (double)bb.minY, (double)bb.minZ);
            GL11.glVertex3d((double)bb.minX, (double)bb.maxY, (double)bb.minZ);
            GL11.glVertex3d((double)bb.maxX, (double)bb.minY, (double)bb.minZ);
            GL11.glVertex3d((double)bb.maxX, (double)bb.maxY, (double)bb.minZ);
            GL11.glVertex3d((double)bb.maxX, (double)bb.minY, (double)bb.maxZ);
            GL11.glVertex3d((double)bb.maxX, (double)bb.maxY, (double)bb.maxZ);
            GL11.glVertex3d((double)bb.minX, (double)bb.minY, (double)bb.maxZ);
            GL11.glVertex3d((double)bb.minX, (double)bb.maxY, (double)bb.maxZ);
            GL11.glVertex3d((double)bb.minX, (double)bb.maxY, (double)bb.minZ);
            GL11.glVertex3d((double)bb.maxX, (double)bb.maxY, (double)bb.minZ);
            GL11.glVertex3d((double)bb.maxX, (double)bb.maxY, (double)bb.minZ);
            GL11.glVertex3d((double)bb.maxX, (double)bb.maxY, (double)bb.maxZ);
            GL11.glVertex3d((double)bb.maxX, (double)bb.maxY, (double)bb.maxZ);
            GL11.glVertex3d((double)bb.minX, (double)bb.maxY, (double)bb.maxZ);
            GL11.glVertex3d((double)bb.minX, (double)bb.maxY, (double)bb.maxZ);
            GL11.glVertex3d((double)bb.minX, (double)bb.maxY, (double)bb.minZ);
            GL11.glEnd();
        }
    }

    public static void fillBox(AxisAlignedBB boundingBox) {
        if (boundingBox != null) {
            GL11.glBegin((int)7);
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.minY), (double)((float)boundingBox.maxZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.minY), (double)((float)boundingBox.maxZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.maxZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.maxZ));
            GL11.glEnd();
            GL11.glBegin((int)7);
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.minY), (double)((float)boundingBox.maxZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.minY), (double)((float)boundingBox.maxZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.maxZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.maxZ));
            GL11.glEnd();
            GL11.glBegin((int)7);
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.minY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.minY), (double)((float)boundingBox.maxZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.maxZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.minZ));
            GL11.glEnd();
            GL11.glBegin((int)7);
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.minY), (double)((float)boundingBox.maxZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.minY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.maxZ));
            GL11.glEnd();
            GL11.glBegin((int)7);
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.minY), (double)((float)boundingBox.maxZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.minY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.maxZ));
            GL11.glEnd();
            GL11.glBegin((int)7);
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.minY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.minY), (double)((float)boundingBox.maxZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.maxZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.minZ));
            GL11.glEnd();
            GL11.glBegin((int)7);
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.minY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.minY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.minZ));
            GL11.glEnd();
            GL11.glBegin((int)7);
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.minY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.minY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.minZ));
            GL11.glEnd();
            GL11.glBegin((int)7);
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.maxZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.maxZ));
            GL11.glEnd();
            GL11.glBegin((int)7);
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.maxZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.maxZ));
            GL11.glEnd();
            GL11.glBegin((int)7);
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.minY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.minY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.minY), (double)((float)boundingBox.maxZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.minY), (double)((float)boundingBox.maxZ));
            GL11.glEnd();
            GL11.glBegin((int)7);
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.minY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.minY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.minY), (double)((float)boundingBox.maxZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.minY), (double)((float)boundingBox.maxZ));
            GL11.glEnd();
        }
    }

    public static void prepare(float x, float y, float x1, float y1, float lineWidth, int color, int color1, int color2) {
        RenderUtil.startRender();
        RenderUtil.prepare(x, y, x1, y1, color2, color1);
        RenderUtil.color(color);
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glLineWidth((float)lineWidth);
        GL11.glBegin((int)3);
        GL11.glVertex2f((float)x, (float)y);
        GL11.glVertex2f((float)x, (float)y1);
        GL11.glVertex2f((float)x1, (float)y1);
        GL11.glVertex2f((float)x1, (float)y);
        GL11.glVertex2f((float)x, (float)y);
        GL11.glEnd();
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
        RenderUtil.endRender();
    }

    public static void prepare(float x, float y, float x1, float y1, float lineWidth, int color, int color1) {
        RenderUtil.startRender();
        RenderUtil.prepare(x, y, x1, y1, color);
        RenderUtil.color(color1);
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glLineWidth((float)lineWidth);
        GL11.glBegin((int)3);
        GL11.glVertex2f((float)x, (float)y);
        GL11.glVertex2f((float)x, (float)y1);
        GL11.glVertex2f((float)x1, (float)y1);
        GL11.glVertex2f((float)x1, (float)y);
        GL11.glVertex2f((float)x, (float)y);
        GL11.glEnd();
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
        RenderUtil.endRender();
    }

    public static void prepare(float x, float y, float x1, float y1, int color, int color1) {
        RenderUtil.startRender();
        GL11.glShadeModel((int)7425);
        GL11.glBegin((int)7);
        RenderUtil.color(color);
        GL11.glVertex2f((float)x, (float)y1);
        GL11.glVertex2f((float)x1, (float)y1);
        RenderUtil.color(color1);
        GL11.glVertex2f((float)x1, (float)y);
        GL11.glVertex2f((float)x, (float)y);
        GL11.glEnd();
        GL11.glShadeModel((int)7424);
        RenderUtil.endRender();
    }

    public static void prepare(float x, float y, float x1, float y1, int color) {
        RenderUtil.startRender();
        RenderUtil.color(color);
        RenderUtil.scissor(x, y, x1, y1);
        RenderUtil.endRender();
    }

    public static void scissor(float x, float y, float x1, float y1) {
        res = new ScaledResolution(mc);
        int scale = res.getScaleFactor();
        GL11.glScissor((int)((int)(x * (float)scale)), (int)((int)(((float)res.getScaledHeight() - y1) * (float)scale)), (int)((int)((x1 - x) * (float)scale)), (int)((int)((y1 - y) * (float)scale)));
    }

    public static void color(Color color) {
        GL11.glColor4f((float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)color.getAlpha() / 255.0f));
    }

    public static void color(int color) {
        float[] color4f = ColorUtil.toArray(color);
        GL11.glColor4f((float)color4f[0], (float)color4f[1], (float)color4f[2], (float)color4f[3]);
    }

    public static void color(float r, float g, float b, float a) {
        GL11.glColor4f((float)r, (float)g, (float)b, (float)a);
    }

    public static void startRender() {
        GL11.glPushAttrib((int)1048575);
        GL11.glPushMatrix();
        GL11.glDisable((int)3008);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDisable((int)3553);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        GL11.glEnable((int)2884);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4353);
        GL11.glDisable((int)2896);
    }

    public static void endRender() {
        GL11.glEnable((int)2896);
        GL11.glDisable((int)2848);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2929);
        GL11.glDisable((int)3042);
        GL11.glEnable((int)3008);
        GL11.glDepthMask((boolean)true);
        GL11.glCullFace((int)1029);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    public static void doPosition(AxisAlignedBB bb) {
        GL11.glBegin((int)3);
        GL11.glVertex3d((double)bb.minX, (double)bb.minY, (double)bb.minZ);
        GL11.glVertex3d((double)bb.maxX, (double)bb.minY, (double)bb.minZ);
        GL11.glVertex3d((double)bb.maxX, (double)bb.minY, (double)bb.maxZ);
        GL11.glVertex3d((double)bb.minX, (double)bb.minY, (double)bb.maxZ);
        GL11.glVertex3d((double)bb.minX, (double)bb.minY, (double)bb.minZ);
        GL11.glEnd();
        GL11.glBegin((int)3);
        GL11.glVertex3d((double)bb.minX, (double)bb.maxY, (double)bb.minZ);
        GL11.glVertex3d((double)bb.maxX, (double)bb.maxY, (double)bb.minZ);
        GL11.glVertex3d((double)bb.maxX, (double)bb.maxY, (double)bb.maxZ);
        GL11.glVertex3d((double)bb.minX, (double)bb.maxY, (double)bb.maxZ);
        GL11.glVertex3d((double)bb.minX, (double)bb.maxY, (double)bb.minZ);
        GL11.glEnd();
        GL11.glBegin((int)1);
        GL11.glVertex3d((double)bb.minX, (double)bb.minY, (double)bb.minZ);
        GL11.glVertex3d((double)bb.minX, (double)bb.maxY, (double)bb.minZ);
        GL11.glVertex3d((double)bb.maxX, (double)bb.minY, (double)bb.minZ);
        GL11.glVertex3d((double)bb.maxX, (double)bb.maxY, (double)bb.minZ);
        GL11.glVertex3d((double)bb.maxX, (double)bb.minY, (double)bb.maxZ);
        GL11.glVertex3d((double)bb.maxX, (double)bb.maxY, (double)bb.maxZ);
        GL11.glVertex3d((double)bb.minX, (double)bb.minY, (double)bb.maxZ);
        GL11.glVertex3d((double)bb.minX, (double)bb.maxY, (double)bb.maxZ);
        GL11.glEnd();
    }

    public static boolean mouseWithinBounds(int mouseX, int mouseY, double x, double y, double width, double height) {
        return (double)mouseX >= x && (double)mouseX <= x + width && (double)mouseY >= y && (double)mouseY <= y + height;
    }

    public static void drawNametag(String text, AxisAlignedBB interpolated, double scale, int color) {
        RenderUtil.drawNametag(text, interpolated, scale, color, true);
    }

    public static void drawNametag(String text, AxisAlignedBB interpolated, double scale, int color, boolean rectangle) {
        double x = (interpolated.minX + interpolated.maxX) / 2.0;
        double y = (interpolated.minY + interpolated.maxY) / 2.0;
        double z = (interpolated.minZ + interpolated.maxZ) / 2.0;
        RenderUtil.drawNametag(text, x, y, z, scale, color, rectangle);
    }

    public static void drawNametag(String text, double x, double y, double z, double scale, int color) {
        RenderUtil.drawNametag(text, x, y, z, scale, color, true);
    }

    public static void drawNametag(String text, double x, double y, double z, double scale, int color, boolean rectangle) {
        double dist = RenderUtil.getEntity().getDistance(x + RenderUtil.mc.getRenderManager().viewerPosX, y + RenderUtil.mc.getRenderManager().viewerPosY, z + RenderUtil.mc.getRenderManager().viewerPosZ);
        int textWidth = Managers.TEXT.getStringWidth(text) / 2;
        double scaling = 0.0018 + scale * dist;
        if (dist <= 8.0) {
            scaling = 0.0245;
        }
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset((float)1.0f, (float)-1500000.0f);
        GlStateManager.disableLighting();
        GlStateManager.translate((double)x, (double)(y + (double)0.4f), (double)z);
        GlStateManager.rotate((float)(-RenderUtil.mc.getRenderManager().playerViewY), (float)0.0f, (float)1.0f, (float)0.0f);
        float xRot = RenderUtil.mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f;
        GlStateManager.rotate((float)RenderUtil.mc.getRenderManager().playerViewX, (float)xRot, (float)0.0f, (float)0.0f);
        GlStateManager.scale((double)(-scaling), (double)(-scaling), (double)scaling);
        GlStateManager.disableDepth();
        if (rectangle) {
            GlStateManager.enableBlend();
            RenderUtil.prepare(-textWidth - 1, -Managers.TEXT.getStringHeight(), textWidth + 2, 1.0f, 1.8f, 0x55000400, 0x33000000);
            GlStateManager.disableBlend();
        }
        GlStateManager.enableBlend();
        Managers.TEXT.drawStringWithShadow(text, -textWidth, -(RenderUtil.mc.fontRenderer.FONT_HEIGHT - 1), color);
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset((float)1.0f, (float)1500000.0f);
        GlStateManager.popMatrix();
    }

    public static void drawImageBlock(BlockPos pos) {
        assert (IMAGE_SHADER != null);
        GL11.glPushAttrib((int)1048575);
        GL11.glPushMatrix();
        AxisAlignedBB bb = Interpolation.interpolatePos(pos, 1.0f);
        IMAGE_SHADER.bind();
        mc.getTextureManager().bindTexture(new ResourceLocation("earthhack:textures/client/galaxy.jpg"));
        IMAGE_SHADER.set("overlaySampler", 0);
        IMAGE_SHADER.set("dimensions", new Vec2f((float)RenderUtil.mc.displayWidth, (float)RenderUtil.mc.displayHeight));
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        RenderUtil.fillBox(bb);
        GL11.glDepthMask((boolean)false);
        GL11.glDisable((int)2929);
        IMAGE_SHADER.unbind();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    public static Vec2d to2D(double x, double y, double z) {
        GLUProjection.Projection projection = GLUProjection.getInstance().project(x, y, z, GLUProjection.ClampMode.ORTHOGONAL, true);
        return new Vec2d(projection.getX(), projection.getY());
    }

    static {
        IMAGE_SHADER = GlShader.createShader("image");
        screenCoords = BufferUtils.createFloatBuffer((int)3);
        viewport = BufferUtils.createIntBuffer((int)16);
        modelView = BufferUtils.createFloatBuffer((int)16);
        projection = BufferUtils.createFloatBuffer((int)16);
        res = new ScaledResolution(mc);
    }
}

