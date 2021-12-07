/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.tileentity.TileEntityEnderChest
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 */
package me.earth.earthhack.impl.modules.player.automine;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.antisurround.AntiSurround;
import me.earth.earthhack.impl.modules.combat.anvilaura.AnvilAura;
import me.earth.earthhack.impl.modules.player.automine.AutoMine;
import me.earth.earthhack.impl.modules.player.automine.AutoMineCalc;
import me.earth.earthhack.impl.modules.player.automine.mode.AutoMineMode;
import me.earth.earthhack.impl.modules.player.automine.util.BigConstellation;
import me.earth.earthhack.impl.modules.player.automine.util.Constellation;
import me.earth.earthhack.impl.modules.player.automine.util.EchestConstellation;
import me.earth.earthhack.impl.modules.player.automine.util.IAutomine;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.modules.player.speedmine.mode.MineMode;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.mine.MineUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

final class ListenerUpdate
extends ModuleListener<AutoMine, UpdateEvent> {
    private static final ModuleCache<Speedmine> SPEED_MINE = Caches.getModule(Speedmine.class);
    private static final ModuleCache<AnvilAura> ANVIL_AURA = Caches.getModule(AnvilAura.class);
    private static final ModuleCache<AntiSurround> ANTISURROUND = Caches.getModule(AntiSurround.class);

    public ListenerUpdate(AutoMine module) {
        super(module, UpdateEvent.class, 1);
    }

    @Override
    public void invoke(UpdateEvent event) {
        BlockPos boost;
        if (ANTISURROUND.returnIfPresent(AntiSurround::isActive, false).booleanValue() || ANVIL_AURA.isEnabled() && ((AnvilAura)ANVIL_AURA.get()).isMining()) {
            return;
        }
        if (!SPEED_MINE.isPresent()) {
            ModuleUtil.disable((Module)this.module, "\u00a7cDisabled, Speedmine isn't registered on this version of the client!");
            return;
        }
        if (!(((AutoMine)this.module).mode.getValue() != AutoMineMode.Combat && ((AutoMine)this.module).mode.getValue() != AutoMineMode.AntiTrap || SPEED_MINE.isEnabled() && (((Speedmine)SPEED_MINE.get()).getMode() == MineMode.Smart || ((Speedmine)SPEED_MINE.get()).getMode() == MineMode.Instant))) {
            ModuleUtil.disable((Module)this.module, "\u00a7cDisabled, enable Speedmine - Smart for AutoMine - Combat!");
            return;
        }
        if (ListenerUpdate.mc.player.isCreative() || ListenerUpdate.mc.player.isSpectator() || !((AutoMine)this.module).timer.passed(((AutoMine)this.module).delay.getValue().intValue()) || ((AutoMine)this.module).mode.getValue() == AutoMineMode.Combat && ((Speedmine)SPEED_MINE.get()).getPos() != null && (((AutoMine)this.module).current == null || !((AutoMine)this.module).current.equals((Object)((Speedmine)SPEED_MINE.get()).getPos()))) {
            return;
        }
        BlockPos invalid = null;
        if (((AutoMine)this.module).constellation != null) {
            ((AutoMine)this.module).constellation.update((IAutomine)this.module);
        }
        if (((AutoMine)this.module).constellationCheck.getValue().booleanValue() && ((AutoMine)this.module).constellation != null) {
            if (((AutoMine)this.module).constellation.isValid((IBlockAccess)ListenerUpdate.mc.world, ((AutoMine)this.module).checkPlayerState.getValue()) && !((AutoMine)this.module).constellationTimer.passed(((AutoMine)this.module).maxTime.getValue().intValue()) && ((AutoMine)this.module).constellation.cantBeImproved()) {
                return;
            }
            if (((AutoMine)this.module).constellation.cantBeImproved()) {
                invalid = ((AutoMine)this.module).current;
                ((AutoMine)this.module).constellation = null;
                ((AutoMine)this.module).current = null;
            }
        }
        if (!((AutoMine)this.module).improve.getValue().booleanValue() && ((AutoMine)this.module).constellation != null && ((AutoMine)this.module).constellation.cantBeImproved()) {
            return;
        }
        ((AutoMine)this.module).blackList.entrySet().removeIf(e -> (float)(System.currentTimeMillis() - (Long)e.getValue()) / 1000.0f > (float)((AutoMine)this.module).blackListFor.getValue().intValue());
        if (((AutoMine)this.module).mode.getValue() == AutoMineMode.Combat) {
            IBlockState state;
            if (((AutoMine)this.module).prioSelf.getValue().booleanValue() && this.checkSelfTrap() || this.checkEnemies(false)) {
                return;
            }
            BlockPos position = PositionUtil.getPosition();
            if (((AutoMine)this.module).self.getValue().booleanValue() && (!((AutoMine)this.module).prioSelf.getValue().booleanValue() && this.checkSelfTrap() || this.checkPos((EntityPlayer)ListenerUpdate.mc.player, position))) {
                return;
            }
            if (((AutoMine)this.module).mineBurrow.getValue().booleanValue() && this.checkEnemies(true)) {
                return;
            }
            if (((AutoMine)this.module).selfEchestMine.getValue().booleanValue() && ((AutoMine)this.module).isValid(Blocks.ENDER_CHEST.getDefaultState()) && (state = ListenerUpdate.mc.world.getBlockState(position)).getBlock() == Blocks.ENDER_CHEST) {
                this.attackPos(position, new Constellation((IBlockAccess)ListenerUpdate.mc.world, (EntityPlayer)ListenerUpdate.mc.player, position, position, state));
                return;
            }
            if (invalid != null && invalid.equals((Object)((Speedmine)SPEED_MINE.get()).getPos()) && ((AutoMine)this.module).resetIfNotValid.getValue().booleanValue()) {
                ((Speedmine)SPEED_MINE.get()).reset();
            }
            if (((AutoMine)this.module).constellation == null && ((AutoMine)this.module).echest.getValue().booleanValue()) {
                TileEntity closest = null;
                double minDist = Double.MAX_VALUE;
                for (TileEntity entity : ListenerUpdate.mc.world.loadedTileEntityList) {
                    double dist;
                    if (!(entity instanceof TileEntityEnderChest) || !(BlockUtil.getDistanceSq(entity.getPos()) < (double)MathUtil.square(((AutoMine)this.module).echestRange.getValue().floatValue())) || !((dist = entity.getPos().distanceSqToCenter(RotationUtil.getRotationPlayer().posX, RotationUtil.getRotationPlayer().posY + (double)ListenerUpdate.mc.player.getEyeHeight(), RotationUtil.getRotationPlayer().posZ)) < minDist)) continue;
                    minDist = dist;
                    closest = entity;
                }
                if (closest != null) {
                    ((AutoMine)this.module).offer(new EchestConstellation(closest.getPos()));
                    ((AutoMine)this.module).attackPos(closest.getPos());
                    return;
                }
            }
            if ((((AutoMine)this.module).constellation == null || !((AutoMine)this.module).constellation.cantBeImproved() && !(((AutoMine)this.module).constellation instanceof BigConstellation)) && ((AutoMine)this.module).terrain.getValue().booleanValue() && ((AutoMine)this.module).terrainTimer.passed(((AutoMine)this.module).terrainDelay.getValue().intValue()) && ((AutoMine)this.module).future == null && (!((AutoMine)this.module).checkCrystalDownTime.getValue().booleanValue() || ((AutoMine)this.module).downTimer.passed(((AutoMine)this.module).downTime.getValue().intValue()))) {
                boolean c = ((AutoMine)this.module).closestPlayer.getValue();
                double closest = Double.MAX_VALUE;
                EntityPlayer best = null;
                ArrayList<EntityPlayer> players = new ArrayList<EntityPlayer>(c ? 0 : 10);
                for (EntityPlayer p : ListenerUpdate.mc.world.playerEntities) {
                    if (p == null || EntityUtil.isDead((Entity)p) || p.getDistanceSq((Entity)RotationUtil.getRotationPlayer()) > 400.0 || Managers.FRIENDS.contains(p)) continue;
                    if (c) {
                        double dist = p.getDistanceSq((Entity)RotationUtil.getRotationPlayer());
                        if (!(dist < closest)) continue;
                        closest = dist;
                        best = p;
                        continue;
                    }
                    players.add(p);
                }
                if (c && best == null || !c && players.isEmpty()) {
                    return;
                }
                List<Entity> entities = ListenerUpdate.mc.world.loadedEntityList.stream().filter(Objects::nonNull).filter(e -> !(e instanceof EntityItem)).filter(e -> !EntityUtil.isDead(e)).filter(e -> e.getDistanceSq((Entity)RotationUtil.getRotationPlayer()) < (double)MathUtil.square(((AutoMine)this.module).range.getValue().floatValue())).collect(Collectors.toList());
                AutoMineCalc calc = new AutoMineCalc((IAutomine)this.module, players, entities, best, ((AutoMine)this.module).minDmg.getValue().floatValue(), ((AutoMine)this.module).maxSelfDmg.getValue().floatValue(), ((AutoMine)this.module).range.getValue().floatValue(), ((AutoMine)this.module).obbyPositions.getValue(), ((AutoMine)this.module).newV.getValue(), ((AutoMine)this.module).newVEntities.getValue(), ((AutoMine)this.module).mineObby.getValue(), ((AutoMine)this.module).breakTrace.getValue().floatValue(), ((AutoMine)this.module).suicide.getValue());
                ((AutoMine)this.module).future = Managers.THREAD.submit(calc);
                ((AutoMine)this.module).terrainTimer.reset();
            }
        } else if (((AutoMine)this.module).mode.getValue() == AutoMineMode.AntiTrap && !(boost = PositionUtil.getPosition().up(2)).equals((Object)((AutoMine)this.module).last) && !MovementUtil.isMoving()) {
            ((Speedmine)SPEED_MINE.get()).getTimer().setTime(0L);
            ((AutoMine)this.module).current = boost;
            ListenerUpdate.mc.playerController.onPlayerDamageBlock(boost, EnumFacing.DOWN);
            ((AutoMine)this.module).timer.reset();
            ((AutoMine)this.module).last = boost;
        }
    }

    private boolean checkEnemies(boolean burrow) {
        BlockPos closestPos = null;
        Constellation closest = null;
        double distance = Double.MAX_VALUE;
        for (EntityPlayer player : ListenerUpdate.mc.world.playerEntities) {
            BlockPos upUp;
            IBlockState state;
            if (!EntityUtil.isValid((Entity)player, ((AutoMine)this.module).range.getValue().floatValue() + 1.0f) || player.equals((Object)ListenerUpdate.mc.player)) continue;
            BlockPos playerPos = PositionUtil.getPosition((Entity)player);
            if (burrow) {
                double dist = ListenerUpdate.mc.player.getDistanceSq(playerPos);
                if (dist >= distance || !this.isValid(playerPos, state = ListenerUpdate.mc.world.getBlockState(playerPos))) continue;
                closestPos = playerPos;
                closest = new Constellation((IBlockAccess)ListenerUpdate.mc.world, player, playerPos, playerPos, state);
                distance = dist;
                continue;
            }
            IBlockState playerPosState = ListenerUpdate.mc.world.getBlockState(playerPos);
            if (!playerPosState.getMaterial().isReplaceable() && !(playerPosState.getBlock().getExplosionResistance((Entity)ListenerUpdate.mc.player) < 100.0f)) continue;
            if (((AutoMine)this.module).head.getValue().booleanValue() && this.isValid(upUp = playerPos.up(2), state = ListenerUpdate.mc.world.getBlockState(upUp))) {
                this.attackPos(upUp, new Constellation((IBlockAccess)ListenerUpdate.mc.world, player, upUp, playerPos, state));
                return true;
            }
            for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                IBlockState state2;
                BlockPos offset = playerPos.offset(facing);
                double dist = ListenerUpdate.mc.player.getDistanceSq(offset);
                if (dist >= distance || !this.isValid(offset, state2 = ListenerUpdate.mc.world.getBlockState(offset))) continue;
                if (((AutoMine)this.module).mineL.getValue().booleanValue() && ListenerUpdate.mc.world.getBlockState(offset.up()).getMaterial().isReplaceable()) {
                    boolean found = false;
                    for (EnumFacing l : EnumFacing.HORIZONTALS) {
                        if (l == facing || l == facing.getOpposite() || !((AutoMine)this.module).checkCrystalPos(offset.offset(l).down())) continue;
                        closestPos = offset;
                        closest = new Constellation((IBlockAccess)ListenerUpdate.mc.world, player, offset, playerPos, state2);
                        distance = dist;
                        found = true;
                        break;
                    }
                    if (found) continue;
                }
                if (!((AutoMine)this.module).checkCrystalPos(offset.offset(facing).down())) continue;
                closestPos = offset;
                closest = new Constellation((IBlockAccess)ListenerUpdate.mc.world, player, offset, playerPos, state2);
                distance = dist;
            }
        }
        if (closest != null) {
            this.attackPos(closestPos, closest);
            return true;
        }
        return false;
    }

    private boolean checkSelfTrap() {
        IBlockState state;
        BlockPos playerPos = PositionUtil.getPosition();
        BlockPos upUp = playerPos.up(2);
        if (this.isValid(upUp, state = ListenerUpdate.mc.world.getBlockState(upUp))) {
            this.attackPos(upUp, new Constellation((IBlockAccess)ListenerUpdate.mc.world, (EntityPlayer)ListenerUpdate.mc.player, upUp, playerPos, state));
            return true;
        }
        return false;
    }

    private boolean checkPos(EntityPlayer player, BlockPos playerPos) {
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            IBlockState state;
            BlockPos offset = playerPos.offset(facing);
            if (!this.isValid(offset, state = ListenerUpdate.mc.world.getBlockState(offset)) || !((AutoMine)this.module).checkCrystalPos(offset.offset(facing).down())) continue;
            this.attackPos(offset, new Constellation((IBlockAccess)ListenerUpdate.mc.world, player, offset, playerPos, state));
            return true;
        }
        return false;
    }

    private boolean isValid(BlockPos pos, IBlockState state) {
        return !((AutoMine)this.module).blackList.containsKey((Object)pos) && MineUtil.canBreak(state, pos) && ((AutoMine)this.module).isValid(state) && ListenerUpdate.mc.player.getDistanceSq(pos) <= (double)MathUtil.square(((Speedmine)SPEED_MINE.get()).getRange()) && !state.getMaterial().isReplaceable();
    }

    public void attackPos(BlockPos pos, Constellation c) {
        if (((AutoMine)this.module).checkCurrent.getValue().booleanValue() && pos.equals((Object)((AutoMine)this.module).current)) {
            return;
        }
        ((AutoMine)this.module).offer(c);
        ((AutoMine)this.module).attackPos(pos);
    }
}

