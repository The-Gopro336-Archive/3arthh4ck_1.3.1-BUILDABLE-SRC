package me.earth.earthhack.impl.modules.render.nametags;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.render.nametags.Nametag;
import me.earth.earthhack.impl.modules.render.nametags.Nametags;
import me.earth.earthhack.impl.modules.render.nametags.StackRenderer;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

final class ListenerRender
extends ModuleListener<Nametags, Render3DEvent> {
    private int xOffset;
    private int maxEnchHeight;
    private boolean renderDurability;

    public ListenerRender(Nametags module) {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
        if (!((Nametags)this.module).twoD.getValue().booleanValue()) {
            ((Nametags)this.module).updateNametags();
            Vec3d interp = Interpolation.interpolateEntity(RenderUtil.getEntity());
            Nametag.isRendering = true;
            for (Nametag nametag : ((Nametags)this.module).nametags) {
                if (nametag.player.isDead || nametag.player.isInvisible() && !((Nametags)this.module).invisibles.getValue().booleanValue() || ((Nametags)this.module).fov.getValue().booleanValue() && !RotationUtil.inFov(nametag.player)) continue;
                Vec3d i = Interpolation.interpolateEntity(nametag.player);
                this.renderNametag(nametag, nametag.player, i.x, i.y, i.z, interp);
            }
            Nametag.isRendering = false;
            if (((Nametags)this.module).debug.getValue().booleanValue()) {
                Entity renderEntity = RenderUtil.getEntity();
                Frustum frustum = Interpolation.createFrustum(renderEntity);
                for (Entity entity : ListenerRender.mc.world.loadedEntityList) {
                    if (entity == null || EntityUtil.isDead(entity) || entity instanceof EntityPlayer || entity.isInvisible() && !((Nametags)this.module).invisibles.getValue().booleanValue() || ((Nametags)this.module).fov.getValue().booleanValue() && !frustum.isBoundingBoxInFrustum(entity.getRenderBoundingBox())) continue;
                    Vec3d i = Interpolation.interpolateEntity(entity);
                    RenderUtil.drawNametag(entity.getEntityId() + "", i.x, i.y, i.z, ((Nametags)this.module).scale.getValue().floatValue(), -1, false);
                }
            }
        }
    }

    private void renderNametag(Nametag nametag, EntityPlayer player, double x, double y, double z, Vec3d mcPlayerInterpolation) {
        double yOffset = y + (player.isSneaking() ? 0.5 : 0.7);
        double xDist = mcPlayerInterpolation.x - x;
        double yDist = mcPlayerInterpolation.y - y;
        double zDist = mcPlayerInterpolation.z - z;
        y = MathHelper.sqrt((double)(xDist * xDist + yDist * yDist + zDist * zDist));
        int nameWidth = nametag.nameWidth / 2;
        double scaling = 0.0018 + (double)((Nametags)this.module).scale.getValue().floatValue() * y;
        if (y <= 8.0) {
            scaling = 0.0245;
        }
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset((float)1.0f, (float)-1500000.0f);
        GlStateManager.disableLighting();
        GlStateManager.translate((float)((float)x), (float)((float)yOffset + 1.4f), (float)((float)z));
        GlStateManager.rotate((float)(-ListenerRender.mc.getRenderManager().playerViewY), (float)0.0f, (float)1.0f, (float)0.0f);
        float xRot = ListenerRender.mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f;
        GlStateManager.rotate((float)ListenerRender.mc.getRenderManager().playerViewX, (float)xRot, (float)0.0f, (float)0.0f);
        GlStateManager.scale((double)(-scaling), (double)(-scaling), (double)scaling);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.enableBlend();
        RenderUtil.prepare(-nameWidth - 1, -Managers.TEXT.getStringHeight(), nameWidth + 2, 1.0f, 1.8f, 0x55000400, 0x33000000);
        GlStateManager.disableBlend();
        Managers.TEXT.drawStringWithShadow(nametag.nameString, -nameWidth, -(Managers.TEXT.getStringHeight() - 1.0f), nametag.nameColor);
        this.xOffset = -nametag.stacks.size() * 8 - (nametag.mainHand == null ? 0 : 8);
        this.maxEnchHeight = nametag.maxEnchHeight;
        this.renderDurability = nametag.renderDura;
        GlStateManager.pushMatrix();
        if (nametag.mainHand != null) {
            this.renderStackRenderer(nametag.mainHand, true);
        }
        for (StackRenderer sr : nametag.stacks) {
            this.renderStackRenderer(sr, false);
        }
        GlStateManager.popMatrix();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset((float)1.0f, (float)1500000.0f);
        GlStateManager.popMatrix();
    }

    private void renderStackRenderer(StackRenderer sr, boolean main) {
        int fontOffset = ((Nametags)this.module).getFontOffset(this.maxEnchHeight);
        if (((Nametags)this.module).armor.getValue().booleanValue()) {
            sr.renderStack(this.xOffset, fontOffset, this.maxEnchHeight);
            fontOffset -= 32;
        }
        if (((Nametags)this.module).durability.getValue().booleanValue() && sr.isDamageable()) {
            sr.renderDurability(this.xOffset, fontOffset);
            fontOffset = (int)((float)fontOffset - Managers.TEXT.getStringHeight());
        } else if (this.renderDurability) {
            fontOffset = (int)((float)fontOffset - Managers.TEXT.getStringHeight());
        }
        if (((Nametags)this.module).itemStack.getValue().booleanValue() && main) {
            sr.renderText(fontOffset);
        }
        if (((Nametags)this.module).armor.getValue().booleanValue() || ((Nametags)this.module).durability.getValue().booleanValue() || sr.isDamageable()) {
            this.xOffset += 16;
        }
    }
}
