package me.earth.earthhack.impl.core.mixins.render.entity;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.core.ducks.render.IRenderItem;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.rainbowenchant.RainbowEnchant;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={RenderItem.class})
public abstract class MixinRenderItem
implements IRenderItem {
    private static final ResourceLocation RESOURCE = new ResourceLocation("textures/rainbow.png");
    private static final ModuleCache<RainbowEnchant> ENCHANT = Caches.getModule(RainbowEnchant.class);

    @Override
    @Accessor(value="notRenderingEffectsInGUI")
    public abstract void setNotRenderingEffectsInGUI(boolean var1);

    @Redirect(method={"renderEffect"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/texture/TextureManager;bindTexture(Lnet/minecraft/util/ResourceLocation;)V", ordinal=0))
    public void bindHook(TextureManager textureManager, ResourceLocation resource) {
        if (ENCHANT.isEnabled()) {
            textureManager.bindTexture(RESOURCE);
        } else {
            textureManager.bindTexture(resource);
        }
    }
}
