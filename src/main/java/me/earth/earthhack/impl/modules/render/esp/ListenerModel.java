/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.entity.Entity
 */
package me.earth.earthhack.impl.modules.render.esp;

import java.awt.Color;
import me.earth.earthhack.impl.event.events.render.ModelRenderEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.esp.ESP;
import me.earth.earthhack.impl.modules.render.esp.mode.EspMode;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

final class ListenerModel
extends ModuleListener<ESP, ModelRenderEvent.Pre> {
    public ListenerModel(ESP module) {
        super(module, ModelRenderEvent.Pre.class);
    }

    @Override
    public void invoke(ModelRenderEvent.Pre event) {
        if (((ESP)this.module).mode.getValue() == EspMode.Outline) {
            if (!((ESP)this.module).isValid((Entity)event.getEntity())) {
                return;
            }
            this.render(event);
            Color clr = ((ESP)this.module).getEntityColor((Entity)event.getEntity());
            ((ESP)this.module).renderOne(((ESP)this.module).lineWidth.getValue().floatValue());
            this.render(event);
            GlStateManager.glLineWidth((float)((ESP)this.module).lineWidth.getValue().floatValue());
            ((ESP)this.module).renderTwo();
            this.render(event);
            GlStateManager.glLineWidth((float)((ESP)this.module).lineWidth.getValue().floatValue());
            ((ESP)this.module).renderThree();
            ((ESP)this.module).renderFour(clr);
            this.render(event);
            GlStateManager.glLineWidth((float)((ESP)this.module).lineWidth.getValue().floatValue());
            ((ESP)this.module).renderFive();
            event.setCancelled(true);
        }
    }

    private void render(ModelRenderEvent.Pre event) {
        event.getModel().render((Entity)event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScale());
    }
}

