package me.earth.earthhack.impl.modules.render.tracers;

import java.awt.Color;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.core.ducks.entity.IEntityRenderer;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.esp.ESP;
import me.earth.earthhack.impl.modules.render.tracers.Tracers;
import me.earth.earthhack.impl.modules.render.tracers.mode.BodyPart;
import me.earth.earthhack.impl.modules.render.tracers.mode.TracerMode;
import me.earth.earthhack.impl.util.render.ColorHelper;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

final class ListenerRender
extends ModuleListener<Tracers, Render3DEvent> {
    private static final ModuleCache<ESP> ESP = Caches.getModule(ESP.class);

    public ListenerRender(Tracers module) {
        super(module, Render3DEvent.class, Integer.MIN_VALUE);
    }

    @Override
    public void invoke(Render3DEvent event) {
        Entity renderEntity = mc.getRenderViewEntity() == null ? ListenerRender.mc.player : mc.getRenderViewEntity();
        int i = 0;
        for (Entity entity : ((Tracers)this.module).sorted) {
            if (i >= ((Tracers)this.module).tracers.getValue()) break;
            if (!((Tracers)this.module).isValid(entity)) continue;
            Vec3d interpolation = Interpolation.interpolateEntity(entity);
            double x = interpolation.x;
            double y = interpolation.y;
            double z = interpolation.z;
            AxisAlignedBB bb = ((Tracers)this.module).target.getValue() == BodyPart.Head ? new AxisAlignedBB(x - 0.25, y + (double)entity.height - 0.45, z - 0.25, x + 0.25, y + (double)entity.height + 0.055, z + 0.25) : new AxisAlignedBB(x - 0.4, y, z - 0.4, x + 0.4, y + (double)entity.height + 0.18, z + 0.4);
            RenderUtil.startRender();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            if (entity instanceof EntityPlayer && Managers.FRIENDS.contains(entity.getName())) {
                GL11.glColor4f(0.33333334f, 0.78431374f, 0.78431374f, 0.55f);
            } else {
                float distance = renderEntity.getDistance(entity);
                float red = distance >= 60.0f ? 120.0f : distance + distance;
                Color color = ColorHelper.toColor(red, 100.0f, 50.0f, 0.55f);
                GL11.glColor4f((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f);
            }
            boolean viewBobbing = ListenerRender.mc.gameSettings.viewBobbing;
            ListenerRender.mc.gameSettings.viewBobbing = false;
            ((IEntityRenderer)((Object)ListenerRender.mc.entityRenderer)).invokeOrientCamera(event.getPartialTicks());
            ListenerRender.mc.gameSettings.viewBobbing = viewBobbing;
            GL11.glLineWidth(((Tracers)this.module).lineWidth.getValue().floatValue());
            Vec3d rotateYaw = new Vec3d(0.0, 0.0, 1.0).rotatePitch(-((float)Math.toRadians(renderEntity.rotationPitch))).rotateYaw(-((float)Math.toRadians(renderEntity.rotationYaw)));
            GL11.glBegin(1);
            if (((Tracers)this.module).mode.getValue() == TracerMode.Stem && !ESP.isEnabled()) {
                GL11.glVertex3d(x, y, z);
                GL11.glVertex3d(x, (double)renderEntity.getEyeHeight() + y, z);
            }
            if (((Tracers)this.module).lines.getValue().booleanValue()) {
                GL11.glVertex3d(rotateYaw.x, (double)renderEntity.getEyeHeight() + rotateYaw.y, rotateYaw.z);
                switch (((Tracers)this.module).target.getValue()) {
                    case Head: {
                        GL11.glVertex3d(x, y + (double)entity.height - 0.18, z);
                        break;
                    }
                    case Body: {
                        GL11.glVertex3d(x, y + (double)(entity.height / 2.0f), z);
                        break;
                    }
                    case Feet: {
                        GL11.glVertex3d(x, y, z);
                    }
                }
            }
            GL11.glEnd();
            GL11.glTranslated(x, y, z);
            GL11.glTranslated(-x, -y, -z);
            switch (((Tracers)this.module).mode.getValue()) {
                case Outline: {
                    RenderUtil.doPosition(bb);
                    break;
                }
                case Fill: {
                    RenderUtil.fillBox(bb);
                }
            }
            GlStateManager.popMatrix();
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            RenderUtil.endRender();
            ++i;
        }
    }
}
