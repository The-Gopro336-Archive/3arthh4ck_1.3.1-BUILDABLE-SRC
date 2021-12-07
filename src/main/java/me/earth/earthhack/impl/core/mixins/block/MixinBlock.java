/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.MapColor
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.Minecraft
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.core.mixins.block;

import java.util.Arrays;
import java.util.List;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.core.ducks.block.IBlock;
import me.earth.earthhack.impl.event.events.misc.CollisionEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.jesus.Jesus;
import me.earth.earthhack.impl.modules.movement.phase.Phase;
import me.earth.earthhack.impl.modules.render.xray.XRay;
import me.earth.earthhack.impl.modules.render.xray.mode.XrayMode;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Block.class})
public abstract class MixinBlock
implements IBlock {
    private static final ModuleCache<XRay> XRAY = Caches.getModule(XRay.class);
    private static final ModuleCache<Jesus> JESUS = Caches.getModule(Jesus.class);
    private static final ModuleCache<Phase> PHASE = Caches.getModule(Phase.class);
    private static final Minecraft MC = Minecraft.getMinecraft();
    private final String[] harvestToolNonForge = new String[16];
    private final int[] harvestLevelNonForge = new int[16];
    @Shadow
    @Final
    protected Material material;

    @Shadow
    protected static void addCollisionBoxToList(BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> cBoxes, AxisAlignedBB blockBox) {
        throw new IllegalStateException("MixinBlock.addCollisionBoxToList has not been shadowed");
    }

    @Shadow
    public abstract BlockStateContainer getBlockState();

    @Shadow
    public abstract int getMetaFromState(IBlockState var1);

    @Override
    @Unique
    public void setHarvestLevelNonForge(String toolClass, int level) {
        for (IBlockState state : this.getBlockState().getValidStates()) {
            int idx = this.getMetaFromState(state);
            this.harvestToolNonForge[idx] = toolClass;
            this.harvestLevelNonForge[idx] = level;
        }
    }

    @Override
    @Unique
    public String getHarvestToolNonForge(IBlockState state) {
        return this.harvestToolNonForge[this.getMetaFromState(state)];
    }

    @Override
    @Unique
    public int getHarvestLevelNonForge(IBlockState state) {
        return this.harvestLevelNonForge[this.getMetaFromState(state)];
    }

    @Inject(method={"<init>(Lnet/minecraft/block/material/Material;Lnet/minecraft/block/material/MapColor;)V"}, at={@At(value="RETURN")})
    private void ctrHook(Material blockMaterialIn, MapColor blockMapColorIn, CallbackInfo ci) {
        Arrays.fill(this.harvestLevelNonForge, -1);
    }

    @Deprecated
    @Inject(method={"addCollisionBoxToList(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/entity/Entity;Z)V"}, at={@At(value="HEAD")}, cancellable=true)
    private void addCollisionBoxToListHook_Pre(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> cBoxes, Entity entity, boolean isActualState, CallbackInfo info) {
        if (!JESUS.isEnabled() && !PHASE.isEnabled()) {
            return;
        }
        Block block = (Block)Block.class.cast(this);
        AxisAlignedBB bb = block.getCollisionBoundingBox(state, (IBlockAccess)world, pos);
        CollisionEvent event = new CollisionEvent(pos, bb, entity, block);
        ((Jesus)JESUS.get()).onCollision(event);
        ((Phase)PHASE.get()).onCollision(event);
        if (bb != event.getBB()) {
            bb = event.getBB();
        }
        if (bb != null && entityBox.intersects(bb)) {
            cBoxes.add(bb);
        }
        MixinBlock.addCollisionBoxToList(pos, entityBox, cBoxes, bb);
        info.cancel();
    }

    @Inject(method={"addCollisionBoxToList(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/util/math/AxisAlignedBB;)V"}, at={@At(value="HEAD")}, cancellable=true)
    private static void addCollisionBoxToListHook(BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> cBoxes, AxisAlignedBB blockBox, CallbackInfo info) {
        if (blockBox != Block.NULL_AABB && (JESUS.isEnabled() || PHASE.isEnabled())) {
            AxisAlignedBB bb = blockBox.offset(pos);
            CollisionEvent event = new CollisionEvent(pos, bb, null, MixinBlock.MC.world != null ? MixinBlock.MC.world.getBlockState(pos).getBlock() : null);
            ((Jesus)JESUS.get()).onCollision(event);
            ((Phase)PHASE.get()).onCollision(event);
            if (bb != event.getBB()) {
                bb = event.getBB();
            }
            if (bb != null && entityBox.intersects(bb)) {
                cBoxes.add(bb);
            }
            info.cancel();
        }
    }

    @Inject(method={"isFullCube"}, at={@At(value="HEAD")}, cancellable=true)
    public void isFullCubeHook(IBlockState blockState, CallbackInfoReturnable<Boolean> info) {
        if (XRAY.isEnabled() && ((XRay)XRAY.get()).getMode() == XrayMode.Simple) {
            info.setReturnValue(((XRay)XRAY.get()).shouldRender((Block)Block.class.cast(this)));
        }
    }

    @Inject(method={"getAmbientOcclusionLightValue"}, at={@At(value="HEAD")}, cancellable=true)
    private void ambientValueHook(CallbackInfoReturnable<Float> info) {
        if (XRAY.isEnabled() && ((XRay)XRAY.get()).getMode() == XrayMode.Opacity) {
            info.setReturnValue(Float.valueOf(1.0f));
        }
    }
}

