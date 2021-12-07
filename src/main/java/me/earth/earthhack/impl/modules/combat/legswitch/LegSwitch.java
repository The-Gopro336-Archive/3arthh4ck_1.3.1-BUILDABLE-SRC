package me.earth.earthhack.impl.modules.combat.legswitch;

import java.util.List;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.minecraft.combat.util.SoundObserver;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.legswitch.ConstellationFactory;
import me.earth.earthhack.impl.modules.combat.legswitch.LegConstellation;
import me.earth.earthhack.impl.modules.combat.legswitch.LegSwitchData;
import me.earth.earthhack.impl.modules.combat.legswitch.ListenerBlockBreak;
import me.earth.earthhack.impl.modules.combat.legswitch.ListenerBlockChange;
import me.earth.earthhack.impl.modules.combat.legswitch.ListenerBlockMulti;
import me.earth.earthhack.impl.modules.combat.legswitch.ListenerMotion;
import me.earth.earthhack.impl.modules.combat.legswitch.ListenerSound;
import me.earth.earthhack.impl.modules.combat.legswitch.ListenerSpawnObject;
import me.earth.earthhack.impl.modules.combat.legswitch.modes.LegAutoSwitch;
import me.earth.earthhack.impl.util.helpers.addable.ListType;
import me.earth.earthhack.impl.util.helpers.addable.RemovingItemAddingModule;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyModule;
import me.earth.earthhack.impl.util.helpers.blocks.modes.PlaceSwing;
import me.earth.earthhack.impl.util.helpers.blocks.modes.RayTraceMode;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.math.DiscreteTimer;
import me.earth.earthhack.impl.util.math.GuardTimer;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.path.BasePath;
import me.earth.earthhack.impl.util.math.path.PathFinder;
import me.earth.earthhack.impl.util.math.path.Pathable;
import me.earth.earthhack.impl.util.math.raytrace.Ray;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.states.BlockStateHelper;
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
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;

