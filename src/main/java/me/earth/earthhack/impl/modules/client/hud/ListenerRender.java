package me.earth.earthhack.impl.modules.client.hud;

import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.impl.event.events.render.Render2DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.hud.HUD;
import me.earth.earthhack.impl.modules.client.hud.modes.RenderMode;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

final class ListenerRender
extends ModuleListener<HUD, Render2DEvent> {
    public ListenerRender(HUD module) {
        super(module, Render2DEvent.class);
    }

    @Override
    public void invoke(Render2DEvent event) {
        GL11.glPushMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (((HUD)this.module).animations.getValue().booleanValue()) {
            float ySpeed = 22.0f / (float)(Minecraft.getDebugFPS() >> 2);
            if (ListenerRender.mc.ingameGUI.getChatGUI().getChatOpen()) {
                if (((HUD)this.module).animationY != 14.0f) {
                    if (((HUD)this.module).animationY > 14.0f) {
                        ((HUD)this.module).animationY = Math.max(((HUD)this.module).animationY - ySpeed, 14.0f);
                    } else if (((HUD)this.module).animationY < 14.0f) {
                        ((HUD)this.module).animationY = Math.min(((HUD)this.module).animationY + ySpeed, 14.0f);
                    }
                }
            } else if (((HUD)this.module).animationY != 0.0f) {
                if (((HUD)this.module).animationY > 0.0f) {
                    ((HUD)this.module).animationY = Math.max(((HUD)this.module).animationY - ySpeed, 0.0f);
                } else if (((HUD)this.module).animationY < 0.0f) {
                    ((HUD)this.module).animationY = Math.min(((HUD)this.module).animationY + ySpeed, 0.0f);
                }
            }
        } else {
            ((HUD)this.module).animationY = ListenerRender.mc.ingameGUI.getChatGUI().getChatOpen() ? 14.0f : 0.0f;
        }
        if (((HUD)this.module).renderMode.getValue() == RenderMode.Normal) {
            ((HUD)this.module).renderLogo();
            ((HUD)this.module).renderModules();
        } else if (((HUD)this.module).renderMode.getValue() == RenderMode.Editor) {
            for (HudElement element : Managers.ELEMENTS.getRegistered()) {
                if (ListenerRender.mc.currentScreen != null) continue;
                element.hudUpdate(mc.getRenderPartialTicks());
                if (!element.isEnabled()) continue;
                element.hudDraw(mc.getRenderPartialTicks());
            }
        } else {
            ((HUD)this.module).renderLogo();
            ((HUD)this.module).renderModules();
            for (HudElement element : Managers.ELEMENTS.getRegistered()) {
                if (ListenerRender.mc.currentScreen != null) continue;
                element.hudUpdate(mc.getRenderPartialTicks());
                if (!element.isEnabled()) continue;
                element.hudDraw(mc.getRenderPartialTicks());
            }
        }
        GL11.glPopMatrix();
    }
}
