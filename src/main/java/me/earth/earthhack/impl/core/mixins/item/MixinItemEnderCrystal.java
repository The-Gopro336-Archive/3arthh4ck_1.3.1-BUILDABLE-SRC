/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.entity.Entity
 *  net.minecraft.item.ItemEndCrystal
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.core.mixins.item;

import java.util.ArrayList;
import java.util.List;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.player.blocktweaks.BlockTweaks;
import me.earth.earthhack.impl.modules.player.freecam.Freecam;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={ItemEndCrystal.class})
public abstract class MixinItemEnderCrystal {
    private static final ModuleCache<Freecam> FREECAM = Caches.getModule(Freecam.class);
    private static final ModuleCache<BlockTweaks> BLOCK_TWEAKS = Caches.getModule(BlockTweaks.class);

    @Redirect(method={"onItemUse"}, at=@At(value="NEW", target="net/minecraft/util/math/AxisAlignedBB"))
    private AxisAlignedBB newBBHook(double x1, double y1, double z1, double x2, double y2, double z2) {
        if (BLOCK_TWEAKS.returnIfPresent(BlockTweaks::areNewVerEntitiesActive, false).booleanValue()) {
            return new AxisAlignedBB(x1, y1, z1, x2, y2 - 1.0, z2);
        }
        return new AxisAlignedBB(x1, y1, z1, x2, y2, z2);
    }

    @Redirect(method={"onItemUse"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/World;getEntitiesWithinAABBExcludingEntity(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;)Ljava/util/List;", remap=false))
    private List<Entity> getEntitiesWithinAABBExcludingEntityHook(World world, Entity entityIn, AxisAlignedBB bb) {
        List entities = world.getEntitiesWithinAABBExcludingEntity(entityIn, bb);
        if (FREECAM.isEnabled()) {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            if (player == null) {
                return entities;
            }
            for (Entity entity : entities) {
                if (player.equals((Object)entity)) continue;
                return entities;
            }
            return new ArrayList<Entity>(0);
        }
        return entities;
    }
}

