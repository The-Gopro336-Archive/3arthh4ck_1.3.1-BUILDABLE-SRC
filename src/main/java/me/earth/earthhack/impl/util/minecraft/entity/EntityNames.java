package me.earth.earthhack.impl.util.minecraft.entity;

import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.item.EntityMinecartFurnace;
import net.minecraft.entity.item.EntityMinecartTNT;

public class EntityNames {
    private static final List<Map.Entry<Class<? extends Entity>, String>> entityNames = new LinkedList<Map.Entry<Class<? extends Entity>, String>>();

    public static void register(Class<? extends Entity> type, String name) {
        entityNames.add(0, new AbstractMap.SimpleEntry<Class<? extends Entity>, String>(type, name));
    }

    public static String getName(Entity entity) {
        for (Map.Entry<Class<? extends Entity>, String> entry : entityNames) {
            if (!entry.getKey().isInstance(entity)) continue;
            return entry.getValue();
        }
        return entity.getName();
    }

    static {
        EntityNames.register(EntityItemFrame.class, "Item Frame");
        EntityNames.register(EntityEnderCrystal.class, "End Crystal");
        EntityNames.register(EntityMinecartEmpty.class, "Minecart");
        EntityNames.register(EntityMinecart.class, "Minecart");
        EntityNames.register(EntityMinecartFurnace.class, "Minecart with Furnace");
        EntityNames.register(EntityMinecartTNT.class, "Minecart with TNT");
    }
}
