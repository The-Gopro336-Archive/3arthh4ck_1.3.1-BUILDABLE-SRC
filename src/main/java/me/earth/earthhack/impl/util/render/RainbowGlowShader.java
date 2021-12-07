/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL20
 */
package me.earth.earthhack.impl.util.render;

import me.earth.earthhack.impl.util.render.FramebufferShader;
import org.lwjgl.opengl.GL20;

public class RainbowGlowShader
extends FramebufferShader {
    public static final RainbowGlowShader RAINBOW_GLOW_SHADER = new RainbowGlowShader();
    private final long initTime = System.currentTimeMillis();

    public RainbowGlowShader() {
        super("rainbow.frag");
    }

    @Override
    public void setupUniforms() {
        this.setupUniform("texture");
        this.setupUniform("texelSize");
        this.setupUniform("divider");
        this.setupUniform("radius");
        this.setupUniform("maxSample");
        this.setupUniform("time");
        this.setupUniform("resolution");
    }

    @Override
    public void updateUniforms() {
        GL20.glUniform1i((int)this.getUniform("texture"), (int)0);
        GL20.glUniform2f((int)this.getUniform("texelSize"), (float)(1.0f / (float)RainbowGlowShader.mc.displayWidth * (this.radius * this.quality)), (float)(1.0f / (float)RainbowGlowShader.mc.displayHeight * (this.radius * this.quality)));
        GL20.glUniform1f((int)this.getUniform("divider"), (float)140.0f);
        GL20.glUniform1f((int)this.getUniform("radius"), (float)this.radius);
        GL20.glUniform1f((int)this.getUniform("maxSample"), (float)10.0f);
        GL20.glUniform1f((int)this.getUniform("time"), (float)((float)(System.currentTimeMillis() - this.initTime) / 1000.0f));
        GL20.glUniform2f((int)this.getUniform("resolution"), (float)((float)(RainbowGlowShader.mc.displayWidth * 2) / 20.0f), (float)((float)(RainbowGlowShader.mc.displayHeight * 2) / 20.0f));
    }
}

