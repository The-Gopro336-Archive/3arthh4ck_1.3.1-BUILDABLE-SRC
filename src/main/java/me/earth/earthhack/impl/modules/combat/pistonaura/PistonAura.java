package me.earth.earthhack.impl.modules.combat.pistonaura;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.pistonaura.ListenerBlockChange;
import me.earth.earthhack.impl.modules.combat.pistonaura.ListenerDestroyEntities;
import me.earth.earthhack.impl.modules.combat.pistonaura.ListenerExplosion;
import me.earth.earthhack.impl.modules.combat.pistonaura.ListenerMotion;
import me.earth.earthhack.impl.modules.combat.pistonaura.ListenerMultiBlockChange;
import me.earth.earthhack.impl.modules.combat.pistonaura.ListenerSpawnObject;
import me.earth.earthhack.impl.modules.combat.pistonaura.PistonAuraData;
import me.earth.earthhack.impl.modules.combat.pistonaura.modes.PistonTarget;
import me.earth.earthhack.impl.modules.combat.pistonaura.util.PistonData;
import me.earth.earthhack.impl.modules.combat.pistonaura.util.PistonStage;
import me.earth.earthhack.impl.util.helpers.blocks.BlockPlacingModule;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.HoleUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PistonAura
extends BlockPlacingModule {
    protected final Setting<Boolean> multiDirectional = this.register(new BooleanSetting("MultiDirectional", false));
    protected final Setting<Boolean> explode = this.register(new BooleanSetting("Break", true));
    protected final Setting<Integer> breakDelay = this.register(new NumberSetting<Integer>("BreakDelay", 50, 0, 500));
    protected final Setting<Float> breakRange = this.register(new NumberSetting<Float>("BreakRange", Float.valueOf(4.5f), Float.valueOf(0.1f), Float.valueOf(6.0f)));
    protected final Setting<Float> breakTrace = this.register(new NumberSetting<Float>("BreakTrace", Float.valueOf(3.0f), Float.valueOf(0.1f), Float.valueOf(6.0f)));
    protected final Setting<Float> placeRange = this.register(new NumberSetting<Float>("PlaceRange", Float.valueOf(4.5f), Float.valueOf(0.1f), Float.valueOf(6.0f)));
    protected final Setting<Float> placeTrace = this.register(new NumberSetting<Float>("PlaceTrace", Float.valueOf(4.5f), Float.valueOf(0.1f), Float.valueOf(6.0f)));
    protected final Setting<Integer> coolDown = this.register(new NumberSetting<Integer>("Cooldown", 500, 0, 500));
    protected final Setting<Boolean> suicide = this.register(new BooleanSetting("Suicide", false));
    protected final Setting<Boolean> newVer = this.register(new BooleanSetting("1.13+", false));
    protected final Setting<PistonTarget> targetMode = this.register(new EnumSetting<PistonTarget>("Target", PistonTarget.Calc));
    protected final Setting<Boolean> instant = this.register(new BooleanSetting("Instant", true));
    protected final Setting<Integer> confirmation = this.register(new NumberSetting<Integer>("Confirm", 250, 0, 1000));
    protected final Setting<Integer> next = this.register(new NumberSetting<Integer>("NextPhase", 1000, 0, 5000));
    protected final Setting<Boolean> explosions = this.register(new BooleanSetting("Explosions", true));
    protected final Setting<Boolean> destroyEntities = this.register(new BooleanSetting("DestroyEntities", false));
    protected final Setting<Boolean> multiChange = this.register(new BooleanSetting("MultiChange", false));
    protected final Setting<Boolean> change = this.register(new BooleanSetting("Change", false));
    protected final Set<Block> clicked = new HashSet<Block>();
    protected final Queue<Runnable> actions = new LinkedList<Runnable>();
    protected final StopWatch breakTimer = new StopWatch();
    protected final StopWatch packetTimer = new StopWatch();
    protected final StopWatch nextTimer = new StopWatch();
    protected PistonStage stage = PistonStage.PISTON;
    protected int pistonSlot = -1;
    protected int redstoneSlot = -1;
    protected int crystalSlot = -1;
    protected EntityPlayer target;
    protected PistonData current;
    protected int index;
    protected int entityId;
    protected boolean reset;

    public PistonAura() {
        super("PistonAura", Category.Combat);
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerSpawnObject(this));
        this.listeners.add(new ListenerExplosion(this));
        this.listeners.add(new ListenerDestroyEntities(this));
        this.listeners.add(new ListenerMultiBlockChange(this));
        this.listeners.add(new ListenerBlockChange(this));
        this.packet.setValue(false);
        this.delay.setValue(0);
        this.setData(new PistonAuraData(this));
        this.rotate.setValue(Rotate.Normal);
        this.rotate.addObserver(event -> {
            if (event.getValue() != Rotate.Normal) {
                event.setCancelled(true);
            }
        });
    }

    @Override
    public String getDisplayInfo() {
        if (EntityUtil.isValid(this.target, 9.0)) {
            return this.target.getName();
        }
        return null;
    }

    @Override
    protected void onEnable() {
        this.pistonSlot = -1;
        this.redstoneSlot = -1;
        this.crystalSlot = -1;
        this.current = null;
        this.index = 0;
        this.reset = false;
        this.packetTimer.reset();
        this.nextTimer.reset();
        if (PistonAura.mc.player == null) {
            this.disable();
            return;
        }
        this.slot = PistonAura.mc.player.inventory.currentItem;
    }

    public void disableWithMessage(String message) {
        this.disable();
        Managers.CHAT.sendDeleteMessage(message, this.getDisplayName(), 2000);
    }

    protected PistonData findTarget() {
        this.index = 0;
        this.stage = null;
        this.reset = false;
        this.packetTimer.reset();
        this.nextTimer.reset();
        ArrayList<PistonData> data = new ArrayList<PistonData>();
        switch (this.targetMode.getValue()) {
            case FOV: {
                EntityPlayer closest = null;
                double closestAngle = 360.0;
                BlockPos pos = null;
                for (EntityPlayer player : PistonAura.mc.world.playerEntities) {
                    double angle;
                    if (!EntityUtil.isValid(player, 9.0)) continue;
                    BlockPos playerPos = PositionUtil.getPosition(player);
                    if (!this.suicide.getValue().booleanValue() && PositionUtil.getPosition().equals(playerPos) || !HoleUtil.isHole(playerPos, false)[0] && !HoleUtil.is2x1(playerPos) || !((angle = RotationUtil.getAngle(player, 1.4)) < closestAngle)) continue;
                    closest = player;
                    closestAngle = angle;
                    pos = playerPos;
                }
                if (closest == null) break;
                data.addAll(this.checkPlayer(closest, pos));
                break;
            }
            case Closest: {
                EntityPlayer closestEnemy = EntityUtil.getClosestEnemy();
                if (closestEnemy == null) break;
                BlockPos playerPos = PositionUtil.getPosition(closestEnemy);
                if (!this.suicide.getValue().booleanValue() && PositionUtil.getPosition().equals(playerPos) || !HoleUtil.isHole(playerPos, false)[0] && !HoleUtil.is2x1(playerPos)) break;
                data.addAll(this.checkPlayer(closestEnemy, playerPos));
                break;
            }
            case Calc: {
                for (EntityPlayer player : PistonAura.mc.world.playerEntities) {
                    if (!EntityUtil.isValid(player, 9.0)) continue;
                    BlockPos playerPos = PositionUtil.getPosition(player);
                    if (!this.suicide.getValue().booleanValue() && PositionUtil.getPosition().equals(playerPos) || !HoleUtil.isHole(playerPos, false)[0] && !HoleUtil.is2x1(playerPos)) continue;
                    data.addAll(this.checkPlayer(player, playerPos));
                }
                break;
            }
        }
        if (data.isEmpty()) {
            return null;
        }
        List nonMulti = data.stream().filter(d -> !d.isMulti()).collect(Collectors.toList());
        if (!nonMulti.isEmpty()) {
            nonMulti.sort(Comparator.comparingDouble(d -> PistonAura.mc.player.getDistanceSq(d.getCrystalPos())));
            return (PistonData)nonMulti.get(0);
        }
        data.sort(Comparator.comparingDouble(d -> PistonAura.mc.player.getDistanceSq(d.getCrystalPos())));
        return (PistonData)data.get(0);
    }

    private List<PistonData> checkPlayer(EntityPlayer player, BlockPos pos) {
        ArrayList<PistonData> data = new ArrayList<PistonData>(this.checkFacings(player, pos));
        if (data.isEmpty() && PistonAura.mc.world.getBlockState(pos.up(2)).func_185904_a().isReplaceable()) {
            data.addAll(this.checkFacings(player, pos.up()));
        }
        return data;
    }

    protected boolean checkUpdate(BlockPos pos, IBlockState state, BlockPos dataPos, Block block1, Block block2) {
        if (pos.equals(dataPos)) {
            IBlockState before = PistonAura.mc.world.getBlockState(pos);
            return (before.getBlock() == block1 || before.getBlock() == block2) && state.func_185904_a().isReplaceable();
        }
        return false;
    }

    private List<PistonData> checkFacings(EntityPlayer player, BlockPos pos) {
        ArrayList<PistonData> data = new ArrayList<PistonData>();
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            PistonData d;
            BlockPos offset = pos.offset(facing);
            if (!BlockUtil.canPlaceCrystal(offset, true, this.newVer.getValue()) || !(d = this.evaluate(new PistonData(player, offset, facing))).isValid()) continue;
            data.add(d);
        }
        return data;
    }

    private PistonData evaluate(PistonData data) {
        boolean s;
        BlockPos crystal = data.getCrystalPos();
        double placeDist = PistonAura.mc.player.getDistanceSq(crystal);
        if (placeDist > (double)MathUtil.square(this.placeRange.getValue().floatValue()) || placeDist > (double)MathUtil.square(this.placeTrace.getValue().floatValue()) && !RayTraceUtil.raytracePlaceCheck(PistonAura.mc.player, crystal)) {
            return data;
        }
        double breakDist = PistonAura.mc.player.getDistanceSq((double)crystal.getX() + 0.5, (double)crystal.getY() + 1.0, (double)crystal.getZ() + 0.5);
        if (breakDist > (double)MathUtil.square(this.breakRange.getValue().floatValue()) || breakDist > (double)MathUtil.square(this.breakTrace.getValue().floatValue()) && !RayTraceUtil.canBeSeen(new Vec3d((double)crystal.getX() + 0.5, (double)crystal.getY() + 2.7, (double)crystal.getZ() + 0.5), (Entity)PistonAura.mc.player)) {
            return data;
        }
        PistonStage[] order = new PistonStage[4];
        BlockPos piston = data.getCrystalPos().offset(data.getFacing()).up();
        BlockPos piston1 = piston.offset(data.getFacing());
        boolean using1 = false;
        IBlockState toPush = PistonAura.mc.world.getBlockState(piston);
        IBlockState piston1State = PistonAura.mc.world.getBlockState(piston1);
        EnumFacing placeFacing = BlockUtil.getFacing(piston);
        boolean noCrystal = false;
        for (Entity entity : PistonAura.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(crystal, crystal.add(1, 2, 1)))) {
            if (entity == null || EntityUtil.isDead(entity)) continue;
            if (entity instanceof EntityEnderCrystal && crystal.equals(entity.getPosition().down())) {
                noCrystal = true;
                using1 = true;
                continue;
            }
            return data;
        }
        if (PistonAura.mc.player.getDistanceSq(piston) > (double)MathUtil.square(this.placeRange.getValue().floatValue())) {
            using1 = true;
            if (PistonAura.mc.player.getDistanceSq(piston1) > (double)MathUtil.square(this.placeRange.getValue().floatValue())) {
                return data;
            }
        }
        boolean noPiston = false;
        if (!toPush.func_185904_a().isReplaceable()) {
            if (toPush.getBlock() == Blocks.PISTON || toPush.getBlock() == Blocks.STICKY_PISTON) {
                if (toPush.getProperties().get(BlockDirectional.FACING) == data.getFacing().getOpposite()) {
                    noPiston = true;
                    using1 = false;
                } else {
                    using1 = true;
                }
            } else {
                if (!PistonAura.mc.world.getBlockState(piston1).func_185904_a().isReplaceable() && piston1State.getBlock() != Blocks.PISTON && piston1State.getBlock() != Blocks.STICKY_PISTON || toPush.func_185905_o() != EnumPushReaction.DESTROY && !BlockPistonBase.canPush((IBlockState)toPush, (World)PistonAura.mc.world, (BlockPos)piston, (EnumFacing)data.getFacing().getOpposite(), (boolean)false, (EnumFacing)data.getFacing().getOpposite())) {
                    return data;
                }
                using1 = true;
            }
        }
        boolean cantPiston1 = false;
        if (piston1State.getBlock() == Blocks.PISTON || piston1State.getBlock() == Blocks.STICKY_PISTON) {
            if (piston1State.getProperties().get(BlockDirectional.FACING) == data.getFacing().getOpposite() && !((Boolean)piston1State.getProperties().get(BlockPistonBase.EXTENDED)).booleanValue()) {
                using1 = true;
                noPiston = true;
            } else {
                cantPiston1 = true;
            }
        }
        if (noPiston) {
            for (EnumFacing facing : this.getRedstoneFacings(data.getFacing(), using1)) {
                BlockPos redstone;
                BlockPos blockPos = redstone = using1 ? piston1.offset(facing) : piston.offset(facing);
                if (PistonAura.mc.player.getDistanceSq(redstone) > (double)MathUtil.square(this.placeRange.getValue().floatValue()) || !PistonAura.mc.world.getBlockState(redstone).func_185904_a().isReplaceable() || this.checkEntities(redstone)) continue;
                data.setRedstonePos(redstone);
                break;
            }
            if (data.getRedstonePos() != null) {
                order[0] = noCrystal ? null : PistonStage.CRYSTAL;
                order[1] = PistonStage.REDSTONE;
                order[2] = null;
                order[3] = PistonStage.BREAK;
                data.setOrder(order);
                data.setValid(true);
                return data;
            }
            if (!using1) {
                using1 = true;
            } else {
                return data;
            }
        }
        boolean noR = false;
        for (EnumFacing facing : EnumFacing.values()) {
            if (facing == data.getFacing().getOpposite()) continue;
            IBlockState state = PistonAura.mc.world.getBlockState(piston.offset(facing));
            IBlockState state1 = PistonAura.mc.world.getBlockState(piston1.offset(facing));
            if (state.getBlock() == Blocks.REDSTONE_TORCH || state.getBlock() == Blocks.REDSTONE_BLOCK) {
                using1 = true;
            }
            if (state1.getBlock() != Blocks.REDSTONE_BLOCK && state1.getBlock() != Blocks.REDSTONE_TORCH) continue;
            noR = true;
            using1 = true;
            break;
        }
        for (Entity entity : PistonAura.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(piston))) {
            if (entity == null || EntityUtil.isDead(entity) || !entity.preventEntitySpawning) continue;
            using1 = true;
            break;
        }
        EnumFacing pistonFacing = this.getFacing(piston, null);
        if (!using1 && (pistonFacing == EnumFacing.UP || pistonFacing == EnumFacing.DOWN || !this.multiDirectional.getValue().booleanValue() && pistonFacing != data.getFacing().getOpposite())) {
            using1 = true;
        }
        if (using1) {
            EnumFacing pistonFacing1 = this.getFacing(piston1, null);
            if (pistonFacing1 == EnumFacing.UP || pistonFacing1 == EnumFacing.DOWN || !this.multiDirectional.getValue().booleanValue() && pistonFacing1 != data.getFacing().getOpposite()) {
                return data;
            }
            placeFacing = BlockUtil.getFacing(piston1);
            if (pistonFacing1 != data.getFacing().getOpposite()) {
                data.setMulti(true);
            }
        }
        if (using1 && (this.checkEntities(piston1) || PistonAura.mc.player.getDistanceSq(piston1) > (double)MathUtil.square(this.placeRange.getValue().floatValue()))) {
            return data;
        }
        EnumFacing redstoneFacing = null;
        if (!noR) {
            for (EnumFacing facing : this.getRedstoneFacings(data.getFacing(), using1)) {
                BlockPos redstone;
                BlockPos blockPos = redstone = using1 ? piston1.offset(facing) : piston.offset(facing);
                if (PistonAura.mc.player.getDistanceSq(redstone) > (double)MathUtil.square(this.placeRange.getValue().floatValue()) || !PistonAura.mc.world.getBlockState(redstone).func_185904_a().isReplaceable() || this.checkEntities(redstone) || (redstoneFacing = BlockUtil.getFacing(redstone)) == null && (placeFacing == null || !using1)) continue;
                data.setRedstonePos(redstone);
                break;
            }
        }
        if (!noR && data.getRedstonePos() == null || using1 && !PistonAura.mc.world.getBlockState(piston1).func_185904_a().isReplaceable() || using1 && cantPiston1) {
            return data;
        }
        if (!using1 && pistonFacing != data.getFacing().getOpposite()) {
            data.setMulti(true);
        }
        data.setPistonPos(using1 ? piston1 : piston);
        boolean bl = s = redstoneFacing != null && placeFacing == null && using1;
        if (noR) {
            order[0] = null;
            order[1] = noCrystal ? null : PistonStage.CRYSTAL;
            order[2] = PistonStage.PISTON;
        } else {
            order[0] = s ? PistonStage.REDSTONE : PistonStage.PISTON;
            order[1] = noCrystal ? null : PistonStage.CRYSTAL;
            order[2] = s ? PistonStage.PISTON : PistonStage.REDSTONE;
        }
        order[3] = PistonStage.BREAK;
        data.setOrder(order);
        data.setValid(true);
        return data;
    }

    protected EnumFacing getFacing(BlockPos pos, float[] rotations) {
        if (Math.abs(PistonAura.mc.player.posX - (double)((float)pos.getX() + 0.5f)) < 2.0 && Math.abs(PistonAura.mc.player.posZ - (double)((float)pos.getZ() + 0.5f)) < 2.0) {
            double y = PistonAura.mc.player.posY + (double)PistonAura.mc.player.getEyeHeight();
            if (y - (double)pos.getY() > 2.0) {
                return EnumFacing.UP;
            }
            if ((double)pos.getY() - y > 0.0) {
                return EnumFacing.DOWN;
            }
        }
        if (rotations == null) {
            EnumFacing facing = BlockUtil.getFacing(pos);
            rotations = RotationUtil.getRotations(facing == null ? pos : pos.offset(facing), facing == null ? null : facing.getOpposite());
        }
        return EnumFacing.getHorizontal((int)(MathHelper.floor((double)((double)(rotations[0] * 4.0f / 360.0f) + 0.5)) & 3)).getOpposite();
    }

    protected boolean checkEntities(BlockPos pos) {
        for (Entity entity : PistonAura.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
            if (entity == null || EntityUtil.isDead(entity) || !entity.preventEntitySpawning) continue;
            return true;
        }
        return false;
    }

    private EnumFacing[] getRedstoneFacings(EnumFacing facing, boolean piston1) {
        if (piston1) {
            EnumFacing[] result = new EnumFacing[5];
            int i = 0;
            for (EnumFacing f : EnumFacing.values()) {
                if (f == facing.getOpposite()) continue;
                result[i] = f;
                ++i;
            }
            return result;
        }
        return new EnumFacing[]{EnumFacing.DOWN, facing};
    }

    protected int getSlot() {
        switch (this.stage) {
            case CRYSTAL: {
                return this.crystalSlot;
            }
            case PISTON: {
                return this.pistonSlot;
            }
            case REDSTONE: {
                return this.redstoneSlot;
            }
            case BREAK: {
                break;
            }
        }
        return -1;
    }

    public boolean usingTorches() {
        ItemStack stack;
        if (this.redstoneSlot != -1 && (stack = this.redstoneSlot == -2 ? PistonAura.mc.player.getHeldItemOffhand() : PistonAura.mc.player.inventory.getStackInSlot(this.redstoneSlot)).getItem() instanceof ItemBlock) {
            return ((ItemBlock)((Object)stack.getItem())).getBlock() == Blocks.REDSTONE_TORCH;
        }
        return false;
    }
}
