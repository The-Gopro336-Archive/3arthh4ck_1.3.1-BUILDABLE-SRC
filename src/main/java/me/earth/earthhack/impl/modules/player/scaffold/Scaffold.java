/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.modules.player.scaffold;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.player.scaffold.ListenerInput;
import me.earth.earthhack.impl.modules.player.scaffold.ListenerMotion;
import me.earth.earthhack.impl.modules.player.scaffold.ListenerMove;
import me.earth.earthhack.impl.modules.player.scaffold.ListenerPush;
import me.earth.earthhack.impl.util.helpers.addable.BlockAddingModule;
import me.earth.earthhack.impl.util.helpers.addable.ListType;
import me.earth.earthhack.impl.util.helpers.blocks.attack.InstantAttackListener;
import me.earth.earthhack.impl.util.helpers.blocks.attack.InstantAttackingModule;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Pop;
import me.earth.earthhack.impl.util.math.Passable;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class Scaffold
extends BlockAddingModule
implements InstantAttackingModule {
    protected final Setting<Boolean> tower = this.register(new BooleanSetting("Tower", true));
    protected final Setting<Boolean> down = this.register(new BooleanSetting("Down", false));
    protected final Setting<Boolean> offset = this.register(new BooleanSetting("Offset", true));
    protected final Setting<Boolean> rotate = this.register(new BooleanSetting("Rotate", true));
    protected final Setting<Integer> keepRotations = this.register(new NumberSetting<Integer>("Keep-Rotations", 0, 0, 500));
    protected final Setting<Integer> preRotate = this.register(new NumberSetting<Integer>("Pre-Rotations", 0, 0, 500));
    protected final Setting<Boolean> aac = this.register(new BooleanSetting("AAC", false));
    protected final Setting<Integer> aacDelay = this.register(new NumberSetting<Integer>("AAC-Delay", 150, 0, 1000));
    protected final Setting<Boolean> stopSprint = this.register(new BooleanSetting("StopSprint", false));
    protected final Setting<Boolean> fastSneak = this.register(new BooleanSetting("FastDown", false));
    protected final Setting<Boolean> helping = this.register(new BooleanSetting("Helping", false));
    protected final Setting<Boolean> swing = this.register(new BooleanSetting("Swing", false));
    protected final Setting<Boolean> checkState = this.register(new BooleanSetting("CheckState", true));
    protected final Setting<Boolean> smartSneak = this.register(new BooleanSetting("Smart-Sneak", true));
    protected final Setting<Boolean> attack = this.register(new BooleanSetting("Attack", false));
    protected final Setting<Boolean> instant = this.register(new BooleanSetting("Instant", true));
    protected final Setting<Pop> pop = this.register(new EnumSetting<Pop>("Pop", Pop.None));
    protected final Setting<Integer> popTime = this.register(new NumberSetting<Integer>("Pop-Time", 500, 0, 500));
    protected final Setting<Integer> cooldown = this.register(new NumberSetting<Integer>("Cooldown", 500, 0, 500));
    protected final Setting<Integer> breakDelay = this.register(new NumberSetting<Integer>("BreakDelay", 250, 0, 500));
    protected final Setting<Boolean> freecam = this.register(new BooleanSetting("Freecam", false));
    protected final Setting<Boolean> spectate = this.register(new BooleanSetting("Spectate", false));
    protected final StopWatch rotationTimer = new StopWatch();
    protected final StopWatch breakTimer = new StopWatch();
    protected final StopWatch towerTimer = new StopWatch();
    protected final StopWatch aacTimer = new StopWatch();
    protected final StopWatch timer = new StopWatch();
    protected float[] rotations;
    protected EnumFacing facing;
    protected Entity crystal;
    protected BlockPos pos;
    protected BlockPos rot;

    public Scaffold() {
        super("Scaffold", Category.Player, s -> "Black/Whitelist " + s.getName() + " from Scaffolding.");
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerMove(this));
        this.listeners.add(new ListenerPush(this));
        this.listeners.add(new ListenerInput(this));
        this.listeners.add(new InstantAttackListener<Scaffold>(this));
        this.listType.setValue(ListType.BlackList);
    }

    @Override
    protected void onEnable() {
        this.towerTimer.reset();
        this.pos = null;
        this.facing = null;
        this.rot = null;
    }

    protected BlockPos findNextPos() {
        BlockPos leftPos;
        BlockPos backPos;
        BlockPos underPos = new BlockPos((Entity)Scaffold.mc.player).down();
        boolean under = false;
        if (this.down.getValue().booleanValue() && !Scaffold.mc.gameSettings.keyBindJump.isKeyDown() && Scaffold.mc.gameSettings.keyBindSneak.isKeyDown()) {
            under = true;
            underPos = underPos.down();
        }
        if (Scaffold.mc.world.getBlockState(underPos).getMaterial().isReplaceable() && (!under || Scaffold.mc.world.getBlockState(underPos.up()).getMaterial().isReplaceable())) {
            return underPos;
        }
        if (!this.offset.getValue().booleanValue()) {
            return null;
        }
        if (Scaffold.mc.gameSettings.keyBindForward.isKeyDown() && !Scaffold.mc.gameSettings.keyBindBack.isKeyDown()) {
            BlockPos forwardPos = underPos.offset(Scaffold.mc.player.getHorizontalFacing());
            if (Scaffold.mc.world.getBlockState(forwardPos).getMaterial().isReplaceable()) {
                return forwardPos;
            }
        } else if (Scaffold.mc.gameSettings.keyBindBack.isKeyDown() && !Scaffold.mc.gameSettings.keyBindForward.isKeyDown() && Scaffold.mc.world.getBlockState(backPos = underPos.offset(Scaffold.mc.player.getHorizontalFacing().getOpposite())).getMaterial().isReplaceable()) {
            return backPos;
        }
        if (Scaffold.mc.gameSettings.keyBindRight.isKeyDown() && !Scaffold.mc.gameSettings.keyBindLeft.isKeyDown()) {
            BlockPos rightPos = underPos.offset(Scaffold.mc.player.getHorizontalFacing().rotateY());
            if (Scaffold.mc.world.getBlockState(rightPos).getMaterial().isReplaceable()) {
                return rightPos;
            }
        } else if (Scaffold.mc.gameSettings.keyBindLeft.isKeyDown() && !Scaffold.mc.gameSettings.keyBindRight.isKeyDown() && Scaffold.mc.world.getBlockState(leftPos = underPos.offset(Scaffold.mc.player.getHorizontalFacing().rotateYCCW())).getMaterial().isReplaceable()) {
            return leftPos;
        }
        return null;
    }

    @Override
    public Pop getPop() {
        return this.pop.getValue();
    }

    @Override
    public int getPopTime() {
        return this.popTime.getValue();
    }

    @Override
    public double getRange() {
        return 6.0;
    }

    @Override
    public double getTrace() {
        return 3.0;
    }

    @Override
    public boolean shouldAttack(EntityEnderCrystal entity) {
        if (!this.attack.getValue().booleanValue() || !this.instant.getValue().booleanValue() || MovementUtil.noMovementKeys() && !Scaffold.mc.player.movementInput.jump) {
            return false;
        }
        BlockPos pos = this.pos;
        if (pos != null) {
            return entity.getEntityBoundingBox().intersects(new AxisAlignedBB(pos));
        }
        return false;
    }

    @Override
    public Passable getTimer() {
        return this.breakTimer;
    }

    @Override
    public int getBreakDelay() {
        return this.breakDelay.getValue();
    }

    @Override
    public int getCooldown() {
        return this.cooldown.getValue();
    }
}

