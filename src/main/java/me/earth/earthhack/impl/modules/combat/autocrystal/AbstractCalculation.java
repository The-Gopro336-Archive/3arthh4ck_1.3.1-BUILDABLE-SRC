/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemPickaxe
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketUseEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.modules.combat.autocrystal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.core.ducks.entity.IEntity;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.antisurround.AntiSurround;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.HelperLiquids;
import me.earth.earthhack.impl.modules.combat.autocrystal.HelperUtil;
import me.earth.earthhack.impl.modules.combat.autocrystal.helpers.Confirmer;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.AntiWeakness;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.AutoSwitch;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.BreakValidity;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.RotationThread;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.SwingTime;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.Target;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.AntiTotemData;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.BreakData;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.CrystalData;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.CrystalTimeStamp;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.EmptySet;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.ForceData;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.IBreakHelper;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.MineSlots;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.PlaceData;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.PositionData;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.RotationComparator;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.RotationFunction;
import me.earth.earthhack.impl.modules.combat.legswitch.LegSwitch;
import me.earth.earthhack.impl.modules.combat.offhand.Offhand;
import me.earth.earthhack.impl.modules.combat.offhand.modes.OffhandMode;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.util.helpers.Finishable;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.raytrace.Ray;
import me.earth.earthhack.impl.util.math.raytrace.RayTraceFactory;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.blocks.states.BlockStateHelper;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.misc.MutableWrapper;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class AbstractCalculation<T extends CrystalData>
extends Finishable
implements Globals {
    protected static final ModuleCache<Offhand> OFFHAND = Caches.getModule(Offhand.class);
    protected static final ModuleCache<LegSwitch> LEG_SWITCH = Caches.getModule(LegSwitch.class);
    protected static final ModuleCache<Speedmine> SPEEDMINE = Caches.getModule(Speedmine.class);
    protected static final ModuleCache<AntiSurround> ANTISURROUND = Caches.getModule(AntiSurround.class);
    protected final Set<BlockPos> blackList;
    protected final List<Entity> entities;
    protected final AutoCrystal module;
    protected final List<EntityPlayer> raw;
    protected double maxY = Double.MAX_VALUE;
    protected List<EntityPlayer> friends;
    protected List<EntityPlayer> enemies;
    protected List<EntityPlayer> players;
    protected List<EntityPlayer> all;
    protected BreakData<T> breakData;
    protected PlaceData placeData;
    protected boolean scheduling;
    protected boolean attacking;
    protected boolean noPlace = false;
    protected boolean noBreak = false;
    protected boolean rotating;
    protected boolean placing;
    protected boolean fallback;
    protected int motionID;
    protected int count;

    public AbstractCalculation(AutoCrystal module, List<Entity> entities, List<EntityPlayer> players, BlockPos ... blackList) {
        this.motionID = module.motionID.get();
        if (blackList.length == 0) {
            this.blackList = new EmptySet<BlockPos>();
        } else {
            this.blackList = new HashSet<BlockPos>();
            for (BlockPos pos : blackList) {
                if (pos == null) continue;
                this.blackList.add(pos);
            }
        }
        this.module = module;
        this.entities = entities;
        this.raw = players;
    }

    public AbstractCalculation(AutoCrystal module, List<Entity> entities, List<EntityPlayer> players, boolean breakOnly, boolean noBreak, BlockPos ... blackList) {
        this(module, entities, players, blackList);
        this.noPlace = breakOnly;
        this.noBreak = noBreak;
    }

    protected abstract IBreakHelper<T> getBreakHelper();

    @Override
    protected void execute() {
        try {
            if (this.module.clearPost.getValue().booleanValue()) {
                this.module.post.clear();
            }
            this.runCalc();
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void runCalc() {
        if (this.check()) {
            return;
        }
        if (this.module.forceAntiTotem.getValue().booleanValue() && this.module.antiTotem.getValue().booleanValue() && this.checkForceAntiTotem()) {
            return;
        }
        float minDamage = this.module.getMinDamage();
        if (this.module.focusRotations.getValue().booleanValue() && !this.module.rotate.getValue().noRotate(ACRotate.Break) && this.focusBreak()) {
            return;
        }
        this.module.focus = null;
        if (this.breakData == null && this.breakCheck()) {
            this.breakData = this.getBreakHelper().getData(this.getBreakDataSet(), this.entities, this.all, this.friends);
            this.setCount(this.breakData, minDamage);
            if (this.evaluate(this.breakData)) {
                return;
            }
        } else if (this.module.multiPlaceCalc.getValue().booleanValue()) {
            if (this.breakData != null) {
                this.setCount(this.breakData, minDamage);
                this.breakData = null;
            } else {
                BreakData<T> onlyForCountData = this.getBreakHelper().getData(new ArrayList(5), this.entities, this.all, this.friends);
                this.setCount(onlyForCountData, minDamage);
            }
        }
        if (this.placeCheck()) {
            this.placeData = this.module.placeHelper.getData(this.all, this.players, this.enemies, this.friends, this.entities, minDamage, this.blackList, this.maxY);
            if (LEG_SWITCH.returnIfPresent(LegSwitch::isActive, false).booleanValue() || ANTISURROUND.returnIfPresent(AntiSurround::isActive, false).booleanValue()) {
                return;
            }
            if (this.place(this.placeData)) {
                boolean passed = this.module.obbyCalcTimer.passed(this.module.obbyCalc.getValue().intValue());
                if (this.obbyCheck() && passed && this.placeObby(this.placeData, null)) {
                    return;
                }
                if (!(!this.preSpecialCheck() || this.module.requireOnGround.getValue().booleanValue() && !RotationUtil.getRotationPlayer().onGround || !this.module.interruptSpeedmine.getValue().booleanValue() && SPEEDMINE.isEnabled() && ((Speedmine)SPEEDMINE.get()).getPos() != null || this.module.pickaxeOnly.getValue().booleanValue() && !(AbstractCalculation.mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe) || !this.module.liquidTimer.passed(this.module.liqDelay.getValue().intValue()) || !this.module.lava.getValue().booleanValue() && !this.module.water.getValue().booleanValue())) {
                    MineSlots mineSlots = HelperLiquids.getSlots(this.module.requireOnGround.getValue());
                    if (mineSlots.getBlockSlot() == -1 || mineSlots.getDamage() < 1.0f) {
                        return;
                    }
                    PlaceData liquidData = this.module.liquidHelper.calculate(this.module.placeHelper, this.placeData, this.friends, this.all, this.module.minDamage.getValue().floatValue());
                    if (LEG_SWITCH.returnIfPresent(LegSwitch::isActive, false).booleanValue() || ANTISURROUND.returnIfPresent(AntiSurround::isActive, false).booleanValue()) {
                        return;
                    }
                    boolean attackingBefore = this.attacking;
                    if (this.placeNoAntiTotem(liquidData, mineSlots) && attackingBefore == this.attacking && this.module.liquidObby.getValue().booleanValue() && this.obbyCheck() && passed) {
                        this.placeObby(this.placeData, mineSlots);
                    }
                }
            }
        }
    }

    protected void setCount(BreakData<T> breakData, float minDmg) {
        if (this.module.multiPlaceMinDmg.getValue().booleanValue()) {
            this.count = (int)breakData.getData().stream().filter(d -> d.getDamage() > minDmg).count();
            return;
        }
        this.count = breakData.getData().size();
    }

    protected boolean evaluate(BreakData<T> breakData) {
        BreakValidity validity;
        boolean slowReset;
        boolean shouldDanger = this.module.shouldDanger();
        boolean bl = slowReset = !shouldDanger;
        if (this.breakData.getAntiTotem() != null && (validity = HelperUtil.isValid(this.module, this.breakData.getAntiTotem())) != BreakValidity.INVALID) {
            this.attack(this.breakData.getAntiTotem(), validity);
            this.module.breakTimer.reset(this.module.breakDelay.getValue().intValue());
            this.module.antiTotemHelper.setTarget(null);
            this.module.antiTotemHelper.setTargetPos(null);
        } else {
            int packets = !this.module.rotate.getValue().noRotate(ACRotate.Break) ? 1 : this.module.packets.getValue();
            CrystalData firstRotation = null;
            ArrayList<CrystalData> valids = new ArrayList<CrystalData>(packets);
            for (CrystalData data : this.breakData.getData()) {
                validity = HelperUtil.isValid(this.module, data.getCrystal());
                if (validity == BreakValidity.VALID && valids.size() < packets) {
                    valids.add(data);
                    continue;
                }
                if (validity != BreakValidity.ROTATIONS || firstRotation != null) continue;
                firstRotation = data;
            }
            int slowDelay = this.module.slowBreakDelay.getValue();
            float slow = this.module.slowBreakDamage.getValue().floatValue();
            if (valids.isEmpty()) {
                if (firstRotation != null && (this.module.shouldDanger() || !(slowReset = firstRotation.getDamage() <= slow) || this.module.breakTimer.passed(slowDelay))) {
                    this.attack(firstRotation.getCrystal(), BreakValidity.ROTATIONS);
                }
            } else {
                for (CrystalData valid : valids) {
                    boolean high;
                    boolean bl2 = high = valid.getDamage() > this.module.slowBreakDamage.getValue().floatValue();
                    if (!high && !shouldDanger && !this.module.breakTimer.passed(this.module.slowBreakDelay.getValue().intValue())) continue;
                    slowReset = slowReset && !high;
                    this.attack(valid.getCrystal(), BreakValidity.VALID);
                }
            }
        }
        if (this.attacking) {
            this.module.breakTimer.reset(slowReset ? (long)this.module.slowBreakDelay.getValue().intValue() : (long)this.module.breakDelay.getValue().intValue());
        }
        return this.rotating && !this.module.rotate.getValue().noRotate(ACRotate.Place);
    }

    protected boolean breakCheck() {
        return this.module.attack.getValue() != false && !this.noBreak && Managers.SWITCH.getLastSwitch() >= (long)this.module.cooldown.getValue().intValue() && this.module.breakTimer.passed(this.module.breakDelay.getValue().intValue());
    }

    protected boolean placeCheck() {
        Confirmer c;
        if (this.module.damageSync.getValue().booleanValue() && (c = this.module.damageSyncHelper.getConfirmer()).isValid() && (!c.isPlaceConfirmed(this.module.placeConfirm.getValue()) || !c.isBreakConfirmed(this.module.breakConfirm.getValue())) && c.isValid() && this.module.preSynCheck.getValue().booleanValue()) {
            return false;
        }
        return !(this.count >= this.module.multiPlace.getValue() || Managers.SWITCH.getLastSwitch() < (long)this.module.placeCoolDown.getValue().intValue() || this.module.place.getValue() == false || this.attacking && this.module.multiTask.getValue() == false || this.rotating && !this.module.rotate.getValue().noRotate(ACRotate.Place) || !this.module.placeTimer.passed(this.module.placeDelay.getValue().intValue()) || this.noPlace);
    }

    protected boolean obbyCheck() {
        return this.preSpecialCheck() && this.module.obsidian.getValue() != false && this.module.obbyTimer.passed(this.module.obbyDelay.getValue().intValue());
    }

    protected boolean preSpecialCheck() {
        return !this.placing && this.placeData != null && (this.placeData.getTarget() != null || this.module.targetMode.getValue() == Target.Damage) && !this.fallback;
    }

    protected boolean check() {
        if (!this.module.spectator.getValue().booleanValue() && AbstractCalculation.mc.player.isSpectator() || ANTISURROUND.returnIfPresent(AntiSurround::isActive, false).booleanValue() || LEG_SWITCH.returnIfPresent(LegSwitch::isActive, false).booleanValue() || this.raw == null || this.entities == null) {
            return true;
        }
        this.setFriendsAndEnemies();
        if (this.all.isEmpty() || this.module.isPingBypass()) {
            return true;
        }
        if (!(this.module.attackMode.getValue().shouldCalc() || this.module.autoSwitch.getValue() == AutoSwitch.Always || this.module.weaknessHelper.canSwitch() || this.module.switching)) {
            return true;
        }
        return this.module.weaknessHelper.isWeaknessed() && this.module.antiWeakness.getValue() == AntiWeakness.None;
    }

    protected void setFriendsAndEnemies() {
        Predicate[] arrpredicate = new Predicate[3];
        arrpredicate[0] = p -> p == null || EntityUtil.isDead((Entity)p) || p.equals((Object)AbstractCalculation.mc.player) || p.getDistanceSq((Entity)AbstractCalculation.mc.player) > (double)MathUtil.square(this.module.targetRange.getValue().floatValue()) || DamageUtil.cacheLowestDura((EntityLivingBase)p) && this.module.antiNaked.getValue() != false;
        arrpredicate[1] = Managers.FRIENDS::contains;
        arrpredicate[2] = Managers.ENEMIES::contains;
        List<List<EntityPlayer>> split = CollectionUtil.split(this.raw, arrpredicate);
        this.friends = split.get(1);
        this.enemies = split.get(2);
        this.players = split.get(3);
        this.all = new ArrayList<EntityPlayer>(this.enemies.size() + this.players.size());
        this.all.addAll(this.enemies);
        this.all.addAll(this.players);
        if (this.module.yCalc.getValue().booleanValue()) {
            this.maxY = Double.MIN_VALUE;
            for (EntityPlayer player : this.all) {
                if (!(player.posY > this.maxY)) continue;
                this.maxY = player.posY;
            }
        }
    }

    protected boolean attack(Entity entity, BreakValidity validity) {
        this.module.setCrystal(entity);
        switch (validity) {
            case VALID: {
                if (this.module.weaknessHelper.isWeaknessed()) {
                    if (this.module.antiWeakness.getValue() == AntiWeakness.None) {
                        return false;
                    }
                    Runnable r = this.module.rotationHelper.post(entity, new MutableWrapper<Boolean>(false));
                    r.run();
                    this.attacking = true;
                    if (!this.module.rotate.getValue().noRotate(ACRotate.Break)) {
                        this.module.rotation = this.module.rotationHelper.forBreaking(entity, new MutableWrapper<Boolean>(true));
                    }
                    return true;
                }
                if (this.module.breakSwing.getValue() == SwingTime.Pre) {
                    Swing.Packet.swing(EnumHand.MAIN_HAND);
                }
                AbstractCalculation.mc.player.connection.sendPacket((Packet)new CPacketUseEntity(entity));
                if (this.module.pseudoSetDead.getValue().booleanValue()) {
                    ((IEntity)entity).setPseudoDead(true);
                } else if (this.module.setDead.getValue().booleanValue()) {
                    Managers.SET_DEAD.setDead(entity);
                }
                if (this.module.breakSwing.getValue() == SwingTime.Post) {
                    Swing.Packet.swing(EnumHand.MAIN_HAND);
                }
                Swing.Client.swing(EnumHand.MAIN_HAND);
                this.attacking = true;
                if (!this.module.rotate.getValue().noRotate(ACRotate.Break)) {
                    this.module.rotation = this.module.rotationHelper.forBreaking(entity, new MutableWrapper<Boolean>(true));
                }
                return true;
            }
            case ROTATIONS: {
                this.attacking = true;
                this.rotating = true;
                MutableWrapper<Boolean> attacked = new MutableWrapper<Boolean>(false);
                Runnable post = this.module.rotationHelper.post(entity, attacked);
                RotationFunction function = this.module.rotationHelper.forBreaking(entity, attacked);
                if (this.module.multiThread.getValue().booleanValue() && this.module.rotationThread.getValue() == RotationThread.Cancel && this.module.rotationCanceller.setRotations(function) && HelperUtil.isValid(this.module, entity) == BreakValidity.VALID) {
                    this.rotating = false;
                    post.run();
                } else {
                    this.module.rotation = function;
                    this.module.post.add(post);
                }
                return true;
            }
        }
        return false;
    }

    protected boolean checkForceAntiTotem() {
        BlockPos forcePos = this.module.forceHelper.getPos();
        if (forcePos != null && this.module.forceHelper.isForcing(this.module.syncForce.getValue())) {
            for (Entity entity : this.entities) {
                if (!(entity instanceof EntityEnderCrystal) || EntityUtil.isDead(entity) || !entity.getEntityBoundingBox().intersects(new AxisAlignedBB(forcePos.up()))) continue;
                this.attack(entity, HelperUtil.isValid(this.module, entity));
                return true;
            }
            return true;
        }
        return false;
    }

    protected boolean place(PlaceData data) {
        AntiTotemData antiTotem = null;
        boolean god = this.module.godAntiTotem.getValue() != false && this.module.idHelper.isSafe(this.raw, this.module.holdingCheck.getValue(), this.module.toolCheck.getValue());
        for (AntiTotemData antiTotemData : data.getAntiTotem()) {
            if (antiTotemData.getCorresponding().isEmpty() || antiTotemData.isBlocked()) continue;
            BlockPos pos = antiTotemData.getPos();
            EntityEnderCrystal entity = new EntityEnderCrystal((World)AbstractCalculation.mc.world, (double)((float)pos.getX() + 0.5f), (double)(pos.getY() + 1), (double)((float)pos.getZ() + 0.5f));
            if (god) {
                for (PositionData positionData : antiTotemData.getCorresponding()) {
                    double y;
                    if (positionData.isBlocked()) continue;
                    BlockPos up = positionData.getPos().up();
                    double d = y = this.module.newVerEntities.getValue() != false ? 1.0 : 2.0;
                    if (entity.getEntityBoundingBox().intersects(new AxisAlignedBB((double)up.getX(), (double)up.getY(), (double)up.getZ(), (double)up.getX() + 1.0, (double)up.getY() + y, (double)up.getZ() + 1.0))) continue;
                    if (this.module.totemSync.getValue().booleanValue() && this.module.damageSyncHelper.isSyncing(0.0f, true)) {
                        return false;
                    }
                    this.module.noGod = true;
                    this.module.antiTotemHelper.setTargetPos(antiTotemData.getPos());
                    EntityPlayer player = antiTotemData.getFirstTarget();
                    Earthhack.getLogger().info("Attempting God-AntiTotem: " + (player == null ? "null" : player.getName()));
                    this.place(antiTotemData, player, false, false, false);
                    this.module.idHelper.attack(this.module.breakSwing.getValue(), this.module.godSwing.getValue(), 1, this.module.idPackets.getValue(), 0);
                    this.place(positionData, player, false, false, false);
                    this.module.idHelper.attack(this.module.breakSwing.getValue(), this.module.godSwing.getValue(), 2, this.module.idPackets.getValue(), 0);
                    this.module.breakTimer.reset(this.module.breakDelay.getValue().intValue());
                    this.module.noGod = false;
                    return false;
                }
            }
            if (antiTotem != null) continue;
            antiTotem = antiTotemData;
            if (god) continue;
            break;
        }
        if (antiTotem != null) {
            EntityPlayer player = antiTotem.getFirstTarget();
            this.module.setTarget(player);
            if (this.module.totemSync.getValue().booleanValue() && this.module.damageSyncHelper.isSyncing(0.0f, true)) {
                return false;
            }
            Earthhack.getLogger().info("Attempting AntiTotem: " + (player == null ? "null" : player.getName()));
            this.module.antiTotemHelper.setTargetPos(antiTotem.getPos());
            this.place(antiTotem, player, !this.module.rotate.getValue().noRotate(ACRotate.Place), this.rotating || this.scheduling, false);
            return false;
        }
        if (this.module.forceAntiTotem.getValue().booleanValue() && this.module.antiTotem.getValue().booleanValue() && this.module.forceTimer.passed(this.module.attempts.getValue().intValue())) {
            for (Map.Entry entry : data.getForceData().entrySet()) {
                ForceData forceData = (ForceData)entry.getValue();
                PositionData first = forceData.getForceData().stream().findFirst().orElse(null);
                if (first == null || !forceData.hasPossibleAntiTotem() || !forceData.hasPossibleHighDamage()) continue;
                if (this.module.syncForce.getValue().booleanValue() && this.module.damageSyncHelper.isSyncing(0.0f, true)) {
                    return false;
                }
                this.module.forceHelper.setSync(first.getPos(), this.module.newVerEntities.getValue());
                this.place(first, (EntityPlayer)entry.getKey(), !this.module.rotate.getValue().noRotate(ACRotate.Place), this.rotating || this.scheduling, this.module.forceSlow.getValue());
                this.module.forceTimer.reset();
                return false;
            }
        }
        return this.placeNoAntiTotem(data, null);
    }

    protected boolean placeNoAntiTotem(PlaceData data, MineSlots liquid) {
        float maxBlockedDamage = 0.0f;
        PositionData firstData = null;
        for (PositionData d : data.getData()) {
            if (d.isBlocked()) {
                if (!(maxBlockedDamage < d.getMaxDamage())) continue;
                maxBlockedDamage = d.getMaxDamage();
                continue;
            }
            firstData = d;
            break;
        }
        if (this.breakData != null && !this.attacking) {
            Entity fallback = this.breakData.getFallBack();
            if (this.module.fallBack.getValue().booleanValue() && this.breakData.getFallBackDmg() < this.module.fallBackDmg.getValue().floatValue() && fallback != null && maxBlockedDamage != 0.0f && (firstData == null || maxBlockedDamage - firstData.getMaxDamage() >= this.module.fallBackDiff.getValue().floatValue())) {
                this.attack(fallback, HelperUtil.isValid(this.module, fallback));
                return false;
            }
        }
        if (firstData != null && !this.module.damageSyncHelper.isSyncing(firstData.getMaxDamage(), this.module.damageSync.getValue()) && (liquid == null || this.module.minDamage.getValue().floatValue() <= firstData.getMaxDamage())) {
            boolean slow = false;
            if (firstData.getMaxDamage() <= this.module.slowPlaceDmg.getValue().floatValue() && !this.module.shouldDanger()) {
                if (this.module.placeTimer.passed(this.module.slowPlaceDelay.getValue().intValue())) {
                    slow = true;
                } else {
                    return !this.module.damageSyncHelper.isSyncing(0.0f, this.module.damageSync.getValue());
                }
            }
            MutableWrapper<Boolean> liquidBreak = null;
            if (liquid != null) {
                liquidBreak = this.placeAndBreakLiquid(firstData, liquid, this.rotating);
            }
            this.place(firstData, firstData.getTarget(), !this.module.rotate.getValue().noRotate(ACRotate.Place), this.rotating || this.scheduling, slow, slow ? firstData.getMaxDamage() : Float.MAX_VALUE, liquidBreak);
            return false;
        }
        return !this.module.damageSyncHelper.isSyncing(0.0f, this.module.damageSync.getValue());
    }

    protected void place(PositionData data, EntityPlayer target, boolean rotate, boolean schedule, boolean resetSlow) {
        this.place(data, target, rotate, schedule, resetSlow, Float.MAX_VALUE, null);
    }

    protected void place(PositionData data, EntityPlayer target, boolean rotate, boolean schedule, boolean resetSlow, float damage, MutableWrapper<Boolean> liquidBreak) {
        if (liquidBreak != null) {
            this.module.liquidTimer.reset();
        }
        this.module.placeTimer.reset(resetSlow ? (long)this.module.slowPlaceDelay.getValue().intValue() : (long)this.module.placeDelay.getValue().intValue());
        BlockPos pos = data.getPos();
        BlockPos crystalPos = new BlockPos((double)((float)pos.getX() + 0.5f), (double)(pos.getY() + 1), (double)((float)pos.getZ() + 0.5f));
        this.module.placed.put(crystalPos, new CrystalTimeStamp(damage));
        this.module.damageSyncHelper.setSync(pos, data.getMaxDamage(), this.module.newVerEntities.getValue());
        this.module.setTarget(target);
        boolean realtime = this.module.realtime.getValue();
        if (!realtime) {
            this.module.setRenderPos(pos, data.getMaxDamage());
        }
        MutableWrapper<Boolean> hasPlaced = new MutableWrapper<Boolean>(false);
        if (!InventoryUtil.isHolding(Items.END_CRYSTAL) && (this.module.autoSwitch.getValue() == AutoSwitch.Always || this.module.autoSwitch.getValue() == AutoSwitch.Bind && this.module.switching) && !this.module.mainHand.getValue().booleanValue()) {
            mc.addScheduledTask(() -> OFFHAND.computeIfPresent(o -> o.setMode(OffhandMode.CRYSTAL)));
        }
        Runnable post = this.module.rotationHelper.post(this.module, data.getMaxDamage(), realtime, pos, hasPlaced, liquidBreak);
        if (rotate) {
            RotationFunction function = this.module.rotationHelper.forPlacing(pos, hasPlaced);
            if (this.module.rotationCanceller.setRotations(function)) {
                this.module.runPost();
                post.run();
                if (this.module.attack.getValue().booleanValue() && hasPlaced.get().booleanValue()) {
                    this.module.rotation = function;
                }
                return;
            }
            this.module.rotation = function;
        }
        if (schedule || !this.placeCheckPre(pos)) {
            this.module.post.add(post);
        } else {
            post.run();
        }
    }

    protected boolean placeObby(PlaceData data, MineSlots liquid) {
        PositionData bestData = this.module.obbyHelper.findBestObbyData(liquid != null ? data.getLiquidObby() : data.getAllObbyData(), this.all, this.friends, this.entities, data.getTarget(), this.module.newVer.getValue());
        if (LEG_SWITCH.returnIfPresent(LegSwitch::isActive, false).booleanValue() || ANTISURROUND.returnIfPresent(AntiSurround::isActive, false).booleanValue()) {
            return true;
        }
        this.module.obbyCalcTimer.reset();
        if (bestData != null && bestData.getMaxDamage() > this.module.obbyMinDmg.getValue().floatValue()) {
            this.module.setTarget(bestData.getTarget());
            if (this.module.obbyRotate.getValue() != Rotate.None && !this.rotating && bestData.getPath().length > 0) {
                this.module.rotation = this.module.rotationHelper.forObby(bestData);
                this.rotating = true;
            }
            Runnable r = this.module.rotationHelper.postBlock(bestData);
            if (!this.rotating) {
                r.run();
            } else {
                this.module.post.add(r);
            }
            if (liquid != null) {
                this.placeAndBreakLiquid(bestData, liquid, this.rotating);
            }
            this.place(bestData, bestData.getTarget(), !this.module.rotate.getValue().noRotate(ACRotate.Place), this.rotating || this.scheduling, false);
            this.module.obbyTimer.reset();
            return true;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setFinished(boolean finished) {
        if (this.module.multiThread.getValue().booleanValue() && this.module.smartPost.getValue().booleanValue() && this.module.motionID.get() != this.motionID) {
            this.module.runPost();
        }
        super.setFinished(finished);
        if (finished) {
            AutoCrystal autoCrystal = this.module;
            synchronized (autoCrystal) {
                this.module.notifyAll();
            }
        }
    }

    protected boolean placeCheckPre(BlockPos pos) {
        RayTraceResult result;
        double z;
        double y;
        double x = Managers.POSITION.getX();
        if (pos.distanceSqToCenter(x, y = Managers.POSITION.getY(), z = Managers.POSITION.getZ()) >= (double)MathUtil.square(this.module.placeRange.getValue().floatValue())) {
            return false;
        }
        if (!(this.module.rotate.getValue().noRotate(ACRotate.Place) || (result = RotationUtil.rayTraceTo(pos, (IBlockAccess)AbstractCalculation.mc.world)) != null && result.getBlockPos().equals((Object)pos))) {
            return false;
        }
        if (pos.distanceSqToCenter(x, y, z) >= (double)MathUtil.square(this.module.placeTrace.getValue().floatValue())) {
            result = RotationUtil.rayTraceTo(pos, (IBlockAccess)AbstractCalculation.mc.world, (b, p) -> true);
            return result != null && result.getBlockPos().equals((Object)pos);
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected MutableWrapper<Boolean> placeAndBreakLiquid(PositionData data, MineSlots liquid, boolean rotating) {
        int[] order;
        boolean newVer = this.module.newVer.getValue();
        boolean absorb = this.module.absorb.getValue();
        ArrayList<Ray> path = new ArrayList<Ray>((newVer ? 1 : 2) + (absorb ? 1 : 0));
        BlockStateHelper access = new BlockStateHelper();
        path.add(RayTraceFactory.rayTrace(data.getFrom(), data.getPos(), EnumFacing.UP, access, Blocks.NETHERRACK.getDefaultState(), this.module.liquidRayTrace.getValue() != false ? -1.0 : 2.0));
        BlockPos up = data.getPos().up();
        access.addBlockState(up, Blocks.NETHERRACK.getDefaultState());
        IBlockState upState = AbstractCalculation.mc.world.getBlockState(up);
        if (!newVer && upState.getMaterial().isLiquid()) {
            path.add(RayTraceFactory.rayTrace(data.getFrom(), up, EnumFacing.UP, access, Blocks.NETHERRACK.getDefaultState(), this.module.liquidRayTrace.getValue() != false ? -1.0 : 2.0));
            access.addBlockState(up.up(), Blocks.NETHERRACK.getDefaultState());
            order = new int[]{0, 1};
        } else {
            order = new int[]{0};
        }
        if (absorb) {
            BlockPos absorpPos = up;
            EnumFacing absorbFacing = this.module.liquidHelper.getAbsorbFacing(absorpPos, this.entities, access, this.module.placeRange.getValue().floatValue());
            if (absorbFacing == null && !newVer) {
                absorpPos = up.up();
                absorbFacing = this.module.liquidHelper.getAbsorbFacing(absorpPos, this.entities, access, this.module.placeRange.getValue().floatValue());
            }
            if (absorbFacing != null) {
                int[] arrn;
                path.add(RayTraceFactory.rayTrace(data.getFrom(), absorpPos, absorbFacing, access, Blocks.NETHERRACK.getDefaultState(), this.module.liquidRayTrace.getValue() != false ? -1.0 : 2.0));
                if (order.length == 2) {
                    int[] arrn2 = new int[3];
                    arrn2[0] = 2;
                    arrn2[1] = 1;
                    arrn = arrn2;
                    arrn2[2] = 0;
                } else {
                    int[] arrn3 = new int[2];
                    arrn3[0] = 1;
                    arrn = arrn3;
                    arrn3[1] = 0;
                }
                order = arrn;
            }
        }
        Ray[] pathArray = path.toArray(new Ray[0]);
        data.setPath(pathArray);
        data.setValid(true);
        MutableWrapper<Boolean> placed = new MutableWrapper<Boolean>(false);
        MutableWrapper<Integer> postBlock = new MutableWrapper<Integer>(-1);
        Runnable r = this.module.rotationHelper.postBlock(data, liquid.getBlockSlot(), this.module.liqRotate.getValue(), placed, postBlock);
        Runnable b = this.module.rotationHelper.breakBlock(liquid.getToolSlot(), placed, postBlock, order, pathArray);
        Runnable a = null;
        if (this.module.setAir.getValue().booleanValue()) {
            a = () -> {
                for (Ray ray : path) {
                    AbstractCalculation.mc.world.setBlockState(ray.getPos().offset(ray.getFacing()), Blocks.AIR.getDefaultState());
                }
            };
        }
        if (rotating) {
            Queue<Runnable> queue = this.module.post;
            synchronized (queue) {
                this.module.post.add(r);
                this.module.post.add(b);
                if (a != null) {
                    mc.addScheduledTask(a);
                }
            }
        } else {
            r.run();
            b.run();
            if (a != null) {
                mc.addScheduledTask(a);
            }
        }
        return placed;
    }

    protected boolean focusBreak() {
        Entity focus = this.module.focus;
        if (focus != null) {
            if (EntityUtil.isDead(focus) || Managers.POSITION.getDistanceSq(focus) > (double)MathUtil.square(this.module.breakRange.getValue().floatValue()) && RotationUtil.getRotationPlayer().getDistanceSq(focus) > (double)MathUtil.square(this.module.breakRange.getValue().floatValue())) {
                this.module.focus = null;
                return false;
            }
            double exponent = this.module.focusExponent.getValue();
            this.breakData = this.getBreakHelper().getData(this.module.focusAngleCalc.getValue() != false && exponent != 0.0 ? RotationComparator.asSet(exponent, this.module.focusDiff.getValue()) : this.getBreakDataSet(), this.entities, this.all, this.friends);
            ArrayList<CrystalData> focusList = new ArrayList<CrystalData>(1);
            BreakData<T> focusData = this.getBreakHelper().newData(focusList);
            CrystalData minData = null;
            double minAngle = Double.MAX_VALUE;
            for (CrystalData data : this.breakData.getData()) {
                if (data.hasCachedRotations() && data.getAngle() < minAngle) {
                    minAngle = data.getAngle();
                    minData = data;
                }
                if (!data.getCrystal().equals((Object)focus)) continue;
                if (data.getSelfDmg() > this.module.maxSelfBreak.getValue().floatValue() || data.getDamage() < this.module.minBreakDamage.getValue().floatValue()) {
                    return false;
                }
                focusData.getData().add(data);
            }
            Optional<T> first = focusData.getData().stream().findFirst();
            if (!first.isPresent()) {
                this.module.focus = null;
                return false;
            }
            if (this.module.focusAngleCalc.getValue().booleanValue() && minData != null && !minData.equals(first.get())) {
                focusList.set(0, minData);
            }
            this.evaluate(focusData);
            return this.rotating || this.attacking;
        }
        return false;
    }

    protected Set<T> getBreakDataSet() {
        double exponent = this.module.rotationExponent.getValue();
        if (Double.compare(exponent, 0.0) == 0 || this.module.rotate.getValue().noRotate(ACRotate.Break)) {
            return new TreeSet();
        }
        return RotationComparator.asSet(exponent, this.module.minRotDiff.getValue());
    }
}

