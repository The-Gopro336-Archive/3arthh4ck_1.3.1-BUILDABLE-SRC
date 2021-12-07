package me.earth.earthhack.impl.core.mixins.render;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.xray.XRay;
import me.earth.earthhack.impl.modules.render.xray.mode.XrayMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={BlockModelRenderer.class})
public abstract class MixinBlockModelRenderer {
    private static final ModuleCache<XRay> XRAY = Caches.getModule(XRay.class);

    @Shadow
    public abstract boolean func_187498_b(IBlockAccess var1, IBakedModel var2, IBlockState var3, BlockPos var4, BufferBuilder var5, boolean var6, long var7);

    @Inject(method={"renderModel(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/renderer/BufferBuilder;ZJ)Z"}, at={@At(value="HEAD")}, cancellable=true)
    private void renderModelHook(IBlockAccess access, IBakedModel model, IBlockState state, BlockPos pos, BufferBuilder bufferBuilder, boolean checkSides, long rand, CallbackInfoReturnable<Boolean> info) {
        if (XRAY.isEnabled() && ((XRay)XRAY.get()).getMode() == XrayMode.Opacity) {
            info.setReturnValue(this.func_187498_b(access, model, state, pos, bufferBuilder, !((XRay)XRAY.get()).isValid(state.getBlock().getLocalizedName()), rand));
        }
    }
}
