package me.earth.earthhack.impl.core.mixins.entity;

import me.earth.earthhack.impl.core.mixins.entity.MixinEntity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={EntityEnderCrystal.class})
public abstract class MixinEntityEnderCrystal
extends MixinEntity {
    @Inject(method={"<init>(Lnet/minecraft/world/World;DDD)V"}, at={@At(value="RETURN")})
    private void initHook(World worldIn, double x, double y, double z, CallbackInfo ci) {
        this.field_70169_q = x;
        this.field_70167_r = y;
        this.field_70166_s = z;
        this.field_70142_S = x;
        this.field_70137_T = y;
        this.field_70136_U = z;
    }
}
