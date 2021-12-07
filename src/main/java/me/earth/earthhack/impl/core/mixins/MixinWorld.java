package me.earth.earthhack.impl.core.mixins;

import java.util.List;
import javax.annotation.Nullable;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.core.ducks.IWorld;
import me.earth.earthhack.impl.event.events.misc.UpdateEntitiesEvent;
import me.earth.earthhack.impl.event.events.movement.WaterPushEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.packets.Packets;
import me.earth.earthhack.impl.modules.player.blocktweaks.BlockTweaks;
import me.earth.earthhack.impl.modules.render.norender.NoRender;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={World.class})
public abstract class MixinWorld
implements IWorld {
    private static final ModuleCache<NoRender> NO_RENDER = Caches.getModule(NoRender.class);
    private static final ModuleCache<Packets> PACKETS = Caches.getModule(Packets.class);
    private static final ModuleCache<BlockTweaks> BLOCK_TWEAKS = Caches.getModule(BlockTweaks.class);

    @Shadow
    public abstract List<Entity> func_72839_b(@Nullable Entity var1, AxisAlignedBB var2);

    @Shadow
    public abstract World func_175643_b();

    @Override
    @Invoker(value="isChunkLoaded")
    public abstract boolean isChunkLoaded(int var1, int var2, boolean var3);

    @Redirect(method={"handleMaterialAcceleration"}, at=@At(value="INVOKE", target="Lnet/minecraft/entity/Entity;isPushedByWater()Z"))
    public boolean isPushedByWaterHook(Entity entity) {
        WaterPushEvent event = new WaterPushEvent(entity);
        Bus.EVENT_BUS.post(event);
        return entity.isPushedByWater() && !event.isCancelled();
    }

    @Inject(method={"checkLightFor"}, at={@At(value="HEAD")}, cancellable=true)
    private void checkLightForHook(EnumSkyBlock skyBlock, BlockPos pos, CallbackInfoReturnable<Boolean> info) {
        if (NO_RENDER.returnIfPresent(NoRender::noSkyLight, false).booleanValue() && skyBlock == EnumSkyBlock.SKY) {
            info.setReturnValue(false);
        }
    }

    @Inject(method={"getRainStrength"}, at={@At(value="HEAD")}, cancellable=true)
    private void getRainStrengthHook(CallbackInfoReturnable<Float> callbackInfoReturnable) {
        if (NO_RENDER.returnIfPresent(NoRender::noWeather, false).booleanValue()) {
            callbackInfoReturnable.setReturnValue(Float.valueOf(0.0f));
        }
    }

    @Inject(method={"spawnParticle(Lnet/minecraft/util/EnumParticleTypes;DDDDDD[I)V"}, at={@At(value="HEAD")}, cancellable=true)
    private void spawnParticleHook(EnumParticleTypes particleType, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int[] parameters, CallbackInfo ci) {
        if (NO_RENDER.isEnabled() && ((NoRender)MixinWorld.NO_RENDER.get()).explosions.getValue().booleanValue()) {
            switch (particleType) {
                case EXPLOSION_NORMAL: 
                case EXPLOSION_LARGE: 
                case EXPLOSION_HUGE: 
                case FIREWORKS_SPARK: {
                    ci.cancel();
                }
            }
        }
    }

    @Redirect(method={"getEntityByID"}, at=@At(value="INVOKE", target="Lnet/minecraft/util/IntHashMap;lookup(I)Ljava/lang/Object;"))
    private Object getEntityByIDHook(IntHashMap<Entity> intHashMap, int id) {
        Entity entity = (Entity)intHashMap.lookup(id);
        if (entity == null && (entity = Managers.SET_DEAD.getEntity(id)) != null) {
            entity.lastTickPosX = entity.posX;
            entity.lastTickPosY = entity.posY;
            entity.lastTickPosZ = entity.posZ;
            entity.prevPosX = entity.posX;
            entity.prevPosY = entity.posY;
            entity.prevPosZ = entity.posZ;
        }
        return entity;
    }

    @Inject(method={"updateEntities"}, at={@At(value="INVOKE", target="Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", ordinal=2)})
    private void updateEntitiesHook(CallbackInfo ci) {
        UpdateEntitiesEvent event = new UpdateEntitiesEvent();
        Bus.EVENT_BUS.post(event);
    }

    @Inject(method={"getBlockState"}, at={@At(value="HEAD")}, cancellable=true)
    private void getBlockStateHook(BlockPos pos, CallbackInfoReturnable<IBlockState> cir) {
        IBlockState state;
        if (PACKETS.isEnabled() && (state = ((Packets)PACKETS.get()).getStateMap().get(pos)) != null) {
            cir.setReturnValue(state);
        }
    }

    @Redirect(method={"mayPlace"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/World;checkNoEntityCollision(Lnet/minecraft/util/math/AxisAlignedBB;Lnet/minecraft/entity/Entity;)Z"))
    private boolean checkNoEntityCollisionHook(World world, AxisAlignedBB bb, Entity entityIn) {
        if (BLOCK_TWEAKS.returnIfPresent(BlockTweaks::isIgnoreFallingActive, false).booleanValue()) {
            for (Entity entity : world.getEntitiesWithinAABBExcludingEntity(null, bb)) {
                if (entity.isDead || entity instanceof EntityFallingBlock || !entity.preventEntitySpawning || entity == entityIn || entityIn != null && entity.isRidingSameEntity(entityIn)) continue;
                return false;
            }
            return true;
        }
        return world.checkNoEntityCollision(bb, entityIn);
    }
}
