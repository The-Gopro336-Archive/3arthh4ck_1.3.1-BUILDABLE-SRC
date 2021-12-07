package me.earth.earthhack.impl.modules.combat.autotrap;

import java.lang.invoke.LambdaMetafactory;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.autotrap.AutoTrapData;
import me.earth.earthhack.impl.modules.combat.autotrap.ListenerAutoTrap;
import me.earth.earthhack.impl.modules.combat.autotrap.modes.TrapTarget;
import me.earth.earthhack.impl.modules.combat.autotrap.util.Trap;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListenerModule;
import me.earth.earthhack.impl.util.helpers.blocks.util.TargetResult;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockingType;
import me.earth.earthhack.impl.util.minecraft.blocks.HoleUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class AutoTrap
extends ObbyListenerModule<ListenerAutoTrap> {
    private static final EnumFacing[] TOP_FACINGS = new EnumFacing[]{EnumFacing.UP, EnumFacing.NORTH, EnumFacing.WEST, EnumFacing.SOUTH, EnumFacing.EAST};
    protected final Setting<Float> range = this.register(new NumberSetting<Float>("Range", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(6.0f)));
    protected final Setting<Boolean> noScaffold = this.register(new BooleanSetting("NoScaffold", false));
    protected final Setting<Boolean> top = this.register(new BooleanSetting("Top", true));
    protected final Setting<Boolean> noStep = this.register(new BooleanSetting("NoStep", false));
    protected final Setting<Boolean> upperBody = this.register(new BooleanSetting("UpperBody", true));
    protected final Setting<Boolean> legs = this.register(new BooleanSetting("Legs", false));
    protected final Setting<Boolean> platform = this.register(new BooleanSetting("Platform", false));
    protected final Setting<Boolean> noDrop = this.register(new BooleanSetting("NoDrop", false));
    protected final Setting<Integer> extend = this.register(new NumberSetting<Integer>("Extend", 2, 1, 3));
    protected final Setting<TrapTarget> targetMode = this.register(new EnumSetting<TrapTarget>("Target", TrapTarget.Closest));
    protected final Setting<Float> speed = this.register(new NumberSetting<Float>("Speed", Float.valueOf(19.0f), Float.valueOf(0.0f), Float.valueOf(50.0f)));
    protected final Setting<Boolean> freeCam = this.register(new BooleanSetting("FreeCam", false));
    protected final Setting<Boolean> bigExtend = this.register(new BooleanSetting("BigExtend", false));
    protected final Setting<Boolean> helping = this.register(new BooleanSetting("Helping", false));
    protected final Setting<Boolean> smartTop = this.register(new BooleanSetting("SmartTop", true));
    protected final Setting<Boolean> noScaffoldPlus = this.register(new BooleanSetting("NoScaffold+", false));
    protected final Setting<Boolean> upperFace = this.register(new BooleanSetting("Upper-FP", false));
    protected final Map<EntityPlayer, Double> speeds = new HashMap<EntityPlayer, Double>();
    protected final Map<EntityPlayer, List<BlockPos>> cached = new HashMap<EntityPlayer, List<BlockPos>>();
    protected EntityPlayer target;

    public AutoTrap() {
        super("AutoTrap", Category.Combat);
        this.setData(new AutoTrapData(this));
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        Managers.ROTATION.setBlocking(false);
    }

    @Override
    protected boolean checkNull() {
        boolean checkNull = super.checkNull();
        this.cached.clear();
        this.speeds.clear();
        if (checkNull) {
            this.updateSpeed();
        }
        return checkNull;
    }

    @Override
    protected ListenerAutoTrap createListener() {
        return new ListenerAutoTrap(this);
    }

    @Override
    public String getDisplayInfo() {
        return this.target == null ? null : this.target.getName();
    }

    @Override
    protected boolean shouldHelp(EnumFacing facing, BlockPos pos) {
        return super.shouldHelp(facing, pos) && this.helping.getValue() != false && this.legs.getValue() == false;
    }

    public EntityPlayer getTarget() {
        return this.target;
    }

    protected TargetResult getTargets(TargetResult result) {
        this.cached.clear();
        this.updateSpeed();
        EntityPlayer newTarget = this.calcTarget();
        if (newTarget == null || !newTarget.equals(this.target)) {
            ((ListenerAutoTrap)this.listener).placed.clear();
        }
        EntityPlayer entityPlayer = this.target = newTarget == null ? this.target : newTarget;
        if (newTarget == null) {
            return result.setValid(false);
        }
        List<BlockPos> newTrapping = this.cached.get(newTarget);
        if (newTrapping == null) {
            newTrapping = this.getPositions(newTarget);
        }
        return result.setTargets(newTrapping);
    }

    private EntityPlayer calcTarget() {
        EntityPlayer closest = null;
        double distance = Double.MAX_VALUE;
        for (EntityPlayer player : AutoTrap.mc.world.playerEntities) {
            double playerDist = AutoTrap.mc.player.getDistanceSq(player);
            if (!(playerDist < distance) || !this.isValid(player)) continue;
            closest = player;
            distance = playerDist;
        }
        return closest;
    }

    private boolean isValid(EntityPlayer player) {
        if (player != null && !EntityUtil.isDead(player) && !player.equals(AutoTrap.mc.player) && !Managers.FRIENDS.contains(player) && player.getDistanceSq(AutoTrap.mc.player) <= 36.0 && this.getSpeed(player) <= (double)this.speed.getValue().floatValue()) {
            if (this.targetMode.getValue() == TrapTarget.Untrapped) {
                List<BlockPos> positions = this.getPositions(player);
                this.cached.put(player, positions);
                return positions.stream().anyMatch(pos -> AutoTrap.mc.world.getBlockState((BlockPos)pos).func_185904_a().isReplaceable());
            }
            return true;
        }
        return false;
    }

    private void updateSpeed() {
        for (EntityPlayer player : AutoTrap.mc.world.playerEntities) {
            if (!EntityUtil.isValid(player, this.range.getValue().floatValue() * 2.0f)) continue;
            double xDist = player.posX - player.prevPosX;
            double yDist = player.posY - player.prevPosY;
            double zDist = player.posZ - player.prevPosZ;
            double speed = xDist * xDist + yDist * yDist + zDist * zDist;
            this.speeds.put(player, speed);
        }
    }

    private double getSpeed(EntityPlayer player) {
        Double playerSpeed = this.speeds.get(player);
        if (playerSpeed != null && this.speed.getValue().floatValue() != 50.0f) {
            return Math.sqrt(playerSpeed) * 20.0 * 3.6;
        }
        return 0.0;
    }

    private List<BlockPos> getPositions(EntityPlayer player) {
        ArrayList<BlockPos> blocked = new ArrayList<BlockPos>();
        BlockPos playerPos = new BlockPos(player);
        if (HoleUtil.isHole(playerPos, false)[0] || this.extend.getValue() == 1) {
            blocked.add(playerPos.up());
        } else {
            List unfiltered = new ArrayList<BlockPos>(PositionUtil.getBlockedPositions(player)).stream().sorted(Comparator.comparingDouble(BlockUtil::getDistanceSq)).collect(Collectors.toList());
            List filtered = new ArrayList(unfiltered).stream().filter(pos -> AutoTrap.mc.world.getBlockState((BlockPos)pos).func_185904_a().isReplaceable() && AutoTrap.mc.world.getBlockState(pos.up()).func_185904_a().isReplaceable()).collect(Collectors.toList());
            if (this.extend.getValue() == 3 && filtered.size() == 2 && unfiltered.size() == 4 && ((BlockPos)unfiltered.get(0)).equals(filtered.get(0)) && ((BlockPos)unfiltered.get(3)).equals(filtered.get(1))) {
                filtered.clear();
            }
            if (this.extend.getValue() == 2 && filtered.size() > 2 || this.extend.getValue() == 3 && filtered.size() == 3) {
                while (filtered.size() > 2) {
                    filtered.remove(filtered.size() - 1);
                }
            }
            for (BlockPos pos2 : filtered) {
                blocked.add(pos2.up());
            }
        }
        if (blocked.isEmpty()) {
            blocked.add(playerPos.up());
        }
        List<BlockPos> positions = this.positionsFromBlocked(blocked);
        positions.sort(Comparator.comparingDouble(pos -> -BlockUtil.getDistanceSq(pos)));
        positions.sort(Comparator.comparingInt((ToIntFunction<BlockPos>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)I, getY(), (Lnet/minecraft/util/math/BlockPos;)I)()));
        return positions.stream().filter(pos -> BlockUtil.getDistanceSq(pos) <= (double)MathUtil.square(this.range.getValue().floatValue())).collect(Collectors.toList());
    }

    private List<BlockPos> positionsFromBlocked(List<BlockPos> blockedIn) {
        ArrayList<BlockPos> positions = new ArrayList<BlockPos>();
        if (!this.noStep.getValue().booleanValue() && !blockedIn.isEmpty()) {
            BlockPos[] helping = this.findTopHelping(blockedIn, true);
            for (int i = 0; i < helping.length; ++i) {
                BlockPos pos2 = helping[i];
                if (pos2 == null) continue;
                if (!(i != 1 || this.upperBody.getValue().booleanValue() || blockedIn.contains(PositionUtil.getPosition().up()) && this.upperFace.getValue().booleanValue() || helping[5] == null)) {
                    positions.add(helping[5]);
                }
                positions.add(helping[i]);
                break;
            }
        }
        boolean scaffold = this.noScaffold.getValue();
        if (this.top.getValue().booleanValue()) {
            blockedIn.forEach(pos -> positions.addAll(this.applyOffsets((BlockPos)pos, Trap.TOP, (List<BlockPos>)positions)));
        } else if (blockedIn.size() == 1 && this.smartTop.getValue().booleanValue() && scaffold && AutoTrap.mc.world.getBlockState(blockedIn.get(0).add(Trap.TOP[0])).func_185904_a().isReplaceable() && AutoTrap.mc.world.getBlockState(blockedIn.get(0).add(Trap.NO_SCAFFOLD[0])).func_185904_a().isReplaceable() && AutoTrap.mc.world.getBlockState(blockedIn.get(0).add(Trap.NO_SCAFFOLD_P[0])).func_185904_a().isReplaceable()) {
            blockedIn.forEach(pos -> positions.addAll(this.applyOffsets((BlockPos)pos, Trap.TOP, (List<BlockPos>)positions)));
            if (this.noScaffoldPlus.getValue().booleanValue()) {
                blockedIn.forEach(pos -> positions.addAll(this.applyOffsets((BlockPos)pos, Trap.NO_SCAFFOLD_P, (List<BlockPos>)positions)));
            }
            scaffold = false;
            blockedIn.forEach(pos -> positions.addAll(this.applyOffsets((BlockPos)pos, Trap.NO_SCAFFOLD, (List<BlockPos>)positions)));
        }
        if (this.upperBody.getValue().booleanValue() || this.upperFace.getValue().booleanValue() && blockedIn.contains(PositionUtil.getPosition().up())) {
            blockedIn.forEach(pos -> positions.addAll(this.applyOffsets((BlockPos)pos, Trap.OFFSETS, (List<BlockPos>)positions)));
        }
        if (blockedIn.size() == 1 || this.bigExtend.getValue().booleanValue()) {
            if (scaffold) {
                blockedIn.forEach(pos -> positions.addAll(this.applyOffsets((BlockPos)pos, Trap.NO_SCAFFOLD, (List<BlockPos>)positions)));
            }
            if (this.noStep.getValue().booleanValue()) {
                blockedIn.forEach(pos -> positions.addAll(this.applyOffsets((BlockPos)pos, Trap.NO_STEP, (List<BlockPos>)positions)));
            }
            if (this.legs.getValue().booleanValue()) {
                blockedIn.forEach(pos -> positions.addAll(this.applyOffsets((BlockPos)pos, Trap.LEGS, (List<BlockPos>)positions)));
            }
            if (this.platform.getValue().booleanValue()) {
                blockedIn.forEach(pos -> positions.addAll(this.applyOffsets((BlockPos)pos, Trap.PLATFORM, (List<BlockPos>)positions)));
            }
            if (this.noDrop.getValue().booleanValue()) {
                blockedIn.forEach(pos -> positions.addAll(this.applyOffsets((BlockPos)pos, Trap.NO_DROP, (List<BlockPos>)positions)));
            }
        }
        return positions;
    }

    private BlockPos[] findTopHelping(List<BlockPos> positions, boolean first) {
        BlockPos[] bestPos = new BlockPos[]{null, null, null, null, positions.get(0).up().north(), null};
        for (BlockPos pos : positions) {
            BlockPos up = pos.up();
            block9: for (EnumFacing facing : TOP_FACINGS) {
                BlockPos helping = up.offset(facing);
                if (!AutoTrap.mc.world.getBlockState(helping).func_185904_a().isReplaceable()) {
                    bestPos[0] = helping;
                    return bestPos;
                }
                EnumFacing helpingFace = BlockUtil.getFacing(helping, HELPER);
                byte blockingFactor = this.helpingEntityCheck(helping);
                if (helpingFace == null) {
                    switch (blockingFactor) {
                        case 0: {
                            if (!first || bestPos[5] != null) break;
                            ArrayList<BlockPos> hPositions = new ArrayList<BlockPos>();
                            for (BlockPos hPos : positions) {
                                hPositions.add(hPos.down());
                            }
                            bestPos[5] = this.findTopHelping(hPositions, false)[0];
                            bestPos[1] = helping;
                            break;
                        }
                        case 1: {
                            bestPos[3] = helping;
                            break;
                        }
                    }
                    continue;
                }
                switch (blockingFactor) {
                    case 0: {
                        bestPos[0] = helping;
                        continue block9;
                    }
                    case 1: {
                        bestPos[2] = helping;
                        continue block9;
                    }
                }
            }
        }
        return bestPos;
    }

    private byte helpingEntityCheck(BlockPos pos) {
        byte blocking = 0;
        for (Entity entity : AutoTrap.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
            float damage;
            if (entity == null || EntityUtil.isDead(entity) || !entity.preventEntitySpawning || entity instanceof EntityPlayer && !BlockUtil.isBlocking(pos, (EntityPlayer)entity, (BlockingType)((Object)this.blockingType.getValue()))) continue;
            if (entity instanceof EntityEnderCrystal && ((Boolean)this.attack.getValue()).booleanValue() && (double)(damage = DamageUtil.calculate(entity, (EntityLivingBase)AutoTrap.mc.player)) <= (double)EntityUtil.getHealth(AutoTrap.mc.player) + 1.0) {
                blocking = 1;
                continue;
            }
            return 2;
        }
        return blocking;
    }

    private List<BlockPos> applyOffsets(BlockPos pos, Vec3i[] offsets, List<BlockPos> alreadyAdded) {
        ArrayList<BlockPos> positions = new ArrayList<BlockPos>();
        for (Vec3i vec3i : offsets) {
            BlockPos offset = pos.add(vec3i);
            if (alreadyAdded.contains(offset)) continue;
            positions.add(offset);
        }
        return positions;
    }
}
