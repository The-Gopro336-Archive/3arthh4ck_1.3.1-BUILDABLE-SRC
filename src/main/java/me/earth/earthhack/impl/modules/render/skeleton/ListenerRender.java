package me.earth.earthhack.impl.modules.render.skeleton;

import java.awt.Color;
import java.util.List;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.killaura.KillAura;
import me.earth.earthhack.impl.modules.render.skeleton.Skeleton;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

final class ListenerRender
extends ModuleListener<Skeleton, Render3DEvent> {
    private static final ModuleCache<KillAura> KILL_AURA = Caches.getModule(KillAura.class);

    public ListenerRender(Skeleton module) {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
        boolean lightning = GL11.glIsEnabled(2896);
        boolean blend = GL11.glIsEnabled(3042);
        boolean texture = GL11.glIsEnabled(3553);
        boolean depth = GL11.glIsEnabled(2929);
        boolean lineSmooth = GL11.glIsEnabled(2848);
        if (lightning) {
            GL11.glDisable(2896);
        }
        if (!blend) {
            GL11.glEnable(3042);
        }
        GL11.glLineWidth(1.0f);
        if (texture) {
            GL11.glDisable(3553);
        }
        if (depth) {
            GL11.glDisable(2929);
        }
        if (!lineSmooth) {
            GL11.glEnable(2848);
        }
        GlStateManager.blendFunc((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GL11.glHint(3154, 4354);
        GlStateManager.depthMask((boolean)false);
        List playerEntities = ListenerRender.mc.world.playerEntities;
        Entity renderEntity = RenderUtil.getEntity();
        ((Skeleton)this.module).rotations.keySet().removeIf(player -> player == null || !playerEntities.contains(player) || player.equals(renderEntity) || player.isPlayerSleeping() || EntityUtil.isDead(player));
        playerEntities.forEach(player -> {
            float[][] rotations = ((Skeleton)this.module).rotations.get(player);
            if (rotations != null) {
                GlStateManager.pushMatrix();
                if (Managers.FRIENDS.contains(player.getName())) {
                    Color friendClr = ((Skeleton)this.module).friendColor.getValue();
                    GlStateManager.color((float)((float)friendClr.getRed() / 255.0f), (float)((float)friendClr.getGreen() / 255.0f), (float)((float)friendClr.getBlue() / 255.0f), (float)((float)friendClr.getAlpha() / 255.0f));
                } else {
                    EntityPlayer autoCrystal = Managers.TARGET.getAutoCrystal();
                    Entity killAuraTarget = KILL_AURA.returnIfPresent(KillAura::getTarget, null);
                    if (player.equals(autoCrystal) || player.equals(killAuraTarget)) {
                        Color targetClr = ((Skeleton)this.module).targetColor.getValue();
                        GlStateManager.color((float)((float)targetClr.getRed() / 255.0f), (float)((float)targetClr.getGreen() / 255.0f), (float)((float)targetClr.getBlue() / 255.0f), (float)((float)targetClr.getAlpha() / 255.0f));
                    } else {
                        Color clr = ((Skeleton)this.module).color.getValue();
                        GlStateManager.color((float)((float)clr.getRed() / 255.0f), (float)((float)clr.getGreen() / 255.0f), (float)((float)clr.getBlue() / 255.0f), (float)((float)clr.getAlpha() / 255.0f));
                    }
                }
                Vec3d interpolation = Interpolation.interpolateEntity(player);
                double pX = interpolation.x;
                double pY = interpolation.y;
                double pZ = interpolation.z;
                GlStateManager.translate((double)pX, (double)pY, (double)pZ);
                GlStateManager.rotate((float)(-player.renderYawOffset), (float)0.0f, (float)1.0f, (float)0.0f);
                GlStateManager.translate((double)0.0, (double)0.0, (double)(player.isSneaking() ? -0.235 : 0.0));
                float sneak = player.isSneaking() ? 0.6f : 0.75f;
                GlStateManager.pushMatrix();
                GlStateManager.translate((double)-0.125, (double)sneak, (double)0.0);
                if (rotations[3][0] != 0.0f) {
                    GlStateManager.rotate((float)(rotations[3][0] * 57.295776f), (float)1.0f, (float)0.0f, (float)0.0f);
                }
                if (rotations[3][1] != 0.0f) {
                    GlStateManager.rotate((float)(rotations[3][1] * 57.295776f), (float)0.0f, (float)1.0f, (float)0.0f);
                }
                if (rotations[3][2] != 0.0f) {
                    GlStateManager.rotate((float)(rotations[3][2] * 57.295776f), (float)0.0f, (float)0.0f, (float)1.0f);
                }
                GlStateManager.glBegin((int)3);
                GL11.glVertex3d(0.0, 0.0, 0.0);
                GL11.glVertex3d(0.0, -sneak, 0.0);
                GlStateManager.glEnd();
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.translate((double)0.125, (double)sneak, (double)0.0);
                if (rotations[4][0] != 0.0f) {
                    GlStateManager.rotate((float)(rotations[4][0] * 57.295776f), (float)1.0f, (float)0.0f, (float)0.0f);
                }
                if (rotations[4][1] != 0.0f) {
                    GlStateManager.rotate((float)(rotations[4][1] * 57.295776f), (float)0.0f, (float)1.0f, (float)0.0f);
                }
                if (rotations[4][2] != 0.0f) {
                    GlStateManager.rotate((float)(rotations[4][2] * 57.295776f), (float)0.0f, (float)0.0f, (float)1.0f);
                }
                GlStateManager.glBegin((int)3);
                GL11.glVertex3d(0.0, 0.0, 0.0);
                GL11.glVertex3d(0.0, -sneak, 0.0);
                GlStateManager.glEnd();
                GlStateManager.popMatrix();
                GlStateManager.translate((double)0.0, (double)0.0, (double)(player.isSneaking() ? 0.25 : 0.0));
                GlStateManager.pushMatrix();
                double sneakOffset = 0.0;
                if (player.isSneaking()) {
                    sneakOffset = -0.05;
                }
                GlStateManager.translate((double)0.0, (double)sneakOffset, (double)(player.isSneaking() ? -0.01725 : 0.0));
                GlStateManager.pushMatrix();
                GlStateManager.translate((double)-0.375, (double)((double)sneak + 0.55), (double)0.0);
                if (rotations[1][0] != 0.0f) {
                    GlStateManager.rotate((float)(rotations[1][0] * 57.295776f), (float)1.0f, (float)0.0f, (float)0.0f);
                }
                if (rotations[1][1] != 0.0f) {
                    GlStateManager.rotate((float)(rotations[1][1] * 57.295776f), (float)0.0f, (float)1.0f, (float)0.0f);
                }
                if (rotations[1][2] != 0.0f) {
                    GlStateManager.rotate((float)(-rotations[1][2] * 57.295776f), (float)0.0f, (float)0.0f, (float)1.0f);
                }
                GlStateManager.glBegin((int)3);
                GL11.glVertex3d(0.0, 0.0, 0.0);
                GL11.glVertex3d(0.0, -0.5, 0.0);
                GlStateManager.glEnd();
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.translate((double)0.375, (double)((double)sneak + 0.55), (double)0.0);
                if (rotations[2][0] != 0.0f) {
                    GlStateManager.rotate((float)(rotations[2][0] * 57.295776f), (float)1.0f, (float)0.0f, (float)0.0f);
                }
                if (rotations[2][1] != 0.0f) {
                    GlStateManager.rotate((float)(rotations[2][1] * 57.295776f), (float)0.0f, (float)1.0f, (float)0.0f);
                }
                if (rotations[2][2] != 0.0f) {
                    GlStateManager.rotate((float)(-rotations[2][2] * 57.295776f), (float)0.0f, (float)0.0f, (float)1.0f);
                }
                GlStateManager.glBegin((int)3);
                GL11.glVertex3d(0.0, 0.0, 0.0);
                GL11.glVertex3d(0.0, -0.5, 0.0);
                GlStateManager.glEnd();
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.translate((double)0.0, (double)((double)sneak + 0.55), (double)0.0);
                if (rotations[0][0] != 0.0f) {
                    GlStateManager.rotate((float)(rotations[0][0] * 57.295776f), (float)1.0f, (float)0.0f, (float)0.0f);
                }
                GlStateManager.glBegin((int)3);
                GL11.glVertex3d(0.0, 0.0, 0.0);
                GL11.glVertex3d(0.0, 0.3, 0.0);
                GlStateManager.glEnd();
                GlStateManager.popMatrix();
                GlStateManager.popMatrix();
                GlStateManager.rotate((float)(player.isSneaking() ? 25.0f : 0.0f), (float)1.0f, (float)0.0f, (float)0.0f);
                if (player.isSneaking()) {
                    sneakOffset = -0.16175;
                }
                GlStateManager.translate((double)0.0, (double)sneakOffset, (double)(player.isSneaking() ? -0.48025 : 0.0));
                GlStateManager.pushMatrix();
                GlStateManager.translate((double)0.0, (double)sneak, (double)0.0);
                GlStateManager.glBegin((int)3);
                GL11.glVertex3d(-0.125, 0.0, 0.0);
                GL11.glVertex3d(0.125, 0.0, 0.0);
                GlStateManager.glEnd();
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.translate((double)0.0, (double)sneak, (double)0.0);
                GlStateManager.glBegin((int)3);
                GL11.glVertex3d(0.0, 0.0, 0.0);
                GL11.glVertex3d(0.0, 0.55, 0.0);
                GlStateManager.glEnd();
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.translate((double)0.0, (double)((double)sneak + 0.55), (double)0.0);
                GlStateManager.glBegin((int)3);
                GL11.glVertex3d(-0.375, 0.0, 0.0);
                GL11.glVertex3d(0.375, 0.0, 0.0);
                GlStateManager.glEnd();
                GlStateManager.popMatrix();
                GlStateManager.popMatrix();
            }
        });
        GlStateManager.depthMask((boolean)true);
        if (!lineSmooth) {
            GL11.glDisable(2848);
        }
        if (depth) {
            GL11.glEnable(2929);
        }
        if (texture) {
            GL11.glEnable(3553);
        }
        if (!blend) {
            GL11.glDisable(3042);
        }
        if (lightning) {
            GL11.glEnable(2896);
        }
    }
}
