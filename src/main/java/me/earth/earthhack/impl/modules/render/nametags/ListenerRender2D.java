package me.earth.earthhack.impl.modules.render.nametags;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4f;
import me.earth.earthhack.impl.core.ducks.IMinecraft;
import me.earth.earthhack.impl.event.events.render.Render2DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.render.nametags.Nametag;
import me.earth.earthhack.impl.modules.render.nametags.Nametags;
import me.earth.earthhack.impl.modules.render.nametags.StackRenderer;
import me.earth.earthhack.impl.util.render.GLUProjection;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;

final class ListenerRender2D
extends ModuleListener<Nametags, Render2DEvent> {
    private int xOffset;
    private int maxEnchHeight;
    private boolean renderDurability;

    public ListenerRender2D(Nametags module) {
        super(module, Render2DEvent.class);
    }

    @Override
    public void invoke(Render2DEvent event) {
        if (((Nametags)this.module).twoD.getValue().booleanValue()) {
            ((Nametags)this.module).updateNametags();
            Nametag.isRendering = true;
            Entity renderEntity = RenderUtil.getEntity();
            Frustum frustum = Interpolation.createFrustum(renderEntity);
            for (Nametag nametag : ((Nametags)this.module).nametags) {
                if (nametag.player.isDead || nametag.player.isInvisible() && !((Nametags)this.module).invisibles.getValue().booleanValue() || ((Nametags)this.module).fov.getValue().booleanValue() && !frustum.isBoundingBoxInFrustum(nametag.player.getRenderBoundingBox())) continue;
                this.renderNametag(nametag, nametag.player, event);
            }
            Nametag.isRendering = false;
        }
    }

    private void renderNametag(Nametag nametag, EntityPlayer entity, Render2DEvent event) {
        double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)((IMinecraft)ListenerRender2D.mc).getTimer().renderPartialTicks;
        double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)((IMinecraft)ListenerRender2D.mc).getTimer().renderPartialTicks;
        double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)((IMinecraft)ListenerRender2D.mc).getTimer().renderPartialTicks;
        AxisAlignedBB bb = entity.getEntityBoundingBox().expand(0.1, 0.1, 0.1);
        Vector3d[] corners = new Vector3d[]{new Vector3d(posX + bb.minX - bb.maxX + (double)(entity.width / 2.0f), posY, posZ + bb.minZ - bb.maxZ + (double)(entity.width / 2.0f)), new Vector3d(posX + bb.maxX - bb.minX - (double)(entity.width / 2.0f), posY, posZ + bb.minZ - bb.maxZ + (double)(entity.width / 2.0f)), new Vector3d(posX + bb.minX - bb.maxX + (double)(entity.width / 2.0f), posY, posZ + bb.maxZ - bb.minZ - (double)(entity.width / 2.0f)), new Vector3d(posX + bb.maxX - bb.minX - (double)(entity.width / 2.0f), posY, posZ + bb.maxZ - bb.minZ - (double)(entity.width / 2.0f)), new Vector3d(posX + bb.minX - bb.maxX + (double)(entity.width / 2.0f), posY + bb.maxY - bb.minY, posZ + bb.minZ - bb.maxZ + (double)(entity.width / 2.0f)), new Vector3d(posX + bb.maxX - bb.minX - (double)(entity.width / 2.0f), posY + bb.maxY - bb.minY, posZ + bb.minZ - bb.maxZ + (double)(entity.width / 2.0f)), new Vector3d(posX + bb.minX - bb.maxX + (double)(entity.width / 2.0f), posY + bb.maxY - bb.minY, posZ + bb.maxZ - bb.minZ - (double)(entity.width / 2.0f)), new Vector3d(posX + bb.maxX - bb.minX - (double)(entity.width / 2.0f), posY + bb.maxY - bb.minY, posZ + bb.maxZ - bb.minZ - (double)(entity.width / 2.0f))};
        Vector4f transformed = new Vector4f((float)event.getResolution().getScaledWidth() * 2.0f, (float)event.getResolution().getScaledHeight() * 2.0f, -1.0f, -1.0f);
        for (Vector3d vec : corners) {
            GLUProjection.Projection result = GLUProjection.getInstance().project(vec.x - ListenerRender2D.mc.getRenderManager().viewerPosX, vec.y - ListenerRender2D.mc.getRenderManager().viewerPosY, vec.z - ListenerRender2D.mc.getRenderManager().viewerPosZ, GLUProjection.ClampMode.NONE, true);
            transformed.setX((float)Math.min((double)transformed.getX(), result.getX()));
            transformed.setY((float)Math.min((double)transformed.getY(), result.getY()));
            transformed.setW((float)Math.max((double)transformed.getW(), result.getX()));
            transformed.setZ((float)Math.max((double)transformed.getZ(), result.getY()));
        }
        float x1 = transformed.x;
        float w1 = transformed.w - x1;
        float y1 = transformed.y;
        int nameWidth = nametag.nameWidth / 2;
        GlStateManager.pushMatrix();
        Managers.TEXT.drawStringWithShadow(nametag.nameString, x1 + w1 / 2.0f - (float)nameWidth, y1 - 3.0f - (float)ListenerRender2D.mc.fontRenderer.FONT_HEIGHT, nametag.nameColor);
        this.xOffset = -nametag.stacks.size() * 8 - (nametag.mainHand == null ? 0 : 8);
        this.maxEnchHeight = nametag.maxEnchHeight;
        this.renderDurability = nametag.renderDura;
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        if (nametag.mainHand != null) {
            this.renderStackRenderer(nametag.mainHand, x1 + w1 / 2.0f, y1 - 3.0f, true);
        }
        for (StackRenderer sr : nametag.stacks) {
            this.renderStackRenderer(sr, x1 + w1 / 2.0f, y1 - 3.0f, false);
        }
        GlStateManager.popMatrix();
    }

    private void renderStackRenderer(StackRenderer sr, float x, float y, boolean main) {
        int fontOffset = ((Nametags)this.module).getFontOffset(this.maxEnchHeight);
        if (((Nametags)this.module).armor.getValue().booleanValue()) {
            sr.renderStack2D((int)x + this.xOffset, (int)y + fontOffset, this.maxEnchHeight);
            fontOffset -= 32;
        }
        if (((Nametags)this.module).durability.getValue().booleanValue() && sr.isDamageable()) {
            sr.renderDurability(x + (float)this.xOffset, y * 2.0f + (float)fontOffset);
            fontOffset = (int)((float)fontOffset - Managers.TEXT.getStringHeight());
        } else if (this.renderDurability) {
            fontOffset = (int)((float)fontOffset - Managers.TEXT.getStringHeight());
        }
        if (((Nametags)this.module).itemStack.getValue().booleanValue() && main) {
            sr.renderText(x * 2.0f, y * 2.0f + (float)fontOffset);
        }
        if (((Nametags)this.module).armor.getValue().booleanValue() || ((Nametags)this.module).durability.getValue().booleanValue() || sr.isDamageable()) {
            this.xOffset += 16;
        }
    }
}
