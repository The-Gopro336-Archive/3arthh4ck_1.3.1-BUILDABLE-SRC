/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemFood
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.core.mixins.item;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.event.events.misc.EatEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ItemFood.class})
public abstract class MixinItemFood {
    @Inject(method={"onItemUseFinish"}, at={@At(value="HEAD")})
    private void onItemUseFinishHook(ItemStack stack, World world, EntityLivingBase entity, CallbackInfoReturnable<ItemStack> info) {
        if (entity instanceof EntityPlayer) {
            Bus.EVENT_BUS.post(new EatEvent(stack, entity));
        }
    }
}

