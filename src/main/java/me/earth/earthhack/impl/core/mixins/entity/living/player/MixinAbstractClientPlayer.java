package me.earth.earthhack.impl.core.mixins.entity.living.player;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.core.mixins.entity.living.player.MixinEntityPlayer;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.norender.NoRender;
import net.minecraft.client.entity.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={AbstractClientPlayer.class})
public abstract class MixinAbstractClientPlayer
extends MixinEntityPlayer {
    private static final ModuleCache<NoRender> NO_RENDER = Caches.getModule(NoRender.class);

    @Shadow
    public abstract boolean func_175149_v();

    @Inject(method={"getFovModifier"}, at={@At(value="HEAD")}, cancellable=true)
    private void getFovModifierHook(CallbackInfoReturnable<Float> info) {
        if (NO_RENDER.returnIfPresent(NoRender::dynamicFov, false).booleanValue()) {
            info.setReturnValue(Float.valueOf(1.0f));
        }
    }
}
