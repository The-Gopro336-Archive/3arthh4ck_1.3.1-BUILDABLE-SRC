package me.earth.earthhack.impl.core.ducks.render;

import java.util.List;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;

public interface IShaderGroup {
    public List<Framebuffer> getListFramebuffers();

    public List<Shader> getListShaders();
}
