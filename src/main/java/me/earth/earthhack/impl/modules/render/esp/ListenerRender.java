package me.earth.earthhack.impl.modules.render.esp;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.esp.ESP;
import me.earth.earthhack.impl.modules.render.esp.mode.EspMode;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

final class ListenerRender
extends ModuleListener<ESP, Render3DEvent> {
    public ListenerRender(ESP module) {
        super(module, Render3DEvent.class, Integer.MIN_VALUE);
    }

    @Override
    public void invoke(Render3DEvent event) {
        if (((ESP)this.module).mode.getValue() == EspMode.Outline && ((ESP)this.module).storage.getValue().booleanValue()) {
            ((ESP)this.module).drawTileEntities();
        }
        if (((ESP)this.module).items.getValue().booleanValue()) {
            boolean fancyGraphics = ListenerRender.mc.gameSettings.fancyGraphics;
            ListenerRender.mc.gameSettings.fancyGraphics = false;
            ESP.isRendering = true;
            float gammaSetting = ListenerRender.mc.gameSettings.gammaSetting;
            ListenerRender.mc.gameSettings.gammaSetting = 100.0f;
            Entity renderEntity = RenderUtil.getEntity();
            Frustum frustum = Interpolation.createFrustum(renderEntity);
            for (Entity entity : ListenerRender.mc.world.loadedEntityList) {
                AxisAlignedBB bb;
                if (!(entity instanceof EntityItem) || entity.isDead || !frustum.isBoundingBoxInFrustum(bb = entity.getEntityBoundingBox())) continue;
                GL11.glPushMatrix();
                Vec3d i = Interpolation.interpolateEntity(entity);
                RenderUtil.drawNametag(((EntityItem)entity).getItem().getDisplayName(), i.x, i.y, i.z, ((ESP)this.module).scale.getValue().floatValue(), -1, false);
                RenderUtil.color(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glPopMatrix();
            }
            ESP.isRendering = false;
            ListenerRender.mc.gameSettings.gammaSetting = gammaSetting;
            ListenerRender.mc.gameSettings.fancyGraphics = fancyGraphics;
        }
    }
}
