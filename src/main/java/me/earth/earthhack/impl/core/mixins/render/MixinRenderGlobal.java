package me.earth.earthhack.impl.core.mixins.render;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.xray.XRay;
import me.earth.earthhack.impl.modules.render.xray.mode.XrayMode;
import net.minecraft.client.renderer.RenderGlobal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value={RenderGlobal.class})
public abstract class MixinRenderGlobal {
    private static final ModuleCache<XRay> XRAY = Caches.getModule(XRay.class);

    @ModifyVariable(method={"setupTerrain"}, at=@At(value="HEAD"))
    private boolean setupTerrainHook(boolean playerSpectator) {
        if (XRAY.isEnabled() && ((XRay)XRAY.get()).getMode() == XrayMode.Opacity) {
            return true;
        }
        return playerSpectator;
    }
}
