/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemPickaxe
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.modules.combat.anvilaura;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.antisurround.AntiSurround;
import me.earth.earthhack.impl.modules.combat.anvilaura.AnvilAura;
import me.earth.earthhack.impl.modules.combat.anvilaura.modes.AnvilMode;
import me.earth.earthhack.impl.modules.combat.anvilaura.modes.AnvilStage;
import me.earth.earthhack.impl.modules.combat.anvilaura.util.AnvilResult;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListener;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyModule;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyUtil;
import me.earth.earthhack.impl.util.helpers.blocks.modes.RayTraceMode;
import me.earth.earthhack.impl.util.helpers.blocks.util.TargetResult;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.path.BasePath;
import me.earth.earthhack.impl.util.math.path.PathFinder;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.SpecialBlocks;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

final class ListenerAnvilAura
extends ObbyListener<AnvilAura> {
    private static final ModuleCache<AntiSurround> ANTISURROUND = Caches.getModule(AntiSurround.class);
    private static final Vec3i[] CRYSTAL_OFFSETS = new Vec3i[]{new Vec3i(1, 0, -1), new Vec3i(1, 0, 1), new Vec3i(-1, 0, -1), new Vec3i(-1, 0, 1), new Vec3i(1, 1, -1), new Vec3i(1, 1, 1), new Vec3i(-1, 1, -1), new Vec3i(-1, 1, 1)};
    private AnvilMode mode = AnvilMode.Mine;
    private String disableString;

    public ListenerAnvilAura(AnvilAura module, int priority) {
        super(module, priority);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (ANTISURROUND.returnIfPresent(AntiSurround::isActive, false).booleanValue()) {
            return;
        }
        super.invoke(event);
    }

    @Override
    protected void pre(MotionUpdateEvent event) {
        this.mode = ((AnvilAura)this.module).mode.getValue();
        ((AnvilAura)this.module).action = null;
        ((AnvilAura)this.module).renderBBs = Collections.emptyList();
        ((AnvilAura)this.module).target = null;
        super.pre(event);
    }

    @Override
    protected TargetResult getTargets(TargetResult result) {
        BlockPos awaitPos;
        if (this.mode == AnvilMode.Mine && !((AnvilAura)this.module).confirmMine.getValue().booleanValue() && !((AnvilAura)this.module).awaitTimer.passed(((Integer)((AnvilAura)this.module).confirm.getValue()).intValue()) && ((AnvilAura)this.module).awaiting.get() && (awaitPos = ((AnvilAura)this.module).awaitPos) != null) {
            ObbyModule.HELPER.addBlockState(((AnvilAura)this.module).awaitPos, Blocks.AIR.getDefaultState());
        }
        Set<AnvilResult> results = AnvilResult.create(ListenerAnvilAura.mc.world.playerEntities, ListenerAnvilAura.mc.world.loadedEntityList, ((AnvilAura)this.module).yHeight.getValue().intValue(), ((AnvilAura)this.module).range.getValue());
        block0 : switch (this.mode) {
            case Render: {
                ArrayList<AxisAlignedBB> renderBBs = new ArrayList<AxisAlignedBB>(((AnvilAura)this.module).renderBest.getValue() != false ? 1 : results.size());
                for (AnvilResult r : results) {
                    if (r.getMine().stream().anyMatch(p -> p.getY() > r.getPlayerPos().getY())) continue;
                    BlockPos first = null;
                    BlockPos last = null;
                    for (BlockPos pos : r.getPositions()) {
                        if (BlockUtil.getDistanceSq(pos) >= MathUtil.square(((AnvilAura)this.module).range.getValue())) continue;
                        if (first == null) {
                            first = pos;
                        }
                        last = pos;
                    }
                    if (first == null) continue;
                    AxisAlignedBB bb = new AxisAlignedBB((double)first.getX(), (double)first.getY(), (double)first.getZ(), (double)(last.getX() + 1), (double)(last.getY() + 1), (double)(last.getZ() + 1));
                    renderBBs.add(bb);
                    if (!((AnvilAura)this.module).renderBest.getValue().booleanValue()) continue;
                    break;
                }
                ((AnvilAura)this.module).renderBBs = renderBBs;
                break;
            }
            case Pressure: {
                for (AnvilResult r : results) {
                    if (!r.hasValidPressure() && !r.hasSpecialPressure() || ((AnvilAura)this.module).checkFalling.getValue().booleanValue() && r.hasFallingEntities() && (!r.hasSpecialPressure() || !((AnvilAura)this.module).pressureFalling.getValue().booleanValue())) continue;
                    boolean badMine = false;
                    for (BlockPos pos : r.getMine()) {
                        if (pos.getY() <= r.getPlayerPos().getY()) continue;
                        badMine = true;
                        break;
                    }
                    if (badMine) continue;
                    if (this.doTrap(r)) break block0;
                    BlockPos pressure = r.getPressurePos();
                    if (r.hasSpecialPressure() || SpecialBlocks.PRESSURE_PLATES.contains((Object)ObbyModule.HELPER.getBlockState(pressure).getBlock())) {
                        if (!this.placeTop(r, result)) continue;
                        ((AnvilAura)this.module).setCurrentResult(r);
                        break block0;
                    }
                    if (!r.hasValidPressure() || ((AnvilAura)this.module).pressureSlot == -1) continue;
                    ((AnvilAura)this.module).stage = AnvilStage.PRESSURE;
                    result.getTargets().add(pressure);
                    break block0;
                }
                break;
            }
            case Mine: {
                if (((AnvilAura)this.module).stage == AnvilStage.MINE) {
                    if (((AnvilAura)this.module).minePos != null && ((AnvilAura)this.module).mineFacing != null) {
                        ((AnvilAura)this.module).rotations = RotationUtil.getRotations(((AnvilAura)this.module).minePos, ((AnvilAura)this.module).mineFacing);
                        if (!((AnvilAura)this.module).mineTimer.passed(((AnvilAura)this.module).mineTime.getValue().intValue())) break;
                        ((AnvilAura)this.module).awaitPos = ((AnvilAura)this.module).minePos;
                        ((AnvilAura)this.module).awaiting.set(true);
                        ((AnvilAura)this.module).action = () -> {
                            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> {
                                int last = ListenerAnvilAura.mc.player.inventory.currentItem;
                                InventoryUtil.switchTo(((AnvilAura)this.module).pickSlot);
                                PacketUtil.stopDigging(((AnvilAura)this.module).minePos, ((AnvilAura)this.module).mineFacing);
                                ListenerAnvilAura.mc.player.swingArm(EnumHand.MAIN_HAND);
                                InventoryUtil.switchTo(last);
                            });
                            ((AnvilAura)this.module).awaitTimer.reset();
                            Managers.BLOCKS.addCallback(((AnvilAura)this.module).minePos, s -> {
                                ((AnvilAura)this.module).awaiting.set(false);
                                ((AnvilAura)this.module).awaitPos = null;
                            });
                            ((AnvilAura)this.module).minePos = null;
                            ((AnvilAura)this.module).mineFacing = null;
                            ((AnvilAura)this.module).action = null;
                        };
                        break;
                    }
                    if (((AnvilAura)this.module).confirmMine.getValue().booleanValue() && !((AnvilAura)this.module).awaitTimer.passed(((Integer)((AnvilAura)this.module).confirm.getValue()).intValue()) && ((AnvilAura)this.module).awaiting.get()) break;
                }
                for (AnvilResult r : results) {
                    if (((AnvilAura)this.module).checkFalling.getValue().booleanValue() && (!r.hasSpecialPressure() || !((AnvilAura)this.module).pressureFalling.getValue().booleanValue()) && r.hasFallingEntities() || !this.doMine(r, result)) continue;
                    ((AnvilAura)this.module).setCurrentResult(r);
                    break block0;
                }
                break;
            }
            case High: {
                for (AnvilResult r : results) {
                    if (((AnvilAura)this.module).checkFalling.getValue().booleanValue() && (!r.hasSpecialPressure() || !((AnvilAura)this.module).pressureFalling.getValue().booleanValue()) && r.hasFallingEntities()) continue;
                    boolean badMine = false;
                    for (BlockPos pos : r.getMine()) {
                        if (pos.getY() <= r.getPlayerPos().getY()) continue;
                        badMine = true;
                        break;
                    }
                    if (badMine || !this.doTrap(r) && !this.placeTop(r, result)) continue;
                    ((AnvilAura)this.module).setCurrentResult(r);
                    break block0;
                }
                break;
            }
        }
        return result;
    }

    @Override
    protected int getSlot() {
        ((AnvilAura)this.module).obbySlot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN, new Block[0]);
        ((AnvilAura)this.module).crystalSlot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL, new Item[0]);
        ((AnvilAura)this.module).crystalSlot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL, new Item[0]);
        switch (this.mode) {
            case Pressure: {
                ((AnvilAura)this.module).pressureSlot = InventoryUtil.findHotbarBlock(Blocks.WOODEN_PRESSURE_PLATE, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Blocks.STONE_PRESSURE_PLATE, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
                if (((AnvilAura)this.module).pressureSlot != -1 || ((AnvilAura)this.module).pressurePass.getValue().booleanValue()) break;
                this.disableString = "Disabled, No Pressure Plates found!";
                return -1;
            }
            case Mine: {
                ((AnvilAura)this.module).pickSlot = InventoryUtil.findInHotbar(s -> s.getItem() instanceof ItemPickaxe);
                if (((AnvilAura)this.module).pickSlot != -1) break;
                this.disableString = "Disabled, No Pickaxe found!";
                return -1;
            }
        }
        return InventoryUtil.findHotbarBlock(Blocks.ANVIL, new Block[0]);
    }

    @Override
    protected void disableModule() {
        if (((AnvilAura)this.module).holdingAnvil.getValue().booleanValue()) {
            ((AnvilAura)this.module).packets.clear();
            ((AnvilAura)this.module).rotations = null;
            ((AnvilAura)this.module).action = null;
            return;
        }
        super.disableModule();
    }

    @Override
    protected boolean update() {
        this.disableString = null;
        if (((AnvilAura)this.module).holdingAnvil.getValue().booleanValue() && !InventoryUtil.isHolding(Blocks.ANVIL)) {
            return false;
        }
        return super.update();
    }

    @Override
    protected String getDisableString() {
        if (this.disableString != null) {
            return this.disableString;
        }
        return "Disabled, no Anvils.";
    }

    private boolean doTrap(AnvilResult anvilResult) {
        if (!((AnvilAura)this.module).trap.getValue().booleanValue() || ((AnvilAura)this.module).obbySlot == -1) {
            return false;
        }
        BlockPos highest = this.findHighest(anvilResult);
        if (highest == null) {
            return false;
        }
        boolean didPlace = false;
        BlockPos[] ignore = this.toIgnore(anvilResult, highest);
        for (BlockPos pos : anvilResult.getTrap()) {
            if (this.placed.containsKey((Object)pos) || !ObbyModule.HELPER.getBlockState(pos).getMaterial().isReplaceable()) continue;
            BasePath path = new BasePath((Entity)RotationUtil.getRotationPlayer(), pos, ((AnvilAura)this.module).trapHelping.getValue());
            PathFinder.findPath(path, ((AnvilAura)this.module).range.getValue(), ListenerAnvilAura.mc.world.loadedEntityList, (RayTraceMode)((Object)((AnvilAura)this.module).smartRay.getValue()), ObbyModule.HELPER, Blocks.OBSIDIAN.getDefaultState(), PathFinder.CHECK, ignore);
            int before = ((AnvilAura)this.module).slot;
            ((AnvilAura)this.module).slot = ((AnvilAura)this.module).obbySlot;
            didPlace = ObbyUtil.place((ObbyModule)this.module, path) || didPlace;
            ((AnvilAura)this.module).slot = before;
        }
        if (didPlace) {
            ((AnvilAura)this.module).stage = AnvilStage.OBSIDIAN;
            return true;
        }
        if (((AnvilAura)this.module).crystal.getValue().booleanValue() && ((AnvilAura)this.module).crystalSlot != -1 && ((AnvilAura)this.module).crystalTimer.passed(((AnvilAura)this.module).crystalDelay.getValue().intValue())) {
            BlockPos upUp = anvilResult.getPlayerPos().up(2);
            for (EntityEnderCrystal entity : ListenerAnvilAura.mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(upUp))) {
                if (entity == null || EntityUtil.isDead((Entity)entity)) continue;
                return false;
            }
            for (Vec3i vec3i : CRYSTAL_OFFSETS) {
                BlockPos pos = anvilResult.getPlayerPos().add(vec3i);
                if (!BlockUtil.canPlaceCrystal(pos, false, false)) continue;
                EntityPlayer entity = RotationUtil.getRotationPlayer();
                int before = ((AnvilAura)this.module).slot;
                ((AnvilAura)this.module).slot = ((AnvilAura)this.module).crystalSlot;
                ((AnvilAura)this.module).rotations = RotationUtil.getRotations((double)pos.getX() + 0.5, (double)pos.getY() + 1.0, (double)pos.getZ() + 0.5, entity.posX, entity.posY, entity.posZ, ListenerAnvilAura.mc.player.getEyeHeight());
                RayTraceResult result = RayTraceUtil.getRayTraceResult(((AnvilAura)this.module).rotations[0], ((AnvilAura)this.module).rotations[1], 6.0f, (Entity)entity);
                if (!result.getBlockPos().equals((Object)pos)) {
                    result = new RayTraceResult(RayTraceResult.Type.MISS, new Vec3d(0.5, 1.0, 0.5), EnumFacing.UP, BlockPos.ORIGIN);
                }
                ((AnvilAura)this.module).placeBlock(pos, result.sideHit, ((AnvilAura)this.module).rotations, result.hitVec);
                ((AnvilAura)this.module).slot = before;
                ((AnvilAura)this.module).stage = AnvilStage.CRYSTAL;
                ((AnvilAura)this.module).crystalTimer.reset();
                return true;
            }
        }
        return false;
    }

    private boolean doMine(AnvilResult anvilResult, TargetResult result) {
        if (this.doTrap(anvilResult)) {
            return true;
        }
        BlockPos highest = null;
        for (BlockPos pos : anvilResult.getMine()) {
            if (BlockUtil.getDistanceSq(pos) >= MathUtil.square(((AnvilAura)this.module).mineRange.getValue()) || highest != null && highest.getY() >= pos.getY()) continue;
            highest = pos;
        }
        if (highest != null) {
            ((AnvilAura)this.module).mineFacing = RayTraceUtil.getFacing((Entity)ListenerAnvilAura.mc.player, highest, true);
            ((AnvilAura)this.module).rotations = RotationUtil.getRotations(highest, ((AnvilAura)this.module).mineFacing);
            ((AnvilAura)this.module).minePos = highest;
            IBlockState mineState = ObbyModule.HELPER.getBlockState(highest);
            ((AnvilAura)this.module).mineBB = mineState.getSelectedBoundingBox((World)ListenerAnvilAura.mc.world, highest).grow((double)0.002f);
            ((AnvilAura)this.module).mineTimer.reset();
            ((AnvilAura)this.module).stage = AnvilStage.MINE;
            ((AnvilAura)this.module).action = () -> {
                PacketUtil.startDigging(((AnvilAura)this.module).minePos, ((AnvilAura)this.module).mineFacing);
                ListenerAnvilAura.mc.player.swingArm(EnumHand.MAIN_HAND);
                ((AnvilAura)this.module).mineTimer.reset();
                ((AnvilAura)this.module).action = null;
            };
            return true;
        }
        return this.placeTop(anvilResult, result);
    }

    private boolean placeTop(AnvilResult anvilResult, TargetResult result) {
        BlockPos highest = this.findHighest(anvilResult);
        if (highest == null) {
            return false;
        }
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            BlockPos pos = highest.offset(facing);
            if (ObbyModule.HELPER.getBlockState(pos).getMaterial().isReplaceable()) continue;
            result.getTargets().add(highest);
            ((AnvilAura)this.module).stage = AnvilStage.ANVIL;
            return true;
        }
        if (((AnvilAura)this.module).obbySlot == -1) {
            return false;
        }
        BlockPos[] ignore = this.toIgnore(anvilResult, highest);
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            BlockPos pos = highest.offset(facing);
            BasePath path = new BasePath((Entity)RotationUtil.getRotationPlayer(), pos, ((AnvilAura)this.module).helpingBlocks.getValue());
            PathFinder.findPath(path, ((AnvilAura)this.module).range.getValue(), ListenerAnvilAura.mc.world.loadedEntityList, (RayTraceMode)((Object)((AnvilAura)this.module).smartRay.getValue()), ObbyModule.HELPER, Blocks.OBSIDIAN.getDefaultState(), PathFinder.CHECK, ignore);
            int before = ((AnvilAura)this.module).slot;
            ((AnvilAura)this.module).slot = ((AnvilAura)this.module).obbySlot;
            if (ObbyUtil.place((ObbyModule)this.module, path)) {
                ((AnvilAura)this.module).slot = before;
                ((AnvilAura)this.module).stage = AnvilStage.OBSIDIAN;
                return true;
            }
            ((AnvilAura)this.module).slot = before;
        }
        return false;
    }

    private BlockPos findHighest(AnvilResult anvilResult) {
        BlockPos highest = null;
        for (BlockPos pos : anvilResult.getPositions()) {
            if (BlockUtil.getDistanceSq(pos) >= MathUtil.square(((AnvilAura)this.module).range.getValue()) || highest != null && highest.getY() >= pos.getY()) continue;
            highest = pos;
        }
        return highest;
    }

    private BlockPos[] toIgnore(AnvilResult anvilResult, BlockPos highest) {
        BlockPos base = anvilResult.getPressurePos();
        int length = highest.getY() - base.getY();
        BlockPos[] ignore = new BlockPos[length];
        for (int i = 0; i < length; ++i) {
            ignore[i] = base.up(i + 1);
        }
        return ignore;
    }
}

