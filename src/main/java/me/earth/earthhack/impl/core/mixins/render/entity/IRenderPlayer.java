package me.earth.earthhack.impl.core.mixins.render.entity;

import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={RenderPlayer.class})
public interface IRenderPlayer {
}
