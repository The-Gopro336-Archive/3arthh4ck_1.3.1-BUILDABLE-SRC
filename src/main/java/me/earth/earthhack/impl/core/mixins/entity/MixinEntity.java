package me.earth.earthhack.impl.core.mixins.entity;

import java.util.function.Supplier;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.entity.IEntity;
import me.earth.earthhack.impl.core.ducks.entity.IEntityNoInterp;
import me.earth.earthhack.impl.event.events.misc.ReachEvent;
import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.events.movement.OnGroundEvent;
import me.earth.earthhack.impl.event.events.movement.StepEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.management.Management;
import me.earth.earthhack.impl.modules.misc.nointerp.NoInterp;
import me.earth.earthhack.impl.modules.movement.autosprint.AutoSprint;
import me.earth.earthhack.impl.modules.movement.autosprint.mode.SprintMode;
import me.earth.earthhack.impl.modules.movement.velocity.Velocity;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.entity.EntityType;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Entity.class})
public abstract class MixinEntity
implements IEntity,
Globals {
    private static final ModuleCache<AutoSprint> SPRINT = Caches.getModule(AutoSprint.class);
    private static final ModuleCache<Velocity> VELOCITY = Caches.getModule(Velocity.class);
    private static final ModuleCache<NoInterp> NOINTERP = Caches.getModule(NoInterp.class);
    private static final SettingCache<Boolean, BooleanSetting, Velocity> NO_PUSH = Caches.getSetting(Velocity.class, BooleanSetting.class, "NoPush", false);
    private static final SettingCache<Integer, NumberSetting<Integer>, Management> DEATH_TIME = Caches.getSetting(Management.class, Setting.class, "DeathTime", 500);
    @Shadow
    public double field_70165_t;
    @Shadow
    public double field_70163_u;
    @Shadow
    public double field_70161_v;
    @Shadow
    public double field_70159_w;
    @Shadow
    public double field_70181_x;
    @Shadow
    public double field_70179_y;
    @Shadow
    public float field_70177_z;
    @Shadow
    public float field_70125_A;
    @Shadow
    public boolean field_70122_E;
    @Shadow
    public World field_70170_p;
    @Shadow
    public double field_70169_q;
    @Shadow
    public double field_70167_r;
    @Shadow
    public double field_70166_s;
    @Shadow
    public double field_70142_S;
    @Shadow
    public double field_70137_T;
    @Shadow
    public double field_70136_U;
    @Shadow
    protected EntityDataManager field_70180_af;
    @Shadow
    public float field_70138_W;
    @Shadow
    public boolean field_70128_L;
    @Shadow
    public float field_70130_N;
    @Shadow
    public float field_70126_B;
    @Shadow
    public float field_70127_C;
    @Shadow
    public float field_70131_O;
    private final StopWatch pseudoWatch = new StopWatch();
    private MoveEvent moveEvent;
    private Float prevHeight;
    private Supplier<EntityType> type;
    private boolean pseudoDead;
    private long stamp;
    private boolean dummy;
    @Shadow
    public boolean field_70145_X;

    @Shadow
    public abstract AxisAlignedBB func_174813_aQ();

    @Shadow
    public abstract boolean func_70093_af();

    @Shadow
    protected abstract boolean func_70083_f(int var1);

    @Shadow
    public abstract boolean func_189652_ae();

    @Shadow
    public abstract boolean equals(Object var1);

    @Shadow
    protected abstract void func_70101_b(float var1, float var2);

    @Shadow
    public abstract boolean func_184218_aH();

    @Override
    @Accessor(value="isInWeb")
    public abstract boolean inWeb();

    @Override
    public EntityType getType() {
        return this.type.get();
    }

    @Override
    public long getDeathTime() {
        return 0L;
    }

    @Override
    public boolean isPseudoDead() {
        if (this.pseudoDead && !this.field_70128_L && this.pseudoWatch.passed(DEATH_TIME.getValue().intValue())) {
            this.pseudoDead = false;
        }
        return this.pseudoDead;
    }

    @Override
    public void setPseudoDead(boolean pseudoDead) {
        this.pseudoDead = pseudoDead;
        if (pseudoDead) {
            this.pseudoWatch.reset();
        }
    }

    @Override
    public StopWatch getPseudoTime() {
        return this.pseudoWatch;
    }

    @Override
    public long getTimeStamp() {
        return this.stamp;
    }

    @Override
    public boolean isDummy() {
        return this.dummy;
    }

    @Override
    public void setDummy(boolean dummy) {
        this.dummy = dummy;
    }

    @Inject(method={"<init>"}, at={@At(value="RETURN")})
    private void ctrHook(CallbackInfo info) {
        this.type = EntityType.getEntityType((Entity)Entity.class.cast(this));
        this.stamp = System.currentTimeMillis();
    }

    @Inject(method={"createRunningParticles"}, at={@At(value="HEAD")}, cancellable=true)
    private void createRunningParticlesHook(CallbackInfo ci) {
        if (EntityPlayerSP.class.isInstance(this) && SPRINT.isEnabled() && ((AutoSprint)SPRINT.get()).getMode() == SprintMode.Rage) {
            ci.cancel();
        }
    }

    @Inject(method={"move"}, at={@At(value="HEAD")})
    public void moveEntityHook_Head(MoverType type, double x, double y, double z, CallbackInfo info) {
        if (EntityPlayerSP.class.isInstance(this)) {
            this.moveEvent = new MoveEvent(type, x, y, z, this.func_70093_af());
            Bus.EVENT_BUS.post(this.moveEvent);
        }
    }

    @ModifyVariable(method={"move"}, at=@At(value="HEAD"), ordinal=0)
    private double setX(double x) {
        return this.moveEvent != null ? this.moveEvent.getX() : x;
    }

    @ModifyVariable(method={"move"}, at=@At(value="HEAD"), ordinal=1)
    private double setY(double y) {
        return this.moveEvent != null ? this.moveEvent.getY() : y;
    }

    @ModifyVariable(method={"move"}, at=@At(value="HEAD"), ordinal=2)
    private double setZ(double z) {
        return this.moveEvent != null ? this.moveEvent.getZ() : z;
    }

    @Redirect(method={"move"}, at=@At(value="INVOKE", target="net/minecraft/entity/Entity.isSneaking()Z", ordinal=0))
    private boolean isSneakingHook(Entity entity) {
        return this.moveEvent != null ? this.moveEvent.isSneaking() : entity.isSneaking();
    }

    @Inject(method={"move"}, at={@At(value="FIELD", target="net/minecraft/entity/Entity.onGround:Z", ordinal=1)})
    private void onGroundHook(MoverType type, double x, double y, double z, CallbackInfo info) {
        if (EntityPlayerSP.class.isInstance(this)) {
            StepEvent event = new StepEvent(Stage.PRE, this.func_174813_aQ(), this.field_70138_W);
            Bus.EVENT_BUS.post(event);
            this.prevHeight = Float.valueOf(this.field_70138_W);
            this.field_70138_W = event.getHeight();
        }
    }

    @Inject(method={"move"}, at={@At(value="FIELD", target="net/minecraft/entity/Entity.onGround:Z", ordinal=2, shift=At.Shift.AFTER)})
    private void onGroundHook2(MoverType type, double x, double y, double z, CallbackInfo info) {
        if (EntityPlayerSP.class.isInstance(this)) {
            OnGroundEvent event = new OnGroundEvent();
            Bus.EVENT_BUS.post(event);
            this.field_70122_E = this.field_70122_E || event.isCancelled();
        }
    }

    @Inject(method={"move"}, at={@At(value="INVOKE", target="net/minecraft/entity/Entity.setEntityBoundingBox(Lnet/minecraft/util/math/AxisAlignedBB;)V", ordinal=7, shift=At.Shift.AFTER)})
    private void setEntityBoundingBoxHook(MoverType type, double x, double y, double z, CallbackInfo info) {
        if (EntityPlayerSP.class.isInstance(this)) {
            StepEvent event = new StepEvent(Stage.POST, this.func_174813_aQ(), this.prevHeight != null ? this.prevHeight.floatValue() : 0.0f);
            Bus.EVENT_BUS.postReversed(event, null);
        }
    }

    @Inject(method={"setPositionAndRotation"}, at={@At(value="RETURN")})
    private void setPositionAndRotationHook(double x, double y, double z, float yaw, float pitch, CallbackInfo ci) {
        if (this instanceof IEntityNoInterp) {
            ((IEntityNoInterp)((Object)this)).setNoInterpX(x);
            ((IEntityNoInterp)((Object)this)).setNoInterpY(y);
            ((IEntityNoInterp)((Object)this)).setNoInterpZ(z);
        }
    }

    @Inject(method={"move"}, at={@At(value="INVOKE", target="net/minecraft/entity/Entity.resetPositionToBB()V", ordinal=1)})
    private void resetPositionToBBHook(MoverType type, double x, double y, double z, CallbackInfo info) {
        if (EntityPlayerSP.class.isInstance(this) && this.prevHeight != null) {
            this.field_70138_W = this.prevHeight.floatValue();
            this.prevHeight = null;
        }
    }

    @Inject(method={"move"}, at={@At(value="RETURN")})
    public void moveEntityHook_Return(MoverType type, double x, double y, double z, CallbackInfo info) {
        this.moveEvent = null;
    }

    @Inject(method={"getCollisionBorderSize"}, at={@At(value="RETURN")}, cancellable=true)
    private void getCollisionBorderSizeHook(CallbackInfoReturnable<Float> info) {
        ReachEvent event = new ReachEvent(0.0f, info.getReturnValue().floatValue());
        Bus.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            info.setReturnValue(Float.valueOf(event.getHitBox()));
        }
    }

    @Redirect(method={"applyEntityCollision"}, at=@At(value="INVOKE", target="Lnet/minecraft/entity/Entity;addVelocity(DDD)V"))
    public void addVelocityHook(Entity entity, double x, double y, double z) {
        if (!(entity == null || VELOCITY.isEnabled() && NO_PUSH.getValue().booleanValue() && entity.equals(MixinEntity.mc.player))) {
            entity.addVelocity(x, y, z);
        }
    }

    @Inject(method={"setDead"}, at={@At(value="RETURN")})
    private void setDeadHook(CallbackInfo ci) {
        if (NOINTERP.isPresent() && ((NoInterp)NOINTERP.get()).shouldFixDeathJitter()) {
            this.removeInterpolation();
            mc.addScheduledTask(this::removeInterpolation);
        }
    }

    private void removeInterpolation() {
        this.field_70169_q = this.field_70165_t;
        this.field_70167_r = this.field_70163_u;
        this.field_70166_s = this.field_70161_v;
        this.field_70142_S = this.field_70165_t;
        this.field_70137_T = this.field_70163_u;
        this.field_70136_U = this.field_70161_v;
        this.prevHeight = Float.valueOf(this.field_70131_O);
        this.field_70127_C = this.field_70125_A;
        this.field_70126_B = this.field_70177_z;
        if (this instanceof IEntityNoInterp) {
            ((IEntityNoInterp)((Object)this)).setNoInterpX(this.field_70165_t);
            ((IEntityNoInterp)((Object)this)).setNoInterpY(this.field_70163_u);
            ((IEntityNoInterp)((Object)this)).setNoInterpZ(this.field_70161_v);
        }
    }
}
