package me.earth.earthhack.impl.util.helpers.addable;

import java.util.function.Function;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.util.helpers.addable.ItemAddingModule;
import me.earth.earthhack.impl.util.helpers.addable.RemovingItemAddingModule;
import net.minecraft.item.ItemBlock;

public class BlockAddingModule
extends RemovingItemAddingModule {
    public BlockAddingModule(String name, Category category, Function<Setting<?>, String> settingDescription) {
        super(name, category, settingDescription);
    }

    @Override
    public String getItemStartingWith(String name) {
        return ItemAddingModule.getItemStartingWithDefault(name, i -> i instanceof ItemBlock);
    }
}
