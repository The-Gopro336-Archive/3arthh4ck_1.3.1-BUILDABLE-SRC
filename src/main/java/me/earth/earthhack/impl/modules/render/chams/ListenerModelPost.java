package me.earth.earthhack.impl.modules.render.chams;

import java.awt.Color;
import me.earth.earthhack.impl.event.events.render.ModelRenderEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.chams.Chams;
import me.earth.earthhack.impl.modules.render.chams.mode.ChamsMode;
import me.earth.earthhack.impl.modules.render.esp.ESP;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

final class ListenerModelPost
extends ModuleListener<Chams, ModelRenderEvent.Post> {
    public ListenerModelPost(Chams module) {
        super(module, ModelRenderEvent.Post.class);
    }

    @Override
    public void invoke(ModelRenderEvent.Post event) {
        EntityLivingBase entity;
        if (!ESP.isRendering && ((Chams)this.module).mode.getValue() == ChamsMode.JelloTop && ((Chams)this.module).isValid(entity = event.getEntity())) {
            Color color = ((Chams)this.module).getVisibleColor(event.getEntity());
            GL11.glPushMatrix();
            GL11.glPushAttrib(1048575);
            GL11.glDisable(3008);
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glEnable(3042);
            GL11.glLineWidth(1.5f);
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(2960);
            GL11.glEnable(10754);
            GL11.glDepthMask(false);
            GL11.glDisable(2929);
            GL11.glColor4f((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f);
            this.render(event);
            GL11.glDepthMask(true);
            GL11.glEnable(2929);
            GL11.glEnable(3553);
            GL11.glEnable(2896);
            GL11.glDisable(3042);
            GL11.glEnable(3008);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
            event.setCancelled(true);
            this.render(event);
        }
    }

    private void render(ModelRenderEvent.Post event) {
        event.getModel().render(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScale());
    }
}
