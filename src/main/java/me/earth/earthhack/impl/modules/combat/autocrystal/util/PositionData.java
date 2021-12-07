package me.earth.earthhack.impl.modules.combat.autocrystal.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.entity.IEntity;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.path.BasePath;
import me.earth.earthhack.impl.util.math.path.BlockingEntity;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class PositionData
extends BasePath
implements Globals,
Comparable<PositionData> {
    private final List<EntityPlayer> forced = new ArrayList<EntityPlayer>();
    private final Set<EntityPlayer> antiTotems;
    private EntityPlayer target;
    private EntityPlayer facePlace;
    private IBlockState state;
    private float selfDamage;
    private float damage;
    private boolean obby;
    private boolean obbyValid;
    private boolean blocked;
    private boolean liquidValid;
    private boolean liquid;
    private float minDiff;

    public PositionData(BlockPos pos, int blocks) {
        this(pos, blocks, new HashSet<EntityPlayer>());
    }

    public PositionData(BlockPos pos, int blocks, Set<EntityPlayer> antiTotems) {
        super(RotationUtil.getRotationPlayer(), pos, blocks);
        this.antiTotems = antiTotems;
        this.minDiff = Float.MAX_VALUE;
    }

    public boolean usesObby() {
        return this.obby;
    }

    public boolean isObbyValid() {
        return this.obbyValid;
    }

    public float getMaxDamage() {
        return this.damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getSelfDamage() {
        return this.selfDamage;
    }

    public void setSelfDamage(float selfDamage) {
        this.selfDamage = selfDamage;
    }

    public EntityPlayer getTarget() {
        return this.target;
    }

    public void setTarget(EntityPlayer target) {
        this.target = target;
    }

    public EntityPlayer getFacePlacer() {
        return this.facePlace;
    }

    public void setFacePlacer(EntityPlayer facePlace) {
        this.facePlace = facePlace;
    }

    public Set<EntityPlayer> getAntiTotems() {
        return this.antiTotems;
    }

    public void addAntiTotem(EntityPlayer player) {
        this.antiTotems.add(player);
    }

    public boolean isBlocked() {
        return this.blocked;
    }

    public float getMinDiff() {
        return this.minDiff;
    }

    public void setMinDiff(float minDiff) {
        this.minDiff = minDiff;
    }

    public boolean isForce() {
        return !this.forced.isEmpty();
    }

    public void addForcePlayer(EntityPlayer player) {
        this.forced.add(player);
    }

    public List<EntityPlayer> getForced() {
        return this.forced;
    }

    public boolean isLiquidValid() {
        return this.liquidValid;
    }

    public boolean isLiquid() {
        return this.liquid;
    }

    @Override
    public int compareTo(PositionData o) {
        if (Math.abs(o.damage - this.damage) < 1.0f) {
            if (this.usesObby() && o.usesObby()) {
                return Integer.compare(this.getPath().length, o.getPath().length) + Float.compare(this.selfDamage, o.selfDamage);
            }
            return Float.compare(this.selfDamage, o.getSelfDamage());
        }
        return Float.compare(o.damage, this.damage);
    }

    public boolean equals(Object o) {
        if (o instanceof PositionData) {
            return ((PositionData)o).getPos().equals(this.getPos());
        }
        return false;
    }

    public int hashCode() {
        return this.getPos().hashCode();
    }

    public static PositionData create(BlockPos pos, boolean obby, int helpingBlocks, boolean newVer, boolean newVerEntities, int deathTime, List<Entity> entities, boolean lava, boolean water, boolean lavaItems) {
        boolean checkLavaItems;
        IBlockState upUpState;
        BlockPos up;
        IBlockState upState;
        PositionData data = new PositionData(pos, helpingBlocks);
        data.state = PositionData.mc.world.getBlockState(pos);
        if (data.state.getBlock() != Blocks.BEDROCK && data.state.getBlock() != Blocks.OBSIDIAN) {
            if (!obby || !data.state.func_185904_a().isReplaceable() || PositionData.checkEntities(data, pos, entities, 0, true, true, false)) {
                return data;
            }
            data.obby = true;
        }
        if ((upState = PositionData.mc.world.getBlockState(up = pos.up())).getBlock() != Blocks.AIR) {
            if (PositionData.checkLiquid(upState.getBlock(), water, lava)) {
                data.liquid = true;
            } else {
                return data;
            }
        }
        if (!newVer && (upUpState = PositionData.mc.world.getBlockState(up.up())).getBlock() != Blocks.AIR) {
            if (PositionData.checkLiquid(upUpState.getBlock(), water, lava)) {
                data.liquid = true;
            } else {
                return data;
            }
        }
        boolean bl = checkLavaItems = lavaItems && upState.func_185904_a() == Material.LAVA;
        if (PositionData.checkEntities(data, up, entities, deathTime, false, false, checkLavaItems) || !newVerEntities && PositionData.checkEntities(data, up.up(), entities, deathTime, false, false, checkLavaItems)) {
            return data;
        }
        if (data.obby) {
            if (data.liquid) {
                data.liquidValid = true;
            }
            data.obbyValid = true;
            return data;
        }
        if (data.liquid) {
            data.liquidValid = true;
            return data;
        }
        data.setValid(true);
        return data;
    }

    private static boolean checkEntities(PositionData data, BlockPos pos, List<Entity> entities, int deathTime, boolean dead, boolean spawning, boolean lavaItems) {
        AxisAlignedBB bb = new AxisAlignedBB(pos);
        for (Entity entity : entities) {
            if (entity == null || spawning && !entity.preventEntitySpawning || dead && EntityUtil.isDead(entity) || !entity.getEntityBoundingBox().intersects(bb) || lavaItems && entity instanceof EntityItem) continue;
            if (entity instanceof EntityEnderCrystal) {
                if (!dead) {
                    if (EntityUtil.isDead(entity)) {
                        if (Managers.SET_DEAD.passedDeathTime(entity, (long)deathTime) || ((IEntity)((Object)entity)).getPseudoTime().passed(deathTime)) continue;
                        return true;
                    }
                    data.blocked = true;
                }
                data.getBlockingEntities().add(new BlockingEntity(entity, pos));
                continue;
            }
            return true;
        }
        return false;
    }

    private static boolean checkLiquid(Block block, boolean water, boolean lava) {
        return water && (block == Blocks.WATER || block == Blocks.FLOWING_WATER) || lava && (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA);
    }
}
