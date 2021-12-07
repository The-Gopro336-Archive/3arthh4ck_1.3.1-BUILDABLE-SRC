/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBow
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.util.math.Vec3d
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.util.glu.Cylinder
 */
package me.earth.earthhack.impl.modules.render.trajectories;

import java.util.List;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.trajectories.Trajectories;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;

final class ListenerRender
extends ModuleListener<Trajectories, Render3DEvent> {
    public ListenerRender(Trajectories module) {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
        if (ListenerRender.mc.player == null || ListenerRender.mc.world == null || ListenerRender.mc.gameSettings.thirdPersonView != 0) {
            return;
        }
        if (!(ListenerRender.mc.player.getHeldItemMainhand() != ItemStack.EMPTY && ListenerRender.mc.player.getHeldItemMainhand().getItem() instanceof ItemBow || ListenerRender.mc.player.getHeldItemMainhand() != ItemStack.EMPTY && ((Trajectories)this.module).isThrowable(ListenerRender.mc.player.getHeldItemMainhand().getItem()) || ListenerRender.mc.player.getHeldItemOffhand() != ItemStack.EMPTY && ((Trajectories)this.module).isThrowable(ListenerRender.mc.player.getHeldItemOffhand().getItem()))) {
            return;
        }
        double renderPosX = Interpolation.getRenderPosX();
        double renderPosY = Interpolation.getRenderPosY();
        double renderPosZ = Interpolation.getRenderPosZ();
        Item item = null;
        if (ListenerRender.mc.player.getHeldItemMainhand() != ItemStack.EMPTY && (ListenerRender.mc.player.getHeldItemMainhand().getItem() instanceof ItemBow || ((Trajectories)this.module).isThrowable(ListenerRender.mc.player.getHeldItemMainhand().getItem()))) {
            item = ListenerRender.mc.player.getHeldItemMainhand().getItem();
        } else if (ListenerRender.mc.player.getHeldItemOffhand() != ItemStack.EMPTY && ((Trajectories)this.module).isThrowable(ListenerRender.mc.player.getHeldItemOffhand().getItem())) {
            item = ListenerRender.mc.player.getHeldItemOffhand().getItem();
        }
        if (item == null) {
            return;
        }
        RenderUtil.startRender();
        double posX = renderPosX - (double)(MathHelper.cos((float)(ListenerRender.mc.player.rotationYaw / 180.0f * (float)Math.PI)) * 0.16f);
        double posY = renderPosY + (double)ListenerRender.mc.player.getEyeHeight() - 0.1000000014901161;
        double posZ = renderPosZ - (double)(MathHelper.sin((float)(ListenerRender.mc.player.rotationYaw / 180.0f * (float)Math.PI)) * 0.16f);
        float maxDist = ((Trajectories)this.module).getDistance(item);
        double motionX = -MathHelper.sin((float)(ListenerRender.mc.player.rotationYaw / 180.0f * (float)Math.PI)) * MathHelper.cos((float)(ListenerRender.mc.player.rotationPitch / 180.0f * (float)Math.PI)) * maxDist;
        double motionY = -MathHelper.sin((float)((ListenerRender.mc.player.rotationPitch - (float)((Trajectories)this.module).getThrowPitch(item)) / 180.0f * 3.141593f)) * maxDist;
        double motionZ = MathHelper.cos((float)(ListenerRender.mc.player.rotationYaw / 180.0f * (float)Math.PI)) * MathHelper.cos((float)(ListenerRender.mc.player.rotationPitch / 180.0f * (float)Math.PI)) * maxDist;
        int var6 = 72000 - ListenerRender.mc.player.getItemInUseCount();
        float power = (float)var6 / 20.0f;
        power = (power * power + power * 2.0f) / 3.0f;
        if (power > 1.0f) {
            power = 1.0f;
        }
        float distance = MathHelper.sqrt((double)(motionX * motionX + motionY * motionY + motionZ * motionZ));
        motionX /= (double)distance;
        motionY /= (double)distance;
        motionZ /= (double)distance;
        float pow = (item instanceof ItemBow ? power * 2.0f : 1.0f) * ((Trajectories)this.module).getThrowVelocity(item);
        motionX *= (double)pow;
        motionY *= (double)pow;
        motionZ *= (double)pow;
        if (!ListenerRender.mc.player.onGround) {
            motionY += ListenerRender.mc.player.motionY;
        }
        GlStateManager.color((float)((float)((Trajectories)this.module).color.getValue().getRed() / 255.0f), (float)((float)((Trajectories)this.module).color.getValue().getGreen() / 255.0f), (float)((float)((Trajectories)this.module).color.getValue().getBlue() / 255.0f), (float)((float)((Trajectories)this.module).color.getValue().getAlpha() / 255.0f));
        GL11.glEnable((int)2848);
        float size = (float)(item instanceof ItemBow ? 0.3 : 0.25);
        boolean hasLanded = false;
        Entity landingOnEntity = null;
        RayTraceResult landingPosition = null;
        GL11.glBegin((int)3);
        while (!hasLanded && posY > 0.0) {
            Vec3d present = new Vec3d(posX, posY, posZ);
            Vec3d future = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
            RayTraceResult possibleLandingStrip = ListenerRender.mc.world.rayTraceBlocks(present, future, false, true, false);
            if (possibleLandingStrip != null && possibleLandingStrip.typeOfHit != RayTraceResult.Type.MISS) {
                landingPosition = possibleLandingStrip;
                hasLanded = true;
            }
            AxisAlignedBB arrowBox = new AxisAlignedBB(posX - (double)size, posY - (double)size, posZ - (double)size, posX + (double)size, posY + (double)size, posZ + (double)size);
            List<Entity> entities = ((Trajectories)this.module).getEntitiesWithinAABB(arrowBox.offset(motionX, motionY, motionZ).expand(1.0, 1.0, 1.0));
            for (Entity entity : entities) {
                Entity boundingBox = entity;
                if (!boundingBox.canBeCollidedWith() || boundingBox == ListenerRender.mc.player) continue;
                float var7 = 0.3f;
                AxisAlignedBB var8 = boundingBox.getEntityBoundingBox().expand((double)var7, (double)var7, (double)var7);
                RayTraceResult possibleEntityLanding = var8.calculateIntercept(present, future);
                if (possibleEntityLanding == null) continue;
                hasLanded = true;
                landingOnEntity = boundingBox;
                landingPosition = possibleEntityLanding;
            }
            if (landingOnEntity != null) {
                GlStateManager.color((float)1.0f, (float)0.0f, (float)0.0f, (float)1.0f);
            }
            float motionAdjustment = 0.99f;
            motionY *= (double)0.99f;
            this.drawLine3D((posX += (motionX *= (double)0.99f)) - renderPosX, (posY += (motionY -= (double)((Trajectories)this.module).getGravity(item))) - renderPosY, (posZ += (motionZ *= (double)0.99f)) - renderPosZ);
        }
        GL11.glEnd();
        if (((Trajectories)this.module).landed.getValue().booleanValue() && landingPosition != null && landingPosition.typeOfHit == RayTraceResult.Type.BLOCK) {
            GlStateManager.translate((double)(posX - renderPosX), (double)(posY - renderPosY), (double)(posZ - renderPosZ));
            int side = landingPosition.sideHit.getIndex();
            if (side == 2) {
                GlStateManager.rotate((float)90.0f, (float)1.0f, (float)0.0f, (float)0.0f);
            } else if (side == 3) {
                GlStateManager.rotate((float)90.0f, (float)1.0f, (float)0.0f, (float)0.0f);
            } else if (side == 4) {
                GlStateManager.rotate((float)90.0f, (float)0.0f, (float)0.0f, (float)1.0f);
            } else if (side == 5) {
                GlStateManager.rotate((float)90.0f, (float)0.0f, (float)0.0f, (float)1.0f);
            }
            Cylinder c = new Cylinder();
            GlStateManager.rotate((float)-90.0f, (float)1.0f, (float)0.0f, (float)0.0f);
            c.setDrawStyle(100013);
            if (landingOnEntity != null) {
                GlStateManager.color((float)0.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glLineWidth((float)2.5f);
                c.draw(0.5f, 0.15f, 0.0f, 8, 1);
                GL11.glLineWidth((float)0.1f);
                GlStateManager.color((float)1.0f, (float)0.0f, (float)0.0f, (float)1.0f);
            }
            c.draw(0.5f, 0.15f, 0.0f, 8, 1);
        }
        RenderUtil.endRender();
    }

    public void drawLine3D(double var1, double var2, double var3) {
        GL11.glVertex3d((double)var1, (double)var2, (double)var3);
    }
}

