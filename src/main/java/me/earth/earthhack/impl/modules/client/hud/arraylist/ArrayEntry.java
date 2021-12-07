/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.renderer.GlStateManager
 *  org.lwjgl.opengl.GL11
 */
package me.earth.earthhack.impl.modules.client.hud.arraylist;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Hidden;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.hud.HUD;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class ArrayEntry
implements Globals {
    private Module module;
    private float x;
    private float startX;
    private boolean atDesired;
    private static final ModuleCache<HUD> HUD_MODULE_CACHE = Caches.getModule(HUD.class);
    private final StopWatch stopWatch = new StopWatch();

    public ArrayEntry(Module module) {
        this.module = module;
        this.x = this.startX = (float)new ScaledResolution(mc).getScaledWidth();
        this.stopWatch.reset();
    }

    public void drawArrayEntry(float desiredX, float desiredY) {
        float textWidth = HUD.RENDERER.getStringWidth(ModuleUtil.getHudName(this.getModule()));
        float xSpeed = textWidth / (float)(Minecraft.getDebugFPS() >> 2);
        GlStateManager.pushMatrix();
        GL11.glEnable((int)3089);
        RenderUtil.scissor(desiredX - textWidth - 1.0f, desiredY - 1.0f, desiredX + 1.0f, desiredY + HUD.RENDERER.getStringHeight() + 3.0f);
        ((HUD)HUD_MODULE_CACHE.get()).renderText(ModuleUtil.getHudName(this.getModule()), this.getX(), desiredY);
        GL11.glDisable((int)3089);
        GlStateManager.popMatrix();
        if (((Module)Caches.getModule(this.getModule().getClass()).get()).isEnabled() && ((Module)Caches.getModule(this.getModule().getClass()).get()).isHidden() != Hidden.Hidden) {
            if (this.stopWatch.passed(1000L)) {
                this.setX(desiredX - textWidth);
                this.setAtDesired(true);
            } else if (this.isAtDesired()) {
                this.setX(desiredX - textWidth);
            } else if (!this.isDone(desiredX)) {
                if (this.getX() != desiredX - textWidth) {
                    this.setX(Math.max(this.getX() - xSpeed, desiredX - textWidth));
                }
            } else {
                this.setAtDesired(true);
            }
        } else {
            if (!this.shouldDelete()) {
                this.setX(this.getX() + xSpeed);
            } else {
                ((HUD)HUD_MODULE_CACHE.get()).getRemoveEntries().put(this.module, this);
            }
            this.setAtDesired(false);
            this.stopWatch.reset();
        }
    }

    private boolean isDone(float desiredX) {
        float textWidth = HUD.RENDERER.getStringWidth(ModuleUtil.getHudName(this.getModule()));
        return this.getX() <= desiredX - textWidth;
    }

    private boolean shouldDelete() {
        return this.getX() > this.getStartX();
    }

    public Module getModule() {
        return this.module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public float getX() {
        return this.x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public boolean isAtDesired() {
        return this.atDesired;
    }

    public void setAtDesired(boolean atDesired) {
        this.atDesired = atDesired;
    }

    public float getStartX() {
        return this.startX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }
}

