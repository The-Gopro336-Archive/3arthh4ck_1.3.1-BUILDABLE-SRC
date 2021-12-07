/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  org.lwjgl.opengl.GL11
 */
package me.earth.earthhack.impl.modules.render.chams;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.chams.Chams;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class ListenerRender
extends ModuleListener<Chams, Render3DEvent> {
    public ListenerRender(Chams module) {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
    }

    private void renderEntities(double renderPosX, double renderPosY, double renderPosZ) {
        for (Entity e : ListenerRender.mc.world.loadedEntityList) {
            if (!((Chams)this.module).isValid(e) || e == mc.getRenderViewEntity()) continue;
            if (e.ticksExisted == 0) {
                e.lastTickPosX = e.posX;
                e.lastTickPosY = e.posY;
                e.lastTickPosZ = e.posZ;
            }
            double d0 = e.lastTickPosX + (e.posX - e.lastTickPosX) * (double)mc.getRenderPartialTicks();
            double d1 = e.lastTickPosY + (e.posY - e.lastTickPosY) * (double)mc.getRenderPartialTicks();
            double d2 = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * (double)mc.getRenderPartialTicks();
            double f = e.prevRotationYaw + (e.rotationYaw - e.prevRotationYaw) * mc.getRenderPartialTicks();
            mc.getRenderManager().renderEntity(e, d0 - renderPosX, d1 - renderPosY, d2 - renderPosZ, (float)f, mc.getRenderPartialTicks(), true);
        }
    }

    public static void drawCompleteImage(float posX, float posY, float width, float height) {
        GL11.glTranslatef((float)posX, (float)posY, (float)0.0f);
        GL11.glBegin((int)7);
        GL11.glTexCoord2f((float)0.0f, (float)0.0f);
        GL11.glVertex3f((float)0.0f, (float)0.0f, (float)0.0f);
        GL11.glTexCoord2f((float)0.0f, (float)1.0f);
        GL11.glVertex3f((float)0.0f, (float)height, (float)0.0f);
        GL11.glTexCoord2f((float)1.0f, (float)1.0f);
        GL11.glVertex3f((float)width, (float)height, (float)0.0f);
        GL11.glTexCoord2f((float)1.0f, (float)0.0f);
        GL11.glVertex3f((float)width, (float)0.0f, (float)0.0f);
        GL11.glEnd();
    }
}

