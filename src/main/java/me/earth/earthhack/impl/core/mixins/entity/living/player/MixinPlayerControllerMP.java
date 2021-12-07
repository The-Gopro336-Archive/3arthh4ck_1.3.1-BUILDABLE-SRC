/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.multiplayer.PlayerControllerMP
 *  net.minecraft.client.multiplayer.WorldClient
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.network.play.client.CPacketPlayerDigging
 *  net.minecraft.network.play.client.CPacketPlayerDigging$Action
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.core.mixins.entity.living.player;

import java.lang.invoke.LambdaMetafactory;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.core.ducks.network.ICPacketPlayerDigging;
import me.earth.earthhack.impl.core.ducks.network.IPlayerControllerMP;
import me.earth.earthhack.impl.event.events.misc.ClickBlockEvent;
import me.earth.earthhack.impl.event.events.misc.DamageBlockEvent;
import me.earth.earthhack.impl.event.events.misc.ResetBlockEvent;
import me.earth.earthhack.impl.event.events.misc.RightClickItemEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.tpssync.TpsSync;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={PlayerControllerMP.class})
public abstract class MixinPlayerControllerMP
implements IPlayerControllerMP {
    private static final ModuleCache<TpsSync> TPS_SYNC = Caches.getModule(TpsSync.class);
    private static final SettingCache<Boolean, BooleanSetting, TpsSync> MINE = Caches.getSetting(TpsSync.class, BooleanSetting.class, "Mine", false);
    @Shadow
    private float curBlockDamageMP;
    @Shadow
    private int blockHitDelay;

    @Shadow
    protected abstract void syncCurrentPlayItem();

    @Override
    @Invoker(value="syncCurrentPlayItem")
    public abstract void syncItem();

    @Override
    @Accessor(value="currentPlayerItem")
    public abstract int getItem();

    @Override
    @Accessor(value="blockHitDelay")
    public abstract void setBlockHitDelay(int var1);

    @Override
    @Accessor(value="blockHitDelay")
    public abstract int getBlockHitDelay();

    @Override
    @Accessor(value="curBlockDamageMP")
    public abstract float getCurBlockDamageMP();

    @Override
    @Accessor(value="curBlockDamageMP")
    public abstract void setCurBlockDamageMP(float var1);

    @Override
    @Accessor(value="isHittingBlock")
    public abstract boolean getIsHittingBlock();

    @Override
    @Accessor(value="isHittingBlock")
    public abstract void setIsHittingBlock(boolean var1);

    @Inject(method={"clickBlock"}, at={@At(value="HEAD")}, cancellable=true)
    private void clickBlockHook(BlockPos pos, EnumFacing facing, CallbackInfoReturnable<Boolean> info) {
        ClickBlockEvent event = new ClickBlockEvent(pos, facing);
        Bus.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            info.cancel();
        }
    }

    @Inject(method={"resetBlockRemoving"}, at={@At(value="HEAD")}, cancellable=true)
    public void resetBlockRemovingHook(CallbackInfo info) {
        ResetBlockEvent event = new ResetBlockEvent();
        Bus.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            info.cancel();
        }
    }

    @Inject(method={"processRightClick"}, at={@At(value="HEAD")}, cancellable=true)
    private void processClickHook(EntityPlayer player, World worldIn, EnumHand hand, CallbackInfoReturnable<EnumActionResult> cir) {
        RightClickItemEvent event = new RightClickItemEvent(player, worldIn, hand);
        Bus.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            cir.setReturnValue(EnumActionResult.PASS);
        }
    }

    @Inject(method={"processRightClickBlock"}, at={@At(value="HEAD")}, cancellable=true)
    private void clickBlockHook(EntityPlayerSP player, WorldClient worldIn, BlockPos pos, EnumFacing direction, Vec3d vec, EnumHand hand, CallbackInfoReturnable<EnumActionResult> info) {
        ClickBlockEvent.Right event = new ClickBlockEvent.Right(pos, direction, vec, hand);
        Bus.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            info.cancel();
        }
    }

    @Inject(method={"onPlayerDamageBlock"}, at={@At(value="HEAD")}, cancellable=true)
    private void onPlayerDamageBlock(BlockPos pos, EnumFacing facing, CallbackInfoReturnable<Boolean> info) {
        DamageBlockEvent event = new DamageBlockEvent(pos, facing, this.curBlockDamageMP, this.blockHitDelay);
        Bus.EVENT_BUS.post(event);
        this.curBlockDamageMP = event.getDamage();
        this.blockHitDelay = event.getDelay();
        if (event.isCancelled()) {
            info.cancel();
        }
    }

    @Redirect(method={"onPlayerDamageBlock"}, at=@At(value="NEW", target="net/minecraft/network/play/client/CPacketPlayerDigging"))
    private CPacketPlayerDigging cPacketPlayerDiggingInitHook(CPacketPlayerDigging.Action actionIn, BlockPos posIn, EnumFacing facingIn) {
        CPacketPlayerDigging packet = new CPacketPlayerDigging(actionIn, posIn, facingIn);
        if (actionIn == CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
            ((ICPacketPlayerDigging)packet).setClientSideBreaking(true);
        }
        return packet;
    }

    @Redirect(method={"onPlayerDamageBlock"}, at=@At(value="INVOKE", target="Lnet/minecraft/block/state/IBlockState;getPlayerRelativeBlockHardness(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)F"))
    public float getPlayerRelativeBlockHardnessHook(IBlockState state, EntityPlayer player, World worldIn, BlockPos pos) {
        return state.getPlayerRelativeBlockHardness(player, worldIn, pos) * (TPS_SYNC.isEnabled() && MINE.getValue() != false ? 1.0f / Managers.TPS.getFactor() : 1.0f);
    }

    @Redirect(method={"updateController"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/multiplayer/PlayerControllerMP;syncCurrentPlayItem()V"))
    private void syncCurrentPlayItemHook(PlayerControllerMP playerControllerMP) {
        Locks.acquire(Locks.PLACE_SWITCH_LOCK, (Runnable)LambdaMetafactory.metafactory(null, null, null, ()V, func_78750_j(), ()V)((MixinPlayerControllerMP)this));
    }
}

