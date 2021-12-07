/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$MutableBlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.IBlockAccess
 */
package me.earth.earthhack.impl.managers.thread.safety;

import java.util.List;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.safety.SafetyManager;
import me.earth.earthhack.impl.util.math.geocache.Sphere;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.HoleUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.thread.SafeRunnable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;

public class SafetyRunnable
implements Globals,
SafeRunnable {
    private final SafetyManager manager;
    private final List<Entity> crystals;
    private final boolean newVerEntities;
    private final boolean newerVersion;
    private final boolean bedCheck;
    private final float maxDamage;
    private final boolean longs;
    private final boolean big;
    private final boolean anvils;
    private final boolean terrain;

    public SafetyRunnable(SafetyManager manager, List<Entity> crystals, boolean newVerEntities, boolean newerVersion, boolean bedCheck, float maxDamage, boolean longs, boolean big, boolean anvils, boolean terrain) {
        this.manager = manager;
        this.crystals = crystals;
        this.newVerEntities = newVerEntities;
        this.newerVersion = newerVersion;
        this.bedCheck = bedCheck;
        this.maxDamage = maxDamage;
        this.longs = longs;
        this.big = big;
        this.anvils = anvils;
        this.terrain = terrain;
    }

    @Override
    public void runSafely() {
        for (Entity entity : this.crystals) {
            float damage;
            if (!(entity instanceof EntityEnderCrystal) || entity.isDead || !((damage = DamageUtil.calculate(entity)) > this.maxDamage) && !((double)damage > (double)EntityUtil.getHealth((EntityLivingBase)SafetyRunnable.mc.player) - 1.0)) continue;
            this.manager.setSafe(false);
            return;
        }
        boolean fullArmor = true;
        for (ItemStack stack : SafetyRunnable.mc.player.inventory.armorInventory) {
            if (!stack.isEmpty()) continue;
            fullArmor = false;
            break;
        }
        Vec3d vec3d = Managers.POSITION.getVec();
        BlockPos position = new BlockPos(vec3d);
        if (fullArmor && (double)position.getY() == vec3d.y) {
            boolean[] hole = HoleUtil.isHole(position, false);
            if (!(!hole[0] || this.anvils && !hole[1] || this.newerVersion && this.bedCheck)) {
                this.manager.setSafe(true);
                return;
            }
            if (!this.anvils && (HoleUtil.is2x1(position) && this.longs || HoleUtil.is2x2Partial(position) && this.big) && !this.bedCheck) {
                this.manager.setSafe(true);
                return;
            }
        }
        AxisAlignedBB serverBB = Managers.POSITION.getBB();
        BlockPos middle = PositionUtil.fromBB(serverBB);
        int x = middle.getX();
        int y = middle.getY();
        int z = middle.getZ();
        int maxRadius = Sphere.getRadius(6.0);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int i = 1; i < maxRadius; ++i) {
            float damage;
            Vec3i v = Sphere.get(i);
            pos.setPos(x + v.getX(), y + v.getY(), z + v.getZ());
            if (!BlockUtil.canPlaceCrystal((BlockPos)pos, true, this.newerVersion, this.crystals, this.newVerEntities, 0L) && (!this.bedCheck || !BlockUtil.canPlaceBed((BlockPos)pos, this.newerVersion)) || !((damage = DamageUtil.calculate((float)pos.getX() + 0.5f, pos.getY() + 1, (float)pos.getZ() + 0.5f, serverBB, (EntityLivingBase)SafetyRunnable.mc.player, (IBlockAccess)SafetyRunnable.mc.world, this.terrain, this.anvils)) > this.maxDamage) && !((double)damage > (double)EntityUtil.getHealth((EntityLivingBase)SafetyRunnable.mc.player) - 1.0)) continue;
            this.manager.setSafe(false);
            return;
        }
        this.manager.setSafe(true);
    }
}