public class LegSwitch
extends RemovingItemAddingModule {
    private static final ModuleCache<AutoCrystal> AUTO_CRYSTAL = Caches.getModule(AutoCrystal.class);
    protected final Setting<LegAutoSwitch> autoSwitch = this.register(new EnumSetting<LegAutoSwitch>("AutoSwitch", LegAutoSwitch.None));
    protected final Setting<Integer> delay = this.register(new NumberSetting<Integer>("Delay", 500, 0, 500));
    protected final Setting<Boolean> closest = this.register(new BooleanSetting("Closest", true));
    protected final Setting<ACRotate> rotate = this.register(new EnumSetting<ACRotate>("Rotate", ACRotate.None));
    protected final Setting<Float> minDamage = this.register(new NumberSetting<Float>("MinDamage", Float.valueOf(7.0f), Float.valueOf(0.0f), Float.valueOf(36.0f)));
    protected final Setting<Float> maxSelfDamage = this.register(new NumberSetting<Float>("MaxSelfDamage", Float.valueOf(4.0f), Float.valueOf(0.0f), Float.valueOf(36.0f)));
    protected final Setting<Float> placeRange = this.register(new NumberSetting<Float>("PlaceRange", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(6.0f)));
    protected final Setting<Float> placeTrace = this.register(new NumberSetting<Float>("PlaceTrace", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(6.0f)));
    protected final Setting<Float> breakRange = this.register(new NumberSetting<Float>("BreakRange", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(6.0f)));
    protected final Setting<Float> breakTrace = this.register(new NumberSetting<Float>("BreakTrace", Float.valueOf(3.0f), Float.valueOf(0.0f), Float.valueOf(6.0f)));
    protected final Setting<Float> combinedTrace = this.register(new NumberSetting<Float>("CombinedTrace", Float.valueOf(3.0f), Float.valueOf(0.0f), Float.valueOf(6.0f)));
    protected final Setting<Boolean> instant = this.register(new BooleanSetting("Instant", true));
    protected final Setting<Boolean> setDead = this.register(new BooleanSetting("SetDead", false));
    protected final Setting<Boolean> requireMid = this.register(new BooleanSetting("Mid", false));
    protected final Setting<Boolean> soundRemove = this.register(new BooleanSetting("SoundRemove", true));
    protected final Setting<Boolean> antiWeakness = this.register(new BooleanSetting("AntiWeakness", false));
    protected final Setting<Boolean> soundStart = this.register(new BooleanSetting("SoundStart", false));
    protected final Setting<Boolean> newVer = this.register(new BooleanSetting("1.13+", false));
    protected final Setting<Boolean> newVerEntities = this.register(new BooleanSetting("1.13-Entities", false));
    protected final Setting<Boolean> rotationPacket = this.register(new BooleanSetting("Rotation-Packet", false));
    protected final Setting<Integer> coolDown = this.register(new NumberSetting<Integer>("CoolDown", 0, 0, 500));
    protected final Setting<Float> targetRange = this.register(new NumberSetting<Float>("Target-Range", Float.valueOf(10.0f), Float.valueOf(0.0f), Float.valueOf(20.0f)));
    protected final Setting<Boolean> breakBlock = this.register(new BooleanSetting("BlockStart", false));
    protected final Setting<Boolean> obsidian = this.register(new BooleanSetting("Obsidian", false));
    protected final Setting<Integer> helpingBlocks = this.register(new NumberSetting<Integer>("HelpingBlocks", 1, 1, 10));
    protected final Setting<RayTraceMode> smartRay = this.register(new EnumSetting<RayTraceMode>("Raytrace", RayTraceMode.Fast));
    protected final Setting<Rotate> obbyRotate = this.register(new EnumSetting<Rotate>("Obby-Rotate", Rotate.None));
    protected final Setting<Boolean> normalRotate = this.register(new BooleanSetting("NormalRotate", false));
    protected final Setting<Boolean> setBlockState = this.register(new BooleanSetting("SetBlockState", false));
    protected final Setting<PlaceSwing> obbySwing = this.register(new EnumSetting<PlaceSwing>("ObbySwing", PlaceSwing.Once));
    protected final SoundObserver observer = new ListenerSound(this);
    protected final DiscreteTimer timer = new GuardTimer(500L);
    protected LegConstellation constellation;
    protected volatile boolean active;
    protected BlockPos targetPos;
    protected float[] bRotations;
    protected float[] rotations;
    protected Runnable post;
    protected int slot;

    public LegSwitch() {
        super("LegSwitch", Category.Combat, s -> "Black/Whitelist LegSwitch from being active while you hold " + s.getName() + ".");
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerSpawnObject(this));
        this.listeners.add(new ListenerBlockChange(this));
        this.listeners.add(new ListenerBlockMulti(this));
        this.listeners.add(new ListenerBlockBreak(this));
        this.listType.setValue(ListType.BlackList);
        this.setData(new LegSwitchData(this));
    }

    @Override
    public void onEnable() {
        Managers.SET_DEAD.addObserver(this.observer);
    }

    @Override
    public void onDisable() {
        Managers.SET_DEAD.removeObserver(this.observer);
        this.active = false;
        this.constellation = null;
    }

    @Override
    public String getDisplayInfo() {
        return this.constellation == null || !this.active ? null : this.constellation.player.getName();
    }

    public boolean isActive() {
        return this.isEnabled() && this.active;
    }

    protected void startCalculation() {
        this.startCalculation(LegSwitch.mc.world);
    }

    protected void startCalculation(IBlockAccess access) {
        if (!this.isStackValid(LegSwitch.mc.player.getHeldItemOffhand()) && !this.isStackValid(LegSwitch.mc.player.getHeldItemMainhand())) {
            this.active = false;
            return;
        }
        if (this.constellation == null || !this.constellation.isValid(this, LegSwitch.mc.player, access)) {
            this.constellation = ConstellationFactory.create(this, LegSwitch.mc.world.playerEntities);
            if (this.constellation != null && !this.obsidian.getValue().booleanValue() && (this.constellation.firstNeedsObby || this.constellation.secondNeedsObby)) {
                this.constellation = null;
            }
        }
        if (this.constellation == null) {
            this.active = false;
        }
        this.active = true;
        this.prepare();
        this.execute();
    }

    protected boolean isValid(BlockPos pos, IBlockState state, List<EntityPlayer> players) {
        if (state.getBlock() != Blocks.AIR || players == null) {
            return false;
        }
        for (EntityPlayer player : players) {
            if (player == null || Managers.FRIENDS.contains(player) || !(player.getDistanceSq(pos) < 4.0)) continue;
            return true;
        }
        return false;
    }

    protected void prepare() {
        RayTraceResult result;
        BlockPos obbyPos;
        if (!this.timer.passed(this.delay.getValue().intValue())) {
            return;
        }
        int weakSlot = -1;
        if (!(DamageUtil.canBreakWeakness(true) || this.antiWeakness.getValue().booleanValue() && this.coolDown.getValue() == 0 && (weakSlot = DamageUtil.findAntiWeakness()) != -1)) {
            return;
        }
        if (this.constellation == null) {
            this.targetPos = null;
            return;
        }
        Entity crystal = null;
        for (Entity entity : LegSwitch.mc.world.loadedEntityList) {
            if (!(entity instanceof EntityEnderCrystal) || entity.isDead || !entity.getEntityBoundingBox().intersects(new AxisAlignedBB(this.constellation.targetPos))) continue;
            crystal = entity;
            break;
        }
        this.targetPos = this.constellation.firstPos;
        boolean firstNeedsObby = true;
        BlockPos blockPos = obbyPos = this.constellation.firstNeedsObby ? this.constellation.firstPos : null;
        if (crystal != null) {
            if (Managers.SWITCH.getLastSwitch() < (long)this.coolDown.getValue().intValue()) {
                return;
            }
            if (crystal.getPosition().down().equals(this.constellation.firstPos)) {
                obbyPos = this.constellation.secondNeedsObby ? this.constellation.secondPos : null;
                this.targetPos = this.constellation.secondPos;
                firstNeedsObby = false;
            }
            this.bRotations = RotationUtil.getRotations(crystal);
        }
        int obbySlot = -1;
        Pathable path = null;
        if (obbyPos != null) {
            obbySlot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN, new Block[0]);
            if (obbySlot == -1) {
                return;
            }
            path = new BasePath(RotationUtil.getRotationPlayer(), obbyPos, this.helpingBlocks.getValue());
            boolean newVersion = this.newVer.getValue();
            BlockPos[] blacklist = new BlockPos[newVersion ? 4 : 6];
            blacklist[0] = this.constellation.playerPos;
            blacklist[1] = this.constellation.secondPos.up();
            blacklist[2] = this.constellation.firstPos.up();
            blacklist[3] = this.constellation.targetPos;
            if (!newVersion) {
                blacklist[4] = this.constellation.secondPos.up(2);
                blacklist[5] = this.constellation.firstPos.up(2);
            }
            PathFinder.findPath(path, this.placeRange.getValue().floatValue(), LegSwitch.mc.world.loadedEntityList, this.smartRay.getValue(), ObbyModule.HELPER, Blocks.OBSIDIAN.getDefaultState(), PathFinder.CHECK, blacklist);
            if (!path.isValid() || path.getPath().length > 1 && this.normalRotate.getValue().booleanValue() && this.obbyRotate.getValue() == Rotate.Normal) {
                this.constellation.invalid = true;
                return;
            }
        }
        assert (this.targetPos != null);
        this.rotations = path != null ? path.getPath()[0].getRotations() : RotationUtil.getRotationsToTopMiddle(this.targetPos.up());
        if (!this.rotate.getValue().noRotate(ACRotate.Place)) {
            float[] theRotations = this.rotations;
            IBlockAccess access = LegSwitch.mc.world;
            if (path != null) {
                Ray last = path.getPath()[path.getPath().length - 1];
                theRotations = last.getRotations();
                BlockStateHelper helper = new BlockStateHelper();
                helper.addBlockState(last.getPos().offset(last.getFacing()), Blocks.OBSIDIAN.getDefaultState());
                access = helper;
            }
            BlockPos thePos = this.targetPos.up();
            result = RotationUtil.rayTraceWithYP(thePos, access, theRotations[0], theRotations[1], (b, p) -> p.equals(thePos));
        } else {
            result = new RayTraceResult(new Vec3d(0.5, 1.0, 0.5), EnumFacing.UP);
            this.rotations = null;
        }
        Entity finalCrystal = crystal;
        if (this.rotationPacket.getValue().booleanValue() && this.rotations != null && this.bRotations != null && finalCrystal != null) {
            int finalWeakSlot = weakSlot;
            Runnable runnable = () -> {
                LegSwitch.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(this.bRotations[0], this.bRotations[1], LegSwitch.mc.player.onGround));
                int lastSlot = LegSwitch.mc.player.inventory.currentItem;
                if (finalWeakSlot != -1) {
                    InventoryUtil.switchTo(finalWeakSlot);
                }
                LegSwitch.mc.player.connection.sendPacket(new CPacketUseEntity(finalCrystal));
                LegSwitch.mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                this.bRotations = null;
                InventoryUtil.switchTo(lastSlot);
            };
            if (finalWeakSlot != -1) {
                Locks.acquire(Locks.PLACE_SWITCH_LOCK, runnable);
            } else {
                runnable.run();
            }
            if (this.setDead.getValue().booleanValue()) {
                Managers.SET_DEAD.setDead(finalCrystal);
            }
        }
        if (this.rotations == null) {
            this.rotations = this.bRotations;
        }
        Pathable finalPath = path;
        int finalObbySlot = obbySlot;
        boolean finalFirstNeedsObby = firstNeedsObby;
        LegConstellation finalConstellation = this.constellation;
        this.post = Locks.wrap(Locks.PLACE_SWITCH_LOCK, () -> {
            EnumHand hand;
            int slot = -1;
            int lastSlot = LegSwitch.mc.player.inventory.currentItem;
            if (!InventoryUtil.isHolding(Items.END_CRYSTAL)) {
                slot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL, new Item[0]);
                if (this.autoSwitch.getValue() == LegAutoSwitch.None || slot == -1) {
                    this.active = false;
                    return;
                }
            }
            EnumHand enumHand = hand = LegSwitch.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL || slot != -2 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
            if (this.bRotations != null && finalCrystal != null) {
                LegSwitch.mc.player.connection.sendPacket(new CPacketUseEntity(finalCrystal));
                LegSwitch.mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
            }
            if (finalPath != null) {
                InventoryUtil.switchTo(finalObbySlot);
                for (int i = 0; i < finalPath.getPath().length; ++i) {
                    Ray ray = finalPath.getPath()[i];
                    if (i != 0 && this.obbyRotate.getValue() == Rotate.Packet) {
                        Managers.ROTATION.setBlocking(true);
                        float[] r = ray.getRotations();
                        LegSwitch.mc.player.connection.sendPacket(PacketUtil.rotation(r[0], r[1], LegSwitch.mc.player.onGround));
                        Managers.ROTATION.setBlocking(false);
                    }
                    float[] f = RayTraceUtil.hitVecToPlaceVec(ray.getPos(), ray.getResult().hitVec);
                    LegSwitch.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(ray.getPos(), ray.getFacing(), hand, f[0], f[1], f[2]));
                    if (this.setBlockState.getValue().booleanValue()) {
                        mc.addScheduledTask(() -> {
                            if (LegSwitch.mc.world != null) {
                                finalConstellation.states.put(ray.getPos().offset(ray.getFacing()), Blocks.OBSIDIAN.getDefaultState());
                                LegSwitch.mc.world.setBlockState(ray.getPos().offset(ray.getFacing()), Blocks.OBSIDIAN.getDefaultState());
                            }
                        });
                    }
                    if (this.obbySwing.getValue() != PlaceSwing.Always) continue;
                    Swing.Packet.swing(hand);
                }
                Ray ray = finalPath.getPath()[finalPath.getPath().length - 1];
                BlockPos last = ray.getPos().offset(ray.getFacing());
                Managers.BLOCKS.addCallback(last, s -> {
                    if (s.getBlock() == Blocks.OBSIDIAN) {
                        if (finalFirstNeedsObby) {
                            finalConstellation.firstNeedsObby = false;
                        } else {
                            finalConstellation.secondNeedsObby = false;
                        }
                    }
                    finalConstellation.states.put(last, (IBlockState)s);
                });
                if (this.obbySwing.getValue() == PlaceSwing.Once) {
                    Swing.Packet.swing(hand);
                }
            }
            if (slot != -1) {
                InventoryUtil.switchTo(slot);
            } else {
                InventoryUtil.syncItem();
            }
            CPacketPlayerTryUseItemOnBlock place = new CPacketPlayerTryUseItemOnBlock(this.targetPos, result.sideHit, hand, (float)result.hitVec.x, (float)result.hitVec.y, (float)result.hitVec.z);
            CPacketAnimation animation = new CPacketAnimation(hand);
            LegSwitch.mc.player.connection.sendPacket(place);
            LegSwitch.mc.player.connection.sendPacket(animation);
            if (slot != -1 && this.autoSwitch.getValue() != LegAutoSwitch.Keep) {
                InventoryUtil.switchTo(lastSlot);
            }
            AUTO_CRYSTAL.computeIfPresent(a -> a.setRenderPos(this.targetPos, "LS"));
            if (this.setDead.getValue().booleanValue() && finalCrystal != null) {
                Managers.SET_DEAD.setDead(finalCrystal);
            }
        });
        this.timer.reset(this.delay.getValue().intValue());
        if (this.rotate.getValue().noRotate(ACRotate.Place)) {
            this.execute();
        }
    }

    protected void execute() {
        if (this.post != null) {
            this.active = true;
            this.post.run();
        }
        this.post = null;
        this.bRotations = null;
        this.rotations = null;
    }

    protected boolean checkPos(BlockPos pos) {
        if (BlockUtil.getDistanceSq(pos) <= (double)MathUtil.square(this.placeRange.getValue().floatValue()) && LegSwitch.mc.player.getDistanceSq(pos) > (double)MathUtil.square(this.placeTrace.getValue().floatValue()) && !RayTraceUtil.raytracePlaceCheck(LegSwitch.mc.player, pos)) {
            return false;
        }
        BlockPos up = pos.up();
        BlockPos upUp = up.up();
        if (LegSwitch.mc.world.getBlockState(up).getBlock() != Blocks.AIR || !this.newVer.getValue().booleanValue() && LegSwitch.mc.world.getBlockState(upUp).getBlock() != Blocks.AIR || !BlockUtil.checkEntityList(up, true, null) || this.newVerEntities.getValue().booleanValue() && !BlockUtil.checkEntityList(upUp, true, null)) {
            return false;
        }
        if (BlockUtil.getDistanceSq(pos) <= (double)MathUtil.square(this.combinedTrace.getValue().floatValue())) {
            return true;
        }
        return RayTraceUtil.canBeSeen(new Vec3d((double)pos.getX() + 0.5, (double)pos.getY() + 2.7, (double)pos.getZ() + 0.5), (Entity)LegSwitch.mc.player);
    }
}
