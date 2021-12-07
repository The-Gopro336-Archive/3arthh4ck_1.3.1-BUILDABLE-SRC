/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.NonNullList
 */
package me.earth.earthhack.impl.modules.player.sorter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;
import me.earth.earthhack.api.config.Jsonable;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class InventoryLayout
implements Jsonable,
Globals {
    private final Map<Integer, Item> layout = new HashMap<Integer, Item>();

    public void setSlot(int slot, Item item) {
        this.layout.put(slot, item);
    }

    public Item getItem(int slot) {
        return this.layout.get(slot);
    }

    @Override
    public void fromJson(JsonElement element) {
        JsonObject object = element.getAsJsonObject();
        for (Map.Entry entry : object.entrySet()) {
            int id = Integer.parseInt((String)entry.getKey());
            Item item = Item.getItemById((int)((JsonElement)entry.getValue()).getAsInt());
            this.layout.put(id, item);
        }
    }

    @Override
    public String toJson() {
        JsonObject object = new JsonObject();
        for (Map.Entry<Integer, Item> entry : this.layout.entrySet()) {
            object.add(entry.getKey() + "", Jsonable.parse(Item.getIdFromItem((Item)entry.getValue()) + "", false));
        }
        return object.toString();
    }

    public static InventoryLayout createFromMcPlayer() {
        NonNullList<ItemStack> inventory = InventoryUtil.getInventory();
        InventoryLayout layout = new InventoryLayout();
        for (int i = 0; i < inventory.size(); ++i) {
            layout.setSlot(i, ((ItemStack)inventory.get(i)).getItem());
        }
        return layout;
    }
}

