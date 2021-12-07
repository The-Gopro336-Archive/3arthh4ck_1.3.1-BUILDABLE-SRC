/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  org.lwjgl.opengl.GL11
 */
package me.earth.earthhack.impl.modules.render.chams;

import java.awt.Color;
import me.earth.earthhack.impl.event.events.render.ModelRenderEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.chams.Chams;
import me.earth.earthhack.impl.modules.render.chams.mode.ChamsMode;
import me.earth.earthhack.impl.modules.render.esp.ESP;
import net.minecraft.entity.Entity;
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
        if (!ESP.isRendering && ((Chams)this.module).mode.getValue() == ChamsMode.JelloTop && ((Chams)this.module).isValid((Entity)(entity = event.getEntity()))) {
            Color color = ((Chams)this.module).getVisibleColor((Entity)event.getEntity());
            GL11.glPushMatrix();
            GL11.glPushAttrib((int)1048575);
            GL11.glDisable((int)3008);
            GL11.glDisable((int)3553);
            GL11.glDisable((int)2896);
            GL11.glEnable((int)3042);
            GL11.glLineWidth((float)1.5f);
            GL11.glBlendFunc((int)770, (int)771);
            GL11.glEnable((int)2960);
            GL11.glEnable((int)10754);
            GL11.glDepthMask((boolean)false);
            GL11.glDisable((int)2929);
            GL11.glColor4f((float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)color.getAlpha() / 255.0f));
            this.render(event);
            GL11.glDepthMask((boolean)true);
            GL11.glEnable((int)2929);
            GL11.glEnable((int)3553);
            GL11.glEnable((int)2896);
            GL11.glDisable((int)3042);
            GL11.glEnable((int)3008);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
            event.setCancelled(true);
            this.render(event);
        }
    }

    private void render(ModelRenderEvent.Post event) {
        event.getModel().render((Entity)event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScale());
    }
}

