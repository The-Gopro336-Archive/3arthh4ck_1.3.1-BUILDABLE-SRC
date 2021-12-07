package me.earth.earthhack.impl.util.helpers.addable;

import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.util.helpers.addable.RegisteringModule;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemAddingModule<I, E extends Setting<I>>
extends RegisteringModule<I, E> {
    public ItemAddingModule(String name, Category category, Function<String, E> create, Function<Setting<?>, String> settingDescription) {
        super(name, category, "Add_Block", "item/block", create, settingDescription);
    }

    @Override
    public String getInput(String input, boolean add) {
        if (add) {
            String itemName = this.getItemStartingWith(input);
            if (itemName != null) {
                return TextUtil.substring(itemName, input.length());
            }
            return "";
        }
        return super.getInput(input, false);
    }

    public boolean isStackValid(ItemStack stack) {
        return stack != null && this.isValid(stack.getItem().getItemStackDisplayName(stack));
    }

    public String getItemStartingWith(String name) {
        return ItemAddingModule.getItemStartingWithDefault(name, i -> true);
    }

    public static String getItemStartingWithDefault(String name, Predicate<Item> accept) {
        Item item = ItemAddingModule.getItemStartingWith(name, accept);
        if (item != null) {
            return item.getItemStackDisplayName(new ItemStack(item));
        }
        return null;
    }

    public static Item getItemStartingWith(String name, Predicate<Item> accept) {
        if (name == null) {
            return null;
        }
        name = name.toLowerCase();
        Iterator iterator = Item.REGISTRY.iterator();
        while (iterator.hasNext()) {
            Item item = (Item)iterator.next();
            String itemName = item.getItemStackDisplayName(new ItemStack(item));
            if (!itemName.toLowerCase().startsWith(name) || !accept.test(item)) continue;
            return item;
        }
        return null;
    }
}
