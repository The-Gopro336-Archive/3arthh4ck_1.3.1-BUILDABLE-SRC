/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.item.EntityBoat
 */
package me.earth.earthhack.impl.core.mixins.entity;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.core.mixins.entity.MixinEntity;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.boatfly.BoatFly;
import net.minecraft.entity.item.EntityBoat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={EntityBoat.class})
public abstract class MixinEntityBoat
extends MixinEntity {
    private static final ModuleCache<BoatFly> BOAT_FLY = Caches.getModule(BoatFly.class);

    @Redirect(method={"updateMotion"}, at=@At(value="INVOKE", target="net/minecraft/entity/item/EntityBoat.hasNoGravity()Z"))
    private boolean updateMotionHook(EntityBoat boat) {
        return this.hasNoGravity() || BOAT_FLY.isEnabled() && ((Number)BOAT_FLY.returnIfPresent(BoatFly::getGlideSpeed, Float.valueOf(1.0E-4f))).floatValue() == 0.0f;
    }
}

