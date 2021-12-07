/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockLiquid
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 */
package me.earth.earthhack.impl.core.mixins.block;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.core.mixins.block.MixinBlock;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.player.liquids.LiquidInteract;
import me.earth.earthhack.impl.modules.render.xray.XRay;
import me.earth.earthhack.impl.modules.render.xray.mode.XrayMode;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={BlockLiquid.class})
public abstract class MixinBlockLiquid
extends MixinBlock {
    private static final ModuleCache<LiquidInteract> LIQUID_INTERACT = Caches.getModule(LiquidInteract.class);
    private static final ModuleCache<XRay> XRAY = Caches.getModule(XRay.class);

    @Inject(method={"canCollideCheck"}, at={@At(value="HEAD")}, cancellable=true)
    public void canCollideCheckHook(IBlockState blockState, boolean hitIfLiquid, CallbackInfoReturnable<Boolean> info) {
        if (LIQUID_INTERACT.isEnabled()) {
            info.setReturnValue(true);
        }
    }

    @Inject(method={"shouldSideBeRendered"}, at={@At(value="HEAD")}, cancellable=true)
    private void shouldSideBeRenderedHook(IBlockState state, IBlockAccess access, BlockPos pos, EnumFacing facing, CallbackInfoReturnable<Boolean> info) {
        if (XRAY.isEnabled() && ((XRay)XRAY.get()).getMode() == XrayMode.Opacity) {
            info.setReturnValue(access.getBlockState(pos.offset(facing)).getMaterial() != this.material);
        }
    }
}

