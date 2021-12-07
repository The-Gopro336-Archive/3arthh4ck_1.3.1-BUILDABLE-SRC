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
        GL20.glUniform1i(this.getUniform("texture"), 0);
        GL20.glUniform2f(this.getUniform("texelSize"), 1.0f / (float)RainbowGlowShader.mc.displayWidth * (this.radius * this.quality), 1.0f / (float)RainbowGlowShader.mc.displayHeight * (this.radius * this.quality));
        GL20.glUniform1f(this.getUniform("divider"), 140.0f);
        GL20.glUniform1f(this.getUniform("radius"), this.radius);
        GL20.glUniform1f(this.getUniform("maxSample"), 10.0f);
        GL20.glUniform1f(this.getUniform("time"), (float)(System.currentTimeMillis() - this.initTime) / 1000.0f);
        GL20.glUniform2f(this.getUniform("resolution"), (float)(RainbowGlowShader.mc.displayWidth * 2) / 20.0f, (float)(RainbowGlowShader.mc.displayHeight * 2) / 20.0f);
    }
}
