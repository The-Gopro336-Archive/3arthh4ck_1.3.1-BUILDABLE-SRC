package me.earth.earthhack.impl.core.mixins.entity;

import net.minecraft.entity.projectile.EntityArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={EntityArrow.class})
public interface IEntityArrow {
    @Accessor(value="inGround")
    public boolean isInGround();
}
