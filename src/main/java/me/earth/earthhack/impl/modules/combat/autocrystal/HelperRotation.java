/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 *  net.minecraft.network.play.client.CPacketUseEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.modules.combat.autocrystal;

import java.util.concurrent.atomic.AtomicInteger;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.entity.IEntity;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.AntiWeakness;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.AutoSwitch;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.SwingTime;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.PositionData;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.RotationFunction;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.WeaknessSwitch;
import me.earth.earthhack.impl.modules.combat.offhand.Offhand;
import me.earth.earthhack.impl.modules.combat.offhand.modes.OffhandMode;
import me.earth.earthhack.impl.util.helpers.blocks.modes.PlaceSwing;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.raytrace.Ray;
import me.earth.earthhack.impl.util.math.raytrace.RayTraceFactory;
import me.earth.earthhack.impl.util.math.rotation.RotationSmoother;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.misc.MutableWrapper;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class HelperRotation
implements Globals {
    private static final AtomicInteger ID = new AtomicInteger();
    private static final ModuleCache<Offhand> OFFHAND = Caches.getModule(Offhand.class);
    private final RotationSmoother smoother = new RotationSmoother(Managers.ROTATION);
    private final AutoCrystal module;

    public HelperRotation(AutoCrystal module) {
        this.module = module;
    }

    public RotationFunction forPlacing(BlockPos pos, MutableWrapper<Boolean> hasPlaced) {
        int id = ID.incrementAndGet();
        StopWatch timer = new StopWatch();
        MutableWrapper<Boolean> ended = new MutableWrapper<Boolean>(false);
        return (x, y, z, yaw, pitch) -> {
            boolean breaking = false;
            float[] rotations = null;
            if ((Boolean)hasPlaced.get() != false || RotationUtil.getRotationPlayer().getDistanceSq(pos) > 64.0 && pos.distanceSq(x, y, z) > 64.0 || this.module.autoSwitch.getValue() != AutoSwitch.Always && !this.module.switching && !this.module.weaknessHelper.canSwitch() && !InventoryUtil.isHolding(Items.END_CRYSTAL)) {
                if (!((Boolean)ended.get()).booleanValue()) {
                    ended.set(true);
                    timer.reset();
                }
                if (!this.module.attack.getValue().booleanValue() || timer.passed(this.module.endRotations.getValue().intValue())) {
                    if (id == ID.get()) {
                        this.module.rotation = null;
                    }
                    return new float[]{yaw, pitch};
                }
                breaking = true;
                double height = 1.7 * (double)this.module.height.getValue().floatValue();
                rotations = RotationUtil.getRotations((float)pos.getX() + 0.5f, (double)(pos.getY() + 1) + height, (float)pos.getZ() + 0.5f, x, y, z, HelperRotation.mc.player.getEyeHeight());
            } else {
                double height = this.module.placeHeight.getValue();
                if (this.module.smartTrace.getValue().booleanValue()) {
                    for (EnumFacing facing : EnumFacing.values()) {
                        Ray ray = RayTraceFactory.rayTrace((Entity)HelperRotation.mc.player, pos, facing, (IBlockAccess)HelperRotation.mc.world, Blocks.OBSIDIAN.getDefaultState(), this.module.traceWidth.getValue());
                        if (!ray.isLegit()) continue;
                        rotations = ray.getRotations();
                        break;
                    }
                }
                if (rotations == null) {
                    rotations = this.module.fallbackTrace.getValue() != false ? RotationUtil.getRotations((double)pos.getX() + 0.5, (double)pos.getY() + 1.0, (double)pos.getZ() + 0.5, x, y, z, HelperRotation.mc.player.getEyeHeight()) : RotationUtil.getRotations((double)pos.getX() + 0.5, (double)pos.getY() + height, (double)pos.getZ() + 0.5, x, y, z, HelperRotation.mc.player.getEyeHeight());
                }
            }
            return this.smoother.smoothen(rotations, breaking ? (double)this.module.angle.getValue().floatValue() : (double)this.module.placeAngle.getValue().floatValue());
        };
    }

    public RotationFunction forBreaking(Entity entity, MutableWrapper<Boolean> attacked) {
        int id = ID.incrementAndGet();
        StopWatch timer = new StopWatch();
        MutableWrapper<Boolean> ended = new MutableWrapper<Boolean>(false);
        return (x, y, z, yaw, pitch) -> {
            if (RotationUtil.getRotationPlayer().getDistanceSq(entity) > 64.0) {
                attacked.set(true);
            }
            if (((Boolean)attacked.get()).booleanValue()) {
                if (!((Boolean)ended.get()).booleanValue()) {
                    ended.set(true);
                    timer.reset();
                }
                if (((Boolean)ended.get()).booleanValue() && timer.passed(this.module.endRotations.getValue().intValue())) {
                    if (id == ID.get()) {
                        this.module.rotation = null;
                    }
                    return new float[]{yaw, pitch};
                }
            }
            return this.smoother.getRotations(entity, x, y, z, HelperRotation.mc.player.getEyeHeight(), this.module.height.getValue().floatValue(), this.module.angle.getValue().floatValue());
        };
    }

    public RotationFunction forObby(PositionData data) {
        return (x, y, z, yaw, pitch) -> {
            if (data.getPath().length <= 0) {
                return new float[]{yaw, pitch};
            }
            Ray ray = data.getPath()[0];
            ray.updateRotations((Entity)RotationUtil.getRotationPlayer());
            return ray.getRotations();
        };
    }

    public Runnable post(AutoCrystal module, float damage, boolean realtime, BlockPos pos, MutableWrapper<Boolean> placed, MutableWrapper<Boolean> liquidBreak) {
        return () -> {
            RayTraceResult ray;
            if (liquidBreak != null && !((Boolean)liquidBreak.get()).booleanValue()) {
                return;
            }
            if (!InventoryUtil.isHolding(Items.END_CRYSTAL)) {
                if (module.autoSwitch.getValue() != AutoSwitch.Always && (module.autoSwitch.getValue() != AutoSwitch.Bind || !module.switching)) return;
                if (!module.mainHand.getValue().booleanValue()) {
                    OFFHAND.computeIfPresent(o -> o.setMode(OffhandMode.CRYSTAL));
                    if (!module.instantOffhand.getValue().booleanValue()) return;
                    if (((Offhand)OFFHAND.get()).isSafe()) {
                        ((Offhand)OFFHAND.get()).setMode(OffhandMode.CRYSTAL);
                        for (int i = 0; i < 3; ++i) {
                            ((Offhand)OFFHAND.get()).getTimer().setTime(10000L);
                            ((Offhand)OFFHAND.get()).doOffhand();
                        }
                    }
                    if (!InventoryUtil.isHolding(Items.END_CRYSTAL)) {
                        return;
                    }
                }
            }
            int slot = -1;
            EnumHand hand = InventoryUtil.getHand(Items.END_CRYSTAL);
            if (hand == null) {
                if (!module.mainHand.getValue().booleanValue()) return;
                slot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL, new Item[0]);
                if (slot == -1) {
                    return;
                }
                hand = slot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
            }
            if ((ray = RotationUtil.rayTraceTo(pos, (IBlockAccess)HelperRotation.mc.world)) == null || !pos.equals((Object)ray.getBlockPos())) {
                if (!module.rotate.getValue().noRotate(ACRotate.Place)) {
                    return;
                }
                ray = new RayTraceResult(new Vec3d(0.5, 1.0, 0.5), EnumFacing.UP);
            } else if (module.fallbackTrace.getValue().booleanValue() && HelperRotation.mc.world.getBlockState(ray.getBlockPos().offset(ray.sideHit)).getMaterial().isSolid()) {
                ray = new RayTraceResult(new Vec3d(0.5, 1.0, 0.5), EnumFacing.UP);
            }
            module.switching = false;
            SwingTime swingTime = module.placeSwing.getValue();
            float[] f = RayTraceUtil.hitVecToPlaceVec(pos, ray.hitVec);
            boolean noGodded = false;
            if (module.idHelper.isDangerous((EntityPlayer)HelperRotation.mc.player, module.holdingCheck.getValue(), module.toolCheck.getValue())) {
                module.noGod = true;
                noGodded = true;
            }
            int finalSlot = slot;
            EnumHand finalHand = hand;
            RayTraceResult finalRay = ray;
            boolean finalNoGodded = noGodded;
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> {
                int lastSlot = HelperRotation.mc.player.inventory.currentItem;
                if (finalSlot != -1 && finalSlot != -2) {
                    switch (module.cooldownBypass.getValue()) {
                        case None: {
                            InventoryUtil.switchTo(finalSlot);
                            break;
                        }
                        case Slot: {
                            InventoryUtil.switchToBypassAlt(InventoryUtil.hotbarToInventory(finalSlot));
                            break;
                        }
                        case Pick: {
                            InventoryUtil.bypassSwitch(finalSlot);
                        }
                    }
                }
                InventoryUtil.syncItem();
                if (swingTime == SwingTime.Pre) {
                    this.swing(finalHand, false);
                }
                HelperRotation.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(pos, finalRay.sideHit, finalHand, f[0], f[1], f[2]));
                if (finalNoGodded) {
                    module.noGod = false;
                }
                placed.set(true);
                if (swingTime == SwingTime.Post) {
                    this.swing(finalHand, false);
                }
                if (module.switchBack.getValue().booleanValue()) {
                    switch (module.cooldownBypass.getValue()) {
                        case None: {
                            InventoryUtil.switchTo(lastSlot);
                            break;
                        }
                        case Slot: {
                            InventoryUtil.switchToBypassAlt(InventoryUtil.hotbarToInventory(finalSlot));
                            break;
                        }
                        case Pick: {
                            InventoryUtil.bypassSwitch(finalSlot);
                        }
                    }
                }
            });
            if (realtime) {
                module.setRenderPos(pos, damage);
            }
            if (module.simulatePlace.getValue() == 0) return;
            module.crystalRender.addFakeCrystal(new EntityEnderCrystal((World)HelperRotation.mc.world, (double)((float)pos.getX() + 0.5f), (double)(pos.getY() + 1), (double)((float)pos.getZ() + 0.5f)));
        };
    }

    public Runnable post(Entity entity, MutableWrapper<Boolean> attacked) {
        return () -> {
            WeaknessSwitch w = HelperRotation.antiWeakness(this.module);
            if (w.needsSwitch() && w.getSlot() == -1 || EntityUtil.isDead(entity) || !this.module.rotate.getValue().noRotate(ACRotate.Break) && !RotationUtil.isLegit(entity, new Entity[0])) {
                return;
            }
            CPacketUseEntity packet = new CPacketUseEntity(entity);
            SwingTime swingTime = this.module.breakSwing.getValue();
            Runnable runnable = () -> {
                int lastSlot = HelperRotation.mc.player.inventory.currentItem;
                if (w.getSlot() != -1) {
                    switch (this.module.antiWeaknessBypass.getValue()) {
                        case None: {
                            InventoryUtil.switchTo(w.getSlot());
                            break;
                        }
                        case Slot: {
                            InventoryUtil.switchToBypassAlt(InventoryUtil.hotbarToInventory(w.getSlot()));
                            break;
                        }
                        case Pick: {
                            InventoryUtil.bypassSwitch(w.getSlot());
                        }
                    }
                }
                if (swingTime == SwingTime.Pre) {
                    this.swing(EnumHand.MAIN_HAND, true);
                }
                HelperRotation.mc.player.connection.sendPacket((Packet)packet);
                attacked.set(true);
                if (swingTime == SwingTime.Post) {
                    this.swing(EnumHand.MAIN_HAND, true);
                }
                if (w.getSlot() != -1) {
                    switch (this.module.antiWeaknessBypass.getValue()) {
                        case None: {
                            InventoryUtil.switchTo(lastSlot);
                            break;
                        }
                        case Slot: {
                            InventoryUtil.switchToBypassAlt(InventoryUtil.hotbarToInventory(w.getSlot()));
                            break;
                        }
                        case Pick: {
                            InventoryUtil.bypassSwitch(w.getSlot());
                        }
                    }
                }
            };
            if (w.getSlot() != -1) {
                Locks.acquire(Locks.PLACE_SWITCH_LOCK, runnable);
            } else {
                runnable.run();
            }
            if (this.module.pseudoSetDead.getValue().booleanValue()) {
                ((IEntity)entity).setPseudoDead(true);
            }
            if (this.module.setDead.getValue().booleanValue()) {
                Managers.SET_DEAD.setDead(entity);
            }
        };
    }

    public Runnable postBlock(PositionData data) {
        return this.postBlock(data, -1, this.module.obbyRotate.getValue(), null, null);
    }

    public Runnable postBlock(PositionData data, int preSlot, Rotate rotate, MutableWrapper<Boolean> placed, MutableWrapper<Integer> switchBack) {
        return () -> {
            int slot;
            if (!data.isValid()) {
                return;
            }
            int n = slot = preSlot == -1 ? InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN, new Block[0]) : preSlot;
            if (slot == -1) {
                return;
            }
            EnumHand hand = slot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
            PlaceSwing placeSwing = this.module.obbySwing.getValue();
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> {
                int lastSlot = HelperRotation.mc.player.inventory.currentItem;
                if (switchBack != null) {
                    switchBack.set(lastSlot);
                }
                switch (this.module.obsidianBypass.getValue()) {
                    case None: {
                        InventoryUtil.switchTo(slot);
                        break;
                    }
                    case Slot: {
                        InventoryUtil.switchToBypassAlt(InventoryUtil.hotbarToInventory(slot));
                        break;
                    }
                    case Pick: {
                        InventoryUtil.bypassSwitch(slot);
                    }
                }
                for (Ray ray : data.getPath()) {
                    if (rotate == Rotate.Packet && !RotationUtil.isLegit(ray.getPos(), ray.getFacing())) {
                        Managers.ROTATION.setBlocking(true);
                        float[] r = ray.getRotations();
                        HelperRotation.mc.player.connection.sendPacket((Packet)PacketUtil.rotation(r[0], r[1], HelperRotation.mc.player.onGround));
                        Managers.ROTATION.setBlocking(false);
                    }
                    float[] f = RayTraceUtil.hitVecToPlaceVec(ray.getPos(), ray.getResult().hitVec);
                    HelperRotation.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(ray.getPos(), ray.getFacing(), hand, f[0], f[1], f[2]));
                    if (this.module.setState.getValue().booleanValue() && preSlot == -1) {
                        mc.addScheduledTask(() -> {
                            if (HelperRotation.mc.world != null) {
                                HelperRotation.mc.world.setBlockState(ray.getPos().offset(ray.getFacing()), Blocks.OBSIDIAN.getDefaultState());
                            }
                        });
                    }
                    if (placeSwing != PlaceSwing.Always) continue;
                    Swing.Packet.swing(hand);
                }
                if (placeSwing == PlaceSwing.Once) {
                    Swing.Packet.swing(hand);
                }
                if (switchBack == null) {
                    switch (this.module.obsidianBypass.getValue()) {
                        case None: {
                            InventoryUtil.switchTo(lastSlot);
                            break;
                        }
                        case Slot: {
                            InventoryUtil.switchToBypassAlt(InventoryUtil.hotbarToInventory(slot));
                            break;
                        }
                        case Pick: {
                            InventoryUtil.bypassSwitch(slot);
                        }
                    }
                }
                if (placed != null) {
                    placed.set(true);
                }
            });
            EnumHand swingHand = this.module.obbyHand.getValue().getHand();
            if (swingHand != null) {
                Swing.Client.swing(swingHand);
            }
        };
    }

    public Runnable breakBlock(int toolSlot, MutableWrapper<Boolean> placed, MutableWrapper<Integer> lastSlot, int[] order, Ray ... positions) {
        return Locks.wrap(Locks.PLACE_SWITCH_LOCK, () -> {
            if (order.length != positions.length) {
                throw new IndexOutOfBoundsException("Order length: " + order.length + ", Positions length: " + positions.length);
            }
            if (!((Boolean)placed.get()).booleanValue()) {
                return;
            }
            switch (this.module.mineBypass.getValue()) {
                case None: {
                    InventoryUtil.switchTo(toolSlot);
                    break;
                }
                case Slot: {
                    InventoryUtil.switchToBypassAlt(InventoryUtil.hotbarToInventory(toolSlot));
                    break;
                }
                case Pick: {
                    InventoryUtil.bypassSwitch(toolSlot);
                }
            }
            for (int i : order) {
                Ray ray = positions[i];
                BlockPos pos = ray.getPos().offset(ray.getFacing());
                PacketUtil.startDigging(pos, ray.getFacing().getOpposite());
                PacketUtil.stopDigging(pos, ray.getFacing().getOpposite());
                Swing.Packet.swing(EnumHand.MAIN_HAND);
            }
            switch (this.module.mineBypass.getValue()) {
                case None: {
                    InventoryUtil.switchTo((Integer)lastSlot.get());
                    break;
                }
                case Slot: {
                    InventoryUtil.switchToBypassAlt(InventoryUtil.hotbarToInventory(toolSlot));
                    break;
                }
                case Pick: {
                    InventoryUtil.bypassSwitch(toolSlot);
                }
            }
        });
    }

    private void swing(EnumHand hand, boolean breaking) {
        EnumHand swingHand;
        Swing.Packet.swing(hand);
        EnumHand enumHand = swingHand = breaking ? this.module.swing.getValue().getHand() : this.module.placeHand.getValue().getHand();
        if (swingHand != null) {
            Swing.Client.swing(swingHand);
        }
    }

    public static WeaknessSwitch antiWeakness(AutoCrystal module) {
        if (!module.weaknessHelper.isWeaknessed()) {
            return WeaknessSwitch.NONE;
        }
        if (module.antiWeakness.getValue() == AntiWeakness.None || module.cooldown.getValue() != 0) {
            return WeaknessSwitch.INVALID;
        }
        return new WeaknessSwitch(DamageUtil.findAntiWeakness(), true);
    }
}

