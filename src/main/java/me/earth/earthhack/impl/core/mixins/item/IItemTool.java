package me.earth.earthhack.impl.core.mixins.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemTool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={ItemTool.class})
public interface IItemTool {
    @Accessor(value="attackDamage")
    public float getAttackDamage();

    @Accessor(value="toolMaterial")
    public Item.ToolMaterial getToolMaterial();
}
