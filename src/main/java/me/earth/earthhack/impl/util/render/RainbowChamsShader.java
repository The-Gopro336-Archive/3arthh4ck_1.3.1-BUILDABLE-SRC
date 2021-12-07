/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL20
 */
package me.earth.earthhack.impl.util.render;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.chams.Chams;
import me.earth.earthhack.impl.util.render.FramebufferShader;
import org.lwjgl.opengl.GL20;

public class RainbowChamsShader
extends FramebufferShader {
    public static final RainbowChamsShader RAINBOW_CHAMS_SHADER = new RainbowChamsShader();
    private final ModuleCache<Chams> CHAMS = Caches.getModule(Chams.class);
    private final long initTime = System.currentTimeMillis();

    private RainbowChamsShader() {
        super("rainbowchams.frag");
    }

    @Override
    public void setupUniforms() {
        this.setupUniform("time");
        this.setupUniform("resolution");
        this.setupUniform("alpha");
    }

    @Override
    public void updateUniforms() {
        GL20.glUniform1f((int)this.getUniform("time"), (float)((float)(System.currentTimeMillis() - this.initTime) / 1000.0f));
        GL20.glUniform2f((int)this.getUniform("resolution"), (float)((float)(RainbowChamsShader.mc.displayWidth * 2) / 20.0f), (float)((float)(RainbowChamsShader.mc.displayHeight * 2) / 20.0f));
        GL20.glUniform1f((int)this.getUniform("alpha"), (float)1.0f);
    }
}

