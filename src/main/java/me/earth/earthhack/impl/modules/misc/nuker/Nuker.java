/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.init.Blocks
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer$Rotation
 *  net.minecraft.network.play.client.CPacketPlayerDigging
 *  net.minecraft.network.play.client.CPacketPlayerDigging$Action
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.Vec3d
 */
package me.earth.earthhack.impl.modules.misc.nuker;

import java.awt.Color;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.misc.nuker.ListenerChange;
import me.earth.earthhack.impl.modules.misc.nuker.ListenerClickBlock;
import me.earth.earthhack.impl.modules.misc.nuker.ListenerMotion;
import me.earth.earthhack.impl.modules.misc.nuker.ListenerMultiChange;
import me.earth.earthhack.impl.modules.misc.nuker.ListenerRender;
import me.earth.earthhack.impl.modules.misc.nuker.NukerData;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.SpecialBlocks;
import me.earth.earthhack.impl.util.minecraft.blocks.mine.MineUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class Nuker
extends Module {
    protected final Setting<Boolean> nuke = this.register(new BooleanSetting("Nuke", true));
    protected final Setting<Integer> blocks = this.register(new NumberSetting<Integer>("Blocks/Attack", 1, 1, 50));
    protected final Setting<Integer> delay = this.register(new NumberSetting<Integer>("Click-Delay", 25, 0, 250));
    protected final Setting<Rotate> rotate = this.register(new EnumSetting<Rotate>("Rotations", Rotate.None));
    protected final Setting<Integer> width = this.register(new NumberSetting<Integer>("Selection-W", 1, 1, 6));
    protected final Setting<Integer> height = this.register(new NumberSetting<Integer>("Selection-H", 1, 1, 6));
    protected final Setting<Float> range = this.register(new NumberSetting<Float>("Range", Float.valueOf(6.0f), Float.valueOf(0.1f), Float.valueOf(6.0f)));
    protected final Setting<Boolean> render = this.register(new BooleanSetting("Render", true));
    protected final Setting<Color> color = this.register(new ColorSetting("Color", new Color(255, 255, 255, 125)));
    protected final Setting<Boolean> shulkers = this.register(new BooleanSetting("Shulkers", false));
    protected final Setting<Boolean> hoppers = this.register(new BooleanSetting("Hoppers", false));
    protected final Setting<Boolean> instant = this.register(new BooleanSetting("Predict", false));
    protected final Setting<Swing> swing = this.register(new EnumSetting<Swing>("Swing", Swing.Packet));
    protected final Setting<Boolean> speedMine = this.register(new BooleanSetting("Packet", true));
    protected final Setting<Boolean> autoTool = this.register(new BooleanSetting("AutoTool", true));
    protected final Setting<Integer> timeout = this.register(new NumberSetting<Integer>("Delay", 100, 50, 500));
    protected final Queue<Runnable> actions = new LinkedList<Runnable>();
    protected final StopWatch timer = new StopWatch();
    protected Set<BlockPos> currentSelection;
    protected float[] rotations;
    protected boolean breaking;
    protected int lastSlot;

    public Nuker() {
        super("Nuker", Category.Misc);
        this.listeners.add(new ListenerClickBlock(this));
        this.listeners.add(new ListenerMultiChange(this));
        this.listeners.add(new ListenerChange(this));
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerRender(this));
        this.setData(new NukerData(this));
    }

    @Override
    protected void onEnable() {
        this.currentSelection = null;
        this.rotations = null;
        this.breaking = false;
        this.actions.clear();
    }

    protected Set<Block> getBlocks() {
        HashSet<Block> result = new HashSet<Block>();
        if (this.hoppers.getValue().booleanValue()) {
            result.add((Block)Blocks.HOPPER);
        }
        if (this.shulkers.getValue().booleanValue()) {
            result.addAll(SpecialBlocks.SHULKERS);
        }
        return result;
    }

    protected void breakSelection(Set<BlockPos> selection, boolean autoTool) {
        int i = 1;
        this.lastSlot = -1;
        HashSet<BlockPos> toRemove = new HashSet<BlockPos>();
        for (BlockPos pos : selection) {
            RayTraceResult result;
            float[] rotations;
            if (!MineUtil.canBreak(pos)) {
                toRemove.add(pos);
                continue;
            }
            if (this.rotate.getValue() != Rotate.None) {
                rotations = RotationUtil.getRotationsToTopMiddle(pos.up());
                result = RayTraceUtil.getRayTraceResult(rotations[0], rotations[1], this.range.getValue().floatValue());
            } else {
                rotations = null;
                result = new RayTraceResult(new Vec3d(0.5, 1.0, 0.5), EnumFacing.UP);
            }
            if (rotations != null) {
                if (this.rotations == null) {
                    this.rotations = rotations;
                } else {
                    this.actions.add(() -> Nuker.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(rotations[0], rotations[1], Nuker.mc.player.onGround)));
                }
            }
            if (this.rotate.getValue() == Rotate.None) {
                Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> {
                    if (autoTool) {
                        if (this.lastSlot == -1) {
                            this.lastSlot = Nuker.mc.player.inventory.currentItem;
                        }
                        InventoryUtil.switchTo(MineUtil.findBestTool(pos));
                    }
                    if (this.speedMine.getValue().booleanValue()) {
                        Nuker.mc.player.connection.sendPacket(this.getPacket(pos, result.sideHit, true));
                        Nuker.mc.player.connection.sendPacket(this.getPacket(pos, result.sideHit, false));
                    } else {
                        Nuker.mc.playerController.onPlayerDamageBlock(pos, result.sideHit);
                    }
                    this.swing.getValue().swing(EnumHand.MAIN_HAND);
                });
            } else {
                if (autoTool) {
                    this.actions.add(() -> {
                        if (this.lastSlot == -1) {
                            this.lastSlot = Nuker.mc.player.inventory.currentItem;
                        }
                        InventoryUtil.switchTo(MineUtil.findBestTool(pos));
                    });
                }
                if (this.speedMine.getValue().booleanValue()) {
                    this.actions.add(() -> {
                        Nuker.mc.player.connection.sendPacket(this.getPacket(pos, result.sideHit, true));
                        Nuker.mc.player.connection.sendPacket(this.getPacket(pos, result.sideHit, false));
                    });
                } else {
                    this.actions.add(() -> Nuker.mc.playerController.onPlayerDamageBlock(pos, result.sideHit));
                }
                this.actions.add(() -> this.swing.getValue().swing(EnumHand.MAIN_HAND));
            }
            toRemove.add(pos);
            if (i >= this.blocks.getValue() || this.rotate.getValue() == Rotate.Normal) break;
            ++i;
        }
        selection.removeAll(toRemove);
        if (!this.actions.isEmpty()) {
            if (autoTool) {
                InventoryUtil.switchTo(this.lastSlot);
            }
            this.timer.reset();
        }
    }

    protected void attack(BlockPos pos) {
        float[] rotations = RotationUtil.getRotationsToTopMiddle(pos.up());
        RayTraceResult result = RayTraceUtil.getRayTraceResult(rotations[0], rotations[1], this.range.getValue().floatValue());
        if (this.rotate.getValue() == Rotate.Packet) {
            Nuker.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(rotations[0], rotations[1], Nuker.mc.player.onGround));
        }
        Nuker.mc.player.connection.sendPacket(this.getPacket(pos, result.sideHit, true));
        Nuker.mc.player.connection.sendPacket(this.getPacket(pos, result.sideHit, false));
    }

    protected Packet<?> getPacket(BlockPos pos, EnumFacing facing, boolean start) {
        if (start) {
            return new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, facing);
        }
        return new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, facing);
    }

    public Set<BlockPos> getSelection(BlockPos pos) {
        LinkedHashSet<BlockPos> result = new LinkedHashSet<BlockPos>();
        result.add(pos);
        EnumFacing entityF = EnumFacing.getDirectionFromEntityLiving((BlockPos)pos, (EntityLivingBase)Nuker.mc.player).getOpposite();
        EnumFacing horizontal = Nuker.mc.player.getHorizontalFacing();
        for (int i = 1; i < this.width.getValue(); ++i) {
            EnumFacing facing = this.getFacing(i, entityF, false, horizontal);
            BlockPos w = pos.offset(facing);
            while (result.contains((Object)w)) {
                w = w.offset(facing);
            }
            if (!MineUtil.canBreak(w) || !(BlockUtil.getDistanceSqDigging((Entity)Nuker.mc.player, w) <= (double)MathUtil.square(this.range.getValue().floatValue()))) continue;
            result.add(w);
        }
        LinkedHashSet<BlockPos> added = new LinkedHashSet<BlockPos>(result);
        for (int i = 1; i < this.height.getValue(); ++i) {
            EnumFacing facing = this.getFacing(i, entityF, true, horizontal);
            for (BlockPos p : result) {
                BlockPos h = p.offset(facing);
                while (added.contains((Object)h)) {
                    h = h.offset(facing);
                }
                if (!MineUtil.canBreak(h) || !(BlockUtil.getDistanceSqDigging((Entity)Nuker.mc.player, h) <= (double)MathUtil.square(this.range.getValue().floatValue())) || entityF != EnumFacing.DOWN && !(Nuker.mc.player.posY < (double)pos.getY())) continue;
                added.add(h);
            }
        }
        return added;
    }

    private EnumFacing getFacing(int index, EnumFacing entityFacing, boolean h, EnumFacing horizontal) {
        if (entityFacing == EnumFacing.UP || entityFacing == EnumFacing.DOWN) {
            if (h) {
                return index % 2 == 0 ? horizontal.getOpposite() : horizontal;
            }
            EnumFacing result = this.get2ndHorizontalOpposite(horizontal);
            return index % 2 == 0 ? result.getOpposite() : result;
        }
        if (h) {
            return index % 2 == 0 ? EnumFacing.UP : EnumFacing.DOWN;
        }
        EnumFacing result = this.get2ndHorizontalOpposite(horizontal);
        return index % 2 == 0 ? result.getOpposite() : result;
    }

    private EnumFacing get2ndHorizontalOpposite(EnumFacing facing) {
        for (EnumFacing f : EnumFacing.values()) {
            if (f == facing || f.getOpposite() == facing || f == EnumFacing.UP || f == EnumFacing.DOWN) continue;
            return f;
        }
        return EnumFacing.UP;
    }
}

