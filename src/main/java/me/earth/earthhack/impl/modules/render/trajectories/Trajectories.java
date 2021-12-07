package me.earth.earthhack.impl.modules.render.trajectories;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.impl.modules.render.trajectories.ListenerRender;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemLingeringPotion;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemSplashPotion;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;

public class Trajectories
extends Module {
    public final Setting<Color> color = this.register(new ColorSetting("Color", new Color(0, 255, 0)));
    public final Setting<Boolean> landed = this.register(new BooleanSetting("Landed", true));

    public Trajectories() {
        super("Trajectories", Category.Render);
        this.listeners.add(new ListenerRender(this));
    }

    protected boolean isThrowable(Item item) {
        return item instanceof ItemEnderPearl || item instanceof ItemExpBottle || item instanceof ItemSnowball || item instanceof ItemEgg || item instanceof ItemSplashPotion || item instanceof ItemLingeringPotion;
    }

    protected float getDistance(Item item) {
        return item instanceof ItemBow ? 1.0f : 0.4f;
    }

    protected float getThrowVelocity(Item item) {
        if (item instanceof ItemSplashPotion || item instanceof ItemLingeringPotion) {
            return 0.5f;
        }
        if (item instanceof ItemExpBottle) {
            return 0.59f;
        }
        return 1.5f;
    }

    protected int getThrowPitch(Item item) {
        if (item instanceof ItemSplashPotion || item instanceof ItemLingeringPotion || item instanceof ItemExpBottle) {
            return 20;
        }
        return 0;
    }

    protected float getGravity(Item item) {
        if (item instanceof ItemBow || item instanceof ItemSplashPotion || item instanceof ItemLingeringPotion || item instanceof ItemExpBottle) {
            return 0.05f;
        }
        return 0.03f;
    }

    protected List<Entity> getEntitiesWithinAABB(AxisAlignedBB bb) {
        ArrayList<Entity> list = new ArrayList<Entity>();
        int chunkMinX = MathHelper.floor((double)((bb.minX - 2.0) / 16.0));
        int chunkMaxX = MathHelper.floor((double)((bb.maxX + 2.0) / 16.0));
        int chunkMinZ = MathHelper.floor((double)((bb.minZ - 2.0) / 16.0));
        int chunkMaxZ = MathHelper.floor((double)((bb.maxZ + 2.0) / 16.0));
        for (int x = chunkMinX; x <= chunkMaxX; ++x) {
            for (int z = chunkMinZ; z <= chunkMaxZ; ++z) {
                if (Trajectories.mc.world.getChunkProvider().getLoadedChunk(x, z) == null) continue;
                Trajectories.mc.world.getChunkFromChunkCoords(x, z).getEntitiesWithinAABBForEntity(Trajectories.mc.player, bb, list, EntitySelectors.NOT_SPECTATING);
            }
        }
        return list;
    }
}
