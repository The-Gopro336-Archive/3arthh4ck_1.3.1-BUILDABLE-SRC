package me.earth.earthhack.forge.mixins.item;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.truedurability.TrueDurability;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ItemStack.class})
public abstract class MixinItemStack {
    private static ModuleCache<TrueDurability> trueDurability;
    @Shadow
    int field_77991_e;

    @Inject(method={"<init>(Lnet/minecraft/item/Item;IILnet/minecraft/nbt/NBTTagCompound;)V"}, at={@At(value="RETURN")})
    @Dynamic
    private void initHook_Item(Item item, int amount, int meta, NBTTagCompound compound, CallbackInfo info) {
        if (trueDurability == null) {
            trueDurability = Caches.getModule(TrueDurability.class);
        }
        this.field_77991_e = this.checkDurability(this.field_77991_e, meta);
    }

    @Inject(method={"<init>(Lnet/minecraft/nbt/NBTTagCompound;)V"}, at={@At(value="RETURN")})
    private void initHook(NBTTagCompound compound, CallbackInfo info) {
        if (trueDurability == null) {
            trueDurability = Caches.getModule(TrueDurability.class);
        }
        this.field_77991_e = this.checkDurability(this.field_77991_e, compound.getShort("Damage"));
    }

    private int checkDurability(int damage, int meta) {
        int durability = damage;
        if (trueDurability != null && trueDurability.isEnabled() && meta < 0) {
            durability = meta;
        }
        return durability;
    }
}
