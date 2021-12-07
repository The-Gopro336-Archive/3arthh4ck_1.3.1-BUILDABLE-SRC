package me.earth.earthhack.impl.core.mixins.entity.living;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.core.ducks.entity.IEntityLivingBase;
import me.earth.earthhack.impl.core.ducks.entity.IEntityNoInterp;
import me.earth.earthhack.impl.core.ducks.entity.IEntityRemoteAttack;
import me.earth.earthhack.impl.core.mixins.entity.MixinEntity;
import me.earth.earthhack.impl.event.events.misc.DeathEvent;
import me.earth.earthhack.impl.event.events.movement.LiquidJumpEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.nointerp.NoInterp;
import me.earth.earthhack.impl.modules.movement.elytraflight.ElytraFlight;
import me.earth.earthhack.impl.modules.movement.elytraflight.mode.ElytraMode;
import me.earth.earthhack.impl.modules.player.fasteat.FastEat;
import me.earth.earthhack.impl.modules.player.fasteat.mode.FastEatMode;
import me.earth.earthhack.impl.modules.player.spectate.Spectate;
import me.earth.earthhack.impl.util.minecraft.ICachedDamage;
import me.earth.earthhack.impl.util.minecraft.MotionTracker;
import me.earth.earthhack.impl.util.thread.EnchantmentUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={EntityLivingBase.class})
public abstract class MixinEntityLivingBase
extends MixinEntity
implements IEntityLivingBase,
IEntityNoInterp,
ICachedDamage,
IEntityRemoteAttack {
    private static final ModuleCache<ElytraFlight> ELYTRA_FLIGHT = Caches.getModule(ElytraFlight.class);
    private static final ModuleCache<FastEat> FAST_EAT = Caches.getModule(FastEat.class);
    private static final ModuleCache<NoInterp> NOINTERP = Caches.getModule(NoInterp.class);
    private static final ModuleCache<Spectate> SPECTATE = Caches.getModule(Spectate.class);
    @Shadow
    @Final
    private static DataParameter<Float> field_184632_c;
    @Shadow
    public float field_70702_br;
    @Shadow
    public float field_191988_bg;
    @Shadow
    protected int field_184628_bn;
    @Shadow
    protected ItemStack field_184627_bm;
    protected double noInterpX;
    protected double noInterpY;
    protected double noInterpZ;
    protected int noInterpPositionIncrements;
    protected float noInterpPrevSwing;
    protected float noInterpSwingAmount;
    protected float noInterpSwing;
    protected float lowestDura = Float.MAX_VALUE;
    protected boolean noInterping = true;
    protected int armorValue = Integer.MAX_VALUE;
    protected float armorToughness = Float.MAX_VALUE;
    protected int explosionModifier = Integer.MAX_VALUE;

    @Shadow
    public abstract IAttributeInstance func_110148_a(IAttribute var1);

    @Shadow
    public abstract int func_70658_aO();

    @Shadow
    public abstract Iterable<ItemStack> func_184193_aE();

    @Shadow
    public abstract boolean func_70613_aW();

    @Override
    @Invoker(value="getArmSwingAnimationEnd")
    public abstract int armSwingAnimationEnd();

    @Override
    @Accessor(value="ticksSinceLastSwing")
    public abstract int getTicksSinceLastSwing();

    @Override
    @Accessor(value="activeItemStackUseCount")
    public abstract int getActiveItemStackUseCount();

    @Override
    @Accessor(value="ticksSinceLastSwing")
    public abstract void setTicksSinceLastSwing(int var1);

    @Override
    @Accessor(value="activeItemStackUseCount")
    public abstract void setActiveItemStackUseCount(int var1);

    @Override
    public boolean getElytraFlag() {
        return this.func_70083_f(7);
    }

    @Override
    public double getNoInterpX() {
        return this.isNoInterping() ? this.noInterpX : this.field_70165_t;
    }

    @Override
    public double getNoInterpY() {
        return this.isNoInterping() ? this.noInterpY : this.field_70163_u;
    }

    @Override
    public double getNoInterpZ() {
        return this.isNoInterping() ? this.noInterpZ : this.field_70161_v;
    }

    @Override
    public void setNoInterpX(double x) {
        this.noInterpX = x;
    }

    @Override
    public void setNoInterpY(double y) {
        this.noInterpY = y;
    }

    @Override
    public void setNoInterpZ(double z) {
        this.noInterpZ = z;
    }

    @Override
    public int getPosIncrements() {
        return this.noInterpPositionIncrements;
    }

    @Override
    public void setPosIncrements(int posIncrements) {
        this.noInterpPositionIncrements = posIncrements;
    }

    @Override
    public float getNoInterpSwingAmount() {
        return this.noInterpSwingAmount;
    }

    @Override
    public float getNoInterpSwing() {
        return this.noInterpSwing;
    }

    @Override
    public float getNoInterpPrevSwing() {
        return this.noInterpPrevSwing;
    }

    @Override
    public void setNoInterpSwingAmount(float noInterpSwingAmount) {
        this.noInterpSwingAmount = noInterpSwingAmount;
    }

    @Override
    public void setNoInterpSwing(float noInterpSwing) {
        this.noInterpSwing = noInterpSwing;
    }

    @Override
    public void setNoInterpPrevSwing(float noInterpPrevSwing) {
        this.noInterpPrevSwing = noInterpPrevSwing;
    }

    @Override
    public boolean isNoInterping() {
        EntityPlayerSP player = MixinEntityLivingBase.mc.player;
        return !this.func_184218_aH() && this.noInterping && (player == null || !player.isRiding());
    }

    @Override
    public void setNoInterping(boolean noInterping) {
        this.noInterping = noInterping;
    }

    @Override
    public void setLowestDura(float lowest) {
        this.lowestDura = lowest;
    }

    @Override
    public float getLowestDurability() {
        return this.lowestDura;
    }

    @Override
    public int getArmorValue() {
        return this.shouldCache() ? this.armorValue : this.func_70658_aO();
    }

    @Override
    public float getArmorToughness() {
        return this.shouldCache() ? this.armorToughness : (float)this.func_110148_a(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue();
    }

    @Override
    public int getExplosionModifier(DamageSource source) {
        return this.shouldCache() ? this.explosionModifier : EnchantmentUtil.getEnchantmentModifierDamage(this.func_184193_aE(), source);
    }

    @Redirect(method={"attackEntityFrom"}, at=@At(value="FIELD", target="Lnet/minecraft/world/World;isRemote:Z"))
    private boolean isRemoteHook(World world) {
        if (world.isRemote) {
            return !this.shouldRemoteAttack();
        }
        return false;
    }

    @Redirect(method={"onUpdate"}, at=@At(value="FIELD", target="Lnet/minecraft/entity/EntityLivingBase;posX:D"))
    private double posXHookOnUpdate(EntityLivingBase base) {
        if (NoInterp.update((NoInterp)NOINTERP.get(), base)) {
            return ((IEntityNoInterp)((Object)base)).getNoInterpX();
        }
        return base.posX;
    }

    @Redirect(method={"onUpdate"}, at=@At(value="FIELD", target="Lnet/minecraft/entity/EntityLivingBase;posZ:D"))
    private double posZHookOnUpdate(EntityLivingBase base) {
        if (NOINTERP.isEnabled() && base instanceof IEntityNoInterp && ((IEntityNoInterp)((Object)base)).isNoInterping() && ((NoInterp)NOINTERP.get()).isSilent()) {
            return ((IEntityNoInterp)((Object)base)).getNoInterpZ();
        }
        return base.posZ;
    }

    @Inject(method={"isElytraFlying"}, at={@At(value="HEAD")}, cancellable=true)
    private void isElytraFlyingHook(CallbackInfoReturnable<Boolean> info) {
        if (EntityPlayerSP.class.isInstance(this) && ELYTRA_FLIGHT.isEnabled() && ((ElytraFlight)ELYTRA_FLIGHT.get()).getMode() == ElytraMode.Packet) {
            info.setReturnValue(false);
        }
    }

    @Inject(method={"notifyDataManagerChange"}, at={@At(value="RETURN")})
    public void notifyDataManagerChangeHook(DataParameter<?> key, CallbackInfo info) {
        if (key.equals(field_184632_c) && (double)((Float)this.field_70180_af.get(field_184632_c)).floatValue() <= 0.0 && this.field_70170_p != null && this.field_70170_p.isRemote) {
            Bus.EVENT_BUS.post(new DeathEvent((EntityLivingBase)EntityLivingBase.class.cast(this)));
        }
    }

    @Inject(method={"handleJumpWater"}, at={@At(value="HEAD")}, cancellable=true)
    private void handleJumpWaterHook(CallbackInfo info) {
        LiquidJumpEvent event = new LiquidJumpEvent((EntityLivingBase)EntityLivingBase.class.cast(this));
        Bus.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            info.cancel();
        }
    }

    @Inject(method={"handleJumpLava"}, at={@At(value="HEAD")}, cancellable=true)
    private void handleJumpLavaHook(CallbackInfo info) {
        LiquidJumpEvent event = new LiquidJumpEvent((EntityLivingBase)EntityLivingBase.class.cast(this));
        Bus.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            info.cancel();
        }
    }

    @Redirect(method={"onItemUseFinish"}, at=@At(value="INVOKE", target="Lnet/minecraft/entity/EntityLivingBase;resetActiveHand()V"))
    private void resetActiveHandHook(EntityLivingBase base) {
        if (this.field_70170_p.isRemote && FAST_EAT.isEnabled() && base instanceof EntityPlayerSP && !mc.isSingleplayer() && ((FastEat)FAST_EAT.get()).getMode() == FastEatMode.NoDelay && this.field_184627_bm.getItem() instanceof ItemFood) {
            this.field_184628_bn = 0;
            ((EntityPlayerSP)base).connection.sendPacket(new CPacketPlayerTryUseItem(base.getActiveHand()));
        } else {
            base.resetActiveHand();
        }
    }

    @Inject(method={"swingArm"}, at={@At(value="HEAD")})
    private void swingArmHook(EnumHand hand, CallbackInfo ci) {
        EntityPlayer player;
        if (EntityPlayerSP.class.isInstance(this) && SPECTATE.isEnabled() && (player = ((Spectate)SPECTATE.get()).getFake()) != null) {
            player.swingArm(hand);
        }
    }

    @Inject(method={"setPositionAndRotationDirect"}, at={@At(value="RETURN")})
    private void setPositionAndRotationDirectHook(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport, CallbackInfo ci) {
        if (NOINTERP.isEnabled()) {
            NoInterp.handleNoInterp((NoInterp)NOINTERP.get(), (Entity)Entity.class.cast(this), posRotationIncrements, x, y, z, yaw, pitch);
        }
    }

    @Redirect(method={"travel"}, at=@At(value="INVOKE", target="Lnet/minecraft/entity/EntityLivingBase;isServerWorld()Z"))
    public boolean travelHook(EntityLivingBase entityLivingBase) {
        return this.func_70613_aW() || entityLivingBase instanceof MotionTracker;
    }
}
