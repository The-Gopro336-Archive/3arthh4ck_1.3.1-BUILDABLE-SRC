package me.earth.earthhack.impl.core.mixins.entity.living.player;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.core.mixins.entity.living.MixinEntityLivingBase;
import me.earth.earthhack.impl.event.events.movement.SprintEvent;
import me.earth.earthhack.impl.event.events.render.SuffocationEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.tpssync.TpsSync;
import me.earth.earthhack.impl.util.thread.EnchantmentUtil;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={EntityPlayer.class})
public abstract class MixinEntityPlayer
extends MixinEntityLivingBase {
    private static final ModuleCache<TpsSync> TPS_SYNC = Caches.getModule(TpsSync.class);
    private static final SettingCache<Boolean, BooleanSetting, TpsSync> ATTACK = Caches.getSetting(TpsSync.class, BooleanSetting.class, "Attack", false);
    @Shadow
    public InventoryPlayer field_71071_by;
    @Shadow
    public Container field_71069_bz;

    @Shadow
    public void func_70071_h_() {
        throw new IllegalStateException("onUpdate was not shadowed!");
    }

    @Shadow
    public boolean func_70097_a(DamageSource source, float amount) {
        throw new IllegalStateException("attackEntityFrom wasn't shadowed!");
    }

    @Inject(method={"onUpdate"}, at={@At(value="RETURN")})
    private void onUpdateHook(CallbackInfo ci) {
        if (this.shouldCache()) {
            this.armorValue = this.func_70658_aO();
            this.armorToughness = (float)this.func_110148_a(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue();
            this.explosionModifier = EnchantmentUtil.getEnchantmentModifierDamage(this.func_184193_aE(), DamageSource.FIREWORKS);
        }
    }

    @Inject(method={"isEntityInsideOpaqueBlock"}, at={@At(value="HEAD")}, cancellable=true)
    private void isEntityInsideOpaqueBlockHook(CallbackInfoReturnable<Boolean> info) {
        SuffocationEvent event = new SuffocationEvent();
        Bus.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            info.cancel();
        }
    }

    @Redirect(method={"attackTargetEntityWithCurrentItem"}, at=@At(value="INVOKE", target="net/minecraft/entity/player/EntityPlayer.setSprinting(Z)V"))
    private void attackTargetEntityWithCurrentItemHook(EntityPlayer entity, boolean sprinting) {
        SprintEvent event = new SprintEvent(sprinting);
        Bus.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            this.field_70159_w /= 0.6;
            this.field_70179_y /= 0.6;
        } else {
            entity.setSprinting(event.isSprinting());
        }
    }

    @Inject(method={"getCooldownPeriod"}, at={@At(value="HEAD")}, cancellable=true)
    private void getCooldownPeriodHook(CallbackInfoReturnable<Float> info) {
        if (TPS_SYNC.isEnabled() && ATTACK.getValue().booleanValue()) {
            info.setReturnValue(Float.valueOf((float)(1.0 / this.func_110148_a(SharedMonsterAttributes.ATTACK_SPEED).getAttributeValue() * (double)Managers.TPS.getTps())));
        }
    }
}
